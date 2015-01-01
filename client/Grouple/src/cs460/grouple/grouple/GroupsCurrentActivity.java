package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;

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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
	BroadcastReceiver broadcastReceiver;
	private ArrayList<String> groupsNameList = new ArrayList<String>();
	private String email;
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
		// backstack of intents
		// each class has a stack of intents lifo method used to execute them at
		// start of activity
		// intents need to include everything like ParentClassName, things for
		// current page (email, ...)
		// if check that friends
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		// do a check that it is not from a back push
		if (extras.getString("up").equals("true"))
		{
			// pull a new intent from the stack
			// load in everything from that intent
			System.out
					.println("Should be fetching off stack for current friends");
			parentIntent = global.getNextParentIntent(view);
		} else
		{
			// add to stack
			parentIntent = intent;
			// trying to add to stack whenever the page is actually left
		}
		Bundle parentExtras = parentIntent.getExtras();
		String className = parentExtras.getString("ParentClassName");
		email = parentExtras.getString("email");
		//global.fetchName(email);PANDA
		try
		{
			upIntent = new Intent(this, Class.forName("cs460.grouple.grouple."
					+ className));
		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// also need to set the mod privs for the current user -> current list
		// of groups
		new getGroupsTask()
		.execute("http://98.213.107.172/android_connect/get_groups.php?email="
				+ email);

		initActionBar();
		initKillswitchListener();
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
			intent.putExtra("up", "false");
			global.addToParentStack(groupsCurrent, parentIntent);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	private class getGroupsTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			return global.readJSONFeed(urls[0], null);
		}

		@Override
		protected void onPostExecute(String result)
		{
			LayoutInflater li = getLayoutInflater();
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// ArrayList<String> groups = new ArrayList<String>();
					JSONArray jsonGroups = jsonObject.getJSONArray("groups");

					if (jsonGroups != null)
					{
						System.out.println(jsonGroups.toString() + "\n"
								+ jsonGroups.length());
						JSONArray cake = (JSONArray) jsonGroups.get(0);
						LinearLayout currentGroupsLayout = (LinearLayout) findViewById(R.id.currentGroupsLayout);
						Bundle extras = getIntent().getExtras();
						// looping thru json and adding to an array
						for (int i = 0; i < cake.length(); i++)
						{
							String groupname = cake.getString(i);

							System.out.println("Idx: " + i + " Group Name: "
									+ groupname + "\n");

							GridLayout rowView;
							if (extras.getString("mod").equals("true"))
							{
								rowView = (GridLayout) li.inflate(
										R.layout.listitem_group, null);
								groupsNameList.add(i, groupname);
								// Button removeFriendButton = (Button) rowView
								// .findViewById(R.id.removeFriendButton);
								// removeFriendButton.setId(i);
							} else
							{
								rowView = (GridLayout) li.inflate(
										R.layout.listitem_group, null);

							}
							// Grab the buttons and set their IDs. Their IDs
							// will fall inline with the array 'groupsNameList'.
							Button groupNameButton = (Button) rowView
									.findViewById(R.id.groupNameButton);
							Button removeButton = (Button) rowView
									.findViewById(R.id.removeGroupButton);
							groupNameButton.setText(groupname);
							removeButton.setId(i);
							groupNameButton.setId(i);
							rowView.setId(i);
							currentGroupsLayout.addView(rowView);
							// System.out.println("Row: " + row +"\nCount: " +
							// i);

						}

					}
				}
				// user has no groups
				if (jsonObject.getString("success").toString().equals("2"))
				{
					LinearLayout currentGroupsLayout = (LinearLayout) findViewById(R.id.currentGroupsLayout);

					View row = li.inflate(R.layout.listitem_group, null);

					String message = jsonObject.getString("message").toString();
					((Button) row.findViewById(R.id.groupNameButton))
							.setText(message);
					row.findViewById(R.id.removeGroupButton).setVisibility(1);
					currentGroupsLayout.addView(row);
				} else
				{
					// failed
					// TextView loginFail = (TextView)
					// findViewById(R.id.loginFailTextViewLA);
					// loginFail.setVisibility(0);
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
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

	public void addToGroupTable(View view) throws InterruptedException
	{
		Global global = ((Global) getApplicationContext());
		// launches GroupProfileActivity, loading the page to its corresponding
		// group
		int id = view.getId();
		// got the id, now we need to grab the users email and somehow pass it
		// to the activity
		Log.d("message",
				"Executing the group profile: " + groupsNameList.get(id));
		//PANDA HERE IS ISSUE ABOVE
		String groupsName = groupsNameList.get(id);
		Intent intent = new Intent(this, GroupProfileActivity.class);
		// global.fetchNumGroupInvites(groupsName);
		Thread.sleep(500);
		intent.putExtra("gname", groupsName);
		intent.putExtra("up", "false");
		intent.putExtra("ParentClassName", "GroupsCurrentActivity");
		global.addToParentStack(groupsCurrent, parentIntent);

		startActivity(intent);

		finish();
	}

	private class deleteGroupTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			// urls 1, 2 are the emails
			Global global = ((Global) getApplicationContext());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("gname", urls[1]));
			return global.readJSONFeed(urls[0], nameValuePairs);
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
		// Get the id.
		int id = view.getId();
		final String gname = groupsNameList.get(id);
		new AlertDialog.Builder(this)
				.setMessage("Are you sure you want to delete this group?")
				.setCancelable(true)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int id)
					{
						new deleteGroupTask()
								.execute(
										"http://98.213.107.172/android_connect/delete_group.php",
										gname);
						// removing all of the views
						LinearLayout currentGroupsLayout = (LinearLayout) findViewById(R.id.currentGroupsLayout);
						currentGroupsLayout.removeAllViews();
						// Refresh the page to show the removal of the group.
						new getGroupsTask()
								.execute("http://98.213.107.172/android_connect/get_groups.php?email="
										+ email);

					}
				}).setNegativeButton("Cancel", null).show();
	}

}
