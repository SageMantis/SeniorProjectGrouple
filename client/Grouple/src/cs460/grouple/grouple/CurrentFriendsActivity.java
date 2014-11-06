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
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class CurrentFriendsActivity extends ActionBarActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_current_friends);
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(true);
		TextView actionbarTitle = (TextView)findViewById(R.id.actionbarTitleTextView);
		actionbarTitle.setText("Brett's Friends");
		
		Global global = ((Global)getApplicationContext());
		//Grab view
		View currentFriends = findViewById(R.id.currentFriendsLayout);
		try {
			global.fetchNumFriendRequests();
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		global.setNotifications(currentFriends); //PANDA TEST
		String email = global.getCurrentUser();
		System.out.println("Email: " + email);
		new getFriendsTask()
				.execute("http://98.213.107.172/android_connect/get_friends_firstlast.php?email="
						+ email);
		
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
			Global global = ((Global)getApplicationContext());
			global.setAcceptEmail("");
			global.setCurrentUser("");
			global.setDeclineEmail("");
			startLoginActivity(null);
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public String readJSONFeed(String URL)
	{
		StringBuilder stringBuilder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(URL);
		try
		{
			HttpResponse response = httpClient.execute(httpGet);
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
					System.out.println("New line: " + line);
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
	private class getFriendsTask extends AsyncTask<String, Void, String>
	{
		protected String doInBackground(String... urls)
		{
			return readJSONFeed(urls[0]);
		}

		protected void onPostExecute(String result)
		{
			LayoutInflater li = getLayoutInflater();
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					ArrayList<String> friends = new ArrayList<String>();
					JSONArray jsonFriends = (JSONArray)jsonObject.getJSONArray("friends");
					
					if (jsonFriends != null)
					{
						System.out.println(jsonFriends.toString() + "\n" + jsonFriends.length());
						//looping thru json and adding to an array
						for (int i = 0; i < jsonFriends.length(); i++)
						{			
							String firstraw = jsonFriends.getJSONObject(i).getString("first");
							String lastraw = jsonFriends.getJSONObject(i).getString("last");
							String row = firstraw.substring(0,1).toUpperCase() + firstraw.substring(1);
							row = row + " ";
							row = row + lastraw.substring(0,1).toUpperCase() + lastraw.substring(1);
							
							//Do not need to replace out double quotes or brackets
							//String raw = jsonFriends.get(i).toString().replace("\"","").replace("]", "").replace("[", "");
							
							//String raw = jsonFriends.get(i).toString();
							//String row = raw.substring(0,1).toUpperCase() + raw.substring(1);
							friends.add(row);
							//System.out.println("Row: " + row +"\nCount: " + i);
							
						}
						//looping thru array and inflating listitems to the friend requests list
						RelativeLayout currentFriendsRL =  (RelativeLayout)findViewById(R.id.currentFriendsLayout);
						//ScrollView sv = new ScrollView();
						for (int i = 0; i < friends.size(); i++)
						{
						
							li.inflate(R.layout.listitem_friend, currentFriendsRL);
	
							GridLayout rowRL = (GridLayout)currentFriendsRL.findViewById(R.id.friendGridLayout);
							rowRL.setId(i);//(newIDStr);
							((TextView)rowRL.findViewById(R.id.emailTextViewFLI)).setText(friends.get(i));
					
							int y = (100*i)+10;
							rowRL.setY(y);
						}
					}
					//successful
					//startHomeActivity();
					
				
				}
				//user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					RelativeLayout currentFriendsRL =  (RelativeLayout)findViewById(R.id.currentFriendsLayout);
					
					li.inflate(R.layout.listitem_friend, currentFriendsRL);
					GridLayout rowRL = (GridLayout)currentFriendsRL.findViewById(R.id.friendGridLayout);
					rowRL.setId(0);//(newIDStr);
					
					String message = jsonObject.getString("message").toString();
					((TextView)rowRL.findViewById(R.id.emailTextViewFLI)).setText(message);
			
					int y = 100*(0+1);
					rowRL.setY(y);
				}
				else
				{
					// failed
					//TextView loginFail = (TextView) findViewById(R.id.loginFailTextViewLA);
					//loginFail.setVisibility(0);
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
	    if(keyCode == KeyEvent.KEYCODE_BACK)
	    {
	        startFriendsActivity(null);
	    }
	    return false;
	}
	
	/*Start activity function for going back and logging out*/
	public void startFriendsActivity(View view)
	{
		Intent intent = new Intent(this, FriendsActivity.class);
		startActivity(intent);
	}
	public void startLoginActivity(View view)
	{
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
}
