package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
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

/*
 * GroupCreateActivity displays a list of all active group requests of a user.
 */
public class GroupInvitesActivity extends ActionBarActivity
{
	Intent parentIntent;
	Intent upIntent;
	BroadcastReceiver broadcastReceiver;
	User user;//our current user
	private String receiver;
	View groupInvites;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_invites);

		groupInvites = findViewById(R.id.groupInvitesContainer);
		load(groupInvites);
	}

	public void initActionBar()
	{

		Global global = ((Global) getApplicationContext());
		/* Action bar */
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
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
		// upButton.setOnClickListener
		// global.fetchNumFriends(email)
		//actionbarTitle.setText(global.getName() + "'s Group Invites"); //PANDA
	}
	//Loads the page. As well as populates the user.
	public void load(View view)
	{
		Global global = ((Global) getApplicationContext());
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();	
		String className = extras.getString("ParentClassName");
		
		//String email = extras.getString("email");
		user = global.loadUser(global.getCurrentUser().getEmail());
		populateGroupInvites();

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
			intent.putExtra("ParentClassName", "GroupInvitesActivity");
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

	//
	private void populateGroupInvites()
	{
		//Get current layout.
		LinearLayout groupInvitesLayout = (LinearLayout) findViewById(R.id.groupInvitesLayout);
		//Get Global. We use it for global stuff.
		Global global = ((Global) getApplicationContext());
		LayoutInflater li = getLayoutInflater();
		int numInvite = user.getNumGroupInvites();
		//array list needs to have group names, maybe the sender names and needs to have group ids	
		if(numInvite > 0 )
		{
			//Calls user getGroups(). 
			Map<Integer, String> groupInvites = user.getGroupInvites();
			// looping thru the map
			for (Map.Entry<Integer, String> entry : groupInvites.entrySet())
			{
				GridLayout row = (GridLayout) li.inflate(R.layout.listitem_group_request, null);

				((TextView) row.findViewById(R.id.emailTextViewGRLI)).setText(entry.getValue());
				groupInvitesLayout.addView(row);
			}
		}
		else
		{
			//no group requests were found
			GridLayout sadGuy = (GridLayout) li.inflate(R.layout.listitem_sadguy, null);
			TextView sadTextView = (TextView) sadGuy.findViewById(R.id.sadGuyTextView);
			//Set the sad guy text.
			sadTextView.setText("You do not have any group invites.");
			groupInvitesLayout.addView(sadGuy);
		}
	}
			
	
	public void onClick(View view)
	{
		Global global = ((Global) getApplicationContext());
		switch (view.getId())
		{
		case R.id.declineGroupRequestButtonGRLI:

			View parent = (View) view.getParent();
			TextView declineEmail = (TextView) parent
					.findViewById(R.id.emailTextViewGRLI);
		//	global.setDeclineEmail(declineEmail.getText().toString());
			new getDeclineGroupTask()
					.execute("http://98.213.107.172/android_connect/decline_group_request.php");
			break;
		case R.id.acceptGroupRequestButtonGRLI:
			View parent2 = (View) view.getParent();
			TextView acceptEmail = (TextView) parent2
					.findViewById(R.id.emailTextViewGRLI);
			//global.setAcceptEmail(acceptEmail.getText().toString()); PANDA
			new getAcceptGroupTask()
					.execute("http://98.213.107.172/android_connect/accept_group_request.php");
			break;
		}
	}

	// Decline Group Request. Refactor the JSON calls.
	private class getDeclineGroupTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			//String receiver = global.getCurrentUser(); getEmail() PANDA
			//String groupName = global.getDeclineEmail(); PANDA
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("mem", receiver));
			// pass the group name...
		//	nameValuePairs.add(new BasicNameValuePair("gname", groupName)); PANDA NEeds to be gid anyway
			return global.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
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
					// TODO: startFriendRequestsActivity();

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

	// Accept code.
	private class getAcceptGroupTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			//String receiver = global.getCurrentUser(); PANDA
			//String groupName = global.getAcceptEmail(); PANDA

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("mem", receiver));
			//nameValuePairs.add(new BasicNameValuePair("gname", groupName)); PANDA gid 
			return global.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
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

	/*
	 * Start activity functions for refreshing friend requests, going back and
	 * logging out
	 */
	public void startGroupInvitesActivity()
	{
		Global global = ((Global) getApplicationContext());
		Intent intent = new Intent(this, GroupInvitesActivity.class);
		intent.putExtra("up", "true");
		//global.addToParentStack(groupInvites, parentIntent);
		startActivity(intent);
	}

}

