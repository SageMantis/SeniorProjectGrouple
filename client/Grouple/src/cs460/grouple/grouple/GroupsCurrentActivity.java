package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * GroupsCurrentActivity displays a list of all groups a member is a part of.
 */
public class GroupsCurrentActivity extends ActionBarActivity
{

	Intent parentIntent;
	Intent upIntent;
	int clickedRemoveID;
	BroadcastReceiver broadcastReceiver;
	User user; //user whose current groups displayed
	View groupsCurrent;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups_current);

		groupsCurrent = findViewById(R.id.groupsCurrentContainer);
		load(groupsCurrent);
	}

	/* loading actionbar */
	public void initActionBar()
	{
		Global global = ((Global) getApplicationContext());
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
	//	actionbarTitle.setText(global.getName() + "'s Groups"); //PANDA
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

	/* loading in everything for current friends */
	public void load(View view)
	{
		Global global = ((Global) getApplicationContext());
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		
		//grabbing the user with the given email in the extras
		user = global.loadUser(extras.getString("email"));	

		String className = extras.getString("ParentClassName");

		populateGroupsCurrent();

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
		Global global = ((Global) getApplicationContext());
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
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
			intent.putExtra("ParentClassName", "GroupsCurrentActivity");
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * Uses the mapping of ids and group names to display the users current groups
	 * Groups will be loaded into the Group class when profile is viewed,
	 * 		this could be changed to immediately if we can get the User class communicating with global.
	 * 		same goes for friends, friend requests and group invites
	 */
	public void populateGroupsCurrent()
	{
		Global global = ((Global) getApplicationContext());
		LayoutInflater li = getLayoutInflater();
		LinearLayout groupsLayout = ((LinearLayout)findViewById(R.id.groupsCurrentLayout));
		//grabbing the users groups
		Map<Integer, String> groups = user.getGroups();
		System.out.println("USER HAS A NAME OF" + user.getFullName());
		if (groups != null && groups.size() > 0)
		{
			int i = 0;
			
			for (Map.Entry<Integer, String> entry : groups.entrySet()) {
				//Group group = global.loadGroup(id);
				GridLayout rowView;
				
				//if mod true this, if not someting else
				rowView = (GridLayout) li.inflate(
						R.layout.listitem_group, null);

				
				// Grab the buttons and set their IDs. Their IDs
				// will fall inline with the array 'groupsNameList'.
				Button groupNameButton = (Button) rowView
						.findViewById(R.id.groupNameButton);
				Button removeButton = (Button) rowView
						.findViewById(R.id.removeGroupButton);
				groupNameButton.setText(entry.getValue() + " " + entry.getKey());
				
				//setting ids to the id of the group for button functionality
				removeButton.setId(entry.getKey());
				groupNameButton.setId(entry.getKey());
				rowView.setId(entry.getKey());
				
				//adding row to view
				
				groupsLayout.addView(rowView);

				i++;
			}
		}
		else
		{
		
			// user has no groups
			// thinking of putting global func: View row = global.getSadGuy("Text to display");

	
			// The user has no groups so display the sad guy image.
			View row = li.inflate(R.layout.listitem_sadguy, null);
			((TextView) row.findViewById(R.id.sadGuyTextView))
				.setText("You do not have any groups.");


			
			groupsLayout.addView(row);
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

	private class leaveGroupTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			// urls 1, 2 are the emails
			Global global = ((Global) getApplicationContext());

			return global.readJSONFeed(urls[0], null);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);

				if (jsonObject.getString("success").toString().equals("1"))
				{
					// success: group has been deleted
					//reload this activity
					// removing all of the views
					LinearLayout groupsLayout = (LinearLayout) findViewById(R.id.groupsCurrentLayout);
					groupsLayout.removeAllViews();
					// Refresh the page to show the removal of the group.
					populateGroupsCurrent();
					Log.d("dbmsg", jsonObject.getString("message"));
				} else if (jsonObject.getString("success").toString()
						.equals("2"))
				{
					// group was not found in database. Need to throw message
					
					
					// alerting the user.
					Log.d("dbmsg", jsonObject.getString("message"));
				} else
				{
					// sql error
					Log.d("dbmsg", jsonObject.getString("message"));
				}

			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	public void removeGroupButton(View view) throws InterruptedException
	{
		//Get the id.
		clickedRemoveID = view.getId();

		new AlertDialog.Builder(this)
				.setMessage("Are you sure you want to leave this group?")
				.setCancelable(true)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int id)
					{
						System.out.println("Leave grp email / id: " + user.getEmail() + " " + clickedRemoveID);
						new leaveGroupTask()
								.execute(
										"http://68.59.162.183/android_connect/leave_group.php?email=" + user.getEmail() + "&gid=" + clickedRemoveID);
						user.removeGroup(clickedRemoveID);
						clickedRemoveID = -1; //reset
					}
				}).setNegativeButton("Cancel", null).show();
	}

}
