package cs460.grouple.grouple;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
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
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(true);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText("Friends");
		//Global global = ((Global) getApplicationContext());

		Bundle extras = getIntent().getExtras();
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

	@Override
	public Intent getSupportParentActivityIntent()
	{
		Intent parentIntent = getIntent();
		String className = parentIntent.getStringExtra("ParentClassName"); // getting
																			// the
																			// parent
																			// class
																			// name

		Intent newIntent = null;
		try
		{
			newIntent = new Intent(this, Class.forName("cs460.grouple.grouple."
					+ className));
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return newIntent;
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

	// Removing this makes it default to going to the previous page you were on.
	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
	 * if(keyCode == KeyEvent.KEYCODE_BACK) { startFriendsActivity(null); }
	 * return false; }
	 */

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
										friendEmail);

						// refreshing the current friends layout
						Bundle extras = getIntent().getExtras();
						String email = extras.getString("email");
						// removing all views
						LinearLayout currentFriendsLayout = (LinearLayout) findViewById(R.id.currentFriendsLayout);
						currentFriendsLayout.removeAllViews();
						// calling getFriends to repopulate view
						new getFriendsTask()
								.execute("http://98.213.107.172/android_connect/get_friends_firstlast.php?email="
										+ email);
					}
				}).setNegativeButton("Cancel", null).show();
	}

	public String deleteFriendJSONFeed(String URL, String friendEmail)
	{
		// Get all the fields and store locally
		Global global = ((Global) getApplicationContext());
		String sender = global.getCurrentUser();

		StringBuilder stringBuilder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(URL);
		try
		{
			// Add your data
			System.out.println("Receiver Email: " + friendEmail
					+ "Sender Email: " + sender);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("sender", sender));
			nameValuePairs.add(new BasicNameValuePair("receiver", friendEmail));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpClient.execute(httpPost);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200)
			{
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream));
				String line;
				while ((line = reader.readLine()) != null)
				{
					stringBuilder.append(line);
				}
				inputStream.close();
			} else
			{
				Log.d("JSON", "Failed to download file");
			}
		} catch (Exception e)
		{
			Log.d("readJSONFeed", e.getLocalizedMessage());
		}
		return stringBuilder.toString();
	}

	private class deleteFriendTask extends AsyncTask<String, Void, String>
	{
		protected String doInBackground(String... urls)
		{
			return deleteFriendJSONFeed(urls[0], urls[1]);
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
		String friendEmail = friendsEmailList.get(id);
		Intent intent = new Intent(this, FriendProfileActivity.class);
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
