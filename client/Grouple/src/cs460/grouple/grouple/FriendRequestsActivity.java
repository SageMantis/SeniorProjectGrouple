package cs460.grouple.grouple;

import java.io.BufferedReader;
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
import org.json.JSONArray;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * FriendRequestsActivity displays a list of all active friend requests of a user.
 */
public class FriendRequestsActivity extends ActionBarActivity
{
	BroadcastReceiver broadcastReceiver;
	Intent upIntent;
	Intent parentIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_requests);

		// display friend requests
		// Create helper and if successful, will bring the correct home
		// activity.
		View friendRequests = findViewById(R.id.friendRequestsContainer);
		load(friendRequests);

	}

	// Start the action bar.
	public void initActionBar()
	{
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText("Friend Requests");
		ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);
		upButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				startActivity(upIntent);
				finish();
			}
		});
	}

	// Gets the friends requests and displays them to the user
	public void load(View view)
	{
		Global global = ((Global) getApplicationContext());

		// backstack of intents
		// each class has a stack of intents lifo method used to execute them at
		// start of activity
		// intents need to include everything like ParentClassName, things for
		// current page (email, ...)
		// if check that friends
		parentIntent = getIntent();
		upIntent = new Intent(this, FriendsActivity.class);
		upIntent.putExtra("up", "true");

		String receiver = global.getCurrentUser();
		// Php call that gets the users friend requests.
		new getFriendRequestsTask()
				.execute("http://98.213.107.172/android_connect/get_friend_requests.php?receiver="
						+ receiver);

		View friendRequests = findViewById(R.id.friendRequestsLayout);
		View friends = ((View) friendRequests.getParent());
		View home = ((View) friends.getParent());
		/*if (global.setNotifications(friendRequests) == 1)
			{
			;
			}
		if(global.setNotifications(friends) == 1)
			{
			;
			}
		if (global.setNotifications(home) ==1)
			{
			;
			} PANDA */

		initActionBar();
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
			global.setCurrentUser("");
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

	/*
	 * Using the user's email address, we get the user's current friend
	 * requests. On success we display the user's current friends request, if
	 * there are any.
	 */
	private class getFriendRequestsTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			return global.readJSONFeed(urls[0], null);
		}

		@Override
		protected void onPostExecute(String result)
		{
			LayoutInflater li = getLayoutInflater();
			LinearLayout friendRequestsLayout = (LinearLayout) findViewById(R.id.friendRequestsLayout);
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					System.out.println("We are in the success");
					ArrayList<String> senders = new ArrayList<String>();
					JSONArray jsonSenders = jsonObject.getJSONArray("senders")
							.getJSONArray(0);
					Global global = ((Global) getApplicationContext());

					if (jsonSenders != null)
					{
						global.setNumFriendRequests(jsonSenders.length());
					//	global.setNotifications(friendRequestsLayout); PANDA
						System.out.println(jsonSenders.toString() + "\n"
								+ jsonSenders.length());

						// looping thru json and adding to an array
						for (int i = 0; i < jsonSenders.length(); i++)
						{
							// This is a hackish way of getting the friend
							// request from the json object.
							// This was before we had a good understanding of
							// JSON
							// TODO: clean this up.
							String raw = jsonSenders.get(i).toString()
									.replace("\"", "").replace("]", "")
									.replace("[", "");
							String row = raw.substring(0, 1).toUpperCase()
									+ raw.substring(1);
							senders.add(row);
							System.out.println("Row: " + row + "\nCount: " + i);
						}

						// looping thru array and inflating listitems to the
						// friend requests list
						for (int i = 0; i < senders.size(); i++)
						{
							GridLayout row = (GridLayout) li.inflate(
									R.layout.listitem_friend_request, null);
							// Setting text of each friend request to the email
							// of the sender
							((TextView) row
									.findViewById(R.id.emailTextViewFRLI))
									.setText(senders.get(i));
							friendRequestsLayout.addView(row);
						}
					} else
					// no friend requests
					{

						// Setting text of each friend request to the email
						// of the sender
						// ((ImageView) sadGuy
						// .findViewById(R.id.sadGuyImageView))
						// .setText("You have no new friend requests.");
						global.setNumFriendRequests(0);
					}
				} else
				{
					System.out.println("No friends found");
					GridLayout sadGuy = (GridLayout) li.inflate(
							R.layout.listitem_sadguy, null);
					sadGuy.findViewById(R.id.sadGuyTextView);
					friendRequestsLayout.addView(sadGuy);
					// If no friend requests are found, display no friends
					// message
					// TextView noFriends = (TextView)
					// findViewById(R.id.noFriendRequestsTextViewFRA);
					// noFriends.setVisibility(0);
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	/*
	 * On click listener that determines if the user declined or accepted the
	 * code.
	 */
	public void onClick(View view)
	{
		Global global = ((Global) getApplicationContext());
		switch (view.getId())
		{
		case R.id.declineFriendRequestButtonFRLI:
			View parent = (View) view.getParent();
			TextView declineEmail = (TextView) parent
					.findViewById(R.id.emailTextViewFRLI);
			//global.setDeclineEmail(declineEmail.getText().toString()); //PANDA
			new getDeclineFriendTask()
					.execute("http://98.213.107.172/android_connect/decline_friend_request.php");
			break;
		case R.id.acceptFriendRequestButtonFRLI:
			View parent2 = (View) view.getParent();
			TextView acceptEmail = (TextView) parent2
					.findViewById(R.id.emailTextViewFRLI);
			//global.setAcceptEmail(acceptEmail.getText().toString()); //PANDA
			new getAcceptFriendTask()
					.execute("http://98.213.107.172/android_connect/accept_friend_request.php");
			break;
		}
	}

	/*
	 * Code for declining a friend request. On success, we remove the friend
	 * request and refresh the friend requests activity.
	 */
	private class getDeclineFriendTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			String receiver = global.getCurrentUser();
			String sender = "test"; //PANDA
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("sender", sender));
			nameValuePairs.add(new BasicNameValuePair("receiver", receiver));
			return global.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			Global global = ((Global) getApplicationContext());
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				System.out.println(jsonObject.getString("success"));
				if (jsonObject.getString("success").toString().equals("1"))
				{
					View friends = (View) findViewById(
							R.id.friendRequestsLayout).getParent();
					View home = (View) friends.getParent();
					//global.setNotifications(friends); PANDA
					//global.setNotifications(home);
					System.out.println("success in decline!");
					startFriendRequestsActivity();

				} else
				{
					// failed
					System.out.println("fail!");
					// TextView addFriendMessage = (TextView)
					// findViewById(R.id.addFriendMessageTextViewAFA);
					// addFriendMessage.setText("User not found.");
					// addFriendMessage.setVisibility(0);
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	

	/*
	 * Code for accepting a friend request. On success, we remove the friend
	 * request and refresh the friend requests activity. We also confirm the
	 * friendship in the database.
	 */

	private class getAcceptFriendTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			String receiver = global.getCurrentUser();
			String sender = "test"; //PANDA
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("sender", sender));
			nameValuePairs.add(new BasicNameValuePair("receiver", receiver));
			return global.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			Global global = ((Global) getApplicationContext());
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				System.out.println(jsonObject.getString("success"));
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// successful
					System.out.println("success!");
					View friends = (View) findViewById(
							R.id.friendRequestsLayout).getParent();
					View home = (View) friends.getParent();
					//global.setNotifications(friends); PANDA
					//global.setNotifications(home);
					startFriendRequestsActivity();

				} else
				{
					// failed
					System.out.println("fail!");
				}
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
			startActivity(upIntent);
			finish();
		}
		return false;
	}

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
			Global global = ((Global) getApplicationContext());
			View friendRequests = findViewById(R.id.friendRequestsLayout);
			View friends = ((View) friendRequests.getParent());
			//global.fetchNumFriendRequests(global.getCurrentUser()); PANDA
			//global.setNotifications(friendRequests);
			// newIntent.putExtra("email", extras.getString("email"));
			// newIntent.putExtra("ParentEmail", extras.getString("email"));
			newIntent.putExtra("ParentClassName", "FriendRequestsActivity");
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		startActivity(newIntent);
	}

	/*
	 * Start activity functions for refreshing friend requests, going back and
	 * logging out
	 */
	public void startFriendRequestsActivity()
	{
		Intent intent = new Intent(this, FriendRequestsActivity.class);
		startActivity(intent);
	}

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
