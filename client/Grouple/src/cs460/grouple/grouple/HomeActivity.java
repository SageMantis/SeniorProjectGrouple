package cs460.grouple.grouple;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.GridLayout;
import android.widget.TextView;

public class HomeActivity extends ActionBarActivity
{
	LayoutInflater li;
	int friendRequests;
	BroadcastReceiver broadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		try
		{
			Thread.sleep(500);
		} catch (InterruptedException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(true);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);

		Global global = ((Global) getApplicationContext());

		actionbarTitle.setText("Welcome, " + global.getName() + "!");

		Handler handler = new Handler();
		View home = findViewById(R.id.homeLayout);
		// do anything

		global.fetchNumFriendRequests(global.getCurrentUser());
		global.fetchNumFriends(global.getCurrentUser());
		friendRequests = global.getNumFriendRequests();
		global.setNotifications(home);

		/*
		 * handler.postDelayed(new Runnable() { View home =
		 * findViewById(R.id.homeLayout);
		 * 
		 * @Override public void run() {
		 * System.out.println("In Home Main run()"); Global global =
		 * ((Global)getApplicationContext()); global.fetchNumFriendRequests();
		 * if (friendRequests != global.getNumFriendRequests()) {
		 * global.setNotifications(home); } } }, 1000);
		 */

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
					Log.d("app666", "we killin the home");
					// System.exit(1);
					finish();
				}
			}
		};
		registerReceiver(broadcastReceiver, intentFilter);
		// End Kill switch listener
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

	@Override
	public void onResume()
	{
		super.onResume(); // Always call the superclass method first
		System.out.println("In Home onResume()");
		Global global = ((Global) getApplicationContext());
		View home = findViewById(R.id.homeLayout);
		global.fetchNumFriendRequests(global.getCurrentUser());
		// friendRequests = global.getNumFriendRequests();

		global.setNotifications(home);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{

		}
		return false;
	}

	/* Start activity functions for main navigation */
	public void startHomeActivity(View view)
	{
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
	}

	public void startUserActivity(View view)
	{
		Intent intent = new Intent(this, UserActivity.class);
		startActivity(intent);
	}

	public void startGroupsActivity(View view)
	{
		Intent intent = new Intent(this, GroupsActivity.class);
		intent.putExtra("ParentClassName", "HomeActivity");
		startActivity(intent);
	}

	public void startMessagesActivity(View view)
	{
		Intent intent = new Intent(this, MessagesActivity.class);
		startActivity(intent);
	}

	public void startFriendsActivity(View view)
	{
		Intent intent = new Intent(this, FriendsActivity.class);
		startActivity(intent);
	}

	public void startSettingsActivity(View view)
	{
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	public void startEventsActivity(View view)
	{
		Intent intent = new Intent(this, EventsActivity.class);
		intent.putExtra("ParentClassName", "HomeActivity");
		startActivity(intent);
	}

}
