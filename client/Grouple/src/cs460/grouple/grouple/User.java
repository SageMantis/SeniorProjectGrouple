package cs460.grouple.grouple;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONObject;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;


public class User extends Activity
{
	private String email; //primary key (only necessary component of a user)
	private String fName;
	//private imagething profileImg;?
	//birthday?
	private String lName;
	private String bio;
	private String location;
	int age;
	private Map <String, String> friends; //friends emails(key) -> friends names(value)
	private Map<Integer, String> groups; //need to implement the fucking gids correctly friend groupids->groupnames
	private Map<String, String> friendRequests; //friendRequest emails->names
	private Map<Integer, String> groupInvites; //group invite ids->names
	private boolean isCurrentUser;
	
	/*
	 * Constructor for User class
	 */
	public User(String email)
	{
		this.email = email;
		this.fName = "";
		this.lName = "";
		this.isCurrentUser = false;
		System.out.println("Initializing new user.");
	}
	
	/*
	 * Setters for user class below
	 */
	public void setEmail(String email)
	{
		this.email = email;
	}
	public void setFirstName(String fName)
	{
		this.fName = fName;
	}
	public void setLastName(String lName)
	{
		this.lName = lName;
	}
	public void setBio(String bio)
	{
		this.bio = bio;
	}
	public void setLocation(String location)
	{
		this.location = location;
	}
	public void setAge(int age)
	{
		this.age = age;
	}
	public void setIsCurrentUser(boolean isCurrentUser)
	{
		this.isCurrentUser = isCurrentUser;
	}
	public void addToFriends(String email, String fName, String lName)
	{
		if (friends == null)
		{
			friends = new HashMap<String, String>();
		}
		String name = fName + " " + lName;
		friends.put(email, name);
		Log.d("Name for " + email, friends.get(email));
	}
	
	/*
	 * Getters for user class below
	 */
	public String getEmail()
	{
		return email;
	}
	public String getFirstName()
	{
		return fName;
	}
	public String getLastName()
	{
		return lName;
	}
	public String getFullName()
	{
		String fullName = fName + " " + lName;
		return fullName;
	}
	public String getBio()
	{
		return bio;
	}
	public String getLocation()
	{
		return location;
	}
	public int getAge()
	{
		return age;
	}
	public boolean isCurrentUser()
	{
		return isCurrentUser;
	}
	public int getNumFriends()
	{
		return friends.size(); 
	}
	public int getNumGroups()
	{
		return groups.size();
	}
	public int getNumFriendRequests()
	{
		return friendRequests.size();
	}
	public int getNumGroupInvites()
	{
		return groupInvites.size();
	}
	

	/*
	 * To delete the user out of memory and clear all arrays
	 */
	public int delete()
	{
		//delete code here
		
		return 1; //successful
	}
	
	
	
	
	
	
	
	/**
	 * 
	 * 
	 * fetches the user name, bio, and everything
	 * 
	 * 
	 */
	public int fetchUserInfo()
	{
		new getUserInfoTask()
				.execute("http://98.213.107.172/android_connect/get_user_info.php?email="
						+ getEmail());
	
		
		return 1;
	}

