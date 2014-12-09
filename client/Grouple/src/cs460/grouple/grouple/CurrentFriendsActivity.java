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

public class CurrentFriendsActivity extends ActionBarActivity
{
	BroadcastReceiver broadcastReceiver;
	private ArrayList<String> friendsEmailList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_current_friends);

		Bundle extras = getIntent().getExtras();
		Global global = ((Global) getApplicationContext());
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText(extras.getString("Name") + "'s Friends");
		ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);
		upButton.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {

				startParentActivity(view);

			}
		});
		//Global global = ((Global) getApplicationContext());

		String email = extras.getString("email");
		// String email = global.getCurrentUser();
		System.out.println("Email: " + email);
		new getFriendsTask()
				.execute("http://98.213.107.172/android_connect/get_friends_firstlast.php?email="
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

	public void startParentActivity(View view)
	{
		Bundle extras = getIntent().getExtras();
		String parentClassName = extras.getString("ParentParentClassName");
		String className = extras.getString("ParentClassName");
		if (className == null)
		{
			className = "UserActivity";
		}
		Intent newIntent = null;
		try
		{
			newIntent = new Intent(this, Class.forName("cs460.grouple.grouple."
					+ className));
			newIntent.putExtra("email", extras.getString("ParentEmail"));
			if (extras.getString("ParentParentEmail") != null)
			{
				newIntent.putExtra("ParentEmail", extras.getString("ParentParentEmail"));
			}
			if (parentClassName != null)
			{
				newIntent.putExtra("ParentClassName", parentClassName);
			}
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		startActivity(newIntent);
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
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	private class getFriendsTask extends AsyncTask<String, Void, String>
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
					//ArrayList<String> friends = new ArrayList<String>();
					JSONArray jsonFriends = (JSONArray) jsonObject
							.getJSONArray("friends");

					if (jsonFriends != null)
					{
						System.out.println(jsonFriends.toString() + "\n"
								+ jsonFriends.length());
						LinearLayout currentFriendsRL = (LinearLayout) findViewById(R.id.currentFriendsLayout);
						Bundle extras = getIntent().getExtras();
						// looping thru json and adding to an array
						for (int i = 0; i < jsonFriends.length(); i++)
						{
							String firstraw = jsonFriends.getJSONObject(i)
									.getString("first");
							String lastraw = jsonFriends.getJSONObject(i)
									.getString("last");
							String friendEmail = jsonFriends.getJSONObject(i)
									.getString("email");
							friendsEmailList.add(i, friendEmail);

							String row = firstraw.substring(0, 1).toUpperCase()
									+ firstraw.substring(1);
							row = row + " ";
							row = row + lastraw.substring(0, 1).toUpperCase()
									+ lastraw.substring(1);

							System.out.println("Idx: " + i + " Email: "
									+ friendEmail + "\n" + row + "\n");

							GridLayout rowView;
							if (extras.getString("mod").equals("true"))
							{
								rowView = (GridLayout) li.inflate(
										R.layout.listitem_friend, null);
								Button removeFriendButton = (Button) rowView
										.findViewById(R.id.removeFriendButton);
								removeFriendButton.setId(i);
							} else
							{
								rowView = (GridLayout) li.inflate(
										R.layout.listitem_friends_friend, null);

							}
							Button friendNameButton = (Button) rowView
									.findViewById(R.id.friendNameButton);

							friendNameButton.setText(row);

							friendNameButton.setId(i);
							rowView.setId(i);
							currentFriendsRL.addView(rowView);

						}

					}
				}
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					//Double check 2 is the no friends code
					//inflate the new no friends layout 
					LinearLayout currentFriendsRL = (LinearLayout) findViewById(R.id.currentFriendsLayout);

					View row = li.inflate(R.layout.listitem_friend, null);

					String message = jsonObject.getString("message").toString();
					((Button) row.findViewById(R.id.friendNameButton))
							.setText(message);
					row.findViewById(R.id.removeFriendButton).setVisibility(1);
					currentFriendsRL.addView(row);

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			startParentActivity(null);
		}
		return false;
	}

	/* Start activity function for going back and logging out */
	public void startFriendsActivity(View view)
	{
		Intent intent = new Intent(this, FriendsActivity.class);
		startActivity(intent);
		finish();
	}

	public void removeFriendButton(View view)
	{

		final int idz = view.getId(); // Email of user
		final String friendEmail = friendsEmailList.get(idz); // Email of friend
							// to remove
		// refreshing the current friends layout
		Bundle extras = getIntent().getExtras();
		final String email = extras.getString("email");
		//delete confirmation
		new AlertDialog.Builder(this)
				.setMessage("Are you sure you want to remove that friend?")
				.setCancelable(true)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{

						new deleteFriendTask()
								.execute(
										"http://98.213.107.172/android_connect/delete_friend.php",
										email, friendEmail);
						//removing all of the views
						LinearLayout currentFriendsLayout = (LinearLayout) findViewById(R.id.currentFriendsLayout);
						currentFriendsLayout.removeAllViews();
						// calling getFriends to repopulate view
						new getFriendsTask()
								.execute("http://98.213.107.172/android_connect/get_friends_firstlast.php?email="
										+ email);
					}
				}).setNegativeButton("Cancel", null).show();
	}


	private class deleteFriendTask extends AsyncTask<String, Void, String>
	{
		protected String doInBackground(String... urls)
		{
			//urls 1, 2 are the emails
			Global global = ((Global) getApplicationContext());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("sender", urls[1]));
			nameValuePairs.add(new BasicNameValuePair("receiver", urls[2]));
			return global.readJSONFeed(urls[0], nameValuePairs);
		}

		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);

				if (jsonObject.getString("success").toString().equals("1"))
				{
					// success: friend has been deleted
					Log.d("dbmsg", jsonObject.getString("message"));
				} else if (jsonObject.getString("success").toString()
						.equals("2"))
				{
					// friend was not found in database
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

	public void startFriendProfileActivity(View view)
			throws InterruptedException
	{
		// need to get access to this friends email
		// launches friendProfileActivity and loads content based on that email
		int id = view.getId();
		// got the id, now we need to grab the users email and somehow pass it
		// to the activity
		Bundle extras = getIntent().getExtras();
		String friendEmail = friendsEmailList.get(id);
		Intent intent = new Intent(this, FriendProfileActivity.class);
		intent.putExtra("ParentClassName", "CurrentFriendsActivity");
		intent.putExtra("ParentParentEmail", extras.getString("ParentEmail"));
		intent.putExtra("ParentParentClassName", extras.getString("ParentClassName"));
		intent.putExtra("ParentName", extras.getString("Name"));

		String email = extras.getString("email");
		intent.putExtra("ParentEmail", email);
		Global global = ((Global) getApplicationContext());
		global.fetchNumFriends(friendEmail);
		Thread.sleep(500);
		intent.putExtra("email", friendEmail);
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
