package cs460.grouple.grouple;

//import cs460.grouple.grouple.FriendRequestsActivity.getFriendRequestsTask;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;


//import cs460.grouple.grouple.FriendRequestsActivity.getAcceptFriendTask;
//import cs460.grouple.grouple.FriendRequestsActivity.getDeclineFriendTask;

import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GroupInvitesActivity extends ActionBarActivity {
	Intent parentIntent;
	Intent upIntent;
	BroadcastReceiver broadcastReceiver;
	private String receiver;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_invites);
		
		View groupInvites = findViewById(R.id.groupInvitesContainer);
		load(groupInvites);
	}

	public void initActionBar()
	{

		Global global = ((Global) getApplicationContext());
		/*Action bar*/
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);

		upButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				upIntent.putExtra("up", "true");
				startActivity(upIntent);
				finish();
			}
		});
		//upButton.setOnClickListener
		//global.fetchNumFriends(email)
		actionbarTitle.setText(global.getName() + "'s Group Invites");
	}
	
	public void load(View view)
	{
		Global global = ((Global) getApplicationContext());

		
		//backstack of intents
		//each class has a stack of intents lifo method used to execute them at start of activity
		//intents need to include everything like ParentClassName, things for current page (email, ...)
		//if check that friends
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		//do a check that it is not from a back push
		if (extras.getString("up").equals("true"))
		{
			//pull a new intent from the stack
			//load in everything from that intent
			parentIntent = global.getNextParentIntent(view);
		}
		else
		{
			//add to stack
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

		//Get the current users email address
		receiver = global.getCurrentUser();
		//Execute the php to get the the number of group invites.
		new getGroupInvitesTask()
		.execute("http://98.213.107.172/android_connect/get_groups_requests.php?email="
				+ receiver);
		
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Global global = ((Global) getApplicationContext());
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
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
			intent.putExtra("ParentClassName", "GroupInvitesActivity");
			global.addToParentStackGroupInvites(parentIntent);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
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
	
	private class getGroupInvitesTask extends AsyncTask<String, Void, String>
	{
		protected String doInBackground(String... urls)
		{
			//?
			Global global = ((Global) getApplicationContext());
			return global.readJSONFeed(urls[0],null);
		}

		protected void onPostExecute(String result)
		{
			LinearLayout groupInvitesLayout = (LinearLayout) findViewById(R.id.groupInvitesLayout);
			Global global = ((Global) getApplicationContext());
			LayoutInflater li = getLayoutInflater();
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					System.out.println("We are in the success");
					ArrayList<String> senders = new ArrayList<String>();
					ArrayList<String> groups = new ArrayList<String>();
					JSONArray jsonGroupInvites = (JSONArray) jsonObject.getJSONArray("requests");

					
					if (jsonGroupInvites != null)
					{
						View groupInvites = findViewById(R.id.groupInvitesContainer);
						global.setNumGroupInvites(jsonGroupInvites.length());
						global.setNotifications(groupInvites);
						System.out.println(jsonGroupInvites.toString() + "\n"
								+ jsonGroupInvites.length());

						// looping thru json and adding to an array
						for (int i = 0; i < jsonGroupInvites.length(); i++)
						{
							JSONObject object = jsonGroupInvites.getJSONObject(i);
							String raw = object.getString("sender");
							senders.add(raw);
							raw = object.getString("g_name");
							groups.add(raw);
							System.out.println("Row: " + raw + "\nCount: " + i);
						}
						
						// looping thru array and inflating listitems to the
						// GROUP REQUEST NAMES. CAN EASILY ADD SENDERS.
						for (int i = 0; i < groups.size(); i++)
						{
							GridLayout row = (GridLayout) li.inflate(R.layout.listitem_group_request, null);
							// Setting text of each friend request to the email
							// of the sender
							((TextView) row.findViewById(R.id.emailTextViewGRLI)).setText(groups.get(i));
							groupInvitesLayout.addView(row);
						}
					} else
					// no friend requests
					{
						global.setNumGroupInvites(0);
						
						GridLayout row = (GridLayout) li.inflate(R.id.sadGuyGridLayout, null);
						// Setting text of each friend request to the email
						// of the sender
						
						((TextView) row.findViewById(R.id.sadGuyTextView)).setText("You do not have any group invites.");
						groupInvitesLayout.addView(row);
					}
				} else
				{
					System.out.println("No groups found");
					global.setNumGroupInvites(0);
					
					GridLayout row = (GridLayout) li.inflate(R.layout.listitem_sadguy, null);
					// Setting text of each friend request to the email
					// of the sender
					
					((TextView) row.findViewById(R.id.sadGuyTextView)).setText("You do not have any group invites.");
					groupInvitesLayout.addView(row);
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	
	public void onClick(View view)
	{
		Global global = ((Global) getApplicationContext());
		switch (view.getId())
		{
		case R.id.declineGroupRequestButtonGRLI:
			
			View parent = (View) view.getParent();
			TextView declineEmail = (TextView) parent.findViewById(R.id.emailTextViewGRLI);
			global.setDeclineEmail(declineEmail.getText().toString());
			new getDeclineGroupTask().execute("http://98.213.107.172/android_connect/decline_group_request.php");
			break;
		case R.id.acceptGroupRequestButtonGRLI:
			View parent2 = (View) view.getParent();
			TextView acceptEmail = (TextView) parent2
					.findViewById(R.id.emailTextViewGRLI);
			global.setAcceptEmail(acceptEmail.getText().toString());
			new getAcceptGroupTask().execute("http://98.213.107.172/android_connect/accept_group_request.php");
			break;
		}
	}
	//Decline Group Request. Refactor the JSON calls.
	private class getDeclineGroupTask extends AsyncTask<String, Void, String>
	{
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			String receiver = global.getCurrentUser();
			String groupName = global.getDeclineEmail();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("mem", receiver));
			//pass the group name...
			nameValuePairs.add(new BasicNameValuePair("gname", groupName));
			return global.readJSONFeed(urls[0], nameValuePairs);
		}

		protected void onPostExecute(String result)
		{
			Global global = ((Global) getApplicationContext());
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				System.out.println(jsonObject.getString("success"));
				if (jsonObject.getString("success").toString().equals("1"))
				{
					startGroupInvitesActivity();
					//TODO: startFriendRequestsActivity();

				} else
				{
					// failed
					System.out.println("fail!");
					// TextView addFriendMessage = (TextView)
					// findViewById(R.id.addFriendMessageTextViewAFA);
					// addFriendMessage.setText("User not found.");
					// addFriendMessage.setVisibility(0);
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	//Accept code. 
	private class getAcceptGroupTask extends AsyncTask<String, Void, String>
	{
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			String receiver = global.getCurrentUser();
			String groupName = global.getAcceptEmail();

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("mem", receiver));
			nameValuePairs.add(new BasicNameValuePair("gname", groupName));
			return global.readJSONFeed(urls[0], nameValuePairs);
		}

		protected void onPostExecute(String result)
		{
			Global global = ((Global) getApplicationContext());
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				System.out.println(jsonObject.getString("success"));
				if (jsonObject.getString("success").toString().equals("1"))
				{
	
					startGroupInvitesActivity();

				} else
				{
					// failed
					System.out.println("fail!");
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	
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
	
	/*
	 * Start activity functions for refreshing friend requests, going back and
	 * logging out
	 */
	public void startGroupInvitesActivity()
	{
		Global global = ((Global) getApplicationContext());
		Intent intent = new Intent(this, GroupInvitesActivity.class);
		intent.putExtra("up", "true");
		global.addToParentStackGroupInvites(parentIntent);
		startActivity(intent);
	}

}