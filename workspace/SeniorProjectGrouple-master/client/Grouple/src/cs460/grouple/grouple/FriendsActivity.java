package cs460.grouple.grouple;


import android.app.ActionBar;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class FriendsActivity extends ActionBarActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		if (savedInstanceState == null)
		{
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		ActionBar ab = getActionBar();
		ab.setTitle("");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		ab.setIcon(Color.TRANSPARENT);
		
		//START KILL SWITCH LISTENER
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("CLOSE_ALL");
		BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
		    // close activity
			  if(intent.getAction().equals("CLOSE_ALL"))
			  {
				  Log.d("app666","we killin the login it");
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
			startLoginActivity(null);
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public class PlaceholderFragment extends Fragment
	{

		public PlaceholderFragment()
		{
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_friends,
					container, false);
			Global global = ((Global)getApplicationContext());
			global.setNotifications(rootView);
			return rootView;
		}
	}
	
	public void startAddFriendActivity(View view)
	{
		Intent intent = new Intent(this, AddFriendActivity.class);
		startActivity(intent);
	}
	public void startCurrentFriendsActivity(View view)
	{
		Intent intent = new Intent(this, CurrentFriendsActivity.class);
		startActivity(intent);
	}
	public void startUserActivity(View view)
	{
		Intent intent = new Intent(this, UserActivity.class);
		startActivity(intent);
	}
	public void startHomeActivity(View view)
	{
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
	}
	public void startEventsActivity(View view)
	{
		Intent intent = new Intent(this, EventsActivity.class);
		startActivity(intent);
	}
	public void startGroupsActivity(View view)
	{
		Intent intent = new Intent(this, GroupsActivity.class);
		startActivity(intent);
	}
	public void startFriendRequestsActivity(View view)
	{
		Intent intent = new Intent(this, FriendRequestsActivity.class);
		startActivity(intent);
	}
	public void startLoginActivity(View view)
	{
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	    if(keyCode == KeyEvent.KEYCODE_BACK)
	    {
	        startUserActivity(null);
	    }
	    return false;
	}
}


