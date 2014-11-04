package cs460.grouple.grouple;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EventsActivity extends ActionBarActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);

		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(true);
		TextView actionbarTitle = (TextView)findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText("Events");
		
		Global global = ((Global)getApplicationContext());
		View events = findViewById(R.id.eventsLayout);
		try {
			global.fetchNumFriendRequests();
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		global.setNotifications(events);

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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	    if(keyCode == KeyEvent.KEYCODE_BACK)
	    {
	        startHomeActivity(null);
	    }
	    return false;
	}

	/*Start activity functions for going back and logging out*/
	public void startHomeActivity(View view)
	{
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
	}
	public void startLoginActivity(View view)
	{
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
}
