package cs460.grouple.grouple;


import android.support.v7.app.ActionBarActivity;

import android.support.v4.app.Fragment;
import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		getSupportActionBar().setCustomView(R.layout.actionbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		TextView actionbarTitle = (TextView)findViewById(R.id.actionbarTitleTextView);
		
		Global global = ((Global)getApplicationContext());
		View home = findViewById(R.id.homeLayout);
		actionbarTitle.setText("Welcome, "+global.getName()+"!");

		try {
			global.fetchNumFriendRequests();
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		global.setNotifications(home);
	
		
		//START KILL SWITCH LISTENER
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("CLOSE_ALL");
		BroadcastReceiver broadcastReceiver = new BroadcastReceiver() 
		{
		 @Override
			public void onReceive(Context context, Intent intent) 
			 {
				// close activity
				if(intent.getAction().equals("CLOSE_ALL"))
				{
					Log.d("app666","we killin the home");
					//System.exit(1);
					finish();
				}	  
			}
		};
		registerReceiver(broadcastReceiver, intentFilter);
				//End Kill switch listener	
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
			Global global = ((Global)getApplicationContext());
			global.setAcceptEmail("");
			global.setCurrentUser("");
			global.setDeclineEmail("");
			global.setNumFriendRequests(0);
			startLoginActivity(null);
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	    if(keyCode == KeyEvent.KEYCODE_BACK)
	    {
	       
	    }
	    return false;
	}
	
	/*Start activity functions for main navigation*/
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
		startActivity(intent);
	}
	public void startLoginActivity(View view)
	{
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
	
}
