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
import org.apache.http.client.methods.HttpGet;
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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GroupCreateActivity extends ActionBarActivity {

	BroadcastReceiver broadcastReceiver;
	//This holds all of your friends by email address.
	private ArrayList<String> friendsEmailList = new ArrayList<String>();
	//This holds all of your friends by name.
	private ArrayList<String> friendsNameList = new ArrayList<String>();
	private ArrayList<Boolean> isAdmin = new ArrayList<Boolean>();
	private ArrayList<String> alreadyAdded = new ArrayList<String>();
	private ArrayList<String> added = new ArrayList<String>();
	private ArrayList<Boolean> role = new ArrayList<Boolean>();
	private ArrayList<HttpResponse> response = new ArrayList<HttpResponse>();
	//private ArrayList<String> addedEmailAddress = new ArrayList<String>();
	private int increment = 0;
	private String email = null;
	//private final EditText groupName = (EditText)findViewById(R.id.groupName); <- NEVER EVER USE THIS HERE
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_create);
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(true);
		TextView actionBarTitle = (TextView)findViewById(R.id.actionbarTitleTextView);
		actionBarTitle.setText("Groups");
		//Global global = (Global)getApplicationContext();
		/*
		final Button toggle = (Button)findViewById(R.id.removeFriendButtonNoAccess);
		toggle.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg){
				//toggle.setText("+");
				//toggle.setTextColor(Color.parseColor("#FFFFFF"));
			}
		});
		*/
		Bundle extras = getIntent().getExtras();
		email = extras.getString("email");
		
		//Here I believe we should call get_friends.php. that will return all of your friends by email. which is how we would store it in the db.
		new GroupMembers().execute("http://98.213.107.172/" +
				"android_connect/get_friends_firstlast.php?email=" + email);
		
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
	
	public String readGetFriendsJSONFeed(String URL)
	{
		StringBuilder stringBuilder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(URL);
		HttpPost httpPost = new HttpPost(URL);
		///////HttpResponse response;
		Log.d("message", "This is the URL used: " + URL);
		
		try
		{
			if(URL == "http://98.213.107.172/android_connect/create_group.php"){
				EditText groupNameEditText = (EditText)findViewById(R.id.groupName);
				EditText groupBioEditText = (EditText)findViewById(R.id.groupBio);
				
				String groupname = groupNameEditText.getText().toString();
				String groupbio = groupBioEditText.getText().toString();
				
				Log.d("message2", "Group Name: " + groupname + '\n' + "Group Bio: " + groupbio);

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
				//Add yourself to the group.
				nameValuePairs.add(new BasicNameValuePair("gname", groupname));
				nameValuePairs.add(new BasicNameValuePair("gbio", groupbio));
				nameValuePairs.add(new BasicNameValuePair("mem",email));
				//Setting role as true makes you the admin.
				nameValuePairs.add(new BasicNameValuePair("role", "C"));
				//Add the sender
				nameValuePairs.add(new BasicNameValuePair("sender", email));
				//Submit these namevalue pairs to the database.
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				response.add(httpClient.execute(httpPost));
				
				for(int i = 0; i < added.size(); i++){
					
					Log.d("message3", "How many of these are there? " + i);
					//nameValuePairs.add(new BasicNameValuePair("index", "" + i));
					nameValuePairs.add(new BasicNameValuePair("gname", groupname));
					nameValuePairs.add(new BasicNameValuePair("gbio", groupbio));
					//use this name to get the index of the friendslistname and use that index to get the  email address ffs
					int temp_id = friendsNameList.indexOf(added.get(i));
					String temp_email = friendsEmailList.get(temp_id);
					//Add the email address to the mf.
					nameValuePairs.add(new BasicNameValuePair("mem",temp_email));
					
					if(role.get(i)){
						nameValuePairs.add(new BasicNameValuePair("role", "A"));
					}
					else{
						nameValuePairs.add(new BasicNameValuePair("role", "M"));
					}
					//Add the sender. (Yourself)
					nameValuePairs.add(new BasicNameValuePair("sender", email));
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					response.add(httpClient.execute(httpPost));
				}
				
				//Here we will return to the Group main page.
				startGroupsActivity(null);
				finish();

			}
			else{
				response.add(httpClient.execute(httpGet));
			}

			for(; increment < response.size() + 1; increment++){//////////////////////
				StatusLine statusLine = response.get(increment).getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200)
				{
					HttpEntity entity = response.get(increment).getEntity();
					InputStream inputStream = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(inputStream));
					String line;
					while ((line = reader.readLine()) != null)
					{
						System.out.println("New line: " + line);
						stringBuilder.append(line);
					}
					inputStream.close();
				} 
				else
				{
					Log.d("JSON", "Failed to download file");
				}
			}////////////////////////////////////////////
		} catch (Exception e)
		{
			Log.d("readJSONFeed", e.getLocalizedMessage());
		}
		
		Log.d("message", "stringBuilder has: " + stringBuilder.toString());
		return stringBuilder.toString();
	}
	
	///////////Create Group pops up a confirm box to make sure the user wants to create the group.
	public void createGroupButton(View view){
		
		new AlertDialog.Builder(this)
		.setMessage("Are you sure you want to create this group?")
		.setCancelable(true)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				new GroupMembers().execute("http://98.213.107.172/" +
						"android_connect/create_group.php");
			}
		}).setNegativeButton("Cancel", null).show();
	}
	///////////
	
	
	///////////
	/*
	private class createGroup extends AsyncTask<String, Void, String>
	{

		//Override
		protected String doInBackground(String... urls) {
			// TODO Auto-generated method stub
			return readGetFriendsJSONFeed(urls[0]);
		}
		
		//Override
		protected void onPostExecute(String result){

		}
		
	}
	*/
	///////////
	//This is where the initial php leads to.
	private class GroupMembers extends AsyncTask<String, Void, String>
	{

		//Override
		protected String doInBackground(String... urls) {
			// TODO Auto-generated method stub
			return readGetFriendsJSONFeed(urls[0]);
		}
		
		//Override
		protected void onPostExecute(String result){

			/* beginning building the interface */
			Log.d("message", "This is the resultt: " + result);
			LayoutInflater inflater = getLayoutInflater();
			final LayoutInflater inflater2 = getLayoutInflater();
			try
			{
				Log.i("tagconvertstr", "["+result+"]");
				JSONObject jsonObject = new JSONObject(result);
				Log.d("messagefinal", "what's this? " + jsonObject.getString("success").toString());
				
				if (jsonObject.getString("success").toString().equals("1"))
				{
					//ArrayList<String> friends = new ArrayList<String>();
					JSONArray jsonFriends = (JSONArray) jsonObject
							.getJSONArray("friends");

					if (jsonFriends != null)
					{
						System.out.println(jsonFriends.toString() + "\n"
								+ jsonFriends.length());
						LinearLayout membersToAdd = (LinearLayout) findViewById(R.id.linearLayoutNested1);
						final LinearLayout membersToAdd2 = (LinearLayout) findViewById(R.id.linearLayoutNested2);
						//final Button removeFriendButton2 = (Button) findViewById(R.id.removeFriendButtonNoAccess);///////////////////
						
						
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
							Log.d("messilyseparatedbylions", friendEmail);
							friendsEmailList.add(i, friendEmail);
							String row = firstraw.substring(0, 1).toUpperCase()
									+ firstraw.substring(1);
							row = row + " ";
							row = row + lastraw.substring(0, 1).toUpperCase()
									+ lastraw.substring(1);
							//have an arraylist that will hold
							friendsNameList.add(row);
							System.out.println("Idx: " + i + " Email: "
									+ friendEmail + "\n" + row + "\n");
							GridLayout rowView;
							if (extras.getString("mod").equals("true"))
							{
								rowView = (GridLayout) inflater.inflate(R.layout.listitem_friend_noaccess, null);
								
								isAdmin.add(false);
								
								final Button removeFriendButton = (Button) rowView.findViewById(R.id.removeFriendButtonNoAccess);
								removeFriendButton.setOnClickListener(new OnClickListener(){
									public void onClick(View view){
										if(removeFriendButton.getText().toString().equals("-")){
											removeFriendButton.setText("A");
											isAdmin.set(view.getId(), true);
											removeFriendButton.setTextColor(Color.parseColor("#7fff00"));
										}
										else{
											removeFriendButton.setText("-");
											isAdmin.set(view.getId(), false);
											removeFriendButton.setTextColor(Color.parseColor("#ffcc0000"));
										}
									}
								});
								removeFriendButton.setId(i);
								//removeFriendButton2.setId(i);
							} else
							{
								rowView = (GridLayout) inflater.inflate(R.layout.listitem_friends_friend_noaccess, null);

							}
							//Button friendNameButton = (Button) rowView.findViewById(R.id.friendNameButtonNoAccess);
							final Button friendNameButton = (Button) rowView.findViewById(R.id.friendNameButtonNoAccess);
							friendNameButton.setOnClickListener(new OnClickListener(){
								public void onClick(View view){
									String text = friendNameButton.getLayout().getText().toString();
									if(alreadyAdded.size() == 0){
										GridLayout rowView2 = (GridLayout) inflater2.inflate(R.layout.listitem_friend_noaccess, null);
										Button removeFriendButton2 = (Button) rowView2.findViewById(R.id.removeFriendButtonNoAccess);//////
										removeFriendButton2.setId(view.getId()); ////////////////
										if(isAdmin.get(removeFriendButton2.getId())){/////////////view.getId()
											removeFriendButton2.setText("A");////////////////
											//isAdmin.set(view.getId(), true);/////////////////
											removeFriendButton2.setTextColor(Color.parseColor("#7fff00"));///////
											added.add(text); role.add(true);//
										}
										else{
											removeFriendButton2.setText("-");///////
											//isAdmin.set(view.getId(), false);//////////
											removeFriendButton2.setTextColor(Color.parseColor("#ffcc0000"));//////////
											added.add(text); role.add(false);//
										}
										
										
										rowView2.setId(view.getId());
										((Button) rowView2.findViewById(R.id.friendNameButtonNoAccess)).setText(text);
										rowView2.findViewById(R.id.friendNameButtonNoAccess).setVisibility(1);
										membersToAdd2.addView(rowView2);
										alreadyAdded.add(text);
									}
									else{
										boolean flag = false;
										for(int i = 0; i < alreadyAdded.size(); i++){
											if(alreadyAdded.get(i).equals(text)){
												flag = true;
											}
										}
										
										if(!flag){
											GridLayout rowView2 = (GridLayout) inflater2.inflate(R.layout.listitem_friend_noaccess, null);
											Button removeFriendButton2 = (Button) rowView2.findViewById(R.id.removeFriendButtonNoAccess);//////
											removeFriendButton2.setId(view.getId()); ///////////////////
											if(isAdmin.get(removeFriendButton2.getId())){///////////// view.getId()
												removeFriendButton2.setText("A");////////////////
												//isAdmin.set(view.getId(), true);/////////////////
												removeFriendButton2.setTextColor(Color.parseColor("#7fff00"));///////
												added.add(text); role.add(true);//
											}
											else{
												removeFriendButton2.setText("-");///////
												//isAdmin.set(view.getId(), false);//////////
												removeFriendButton2.setTextColor(Color.parseColor("#ffcc0000"));//////////
												added.add(text); role.add(false);//
											}
											
											
											rowView2.setId(view.getId());
											((Button) rowView2.findViewById(R.id.friendNameButtonNoAccess)).setText(text);
											rowView2.findViewById(R.id.friendNameButtonNoAccess).setVisibility(1);
											membersToAdd2.addView(rowView2);
											alreadyAdded.add(text);
										}
									}
								}
							});
							friendNameButton.setText(row);

							friendNameButton.setId(i);
							rowView.setId(i);
							membersToAdd.addView(rowView);
							//membersToAdd2.addView(rowView);
							// System.out.println("Row: " + row +"\nCount: " +
							// i);

						}

					}
				}
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					LinearLayout membersToAdd = (LinearLayout) findViewById(R.id.linearLayoutNested1);

					View row = inflater.inflate(R.layout.listitem_friend_noaccess, null);

					// GridLayout rowRL =
					// (GridLayout)currentFriendsRL.findViewById(R.id.friendGridLayout);
					// rowRL.setId(0);//(newIDStr);

					String message = jsonObject.getString("message").toString();
					((Button) row.findViewById(R.id.friendNameButtonNoAccess)).setText(message);
					row.findViewById(R.id.removeFriendButtonNoAccess).setVisibility(1);
					// ((TextView)rowRL.findViewById(R.id.friendTextView)).setText(message);
					membersToAdd.addView(row);
					// int y = 100*(0+1);
					// rowRL.setY(y);
				} else
				{
					// failed
					// TextView loginFail = (TextView)
					// findViewById(R.id.loginFailTextViewLA);
					// loginFail.setVisibility(0);
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", "" + e.getLocalizedMessage());
			}
		
		
			/* begin building the interface */
			/*
			LinearLayout membersToAdd = (LinearLayout)findViewById(R.id.linearLayoutNested2);
			String[] members = {"Bobby Hill", "Peggy Hill", "Hank Hill"};
			
			for(int i = 0; i < 3; i++){
				GridLayout gridView;
				gridView = (GridLayout)inflater.inflate(R.layout.listitem_friend, null);
				
				Button removeFriendButton = (Button)gridView.findViewById(R.id.removeFriendButton);
				//removeFriendButton.setVisibility(1);
				removeFriendButton.setId(i);
				
				Button friendNameButton = (Button)gridView.findViewById(R.id.friendNameButton);
				friendNameButton.setText(members[i]);
				
				friendNameButton.setId(i);
				gridView.setId(i);
				membersToAdd.addView(gridView);
			}
			*/
			/* end building the interface */
		
		}
	}
	
	
	
	
	
	
	
	
	
	public void toggleAdmin(View view){
		
		//button.setText("+");
		//button.setTextColor(Color.parseColor("#FFFFFF"));
		//final int idz = view.getId();
		//final String 
		//Button button = (Button)findViewById(R.id.removeFriendButtonNoAccess);
		//Log.d("message", "toggle the Admin " + button);
		//view.setBackgroundColor(Color.parseColor("#FFFFFF"));
	}
	
	public void addToGroupTable(View view){
		
	}
	
	/* Start activity function for going back and logging out */
	public void startFriendsActivity(View view)
	{
		Intent intent = new Intent(this, FriendsActivity.class);
		startActivity(intent);
		finish();
	}
	
	public void startGroupsActivity(View view)
	{
		Intent intent = new Intent(this, GroupsActivity.class);
		startActivity(intent);
		finish();
	}

	public void removeFriendButton(View view)
	{

		final int idz = view.getId(); // Email of user
		final String friendEmail = friendsEmailList.get(idz); // Email of friend
																// to remove
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
						LinearLayout groupCreateLayout = (LinearLayout) findViewById(R.id.linearLayoutNested1);
						groupCreateLayout.removeAllViews();
						// calling getFriends to repopulate view
						new GroupMembers()
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
