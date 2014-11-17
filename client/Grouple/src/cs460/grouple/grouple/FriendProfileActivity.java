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

import cs460.grouple.grouple.R;
//import cs460.grouple.grouple.EditProfileActivity.getProfileTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import android.support.v4.app.Fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendProfileActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_profile);
		
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(true);
		TextView actionbarTitle = (TextView)findViewById(R.id.actionbarTitleTextView);
		//Global global = ((Global)getApplicationContext());
		//actionbarTitle.setText(global.getName()+"'s Profile");
		
		//Setting num friends on friends button
		Button friendsButton = (Button)findViewById(R.id.friendsButtonUPA);
		//global.fetchNumFriends();
		//friendsButton.setText("Friends\n("+global.getNumFriends()+")");
		View user = findViewById(R.id.userLayout);
		
		//execute php script, using the current users email address to populate the textviews
		new getProfileTask().execute("http://98.213.107.172/android_connect/get_profile.php");
		
		//global.fetchNumFriendRequests();
		//global.setNotifications(user);
	
		
		//START KILL SWITCH LISTENER
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("CLOSE_ALL");
		BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
		    // close activity
			  if(intent.getAction().equals("CLOSE_ALL"))
			  {
				  Log.d("app666","we killin the login it");
				  //System.exit(1);
				  finish();
			  }
			  
		  }
		};
		registerReceiver(broadcastReceiver, intentFilter);
		//End Kill switch listener
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.friend_profile, menu);
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
			Global global = ((Global)getApplicationContext());
			global.setAcceptEmail("");
			global.setCurrentUser("");
			global.setDeclineEmail("");
			//startLoginActivity(null);
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/*public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            //startHomeActivity(null);
        }
        return false;
    }*/
	
	/*Start activity functions for going back to home and logging out*/
	public void startHomeActivity(View view)
	{
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
	}
	public void startLoginActivity(View view)
	{
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}	
	public void startEditProfileActivity(View view)
	{
		Intent intent = new Intent(this, EditProfileActivity.class);
		startActivity(intent);
	}
	
	/*
	 * Get profile executes get_profile.php. It uses the current users email address to retrieve the users name, age, and bio. 
	 */
	private class getProfileTask extends AsyncTask<String, Void, String>
	{

		protected String doInBackground(String... urls)
		{

			return readJSONFeed(urls[0]);
		}
		public String readJSONFeed(String URL)
		{

			StringBuilder stringBuilder = new StringBuilder();
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(URL);
			try
			{
				//This where we add our friends email. not ours.
				Bundle extras = getIntent().getExtras();
				String email = extras.getString("email");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
				nameValuePairs.add(new BasicNameValuePair("email", email));
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

		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				System.out.println(jsonObject.getString("success"));
				if (jsonObject.getString("success").toString().equals("1"))
				{
					//Success
					JSONArray jsonProfileArray = (JSONArray)jsonObject.getJSONArray("profile");
					
					String name = jsonProfileArray.getString(0)+" "+jsonProfileArray.getString(1);
					String age = jsonProfileArray.getString(2);
					String bio = jsonProfileArray.getString(3);
					String location = jsonProfileArray.getString(4);
					
					//TextView nameTextView = (TextView) findViewById(R.id.nameEditTextEPA);
					TextView ageTextView = (TextView) findViewById(R.id.ageTextViewFPA);
					TextView locationTextView = (TextView) findViewById(R.id.locationTextViewFPA);
					TextView bioTextView = (TextView) findViewById(R.id.bioTextViewFPA);
					//JSONObject bioJson = jsonProfileArray.getJSONObject(0);
					//nameTextView.setText(name);
					
					//We only want to add the profile details if the user filled them out.
					if(!age.equalsIgnoreCase("null"))
					{
						ageTextView.setText(age+" years old");
					}
					if(!bio.equalsIgnoreCase("null"))
					{
						bioTextView.setText(bio);
					}
					if(!location.equalsIgnoreCase(""))
					{
						locationTextView.setText(location);
					}
					
				} 
				else
				{
					//Fail
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	
	public void startGroupsActivity(View view)
	{
		Intent intent = new Intent(this, GroupsActivity.class);
		startActivity(intent);
	}
	public void startCurrentFriendsActivity(View view)
	{
		Intent intent = new Intent(this, CurrentFriendsActivity.class);
		startActivity(intent);
	}
	public void startEventsActivity(View view)
	{
		Intent intent = new Intent(this, EventsActivity.class);
		startActivity(intent);
	}
	
	
}