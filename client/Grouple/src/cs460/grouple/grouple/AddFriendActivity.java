package cs460.grouple.grouple;

import java.io.BufferedReader;
import android.graphics.Color;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
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
import android.widget.EditText;
import android.widget.TextView;

public class AddFriendActivity extends ActionBarActivity
{
	BroadcastReceiver broadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friend);
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(true);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText("Add Friend");

		initKillswitchListener();
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
			Global global = ((Global) getApplicationContext());
			global.setAcceptEmail("");
			global.setCurrentUser("");
			global.setDeclineEmail("");
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

	public void addFriendButton(View view)
	{
		EditText emailEditTextAFA = (EditText) findViewById(R.id.emailEditTextAFA);
		String email = emailEditTextAFA.getText().toString();
		Global global = ((Global) getApplicationContext());
		String senderEmail = global.getCurrentUser();
		System.out.println("Email:" + email + "\nSender Email:" + senderEmail);

		// write the mf
		new getAddFriendTask()
				.execute("http://98.213.107.172/android_connect/add_friend.php");
	}

	

	private class getAddFriendTask extends AsyncTask<String, Void, String>
	{
		protected String doInBackground(String... urls)
		{
			EditText emailEditText = (EditText) findViewById(R.id.emailEditTextAFA);
			Global global = ((Global) getApplicationContext());
			String sender = global.getCurrentUser();
			String receiver = emailEditText.getText().toString();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("sender", sender));
			nameValuePairs.add(new BasicNameValuePair("receiver", receiver));
				
			return global.readJSONFeed(urls[0], nameValuePairs);
		}

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
					addFriendMessage.setTextColor(getResources().getColor(R.color.light_green));
				} 
				else if (jsonObject.getString("success").toString()
								.equals("2"))
				{
					addFriendMessage.setTextColor(getResources().getColor(R.color.orange));
				}
				else
				{
					// user does not exist, self request, or sql error
					System.out.println("fail!");
					addFriendMessage.setTextColor(getResources().getColor(R.color.red));
				}
				addFriendMessage.setVisibility(0);

			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			startFriendsActivity(null);
		}
		return false;
	}

	/* Start activity functions for going back and logging out */

	public void startFriendsActivity(View view)
	{
		Intent intent = new Intent(this, FriendsActivity.class);
		startActivity(intent);
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
