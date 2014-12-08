package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import cs460.grouple.grouple.R;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class UserActivity extends ActionBarActivity 
{

	private ImageView iv;
	private Bitmap bmp;
	BroadcastReceiver broadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);

		/*Action bar*/
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);
		upButton.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {

				startParentActivity(view);

			}
		});
		//upButton.setOnClickListener
		Global global = ((Global) getApplicationContext());
		actionbarTitle.setText(global.getName() + "'s Profile");
		
		/*Notifications*/
		global.fetchNumFriends(global.getCurrentUser());
		global.fetchNumFriendRequests(global.getCurrentUser());
		// Setting num friends on friends button
		//Button friendsButton = (Button) findViewById(R.id.friendsButtonUPA);
		// global.fetchNumFriends();
		//friendsButton.setText("Friends\n(" + global.getNumFriends() + ")");
		View user = findViewById(R.id.userLayout);

		global.setNotifications(user);
		
		// execute php script, using the current users email address to populate
		// the textviews
		new getProfileTask()
				.execute("http://98.213.107.172/android_connect/get_profile.php");

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

		// Set up the image view
		if (iv == null)
		{
			iv = (ImageView) findViewById(R.id.profilePhoto);
		}
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
			bmp = null;
			iv = null;
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			startParentActivity(null);
		}
		return false;
	}


	/* Start activity functions for going back to home and logging out */
	public void startParentActivity(View view)
	{
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		bmp = null;
		iv = null;
		finish();
	}

	public void startEditProfileActivity(View view)
	{
		Intent intent = new Intent(this, EditProfileActivity.class);
		startActivity(intent);
		bmp = null;
		iv = null;
	}

	/*
	 * Get profile executes get_profile.php. It uses the current users email
	 * address to retrieve the users name, age, and bio.
	 */
	private class getProfileTask extends AsyncTask<String, Void, String>
	{

		protected String doInBackground(String... urls)
		{

			Global global = ((Global) getApplicationContext());
			String email = global.getCurrentUser();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
					1);
			nameValuePairs.add(new BasicNameValuePair("email", email));
			return global.readJSONFeed(urls[0], nameValuePairs);
		}

		

		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				System.out.println(jsonObject.getString("success"));
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// Success
					JSONArray jsonProfileArray = (JSONArray) jsonObject
							.getJSONArray("profile");

					//String name = jsonProfileArray.getString(0) + " "
						//	+ jsonProfileArray.getString(1);
					String age = jsonProfileArray.getString(2);
					String bio = jsonProfileArray.getString(3);
					String location = jsonProfileArray.getString(4);
					String img = jsonProfileArray.getString(5);

					// decode image back to android bitmap format
					byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
					if (decodedString != null)
					{
						bmp = BitmapFactory.decodeByteArray(decodedString, 0,
								decodedString.length);
					}
					// set the image
					if (bmp != null)
					{
						if (iv == null)
						{
							iv = (ImageView) findViewById(R.id.profilePhoto);

						}
						iv.setImageBitmap(bmp);
						img = null;
						decodedString = null;
					}

					// TextView nameTextView = (TextView)
					// findViewById(R.id.nameEditTextEPA);
					TextView ageTextView = (TextView) findViewById(R.id.ageTextView);
					TextView locationTextView = (TextView) findViewById(R.id.locationTextView);
					TextView bioTextView = (TextView) findViewById(R.id.bioTextView);
					// JSONObject bioJson = jsonProfileArray.getJSONObject(0);
					// nameTextView.setText(name);
					ageTextView.setText(age + " years old");
					bioTextView.setText(bio);
					locationTextView.setText(location);
				} else
				{
					// Fail
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	public void startGroupsCurrentActivity(View view)
	{
		Intent intent = new Intent(this, GroupsCurrentActivity.class);
		Global global = ((Global) getApplicationContext());
		intent.putExtra("ParentClassName", "UserActivity");
		intent.putExtra("email", global.getCurrentUser());
		intent.putExtra("mod", "true");
		intent.putExtra("ParentEmail", global.getCurrentUser());
		startActivity(intent);
		bmp = null;
		iv = null;
	}

	public void startCurrentFriendsActivity(View view)
	{
		Intent intent = new Intent(this, CurrentFriendsActivity.class);
		Global global = ((Global) getApplicationContext());
		String email = global.getCurrentUser();
		intent.putExtra("ParentClassName", "UserActivity");
		intent.putExtra("ParentEmail", email);
		intent.putExtra("email", email);
		intent.putExtra("mod", "true");
		startActivity(intent);
		bmp = null;
		iv = null;
	}

	public void startEventsActivity(View view)
	{
		Intent intent = new Intent(this, EventsActivity.class);
		intent.putExtra("ParentClassName", "UserActivity");
		startActivity(intent);
		bmp = null;
		iv = null;
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
