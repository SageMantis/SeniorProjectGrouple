package cs460.grouple.grouple;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/*
 * HomeActivity displays the primary navigation and welcome screen after logging in.
 */
public class HomeActivity extends ActionBarActivity
{
	LayoutInflater li;
	User user;
	BroadcastReceiver broadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.d("HomeActivity", "1");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		Global global = ((Global) getApplicationContext());
		
		/* Updating notifications
		 * Not sure how I want to do this yet so they will not exist yet.
		 * View home = findViewById(R.id.homeLayout);
		 */
		
		//grabbing extras from intent
		Bundle extras = getIntent().getExtras(); 
		//grabbing the user with the given email in the extras
		user = global.getUser(extras.getString("email"));

		//initializing action bar and killswitch listener
		initActionBar();
		initKillswitchListener();
	}

	public void initActionBar()
	{
		// Actionbar settings
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		// ab.setDisplayHomeAsUpEnabled(true);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		//Global global = ((Global) getApplicationContext());
		actionbarTitle.setText("Welcome, " + user.getFullName() + "!"); //PANDA
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	// This method closes the app when back button is pressed on main page
	@Override
	public void onBackPressed()
	{
		Log.d("backPress", "Back was pressed on home screen.");
		finish();
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
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume()
	{
		super.onResume(); // Always call the superclass method first
		Log.d("onResume()","after superonresume");
		Global global = ((Global) getApplicationContext());
		Log.d("onResume()","after global declaratione");
		View home = findViewById(R.id.homeLayout);
		//global.fetchNumFriendRequests(global.getCurrentUser());
		//global.fetchNumFriends(global.getCurrentUser());
		// friendRequests = global.getNumFriendRequests();
		Log.d("onResume()","after after all");
		//global.setNotifications(home); PANDA
	}

	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { if
	 * (keyCode == KeyEvent.KEYCODE_BACK) { Bundle extras =
	 * getIntent().getExtras(); if (extras.getString("ParentClassName") != null)
	 * { Intent newIntent = null; try { // you need to define the class with
	 * package name newIntent = new Intent(this,
	 * Class.forName("cs460.grouple.grouple." +
	 * extras.getString("ParentClassName")));
	 * 
	 * String email = extras.getString("ParentEmail");
	 * 
	 * String parentEmail = extras.getString("ParentParentEmail");
	 * 
	 * if (email != null) { newIntent.putExtra("email", email); }
	 * 
	 * if (parentEmail != null) { newIntent.putExtra("ParentEmail",
	 * parentEmail); } //todo: check compared to current user first //or pass in
	 * a parentMod in the extras newIntent.putExtra("mod", "false"); if
	 * (extras.getString("ParentParentClassName") != null) {
	 * newIntent.putExtra("ParentClassName",
	 * extras.getString("ParentParentClassName")); } } catch
	 * (ClassNotFoundException e) { e.printStackTrace(); }
	 * startActivity(newIntent); } }
	 * 
	 * return false; }
	 */

	public void navigate(View view)
	{
		//originally setting intent to null
		Intent intent = null;

		switch (view.getId())
		{
		case R.id.friendsButtonHA:
			intent = new Intent(this, FriendsActivity.class);
			break;
		case R.id.settingsButtonHA:
			intent = new Intent(this, SettingsActivity.class);
			break;
		case R.id.eventsButtonHA:
			intent = new Intent(this, EventsActivity.class);
			break;
		case R.id.messagesButtonHA:
			intent = new Intent(this, MessagesActivity.class);
			break;
		case R.id.groupsButtonHA:
			intent = new Intent(this, GroupsActivity.class);
			break;
		case R.id.userButtonHA:
			intent = new Intent(this, UserProfileActivity.class);
			break;
		default: //default just break out
			break;
		}
		
		//checking that intent was assigned
		if (intent != null)
		{
			intent.putExtra("ParentClassName", "HomeActivity");
			intent.putExtra("up", "false");
			intent.putExtra("email", user.getEmail());
			startActivity(intent);
		}
		//else do nothing
	}

	/*
	 * Initializing the killswitch listener for shutting down
	 */
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
