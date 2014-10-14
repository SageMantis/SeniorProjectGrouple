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


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FriendRequestsActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_requests);
		if (savedInstanceState == null)
		{
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		
		//display friend requests
		// Create helper and if successful, will bring the correct home
		// activity.
		

		
		Global global = ((Global)getApplicationContext());
		String receiver = global.getCurrentUser();
		System.out.println("Receiver: " + receiver);
		new getFriendRequestsTask()
				.execute("http://98.213.107.172/android_connect/get_friend_requests.php?receiver="
						+ receiver);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friend_requests, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment
	{

		public PlaceholderFragment()
		{
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_friend_requests,
					container, false);
			return rootView;
		}
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
	private class getFriendRequestsTask extends AsyncTask<String, Void, String>
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
					ArrayList<String> senders = new ArrayList<String>();
					JSONArray jsonSenders = (JSONArray)jsonObject.getJSONArray("senders").getJSONArray(0);
					
					if (jsonSenders != null)
					{
						System.out.println(jsonSenders.toString() + "\n" + jsonSenders.length());
		
						//looping thru json and adding to an array
						for (int i = 0; i < jsonSenders.length(); i++)
						{					
							String raw = jsonSenders.get(i).toString().replace("\"","").replace("]", "").replace("[", "");
							String row = raw.substring(0,1).toUpperCase() + raw.substring(1);
							senders.add(row);
							System.out.println("Row: " + row +"\nCount: " + i);
						}
						//looping thru array and inflating listitems to the friend requests list
						for (int i = 0; i < senders.size(); i++)
						{
							RelativeLayout friendRequestsRL =  (RelativeLayout)findViewById(R.id.friendRequestsRelativeLayout);
							String newIDStr = "friendRequestRelativeLayout_" +  i;
						
							
							li.inflate(R.layout.listitem_friend_request, friendRequestsRL);
							GridLayout rowRL = (GridLayout)friendRequestsRL.findViewById(R.id.friendRequestGridLayout);
							rowRL.setId(i);//(newIDStr);
							((TextView)rowRL.findViewById(R.id.emailTextViewFRLI)).setText(senders.get(i));
					
							int y = 120*(i+1);
							rowRL.setY(y);
						}
					}
					//successful
					//startHomeActivity();
				} else
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
	
	/*
	 ****************************************************************************************** 
	 ******************************************************************************************
	 ****************************************************************************************** 
	 ****************************************************************************************** 
	 ****************************************************************************************** 
	 **********************************DECLINE CODE********************************************
	 ****************************************************************************************** 
	 ****************************************************************************************** 
	 ****************************************************************************************** 
	 ******************************************************************************************
	 ****************************************************************************************** 
	 ******************************************************************************************  
	 */
	public void onClick(View view)
	{
		Global global = ((Global)getApplicationContext());
		switch (view.getId())
		{
		case R.id.declineFriendRequestButtonFRLI:
			View parent = (View) view.getParent();
			TextView declineEmail = (TextView)parent.findViewById(R.id.emailTextViewFRLI);
			global.setDeclineEmail(declineEmail.getText().toString());
			System.out.println(declineEmail.getText().toString());
			System.out.println("decline pressed\nsender:" + declineEmail.getText().toString() + "\nreceiver:" + global.getCurrentUser());
		
			new getDeclineFriendTask()
			.execute("http://98.213.107.172/android_connect/decline_friend_request.php");
			break;
		case R.id.acceptFriendRequestButtonFRLI:
			View parent2 = (View) view.getParent();
			TextView acceptEmail = (TextView)parent2.findViewById(R.id.emailTextViewFRLI);
			System.out.println("accept pressed");
			global.setAcceptEmail(acceptEmail.getText().toString());
			new getAcceptFriendTask()
			.execute("http://98.213.107.172/android_connect/accept_friend_request.php");
			break;
		}	
	}
	
	private class getDeclineFriendTask extends AsyncTask<String, Void, String>
	{
		protected String doInBackground(String... urls)
		{
			return readJSONFeedDecline(urls[0]);
		}

		protected void onPostExecute(String result)
		{
			Global global = ((Global)getApplicationContext());
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				System.out.println(jsonObject.getString("success"));
				if (jsonObject.getString("success").toString().equals("1"))
				{
					
					System.out.println("success in decline!");
					startFriendRequestsActivity();
			
				} else
				{
					// failed
					System.out.println("fail!");
					//TextView addFriendMessage = (TextView) findViewById(R.id.addFriendMessageTextViewAFA);
					//addFriendMessage.setText("User not found.");
					//addFriendMessage.setVisibility(0);
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	public String readJSONFeedDecline(String URL)
	{
		// Get all the fields and store locally
		TextView emailTextView = (TextView) findViewById(R.id.emailTextViewFRLI);
		Global global = ((Global)getApplicationContext());
		String receiver = global.getCurrentUser();
		String sender = global.getDeclineEmail();
		System.out.println("Send: " + sender + "\nRec: " + receiver);

	

		StringBuilder stringBuilder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(URL);
		try
		{
			// Add your data
			System.out.println("Receiver Email: " + receiver + "Sender Email: " + sender);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("sender", sender));
			nameValuePairs.add(new BasicNameValuePair("receiver", receiver));
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
	
	/*
	 ****************************************************************************************** 
	 ******************************************************************************************
	 ****************************************************************************************** 
	 ****************************************************************************************** 
	 ****************************************************************************************** 
	 ***********************ACCEPT CODE********************************************************
	 ****************************************************************************************** 
	 ****************************************************************************************** 
	 ****************************************************************************************** 
	 ******************************************************************************************
	 ****************************************************************************************** 
	 ******************************************************************************************  
	 */
	
	private class getAcceptFriendTask extends AsyncTask<String, Void, String>
	{
		protected String doInBackground(String... urls)
		{
			return readJSONFeedAccept(urls[0]);
		}

		protected void onPostExecute(String result)
		{
			Global global = ((Global)getApplicationContext());
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				System.out.println(jsonObject.getString("success"));
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// successful
					System.out.println("success!");
	
					startFriendRequestsActivity();
					
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
	
	public String readJSONFeedAccept(String URL)
	{
		Global global = ((Global)getApplicationContext());
	
		
		String receiver = global.getCurrentUser();
		String sender = global.getAcceptEmail();
		System.out.println("Send: " + sender + "\nRec: " + receiver);

	

		StringBuilder stringBuilder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(URL);
		try
		{
			// Add your data
			System.out.println("Receiver Email: " + receiver + "Sender Email: " + sender);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("sender", sender));
			nameValuePairs.add(new BasicNameValuePair("receiver", receiver));
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
	
	public void startFriendRequestsActivity()
	{
		Intent intent = new Intent(this, FriendRequestsActivity.class);
		startActivity(intent);
	}
}


	
	

