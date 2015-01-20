package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * CurrentFriendsActivity displays a list of all friends of user.
 */
public class FriendsCurrentActivity extends ActionBarActivity
{
	Intent parentIntent;
	Intent upIntent;
	BroadcastReceiver broadcastReceiver;
	View friendsCurrent;
	User user; //owner of the list of friends
	// An array list that holds your friends by email address.
	private ArrayList<String> friendsEmailList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Set the activity layout to activity_current_friends.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_current_friends);
		
		/*
		 * 
		 * NEED TO HARD UPDATE THE FRIENDS ARRAY WHEN THEY GET NEW ONES / DELETE THEM
		 */

		// Grabs the current friends container and passes it to load.
		friendsCurrent = findViewById(R.id.currentFriendsContainer);
		// Load populates the container with all of your current friends.
		load(friendsCurrent);

	}

	/* loading actionbar */
	public void initActionBar()
	{
		//Bundle extras = parentIntent.getExtras();
		Global global = ((Global) getApplicationContext());
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);

		// Grabs your name and sets it in the action bar's title.
		actionbarTitle.setText(user.getFirstName() + "'s Friends"); //PANDA
		ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);
		// On click listener for the action bar's back button.
		upButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View view)
			{
				upIntent.putExtra("up", "true");
				startActivity(upIntent);
				finish();
			}
		});

	}

	/* loading in everything for current friends */
	public void load(View view)
	{
		Global global = ((Global) getApplicationContext());
		// backstack of intents
		// each class has a stack of intents lifo method used to execute them at
		// start of activity
		// intents need to include everything like ParentClassName, things for
		// current page (email, ...)
		// if check that friends
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		//grabbing the user with the given email in the extras
		user = global.loadUser(extras.getString("email"));
		

		// Pass the current users email and execute the get friends php.
		populateFriendsCurrent();
		
		// Start the action bar and kill switch.
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
		Global global = ((Global) getApplicationContext());
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_logout)
		{
			Intent login = new Intent(this, LoginActivity.class);
			startActivity(login);
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		if (id == R.id.action_home)
		{
			Intent intent = new Intent(this, HomeActivity.class);

			intent.putExtra("ParentClassName", "FriendsCurrentActivity");
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	public void populateFriendsCurrent()
	{
					//JSONArray jsonFriends = jsonObject.getJSONArray("friends");
		LayoutInflater li = getLayoutInflater();
		Global global = ((Global) getApplicationContext());
		/*
		 * If jsonFriends isn't null, then we have friends and we
		 * loop through the friends and add them to the current
		 * friends container.
		 */
		Map<String, String> friends = user.getFriends();
		if (!friends.isEmpty())
		{
			LinearLayout friendsCurrentRL = (LinearLayout) findViewById(R.id.currentFriendsLayout);
			//Bundle extras = intent.getExtras();
			// looping thru json and adding to an array
			 Iterator it = friends.entrySet().iterator();
			 int index = 0;
			 while (it.hasNext()) {
				 Map.Entry pair = (Map.Entry)it.next();
				 String email = (String) pair.getKey();
				 String fullName = (String) pair.getValue();
				 friendsEmailList.add(index, email);
				 String pri = "index: " + index + ": " + email;
				 Log.d("friendscurrent set friend", pri);
				 //global.loadUser(email);//preloading user, this will slow it down a lot, without array it is useless
				 GridLayout rowView;

				 /*
				 * If you are the mod, add the friend button and the
				 * remove button. If you aren't the mod, then add
				 * the friend of a friend button without the remove
				 * button. In this instance, mod means whether or
				 * not these or your friends. You don't want the
				 * option to delete a friend's friend.
				 */
				 
				 Bundle extras = getIntent().getExtras();
				 if (extras.getString("mod").equals("true"))
				 {
					 rowView = (GridLayout) li.inflate(
							 R.layout.listitem_friend, null);
					 Button removeFriendButton = (Button) rowView
							.findViewById(R.id.removeFriendButton);
					 removeFriendButton.setId(index);
				 } 
				 else
				 {
					rowView = (GridLayout) li.inflate(
							R.layout.listitem_friends_friend, null);
				 }
				 // Add the information to the friendnamebutton and
				 // add it to the next row.
				 Button friendNameButton = (Button) rowView
						 .findViewById(R.id.friendNameButton);

				 friendNameButton.setText(fullName);
				 /*
				 * Setting the ID to i makes it so we can use i to
				 * figure out the friend's email. Important for
				 * finding a friend's profile.
				 */
				 friendNameButton.setId(index);
				 rowView.setId(index);
				 friendsCurrentRL.addView(rowView);	
				 index++;
			 }
		}
		else
		{		
			// user has no friends
			LinearLayout friendsCurrentRL = (LinearLayout) findViewById(R.id.currentFriendsLayout);
	
			// The user has no friend's so display the sad guy image.
			View row = li.inflate(R.layout.listitem_sadguy, null);
			((TextView) row.findViewById(R.id.sadGuyTextView))
				.setText("You do not have any friends.");
			friendsCurrentRL.addView(row);
		}
	}

	@Override
	// Sets the back button code.
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			upIntent.putExtra("up", "true");
			startActivity(upIntent);
			finish();
		}
		return false;
	}

	// Handles removing a friend when the remove friend button is pushed.
	public void removeFriendButton(View view)
	{
		// Email of user
		final int id = view.getId();
		// Email of friend that we are removing.
		final String friendEmail = friendsEmailList.get(id);

		// refreshing the current friends layout
		final String email = user.getEmail();
		// delete confirmation. If the user hits yes then execute the
		// delete_friend php, else do nothing.
		new AlertDialog.Builder(this)
				.setMessage("Are you sure you want to remove that friend?")
				.setCancelable(true)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int id)
					{

						new deleteFriendTask()
								.execute(
										"http://98.213.107.172/android_connect/delete_friend.php",
										email, friendEmail);
						// removing all of the views
						LinearLayout friendsCurrentLayout = (LinearLayout) findViewById(R.id.currentFriendsLayout);
						friendsCurrentLayout.removeAllViews();
						// calling getFriends to repopulate view
						populateFriendsCurrent();
					}
				}).setNegativeButton("Cancel", null).show();
	}

	/*
	 * Code for deleting a friend.
	 */
	private class deleteFriendTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			// urls 1, 2 are the emails
			Global global = ((Global) getApplicationContext());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("sender", urls[1]));
			nameValuePairs.add(new BasicNameValuePair("receiver", urls[2]));
			return global.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);

				if (jsonObject.getString("success").toString().equals("1"))
				{
					// success: friend has been deleted
					Log.d("dbmsg", jsonObject.getString("message"));
				} else if (jsonObject.getString("success").toString()
						.equals("2"))
				{
					// friend was not found in database
					Log.d("dbmsg", jsonObject.getString("message"));
				} else
				{
					// sql error
					Log.d("dbmsg", jsonObject.getString("message"));
				}

			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	// When you click on a friend, this loads up the friend's profile.
	public void startUserProfileActivity(View view)
			throws InterruptedException
	{
		// need to get access to this friends email
		// launches friendProfileActivity and loads content based on that email
		int id = view.getId();
		// got the id, now we need to grab the users email and somehow pass it
		// to the activity

		String friendEmail = friendsEmailList.get(id);
		Intent intent = new Intent(this, UserProfileActivity.class);
		intent.putExtra("ParentClassName", "FriendsCurrentActivity");

		//global.fetchNumFriends(friendEmail); PANDA
		//global.fetchNumGroups(friendEmail);
		// Another sleep that way the php has time to execute. We need to start
		// the activity when the PHP returns..
		intent.putExtra("email", friendEmail);
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
