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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/*
 * GroupsActivity displays displays the group navigation page for the user.
 */
public class GroupsActivity extends ActionBarActivity
{
	BroadcastReceiver broadcastReceiver;
	Intent parentIntent;
	View groups;
	User user;
	Intent upIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups);

		groups = findViewById(R.id.groupsContainer);
		load(groups);

	}

	public void initActionBar()
	{
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText("Groups");
		// ImageView view = (ImdageView)findViewById(android.R.id.home);
		// view.setPadding(15, 20, 5, 40);
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
		user = global.loadUser(global.getCurrentUser().getEmail());
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		
		setNotifications();
		
		String className = extras.getString("ParentClassName");


		initActionBar();
		initKillswitchListener();

	}

	private void setNotifications()
	{
		// Groups activity
		if (findViewById(R.id.pendingGroupsButton) != null)
		{
			System.out.println("Pending groups setting text to what it is");
			((Button) findViewById(R.id.pendingGroupsButton))
					.setText("Group Invites (" + user.getNumGroupInvites() + ")");
		}
		if (findViewById(R.id.yourGroupsButton) != null)
		{
			((Button) findViewById(R.id.yourGroupsButton))
					.setText("My Groups (" + user.getNumGroups() + ")");
		}
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
		Global global = ((Global) getApplicationContext());
		if (id == R.id.action_logout)
		{
			Intent login = new Intent(this, LoginActivity.class);
			startActivity(login);
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		if (id == R.id.action_home)
		{
			Intent intent = new Intent(this, HomeActivity.class);
			intent.putExtra("ParentClassName", "GroupsActivity");
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	/* Start activity methods for group sub-activities */
	public void startGroupCreateActivity(View view)
	{
		Global global = ((Global) getApplicationContext());
		Intent intent = new Intent(this, GroupCreateActivity.class);
		intent.putExtra("ParentClassName", "GroupsActivity");
		//intent.putExtra("email", global.getCurrentUser());
		intent.putExtra("mod", "true");
		startActivity(intent);
	}

	public void startGroupInvitesActivity(View view)
	{
		Global global = (Global) getApplicationContext();
		Intent intent = new Intent(this, GroupInvitesActivity.class);
		intent.putExtra("ParentClassName", "GroupsActivity");
		// intent.putExtra("mod", "true");
		startActivity(intent);
	}

	public void startGroupsCurrentActivity(View view)
	{
		Intent intent = new Intent(this, GroupsCurrentActivity.class);
		Global global = ((Global) getApplicationContext());
		intent.putExtra("ParentClassName", "GroupsActivity");
		System.out.println("ADDING EXTRA EMAIL AS " + user.getEmail());
		intent.putExtra("email", user.getEmail());// specifies which
															// email for the
															// list of groups
		intent.putExtra("mod", "true");// gives user ability admin in the
										// current groups screen
		//intent.putExtra("Name", global.getName()); //PANDA
		System.out.println("Adding parent intent to stack");
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
