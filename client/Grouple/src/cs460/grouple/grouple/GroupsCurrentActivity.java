package cs460.grouple.grouple;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

public class GroupsCurrentActivity extends ActionBarActivity {

	BroadcastReceiver broadcastReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups_current);
		Bundle extras = getIntent().getExtras();
		String email = extras.getString("email");//need to put email in extras when current groups is called
		System.out.println("EMAIL for GETGROUPS: " + email);
		//also need to set the mod privs for the current user -> current list of groups
		new getGroupsTask()
		.execute("http://98.213.107.172/android_connect/get_groups2.php?email="
				+ email);
		
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
			return global.readJSONFeed(urls[0]);
		}

		protected void onPostExecute(String result)
		{
			LayoutInflater li = getLayoutInflater();
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					ArrayList<String> groups = new ArrayList<String>();
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
							if (extras.getString("mod").equals("true"))
							{
								rowView = (GridLayout) li.inflate(
										R.layout.listitem_group, null);
								//Button removeFriendButton = (Button) rowView
									//	.findViewById(R.id.removeFriendButton);
								//removeFriendButton.setId(i);
							} else
							{
								rowView = (GridLayout) li.inflate(
										R.layout.listitem_group, null);

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
}
