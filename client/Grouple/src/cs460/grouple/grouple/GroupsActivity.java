package cs460.grouple.grouple;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class GroupsActivity extends ActionBarActivity
{
	BroadcastReceiver broadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups);
		// Action bar setup
		// ActionBar ab = getActionBar();

		// ab.setIcon(
		// new
		// ColorDrawable(getResources().getColor(android.R.color.transparent)));
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText("Groups");
		// ImageView view = (ImageView)findViewById(android.R.id.home);
		// view.setPadding(15, 20, 5, 40);
		ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);
		upButton.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {

				startParentActivity(view);

			}
		});

		Global global = ((Global) getApplicationContext());
		View groups = findViewById(R.id.groupsLayout);
		global.fetchNumGroupInvites(global.getCurrentUser());
		global.setNotifications(groups);
		
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
			startHomeActivity(null);
		}
		return super.onOptionsItemSelected(item);
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
			newIntent.putExtra("ParentClassName", "GroupsActivity");
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		startActivity(newIntent);
	}
	/* Start activity functions for going back to home and logging out */
	public void startHomeActivity(View view)
	{
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	/* Start activity methods for group sub-activities */
	public void startGroupCreateActivity(View view)
	{
		Bundle extras = getIntent().getExtras();
		Intent intent = new Intent(this, GroupCreateActivity.class);
		intent.putExtra("ParentClassName", "GroupsActivity");
		intent.putExtra("ParentParentClassName", extras.getString("ParentClassName"));
		Global global = (Global)getApplicationContext();
		intent.putExtra("email", global.getCurrentUser());
		intent.putExtra("mod", "true");
		startActivity(intent);
	}
	
	public void startGroupInvitesActivity(View view)
	{
		Intent intent = new Intent(this, GroupInvitesActivity.class);
		startActivity(intent);
	}
	
	public void startGroupsCurrentActivity(View view)
	{
		Intent intent = new Intent(this, GroupsCurrentActivity.class);
		Global global = ((Global) getApplicationContext());
		intent.putExtra("ParentActivity", "GroupsActivity");
		intent.putExtra("email", global.getCurrentUser());//specifies which email for the list of groups
		intent.putExtra("mod", "true");//gives user ability admin in the current groups screen
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
