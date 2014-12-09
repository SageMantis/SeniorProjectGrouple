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
import android.widget.ImageButton;
import android.widget.TextView;

public class FriendsActivity extends ActionBarActivity
{
	BroadcastReceiver broadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);

		//Actionbar settings
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText("Friends");
		ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);
		upButton.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {

				startParentActivity(view);

			}
		});

		Global global = ((Global) getApplicationContext());
		View friends = findViewById(R.id.friendsLayout);
		global.fetchNumFriendRequests(global.getCurrentUser());
		global.fetchNumFriends(global.getCurrentUser());
		global.setNotifications(friends);



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
		View friends = findViewById(R.id.friendsLayout);
		global.fetchNumFriendRequests(global.getCurrentUser());
		// friendRequests = global.getNumFriendRequests();
		global.setNotifications(friends);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			Global global = ((Global) getApplicationContext());
			//todo mess with notification update here
			View friends = findViewById(R.id.friendsLayout);
			View home = ((View) friends.getParent());
			global.fetchNumFriendRequests(global.getCurrentUser());
			global.setNotifications(home);
			startParentActivity(null);
		}
		return false;
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



	/*
	 * Start activity functions for friends sub activities, going back and
	 * logging out
	 */
	public void startAddFriendActivity(View view)
	{
		Intent intent = new Intent(this, AddFriendActivity.class);
		intent.putExtra("ParentClassName", "FriendsActivity");
		startActivity(intent);
	}

	public void startCurrentFriendsActivity(View view)
	{
		Intent intent = new Intent(this, CurrentFriendsActivity.class);
		intent.putExtra("ParentClassName", "FriendsActivity");
		Global global = ((Global) getApplicationContext());
		intent.putExtra("email", global.getCurrentUser());
		intent.putExtra("Name", global.getName());
		intent.putExtra("ParentEmail", global.getCurrentUser());
		intent.putExtra("ParentParentClassName", "HomeActivity");
		intent.putExtra("mod", "true");
		startActivity(intent);
	}

	public void startHomeActivity(View view)
	{
		Intent intent = new Intent(this, HomeActivity.class);
		intent.putExtra("ParentClassName", "FriendsActivity");
		startActivity(intent);
		finish();
	}

	public void startFriendRequestsActivity(View view)
	{
		Global global = ((Global) getApplicationContext());
		Intent intent = new Intent(this, FriendRequestsActivity.class);
		intent.putExtra("email", global.getCurrentUser());
		intent.putExtra("ParentClassName", "FriendsActivity");
		startActivity(intent);
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
			//newIntent.putExtra("email", extras.getString("email"));
			//newIntent.putExtra("ParentEmail", extras.getString("email"));
			newIntent.putExtra("ParentClassName", "FriendsActivity");
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		startActivity(newIntent);
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
