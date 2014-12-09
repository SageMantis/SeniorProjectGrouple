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
import org.json.JSONObject;
import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class Global extends Application {
	private String currentUser;
	private String acceptEmail;
	private String declineEmail;
	private String name;
	private int numFriendRequests;
	private int numFriends;
	private int numGroupInvites;
	private int numGroups;


	public String getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(String email) {
		currentUser = email;
	}

	public void setNumFriendRequests(int num) {
		numFriendRequests = num;
		// System.out.println("Friend requests: " + num);
		// Setting that userNotification
	}
	
	public void setNumGroupInvites(int num)
	{
		numGroupInvites = num;
	}
	
	public void setNumGroups(int num)
	{
		numGroups = num;
	}

	public int getNumFriendRequests() {
		return numFriendRequests;
	}
	
	public int getNumGroupInvites()
	{
		return numGroupInvites;
	}

	public int getNumFriends() {
		return numFriends;
	}
	
	public int getNumGroups()
	{
		return numGroups;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/* PANDA */
	public void setDeclineEmail(String email) {
		declineEmail = email;
	}

	public String getDeclineEmail() {
		return declineEmail;
	}

	public void setAcceptEmail(String email) {
		acceptEmail = email;
	}

	public String getAcceptEmail() {
		return acceptEmail;
	}

	public void setNumFriends(int numFriends) {
		this.numFriends = numFriends;
	}

	public void setNotifications(View view) {
		//todo: think if I can pass an email in here and skip other steps
		int numFriendRequests = getNumFriendRequests();
		int numFriends = getNumFriends();
		int numGroupInvites = getNumGroupInvites();
		int numGroups = getNumGroups();
		Button friendsButton = (Button) view.findViewById(R.id.friendsButtonHA);

		//Friends Activity
		if (view.findViewById(R.id.friendRequestsButtonFA) != null && view.findViewById(R.id.currentFriendsButtonFA) != null) {
			Button friendRequestsButton = (Button) view
					.findViewById(R.id.friendRequestsButtonFA);
			friendRequestsButton.setText("Friend Requests ("
					+ Integer.toString(numFriendRequests) + ")");
			Button currentFriendsButton = (Button) view
					.findViewById(R.id.currentFriendsButtonFA);
			currentFriendsButton.setText("My Friends ("
					+ getNumFriends() + ")");
		} 
		
		//User Profile Buttons
		if ((view.findViewById(R.id.friendsButtonUPA) != null) && (view.findViewById(R.id.groupsButtonUPA) != null) && (view.findViewById(R.id.eventsButtonUPA) != null))
		{
			((Button)view.findViewById(R.id.friendsButtonUPA)).setText("Friends\n(" + numFriends + ")");
			
			//set numfriends, numgroups, and numevents
		}
		
		//User Profile Buttons
		if ((view.findViewById(R.id.friendsButtonFPA) != null) && (view.findViewById(R.id.groupsButtonFPA) != null) && (view.findViewById(R.id.eventsButtonFPA) != null))
		{
			//set numfriends, numgroups, and numevents
		}
		
		//Friends button
		if (numFriendRequests > 0
				&& view.findViewById(R.id.friendsButtonHA) != null) 
		{
			if (numFriendRequests == 1) {
				friendsButton.setText("Friends \n(" + numFriendRequests
						+ " request)");
			} else {
				friendsButton.setText("Friends \n(" + numFriendRequests
						+ " requests)");
			}
		}else if (numFriendRequests == 0
				&& view.findViewById(R.id.friendsButtonHA) != null) {
			friendsButton.setText("Friends");

		}

		//Groups activity
		if (view.findViewById(R.id.pendingGroupsButton) != null)
		{
			((Button) view.findViewById(R.id.pendingGroupsButton)).setText("Group Invites (" + numGroupInvites + ")");
		}
		if (view.findViewById(R.id.yourGroupsButton) != null)
		{
			((Button) view.findViewById(R.id.yourGroupsButton)).setText("My Groups (" + numGroups + ")");
		}
		
		// else do nothing, keep that invisible
	}

	/* Takes in email of user, and sets the friend requests */
	//Should switch this to return the number
	public void fetchNumFriendRequests(String email) 
	{
		new getNumFriendRequestsTask()
				.execute("http://98.213.107.172/android_connect/get_count_friend_requests.php?email="
						+ email);
	}


	private class getNumFriendRequestsTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... urls) {
			return readJSONFeed(urls[0], null);
		}

		protected void onPostExecute(String result) {
			try {
				JSONObject jsonObject = new JSONObject(result);

				if (jsonObject.getString("success").toString().equals("1")) {
					System.out.println("Just success on json return");
					String numRequests =  jsonObject.getString("numRequests");
					System.out.println("Set it to " + numRequests);
					setNumFriendRequests(Integer.parseInt(numRequests));
					System.out.println("Number of Friend Requests: " + numRequests);
					
					// successful
				} else {
					//fetching from server failed
					Log.d("DB Error", "Error fetching number of requests from server");
					setNumFriendRequests(0);
				}
			} catch (Exception e) {
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	// Get numFriends, TODO: work on returning the integer
	public void fetchNumFriends(String email) {
		new getFriendsTask()
				.execute("http://98.213.107.172/android_connect/get_count_friends.php?email="
						+ email);
	}

	private class getFriendsTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... urls) {
			return readJSONFeed(urls[0], null);
		}
		protected void onPostExecute(String result) {

			try {
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1")) {
					String numFriends = jsonObject.getString("numFriends").toString();
					System.out
							.println("Should be setting the num friends to " + numFriends);
					setNumFriends(Integer.parseInt(numFriends));
	
				}
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2")) {
					setNumFriends(0);
				}
			} catch (Exception e) {
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	public void fetchNumGroupInvites(String email) // Should take in email
	{
		new getNumGroupInvitesTask()
				.execute("http://98.213.107.172/android_connect/get_count_group_invites.php?email="
						+ email);
	}
	
	private class getNumGroupInvitesTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... urls) {
			return readJSONFeed(urls[0], null);
		}

		protected void onPostExecute(String result) {
			try {
				JSONObject jsonObject = new JSONObject(result);

				if (jsonObject.getString("success").toString().equals("1")) {
					System.out.println("Just success on json return");
					String numRequests =  jsonObject.getString("numGroups");
					System.out.println("Set it to " + numRequests); 
					setNumGroupInvites(Integer.parseInt(numRequests));
					System.out.println("Number of Friend Requests: " + numRequests);
					
					// successful
				} else {
					//fetching from server failed
					Log.d("DB Error", "Error fetching num requests from server");
					setNumGroupInvites(0);
				}
			} catch (Exception e) {
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	
	public void fetchNumGroups(String email) // Should take in email
	{
		new getNumGroupInvitesTask()
				.execute("http://98.213.107.172/android_connect/get_count_groups.php?email="
						+ email);
	}
	
	private class getNumGroupsTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... urls) {
			return readJSONFeed(urls[0], null);
		}

		protected void onPostExecute(String result) {
			try {
				JSONObject jsonObject = new JSONObject(result);

				if (jsonObject.getString("success").toString().equals("1")) {
					System.out.println("Just success on json return");
					String numGroups =  jsonObject.getString("numGroups");
					System.out.println("Set it to " + numGroups); 
					setNumGroups(Integer.parseInt(numGroups));
					System.out.println("Number of Groups " + numGroups);
					
					// successful
				} else {
					//fetching from server failed
					Log.d("DB Error", "Error fetching num requests from server");
					setNumGroupInvites(0);
				}
			} catch (Exception e) {
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	
	// Get name, should change to take in email
	public void fetchName() {
		new getNameTask()
				.execute("http://98.213.107.172/android_connect/get_user_by_email.php?email="
						+ getCurrentUser());
	}


	private class getNameTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... urls) {
			return readJSONFeed(urls[0], null);
		}

		protected void onPostExecute(String result) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1")) {
					// successful
					String temp = jsonObject.getString("users");
					// Do not need to replace out double quotes or brackets
					String raw = temp.replace("\"", "").replace("]", "")
							.replace("[", "").replace(",", " ");

					// String raw = jsonFriends.get(i).toString();
					// String row = raw.substring(0,1).toUpperCase() +
					// raw.substring(1);

					setName(raw);
				} else {
					// failed

				}
			} catch (Exception e) {
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	
	public String readJSONFeed(String URL, List<NameValuePair> nameValuePairs) {
		StringBuilder stringBuilder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		
		if (nameValuePairs == null)
		{
			HttpGet httpGet = new HttpGet(URL);

			try {
				HttpResponse response = httpClient.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream inputStream = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(inputStream));
					String line;
					while ((line = reader.readLine()) != null) {
						System.out.println("New line: " + line);
						stringBuilder.append(line);
					}
					inputStream.close();
				} else {
					Log.d("JSON", "Failed to download file");
				}
			} catch (Exception e) {
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}
			
		}
		
		else
		{
			
			HttpPost httpPost = new HttpPost(URL);
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpClient.execute(httpPost);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream inputStream = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(inputStream));
					String line;
					while ((line = reader.readLine()) != null) {
						stringBuilder.append(line);
					}
					inputStream.close();
				} else {
					Log.d("JSON", "Failed to download file");
				}
			} catch (Exception e) {
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}
		}
		return stringBuilder.toString();
		}
		

	

}
