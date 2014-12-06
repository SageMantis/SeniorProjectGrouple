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

public class HomeActivity extends ActionBarActivity
{
	LayoutInflater li;
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
		
		//Actionbar settings
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		//ab.setDisplayHomeAsUpEnabled(true);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		Global global = ((Global) getApplicationContext());
		actionbarTitle.setText("Welcome, " + global.getName() + "!");

		//Updating notifications
		View home = findViewById(R.id.homeLayout);
		global.fetchNumFriendRequests(global.getCurrentUser());
		global.fetchNumFriends(global.getCurrentUser());
		global.setNotifications(home);


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

	@Override
	public void onResume()
	{
		super.onResume(); // Always call the superclass method first
		System.out.println("In Home onResume()");
		Global global = ((Global) getApplicationContext());
		View home = findViewById(R.id.homeLayout);
		global.fetchNumFriendRequests(global.getCurrentUser());
		global.fetchNumFriends(global.getCurrentUser());
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

	
	public void navigate(View view)
	{
		Intent intent;
		switch (view.getId())
		{
		case R.id.friendsButtonHA:
			intent = new Intent(this, FriendsActivity.class);
			intent.putExtra("ParentClassName", "HomeActivity");
			startActivity(intent);
			break;
		case R.id.settingsButtonHA:
			intent = new Intent(this, SettingsActivity.class);
			intent.putExtra("ParentClassName", "HomeActivity");
			startActivity(intent);
			break;
		case R.id.eventsButtonHA:
			intent = new Intent(this, EventsActivity.class);
			intent.putExtra("ParentClassName", "HomeActivity");
			startActivity(intent);
			break;
		case R.id.messagesButtonHA:
			intent = new Intent(this, MessagesActivity.class);
			intent.putExtra("ParentClassName", "HomeActivity");
			startActivity(intent);
			break;
		case R.id.groupsButtonHA:
			intent = new Intent(this, GroupsActivity.class);
			intent.putExtra("ParentClassName", "HomeActivity");
			startActivity(intent);
			break;
		case R.id.userButtonHA:
			intent = new Intent(this, UserActivity.class);
			intent.putExtra("ParentClassName", "HomeActivity");
			startActivity(intent);
			break;
		default:
				break;
		}
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