	private class getUserInfoTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			//Global global = ((Global) getApplicationContext());
			return readJSONFeed(urls[0], null);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				//getting json object from the result string
				JSONObject jsonObject = new JSONObject(result);
				//gotta make a json array
				JSONArray jsonArray = jsonObject.getJSONArray("userInfo");
				
				
				//json fetch was successful
				if (jsonObject.getString("success").toString().equals("1"))
				{
					Log.d("getUserInfoOnPost", "success1");

					//at each iteration set to hashmap friendEmail -> 'first last'
					JSONObject o = (JSONObject) jsonArray.get(0);
					//grabbing o at 0
					//set first name
					String fName = o.getString("first");
					Log.d("getUserInfoOnPost", "after sgrabbinging fname to: " + fName);
					setFirstName(fName);
					Log.d("getUserInfoOnPost", "after set first name");
					
					//set last name
					String lName = o.getString("last");
					setFirstName(lName);
					
					//set location
					String location = o.getString("location");
					setLocation(location);
					
					//set birthday (not yet implemented)
					//for now do age
				//	int age = Integer.parseInt(o.getString("age"));
					//setAge(age);
					//Log.d("getUserInfoOnPost", "after set age");
					//String fName = jsonObject.getString("fName").toString();
					//setBirthday(fName); 
					
				} 
				//unsuccessful
				else
				{
					// failed
				}
			} 
			catch (Exception e)
			{
				Log.d("atherjsoninuserpost", "here");
				//Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
			//do next thing here
		}
	}
	
	
	
	
	
	/*
	 * 
	 * will be fetching the friends key->val stuff here
	 * 
	 */
	// Get numFriends, TODO: work on returning the integer
	public int fetchFriends()
	{
		new getFriendsTask()
				.execute("http://98.213.107.172/android_connect/get_friends.php?email="
						+ getEmail());
		return 1;
	}

	private class getFriendsTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			return readJSONFeed(urls[0], null);
			
		}
		


		


		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					//gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("friends");
					
					//looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						//at each iteration set to hashmap friendEmail -> 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						//function adds friend to the friends map
						addToFriends(o.getString("email"), o.getString("first"), o.getString("last"));
					}
				}
				
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("fetchFriends", "failed = 2 return");
					//setNumFriends(0); //PANDA need to set the user class not global
				}
			} catch (Exception e)
			{
				Log.d("fetchFriends", "exception caught");
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	
	
	
	/*
	 * 
	 * 
	 * Should be getting the friendRequest key->vals here
	 */

	// Get numFriends, TODO: work on returning the integer
	public int fetchFriendRequests()
	{
		new getFriendRequestsTask()
				.execute("http://98.213.107.172/android_connect/get_friend_requests.php?email="
						+ getEmail());
		return 1;
	}

	private class getFriendRequestsTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			return global.readJSONFeed(urls[0], null);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					String numFriends = jsonObject.getString("numFriends")
							.toString();
					System.out.println("Should be setting the num friends to "
							+ numFriends);
					//setNumFriends(Integer.parseInt(numFriends)); //PANDA need to set the user class not global
					//change this to populate the friends key->val list

				}
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					//setNumFriends(0); //PANDA need to set the user class not global
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	
	
	
	/*
	 * 
	 * 
	 * should be getting the groups key->vals here
	 * 
	 */
	public int fetchGroups()
	{
		new getGroupsTask()
				.execute("http://98.213.107.172/android_connect/get_groups.php?email="
						+ getEmail());
		return 1;
	}

	private class getGroupsTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			return global.readJSONFeed(urls[0], null);
		}

		@Override
		protected void onPostExecute(String result)
		{

			try
			{
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					String numFriends = jsonObject.getString("numFriends")
							.toString();

					//setNumFriends(Integer.parseInt(numFriends)); //PANDA need to set the user class not global
					//change this to populate the friends key->val list

				}
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					//setNumFriends(0); //PANDA need to set the user class not global
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	

	/*
	 * 
	 * 
	 * should be getting the groupInvites key->vals here
	 * 
	 */

	// Get numFriends, TODO: work on returning the integer
	public int fetchGroupInvites(String email)
	{
		new getGroupInvitesTask()
				.execute("http://98.213.107.172/android_connect/get_group_invites.php?email="
						+ getEmail());
		return 1;
	}

	private class getGroupInvitesTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			return global.readJSONFeed(urls[0], null);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					String numFriends = jsonObject.getString("numFriends")
							.toString();
					System.out.println("Should be setting the num friends to "
							+ numFriends);
					//setNumFriends(Integer.parseInt(numFriends)); //PANDA need to set the user class not global
					//change this to populate the friends key->val list

				}
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					//setNumFriends(0); //PANDA need to set the user class not global
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	
	public String readJSONFeed(String URL, List<NameValuePair> nameValuePairs)
	{
		StringBuilder stringBuilder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();

		if (nameValuePairs == null)
		{
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

		}

		else
		{

			HttpPost httpPost = new HttpPost(URL);
			try
			{
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
		}
		return stringBuilder.toString();
	}
}
