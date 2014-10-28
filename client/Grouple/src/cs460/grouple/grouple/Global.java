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
	private int numFriendRequests = 0;
	private ArrayList<View> views; //All of our views
	
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
		System.out.println("In the get:" + numFriendRequests);
		return numFriendRequests; //+ new messages num ... (when implemented)
	}
	
	public void setNotifications(View view)
	{
		int userNotificationNum = getUserNotificationNum();
		//View homeRL = findViewById(R.id.homeRelativeLayout);
	
		if (userNotificationNum > 0) //user has notifications in their profile
		{
			TextView userNotification = (TextView)view.findViewById(R.id.userNotificationTextView);
			userNotification.setText(Integer.toString(userNotificationNum));
			userNotification.setVisibility(0);
		}
		if (view.findViewById(R.id.friendRequestsButtonFA) != null)
		{
			Button friendRequestsButton = (Button)view.findViewById(R.id.friendRequestsButtonFA);
			friendRequestsButton.setText("Friend Requests (" + Integer.toString(numFriendRequests) + ")");
		}
		else if (userNotificationNum == 0)
		{
			TextView userNotification = (TextView)view.findViewById(R.id.userNotificationTextView);
			userNotification.setText("0");
			userNotification.setVisibility(1);
		}


		//else do nothing, keep that invisible
	}	
	
	//for the attempted implementation of an all in one notification setting
	public void setViews(ArrayList<View> views)
	{
		for (View v : views)
		{
			this.views.add(v);
		}
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
}
