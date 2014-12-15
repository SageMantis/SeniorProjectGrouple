package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import cs460.grouple.grouple.R;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendProfileActivity extends ActionBarActivity
{
	//Set up the fields.
	BroadcastReceiver broadcastReceiver;
	private Bitmap bmp;
	private ImageView iv;
	Intent parentIntent;
	Intent upIntent;
	String profileName;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_profile);

		// actionbarTitle.setText(global.getName()+"'s Profile");
		// This where we add our friends email. not ours.
		//Bundle extras = getIntent().getExtras();
		//String email = extras.getString("email");

		//grabbing current container to load
		View friendProfile = findViewById(R.id.friendProfileContainer);
		load(friendProfile);

	}
	
	public void initActionBar()
	{
		Global global = ((Global) getApplicationContext());
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);

		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText(global.getName() + "'s Profile");
		
		//handling up navigation
		ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);
		upButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				upIntent.putExtra("up", "true");
				startActivity(upIntent);
				finish();
			}
		});
	}
	
	/* loading in necessary components of the friend profile */
	public void load(View view)
	{
		Global global = ((Global) getApplicationContext());

		//backstack of intents
		//each class has a stack of intents lifo method used to execute them at start of activity
		//intents need to include everything like ParentClassName, things for current page (email, ...)
		//if check that friends
		Intent intent = getIntent();
		Bundle extras = getIntent().getExtras();
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
			//use info from this intent
		}	
		Bundle parentExtras = parentIntent.getExtras();	
		String className = parentExtras.getString("ParentClassName");
		String email = parentExtras.getString("email");
		System.out.println("In FPA GlobalUser: " + global.getCurrentUser() + "intentEmail:" + email);
		global.fetchName(email);
		global.fetchNumFriends(email);
		global.fetchNumGroups(email);
		global.setNotifications(view);
		
		try
		{
			upIntent = new Intent(this, Class.forName("cs460.grouple.grouple."
					+ className));
		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		global.setNotifications(view);
		
		// execute php script, using the current users email address to populate
		// the textviews
		new getProfileTask()
				.execute("http://98.213.107.172/android_connect/get_profile.php");
		
		//load in actionbar
		initActionBar();
		//starting killswitch listener
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
		// getMenuInflater().inflate(R.menu.friend_profile, menu);
		getMenuInflater().inflate(R.menu.navigation_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		Global global = ((Global) getApplicationContext());
		int id = item.getItemId();
		if (id == R.id.action_logout)
		{
			global.setAcceptEmail("");
			global.setCurrentUser("");
			global.setDeclineEmail("");
			Intent login = new Intent(this, LoginActivity.class);
			startActivity(login);
			bmp = null;
			iv = null;
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		if (id == R.id.action_home)
		{
			Intent intent = new Intent(this, HomeActivity.class);
			intent.putExtra("up", "false");
			intent.putExtra("ParentClassName", "FriendProfileActivity");
			global.addToParentStackFriendProfile(parentIntent);
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

	/* Start activity functions for going back to home and logging out */
	public void startHomeActivity(View view)
	{
		Global global = ((Global) getApplicationContext());
		Intent intent = new Intent(this, HomeActivity.class);
		intent.putExtra("ParentClassName", "FriendProfileActivity");
		intent.putExtra("up", "false");
		global.addToParentStackFriendProfile(parentIntent);
		startActivity(intent);
		bmp = null;
		iv = null;
	}



	public void startEditProfileActivity(View view)
	{
		Global global = ((Global) getApplicationContext());
		Intent intent = new Intent(this, EditProfileActivity.class);
		intent.putExtra("up", "false");
		intent.putExtra("ParentClassName", "FriendProfileActivity");
		global.addToParentStackFriendProfile(parentIntent);
		startActivity(intent);
		bmp = null;
		iv = null;
	}

	/*
	 * Get profile executes get_profile.php. It uses the current users email
	 * address to retrieve the users name, age, and bio.
	 */
	private class getProfileTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			Bundle extras = getIntent().getExtras();
			String email = extras.getString("email");

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
					1);
			nameValuePairs.add(new BasicNameValuePair("email", email));
			return global.readJSONFeed(urls[0], nameValuePairs);
		}
		/*
		 * On success, we get the friend's profile and set it to the text views in the profile.
		 * On fail, the profile is not loaded. It can be a connection error or incorrect email address for the php. 
		 */
		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				System.out.println(jsonObject.getString("success"));
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// Success
					JSONArray jsonProfileArray = jsonObject.getJSONArray("profile");
					//Get the profile information
					String first = jsonProfileArray.getString(0);
					String last = jsonProfileArray.getString(1);
					first = first.substring(0, 1).toUpperCase()+ first.substring(1);
					last = last.substring(0, 1).toUpperCase()+ last.substring(1);

					profileName = first + " " + last;
					String age = jsonProfileArray.getString(2);
					String bio = jsonProfileArray.getString(3);
					String location = jsonProfileArray.getString(4);
					String img = jsonProfileArray.getString(5);

					// decode image back to android bitmap format
					byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
					Log.d("ReadatherJSONFeedTask", "1");
					if (decodedString != null)
					{
						Log.d("ReadatherJSONFeedTask", "2");
						bmp = BitmapFactory.decodeByteArray(decodedString, 0,
								decodedString.length);
					}
					// set the image
					if (bmp != null)
					{
						if (iv == null)
						{
							Log.d("ReadatherJSONFeedTask", "3");
							iv = (ImageView) findViewById(R.id.profilePhotoFPA);
						}
						Log.d("ReadatherJSONFeedTask", "4");
						img = null;
						decodedString = null;
					}

					TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
					actionbarTitle.setText(profileName + "'s Profile");
					TextView ageTextView = (TextView) findViewById(R.id.ageTextViewFPA);
					TextView locationTextView = (TextView) findViewById(R.id.locationTextViewFPA);
					TextView bioTextView = (TextView) findViewById(R.id.bioTextViewFPA);

					// We only want to add the profile details if the user filled them out.
					//if it's not null, then add the profile info to the the corresponding text views.
					if (!age.equalsIgnoreCase("null"))
					{
						ageTextView.setText(age + " years old");
					} else
					{
						ageTextView.setText("");
					}
					if (!bio.equalsIgnoreCase("null"))
					{
						bioTextView.setText(bio);
					}
					if (!(location.equalsIgnoreCase("") || location
							.equalsIgnoreCase("null")))
					{
						locationTextView.setText(location);
					} else
					{
						locationTextView.setText("");
					}
					iv.setImageBitmap(bmp);

				} else
				{
					// Fail
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", "poop2");
			}
		}
	}
	//Method that handles starting the current groups from the user's profile.
	public void startGroupsCurrentActivity(View view)
	{
		Global global = ((Global) getApplicationContext());
		Bundle extras = parentIntent.getExtras();
		Intent intent = new Intent(this, GroupsCurrentActivity.class);
		intent.putExtra("email", extras.getString("email"));
		intent.putExtra("up", "false");
		intent.putExtra("ParentClassName", "FriendProfileActivity");
		intent.putExtra("ParentEmail", extras.getString("email"));
		intent.putExtra("mod", "false");
		global.addToParentStackFriendProfile(parentIntent);
		startActivity(intent);
		bmp = null;
		iv = null;
	}
	//Method that handles starting the current friends from the user's profile.
	public void startCurrentFriendsActivity(View view)
	{
		Global global = ((Global) getApplicationContext());
		Intent intent = new Intent(this, CurrentFriendsActivity.class);
		Bundle extras = parentIntent.getExtras();
		String email = extras.getString("email");
		intent.putExtra("Name", profileName);
		intent.putExtra("email", email);
		intent.putExtra("up", "false");
		intent.putExtra("ParentClassName", "FriendProfileActivity");
		intent.putExtra("mod", "false");
		startActivity(intent);
		global.addToParentStackFriendProfile(parentIntent);
		bmp = null;
		iv = null;
	}
	//Method that handles starting the current events from the user's profile.
	public void startEventsActivity(View view)
	{
		Global global = ((Global) getApplicationContext());
		Intent intent = new Intent(this, EventsActivity.class);
		intent.putExtra("up", "false");
		intent.putExtra("Name", profileName);
		intent.putExtra("ParentClassName", "FriendProfileActivity");
		intent.putExtra("mod", "false");
		global.addToParentStackFriendProfile(parentIntent);
		startActivity(intent);
		bmp = null;
		iv = null;
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
