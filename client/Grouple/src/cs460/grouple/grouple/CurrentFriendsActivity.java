package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;

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
public class CurrentFriendsActivity extends ActionBarActivity
{
	Intent parentIntent;
	Intent upIntent;
	BroadcastReceiver broadcastReceiver;
	//An array list that holds your friends by email address.
	private ArrayList<String> friendsEmailList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		//Set the activity layout to activity_current_friends.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_current_friends);

		//Grabs the current friends container and passes it to load.
		View currentFriends = findViewById(R.id.currentFriendsContainer);
		//Load populates the container with all of your current friends.
		load(currentFriends);
		
	}

	
	/* loading actionbar */
	public void initActionBar()
	{
		Bundle extras = parentIntent.getExtras();
		Global global = ((Global) getApplicationContext());
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		
		try
		{
			Thread.sleep(300);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Grabs your name and sets it in the action bar's title.
		actionbarTitle.setText(global.getName()+ "'s Friends");
		ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);
		//On click listener for the action bar's back button. 
		upButton.setOnClickListener(new OnClickListener() 
		{
	
			@Override
			public void onClick(View view) {
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
		//backstack of intents
		//each class has a stack of intents lifo method used to execute them at start of activity
		//intents need to include everything like ParentClassName, things for current page (email, ...)
		//if check that friends
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		//do a check that it is not from a back push
		if (extras.getString("up").equals("true"))
		{
			//pull a new intent from the stack
			//load in everything from that intent
			System.out.println("Should be fetching off stack for current friends");
			parentIntent = global.getNextParentIntent(view);
		}
		else
		{
			//add to stack
			parentIntent = intent;
			//trying to add to stack whenever the page is actually left
		}	
		//Get the parent class's name and the email address associated with it. The email is usually the current users.
		Bundle parentExtras = parentIntent.getExtras();
		String className = parentExtras.getString("ParentClassName");
		String email = parentExtras.getString("email");
		global.fetchName(email);
		try
		{
			upIntent = new Intent(this, Class.forName("cs460.grouple.grouple."
					+ className));
		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			try
			{
				//Sleep is added so the php is executed before the page is loaded. This NEEDS to be replaced with sequential programming.
				Thread.sleep(500);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		//Pass the current users email and execute the get friends php.
		new getFriendsTask().execute("http://98.213.107.172/android_connect/get_friends_firstlast.php?email="+ email);
		
		//Start the action bar and kill switch.
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
			
			intent.putExtra("up", "false");
			intent.putExtra("ParentClassName", "CurrentFriendsActivity");
			global.addToParentStackCurrentFriends(parentIntent);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	/*Code for getting the user's current friends*/
	private class getFriendsTask extends AsyncTask<String, Void, String>
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
			try
			{
				/*jsonObject contains the result of get_friends.php.  
				 * If the result is successful, then we add our friends to the current friends container (if the user has any).
				 * If the result is fail, then something went wrong is executing the php. An example is passing an incorrect email address to the php.*/
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					//ArrayList<String> friends = new ArrayList<String>();
					JSONArray jsonFriends = jsonObject.getJSONArray("friends");
					
					/*If jsonFriends isn't null, then we have friends and we loop through the friends and add them to 
					 * the current friends container.
					 */
					if (jsonFriends != null)
					{
						LinearLayout currentFriendsRL = (LinearLayout) findViewById(R.id.currentFriendsLayout);
						Bundle extras = parentIntent.getExtras();
						// looping thru json and adding to an array
						for (int i = 0; i < jsonFriends.length(); i++)
						{
							/*Get the friend's first name, last name, and email address
							 * Although we don't display the email address, we store it in an array that way we have it so we can
							 * use the email address to get the friends profile. The email address is a UID, unlike the names.
							 */
							String firstraw = jsonFriends.getJSONObject(i).getString("first");
							String lastraw = jsonFriends.getJSONObject(i).getString("last");
							String friendEmail = jsonFriends.getJSONObject(i).getString("email");
							friendsEmailList.add(i, friendEmail);
							
							//Concatenate the friend's first and last name and force the names to be capitalize.
							String fullName = firstraw.substring(0, 1).toUpperCase()+ firstraw.substring(1);
							fullName = fullName + " ";
							fullName = fullName + lastraw.substring(0, 1).toUpperCase()+ lastraw.substring(1);


							GridLayout rowView;
							
							/*
							 * If you are the mod, add the friend button and the remove button.
							 * If you aren't the mod, then add the friend of a friend button without the remove button.
							 * In this instance, mod means whether or not these or your friends.
							 * You don't want the option to delete a friend's friend.
							 */
							if (extras.getString("mod").equals("true"))
							{
								rowView = (GridLayout) li.inflate(
										R.layout.listitem_friend, null);
								Button removeFriendButton = (Button) rowView
										.findViewById(R.id.removeFriendButton);
								removeFriendButton.setId(i);
							} else
							{
								rowView = (GridLayout) li.inflate(
										R.layout.listitem_friends_friend, null);

							}
							//Add the information to the friendnamebutton and add it to the next row.
							Button friendNameButton = (Button) rowView.findViewById(R.id.friendNameButton);

							friendNameButton.setText(fullName);
							/*
							 * Setting the ID to i makes it so we can use i to figure out the friend's email. 
							 * Important for finding a friend's profile.
							 */
							friendNameButton.setId(i);
							rowView.setId(i);
							currentFriendsRL.addView(rowView);
						}
					}
				}
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					//Double check 2 is the no friends code
					//inflate the new no friends layout 
					LinearLayout currentFriendsRL = (LinearLayout) findViewById(R.id.currentFriendsLayout);

					//The user has no friend's so display the sad guy image.
					View row = li.inflate(R.layout.listitem_sadguy, null);
					((TextView) row.findViewById(R.id.sadGuyTextView))
							.setText("You do not have any friends.");
					currentFriendsRL.addView(row);

				} 
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	@Override
	//Sets the back button code.
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

	//Handles removing a friend when the remove friend button is pushed.
	public void removeFriendButton(View view)
	{
		// Email of user
		final int id = view.getId();
		// Email of friend that we are removing.
		final String friendEmail = friendsEmailList.get(id);
							
		// refreshing the current friends layout
		Bundle extras = parentIntent.getExtras();
		final String email = extras.getString("email");
		//delete confirmation. If the user hits yes then execute the delete_friend php, else do nothing.
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
						//removing all of the views
						LinearLayout currentFriendsLayout = (LinearLayout) findViewById(R.id.currentFriendsLayout);
						currentFriendsLayout.removeAllViews();
						// calling getFriends to repopulate view
						new getFriendsTask()
								.execute("http://98.213.107.172/android_connect/get_friends_firstlast.php?email="
										+ email);
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
			//urls 1, 2 are the emails
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

	//When you click on a friend, this loads up the friend's profile.
	public void startFriendProfileActivity(View view)
			throws InterruptedException
	{
		// need to get access to this friends email
		// launches friendProfileActivity and loads content based on that email
		int id = view.getId();
		// got the id, now we need to grab the users email and somehow pass it
		// to the activity
		Bundle extras = parentIntent.getExtras();
		String friendEmail = friendsEmailList.get(id);
		Intent intent = new Intent(this, FriendProfileActivity.class);
		intent.putExtra("ParentClassName", "CurrentFriendsActivity");
		Global global = ((Global) getApplicationContext());
		global.addToParentStackCurrentFriends(parentIntent);
		global.fetchNumFriends(friendEmail);
		global.fetchNumGroups(friendEmail);
		//Another sleep that way the php has time to execute. We need to start the activity when the PHP returns..
		Thread.sleep(500);
		intent.putExtra("email", friendEmail);
		intent.putExtra("up", "false");
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
