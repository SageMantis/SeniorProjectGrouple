package cs460.grouple.grouple;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class FriendsActivity extends ActionBarActivity
{
	int friendRequests;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(true);
		TextView actionbarTitle = (TextView)findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText("Friends");
		
		Global global = ((Global)getApplicationContext());
		View friends = findViewById(R.id.friendsLayout);
    	global.fetchNumFriendRequests(global.getCurrentUser());
    	friendRequests = global.getNumFriendRequests();
		global.setNotifications(friends);
		
	   /* Handler handler = new Handler();
	    handler.postDelayed(new Runnable() {
			View friends = findViewById(R.id.friendsLayout);
			View home = ((View) friends.getParent());
		    @Override
		    public void run() 
		    {
			    System.out.println("In Friends main run()");

				Global global = ((Global)getApplicationContext());
				global.fetchNumFriendRequests(global.getCurrentUser());
				if (friendRequests != global.getNumFriendRequests())
				{
					global.setNotifications(friends);
					global.setNotifications(home);
				}
			}
		    }, 1000);*/
			
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
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
	    System.out.println("In Friends onResume()");
		Global global = ((Global)getApplicationContext());
		View friends = findViewById(R.id.friendsLayout);
    	global.fetchNumFriendRequests(global.getCurrentUser());
    	//friendRequests = global.getNumFriendRequests();
    	global.setNotifications(friends);
   
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	    if(keyCode == KeyEvent.KEYCODE_BACK)
	    {
			Global global = ((Global)getApplicationContext());
			View friends = findViewById(R.id.friendsLayout);
			View home = ((View) friends.getParent());
			global.fetchNumFriendRequests(global.getCurrentUser()); 
			global.setNotifications(home);
	        startHomeActivity(home);
	    }
	    return false;
	}
	
	/*Start activity functions for friends sub activities, going back and logging out*/
	public void startAddFriendActivity(View view)
	{
		Intent intent = new Intent(this, AddFriendActivity.class);
		startActivity(intent);
	}
	public void startCurrentFriendsActivity(View view)
	{
		Intent intent = new Intent(this, CurrentFriendsActivity.class);
		intent.putExtra("ParentClassName", "FriendsActivity");
		Global global = ((Global) getApplicationContext());
		intent.putExtra("email", global.getCurrentUser());
		startActivity(intent);
	}

	public void startHomeActivity(View view)
	{
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
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
		finish();
	}
}


