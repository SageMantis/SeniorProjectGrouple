package cs460.grouple.grouple;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/*
 * FriendsActivity displays displays the friend navigation page for the user.
 */
public class FriendsActivity extends ActionBarActivity
{
	BroadcastReceiver broadcastReceiver;
	Intent parentIntent;
	Intent upIntent;
	View friends; 
	User user; //the current user

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		
		friends = findViewById(R.id.friendsContainer);
		load(friends);

		initKillswitchListener();
	}

	public void initActionBar()
	{
		// Actionbar settings
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText("Friends");
		ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);
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

	public void load(View view)
	{
		Global global = ((Global) getApplicationContext());
		//grabbing extras from intent
		Intent intent = getIntent();
		Bundle extras = intent.getExtras(); 
		//grabbing the user with the given email in the extras
		user = global.loadUser(extras.getString("email"));
		
		//Test set some stuff
		// Friends Activity

			//Button friendRequestsButton = (Button) view
				//	.findViewById(R.id.friendRequestsButtonFA);
			//friendRequestsButton.setText("Friend Requests ("
				//	+ user.getNumFriendRequests() + ")");
			Button currentFriendsButton = (Button) findViewById(R.id.currentFriendsButtonFA);
			currentFriendsButton
					.setText("My Friends (" + user.getNumFriends() + ")"); //PANDA
			Button friendRequestsButton = (Button) findViewById(R.id.friendRequestsButtonFA);
			friendRequestsButton
					.setText("Friend Requests (" + user.getNumFriendRequests() + ")"); //PANDA
		
		
		// backstack of intents
		// each class has a stack of intents lifo method used to execute them at
		// start of activity
		// intents need to include everything like ParentClassName, things for
		// current page (email, ...)
		// if check that friends
		String email;


		//global.fetchNumFriendRequests(global.getCurrentUser()); PANDA
		//global.fetchNumFriends(global.getCurrentUser());
		//global.setNotifications(friends);

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
	public void onResume()
	{
		super.onResume(); // Always call the superclass method first
		System.out.println("In Friends onResume()");
		Global global = ((Global) getApplicationContext());
		View friends = findViewById(R.id.friendsContainer);
		//global.fetchNumFriendRequests(global.getCurrentUser()); PANDA
		// friendRequests = global.getNumFriendRequests();
		//global.setNotifications(friends);

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
			Intent login = new Intent(this, LoginActivity.class);
			startActivity(login);
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		if (id == R.id.action_home)
		{
			Intent intent = new Intent(this, HomeActivity.class);
			intent.putExtra("ParentClassName", "FriendsActivity");
			intent.putExtra("email", user.getEmail());
			intent.putExtra("up", "false");
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * Start activity functions for friends sub activities, going back and
	 * logging out
	 */
	public void startFriendAddActivity(View view)
	{
		Global global = ((Global) getApplicationContext());
		Intent intent = new Intent(this, FriendAddActivity.class);
		intent.putExtra("ParentClassName", "FriendsActivity");
		intent.putExtra("email", user.getEmail());

		startActivity(intent);
	}

	public void startFriendsCurrentActivity(View view)
	{
		Intent intent = new Intent(this, FriendsCurrentActivity.class);
		intent.putExtra("ParentClassName", "FriendsActivity");
		intent.putExtra("email", user.getEmail());
		intent.putExtra("ParentEmail", user.getEmail());
		intent.putExtra("mod", "true");
		startActivity(intent);
	}

	public void startFriendRequestsActivity(View view)
	{
		Global global = ((Global) getApplicationContext());
		Intent intent = new Intent(this, FriendRequestsActivity.class);
		//intent.putExtra("email", global.getCurrentUser()); PANDA getEmail()
		intent.putExtra("ParentClassName", "FriendsActivity");
		intent.putExtra("email", user.getEmail());
		// intent.putExtra("mod", "true");
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
