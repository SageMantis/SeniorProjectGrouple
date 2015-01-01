package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

/*
 * AddFriendActivity allows user to add another user as a friend.
 */
public class FriendAddActivity extends ActionBarActivity
{
	BroadcastReceiver broadcastReceiver;
	User user; //current user
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Set the activity layout.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friend);

		Global global = ((Global) getApplicationContext());
		Intent parentIntent = getIntent();
		Bundle extras = parentIntent.getExtras();
		user = global.loadUser(extras.getString("email"));
		initActionBar();
		// Initialize the kill switch. The kill switch will kill all open
		// activities.
		initKillswitchListener();
	}

	public void initActionBar()
	{
		// Set up the action bar.
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);
		// Set up the back button listener for the action bar.
		upButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View view)
			{

				startParentActivity(view);

			}
		});
		// Set the action bar title.
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText("Add Friend");
	}
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_logout)
		{
			// If the user hits the logout button, then clear global and go to
			// the logout screen.
			Global global = ((Global) getApplicationContext());
			Intent login = new Intent(this, LoginActivity.class);
			startActivity(login);
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		if (id == R.id.action_home)
		{
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	// This function helps the action bar back button go back to the correct
	// activity.
	public void startParentActivity(View view)
	{
		Bundle extras = getIntent().getExtras();

		String className = extras.getString("ParentClassName");
		Intent newIntent = null;
		try
		{
			newIntent = new Intent(this, Class.forName("cs460.grouple.grouple."
					+ className));
			if (extras.getString("ParentEmail") != null)
			{
				newIntent.putExtra("email", extras.getString("ParentEmail"));
			}
			// newIntent.putExtra("email", extras.getString("email"));
			// newIntent.putExtra("ParentEmail", extras.getString("email"));
			newIntent.putExtra("up", "true");
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		startActivity(newIntent);
	}

	// Adds a friend.
	public void addFriendButton(View view)
	{
		EditText emailEditTextAFA = (EditText) findViewById(R.id.emailEditTextAFA);
		String email = emailEditTextAFA.getText().toString();
		Global global = ((Global) getApplicationContext());
		//String senderEmail = global.getCurrentUser(); PANDA
		//will need to be 	 = user.getEmail();
		//System.out.println("Email:" + email + "\nSender Email:" + senderEmail);

		// Execute the add friend php
		//need to sync this PANDA
		new getAddFriendTask()
				.execute("http://98.213.107.172/android_connect/add_friend.php");
	}

	// This task sends a friend request to the given user.
	private class getAddFriendTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			EditText emailEditText = (EditText) findViewById(R.id.emailEditTextAFA);
			Global global = ((Global) getApplicationContext());
			//String sender = global.getCurrentUser(); PANDA same as above user.getEmail()
			String receiver = emailEditText.getText().toString();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("sender", user.getEmail()));
			nameValuePairs.add(new BasicNameValuePair("receiver", receiver));

			return global.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				System.out.println(jsonObject.getString("success"));

				EditText emailEditText = (EditText) findViewById(R.id.emailEditTextAFA);
				emailEditText.setText("");
				TextView addFriendMessage = (TextView) findViewById(R.id.addFriendMessageTextViewAFA);
				addFriendMessage.setText(jsonObject.getString("message"));

				if (jsonObject.getString("success").toString().equals("1"))
				{
					// friend added or already friends (user's goal)
					System.out.println("success!");
					addFriendMessage.setTextColor(getResources().getColor(
							R.color.light_green));
				} else if (jsonObject.getString("success").toString()
						.equals("2"))
				{
					addFriendMessage.setTextColor(getResources().getColor(
							R.color.orange));
				} else
				{
					// user does not exist, self request, or sql error
					System.out.println("fail!");
					addFriendMessage.setTextColor(getResources().getColor(
							R.color.red));
				}
				addFriendMessage.setVisibility(0);

			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	@Override
	// If the user hits the back button (not the one in the action bar, then go
	// back to the parent activity.
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			startParentActivity(null);
		}
		return false;
	}

	public void initKillswitchListener()
	{
		// START KILL SWITCH LISTENER
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("CLOSE_ALL");
		broadcastReceiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				// close activity
				if (intent.getAction().equals("CLOSE_ALL"))
				{
					Log.d("app666", "we killin the login it");
					// System.exit(1);
					finish();
				}

			}
		};
		registerReceiver(broadcastReceiver, intentFilter);
		// End Kill switch listener
	}
}
