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
import android.widget.ImageButton;
import android.widget.TextView;

public class GroupsActivity extends ActionBarActivity
{
	BroadcastReceiver broadcastReceiver;
	Intent parentIntent;

	Intent upIntent;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups);
		
		View groups = findViewById(R.id.groupsContainer);
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
		upButton.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				upIntent.putExtra("up", "true");
				startActivity(upIntent);
				finish();
			}
		});
	}
	
	public void load(View view)
	{
		Global global = ((Global) getApplicationContext());
		// Action bar setup
		// ActionBar ab = getActionBar();
		//backstack of intents
		//each class has a stack of intents lifo method used to execute them at start of activity
		//intents need to include everything like ParentClassName, things for current page (email, ...)
		//if check that friends
		String email;
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		//do a check that it is not from a back push
		if (extras.getString("up").equals("true"))
		{
			//pull a new intent from the stack
			//load in everything from that intent
			parentIntent = global.getNextParentIntent(view);
			System.out.println("Up was true, fetching parent intent...");
			
			System.out.println("ParentName = " +parentIntent.getExtras().getString("ParentClassName"));
		}
		else
		{
			System.out.println("Up was false... not fetching parent");
			parentIntent = intent;
		}	
		
		Bundle parentExtras = parentIntent.getExtras();
		String className = parentExtras.getString("ParentClassName");
		try
		{
			upIntent = new Intent(this, Class.forName("cs460.grouple.grouple."
					+ className));
		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		global.fetchNumGroupInvites(global.getCurrentUser());
		global.setNotifications(view);

		
		initActionBar();
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
		Global global = ((Global) getApplicationContext());
		if (id == R.id.action_logout)
		{
		
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
			intent.putExtra("up", "false");
			intent.putExtra("ParentClassName", "GroupsActivity");
			global.addToParentStackGroups(parentIntent);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			upIntent.putExtra("up", "true");
			startActivity(upIntent);
			finish();
		}
		return false;
	}



	/* Start activity methods for group sub-activities */
	public void startGroupCreateActivity(View view)
	{
		Global global = ((Global) getApplicationContext());
		Intent intent = new Intent(this, GroupCreateActivity.class);
		intent.putExtra("ParentClassName", "GroupsActivity");
		intent.putExtra("email", global.getCurrentUser());
		intent.putExtra("mod", "true");
		intent.putExtra("up", "false");
		global.addToParentStackGroups(parentIntent);
		startActivity(intent);
	}
	
	public void startGroupInvitesActivity(View view)
	{
		Global global = (Global)getApplicationContext();
		Intent intent = new Intent(this, GroupInvitesActivity.class);
		intent.putExtra("email", global.getCurrentUser());
		intent.putExtra("up", "false");
		intent.putExtra("ParentClassName", "GroupsActivity");
	//	intent.putExtra("mod", "true");
		startActivity(intent);
		global.addToParentStackGroups(parentIntent);
	}
	
	public void startGroupsCurrentActivity(View view)
	{
		Intent intent = new Intent(this, GroupsCurrentActivity.class);
		Global global = ((Global) getApplicationContext());
		intent.putExtra("ParentClassName", "GroupsActivity");
		intent.putExtra("email", global.getCurrentUser());//specifies which email for the list of groups
		intent.putExtra("mod", "true");//gives user ability admin in the current groups screen
		intent.putExtra("up", "false");
		intent.putExtra("Name", global.getName());
		global.addToParentStackGroups(parentIntent);
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
