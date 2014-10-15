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
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Global extends Application
{
	private String currentUser;
	private String acceptEmail;
	private String declineEmail;
	private String numFriendRequests = "";
	
	public String getCurrentUser()
	{
		return currentUser;
	}
	public void setCurrentUser(String email)
	{
		currentUser = email;
	}
	public void setNumFriendRequests(String num)
	{
		numFriendRequests = num;
	}
	
	public String getNumFriendRequests()
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
	
	public void doStuff()
	{
		new getFriendRequestsTask()
		.execute("http://98.213.107.172/android_connect/get_friend_requests.php?receiver="
				+ getCurrentUser());
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
			
			try
			{
			
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					ArrayList<String> senders = new ArrayList<String>();
					JSONArray jsonSenders = (JSONArray)jsonObject.getJSONArray("senders").getJSONArray(0);
					System.out.println("Fox in the fence");
					if (jsonSenders != null)
					{
						System.out.println("Fox in the IF json != null " + jsonSenders.length());
						setNumFriendRequests(Integer.toString(jsonSenders.length()));
					}
					else
					{
						System.out.println("jsonSenders = null");
						setNumFriendRequests("");
					}
					//successful
					
				} else
				{
					setNumFriendRequests("");
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
}
