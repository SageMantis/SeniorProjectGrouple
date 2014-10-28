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

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Global extends Application
{
	private String currentUser;
	private String acceptEmail;
	private String declineEmail;
	private String name;
	private int numFriendRequests = 0;
	
	public String getCurrentUser()
	{
		return currentUser;
	}
	
	public void setCurrentUser(String email)
	{
		currentUser = email;
	}
	
	public void setNumFriendRequests(int num)
	{
		numFriendRequests = num;
		System.out.println("Friend requests: " + num);
		//Setting that userNotification
	}
	
	public int getNumFriendRequests()
	{
		return numFriendRequests;
	}
	
	/*PANDA*/
	public void setDeclineEmail(String email)
	{
		declineEmail = email;
	}
	
	public String getDeclineEmail()
	{
		return declineEmail;
	}
	
	public void setAcceptEmail(String email)
	{
		acceptEmail = email;
	}
	
	public String getAcceptEmail()
	{
		return acceptEmail;
	}
	
	public int getUserNotificationNum()
	{
		System.out.println("In the get:" +numFriendRequests);
		return numFriendRequests; //+ new messages num ... (when implemented)
	}
	
	public void setNotifications(View view)
	{
		//View homeRL = findViewById(R.id.homeRelativeLayout);
		Global global = ((Global)getApplicationContext());
		int userNotificationNum = global.getUserNotificationNum();
		
		if (userNotificationNum > 0) //= for testing cause no one likes me
		{
			TextView userNotification = (TextView)view.findViewById(R.id.userNotificationTextView);
			userNotification.setText(Integer.toString(userNotificationNum));
			userNotification.setVisibility(0);
		}
		if (numFriendRequests > 0 && (view.findViewById(R.id.friendRequestsButtonFA) != null))
		{
			Button friendRequestsButton = (Button)view.findViewById(R.id.friendRequestsButtonFA);
			friendRequestsButton.setText("Friend Requests (" + Integer.toString(numFriendRequests) + ")");
		}
		//else do nothing, keep that invisible
	}	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public void fetchNumFriendRequests()
	{
		new getFriendRequestsTask()
		.execute("http://98.213.107.172/android_connect/get_friend_requests.php?receiver="
				+ getCurrentUser());
	}
	
	public String readFriendRequestsJSONFeed(String URL)
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
			return readFriendRequestsJSONFeed(urls[0]);
		}

		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					JSONArray jsonSenders = (JSONArray)jsonObject.getJSONArray("senders").getJSONArray(0);
					if (jsonSenders != null)
					{
						setNumFriendRequests(jsonSenders.length());
					}
					else
					{
						setNumFriendRequests(0);
					}
					//successful		
				} 
				else
				{
					setNumFriendRequests(0);
				}
			} 
			catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	//Get name
	public void fetchName()
	{
		//new getNameTask()
		//.execute("http://98.213.107.172/android_connect/get_friend_requests.php?email="
		//		+ getCurrentUser());
	}
	
	
	public String readNamesJSONFeed(String URL)
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

	private class getNameTask extends AsyncTask<String, Void, String>
	{
		protected String doInBackground(String... urls)
		{
			return readNamesJSONFeed(urls[0]);
		}

		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// successful
					//name = ???
				} 
				else
				{
					// failed
					
				}
			} 
			catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	
}
