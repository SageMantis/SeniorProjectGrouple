package cs460.grouple.grouple;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GroupsCurrentActivity extends ActionBarActivity {

	BroadcastReceiver broadcastReceiver;
	private ArrayList<String> groupsNameList = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups_current);
		
		Bundle extras = getIntent().getExtras();
		String email = extras.getString("email");//need to put email in extras when current groups is called
		Log.d("PANDA", "EMAIL: " + email);
		/*Action bar*/
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		Global global = ((Global) getApplicationContext());
		actionbarTitle.setText("Groups");
		
		
		//also need to set the mod privs for the current user -> current list of groups
		new getGroupsTask()
		.execute("http://98.213.107.172/android_connect/get_groups.php?email="
				+ email);
		
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
	

	private class getGroupsTask extends AsyncTask<String, Void, String>
	{
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			return global.readJSONFeed(urls[0], null);
		}

		protected void onPostExecute(String result)
		{
			LayoutInflater li = getLayoutInflater();
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					//ArrayList<String> groups = new ArrayList<String>();
					JSONArray jsonGroups = (JSONArray) jsonObject
							.getJSONArray("groups");

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
							//same each way for now
							if (extras.getString("mod").equals("true"))
							{
								rowView = (GridLayout) li.inflate(
										R.layout.listitem_group, null);
								groupsNameList.add(i, groupname);
								//Button removeFriendButton = (Button) rowView
									//	.findViewById(R.id.removeFriendButton);
								//removeFriendButton.setId(i);
							} 
							else
							{
								rowView = (GridLayout) li.inflate(
										R.layout.listitem_group, null);
								groupsNameList.add(i, groupname);

							}
							Button groupNameButton = (Button) rowView
									.findViewById(R.id.groupNameButton);

							groupNameButton.setText(groupname);

							groupNameButton.setId(i);
							rowView.setId(i);
							currentGroupsLayout.addView(rowView);
							// System.out.println("Row: " + row +"\nCount: " +
							// i);

						}

					}
				}
				// user has no groups
				if (jsonObject.getString("success").toString().equals("0"))
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
	
	public void addToGroupTable(View view) throws InterruptedException {
		// launches GroupProfileActivity, loading the page to its corresponding group
		int id = view.getId();
		// got the id, now we need to grab the users email and somehow pass it
		// to the activity
		Log.d("message", "Executing the group profile: " + groupsNameList.get(id));
		String groupsName = groupsNameList.get(id);
		Intent intent = new Intent(this, GroupProfileActivity.class);
		Global global = ((Global) getApplicationContext());
		//global.fetchNumGroupInvites(groupsName);
		Thread.sleep(500);
		intent.putExtra("gname", groupsName);
		startActivity(intent);
	}

}
