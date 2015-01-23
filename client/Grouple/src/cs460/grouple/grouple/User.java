package cs460.grouple.grouple;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
import org.json.JSONObject;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
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
	private int age;
	Bitmap profileImg;
	private Map <String, String> friends; //friends emails(key) -> friends names(value)
	private Map<Integer, String> groups; 
	private ArrayList<String> friendRequests; //friendRequest emails->names
	private Map <Integer, String> groupInvites; //group invite ids
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
	
	//testing
	public void removeGroup(int gid)
	{
		if (groups.containsKey(gid))
			groups.remove(gid);
	}
	public void removeFriend(String fEmail)
	{
		if (friends.containsKey(fEmail))
			friends.remove(fEmail);
	}
	public void removeFriendRequest(String email)
	{
		if (friendRequests.contains(email))
			friendRequests.remove(email);
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
	public void addToFriendRequests(String email)
	{
		if (friendRequests == null)
		{
			friendRequests = new ArrayList<String>();
		}
		if (!friendRequests.contains(email))
			friendRequests.add(email);
	}
	public void addToGroups(String id, String name)
	{
		if (groups == null)
		{
			groups = new HashMap<Integer, String>();
		}
		int idNum = Integer.parseInt(id);
		groups.put(idNum, name);
	}
	public void addToGroupInvites(String id, String name, String sender)
	{
		if (groupInvites == null)
		{
			groupInvites = new HashMap<Integer, String>();
		}
		int idNum = Integer.parseInt(id);
		
		groupInvites.put(idNum, name);
		
		//create a new group and throw it in global
		//Global global = ((Global) getApplicationContext());
		//groupInvites.put((idNum, name), sender);
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
	public Bitmap getImage()
	{
		return profileImg;
	}
	public boolean isCurrentUser()
	{
		return isCurrentUser;
	}
	public int getNumFriends()
	{
		if (friends != null)
			return friends.size(); 
		else
			return 0;
	}
	public int getNumGroups()
	{
		if (groups != null)
			return groups.size();
		else
			return 0;
	}
	public int getNumFriendRequests()
	{
		if (friendRequests != null)
			return friendRequests.size();
		else
			return 0;
	}
	public int getNumGroupInvites()
	{
		if (groupInvites != null)
			return groupInvites.size();
		else
			return 0;
	}
	public Map<String, String> getFriends()
	{
		return friends;
	}
	public ArrayList<String> getFriendRequests()
	{
		return friendRequests;
	}
	public Map<Integer, String> getGroups()
	{
		return groups;
	}
	public Map<Integer, String> getGroupInvites()
	{
		return groupInvites;
	}
	
	//img is taken from json string
	private void setImage(String img)
	{
		Bitmap bmp;
		//jsonArray.getString("image");
	
		// decode image back to android bitmap format
		byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
		if (decodedString != null)
		{
			bmp = BitmapFactory.decodeByteArray(decodedString, 0,
					decodedString.length);
			//setting bmp;
			this.profileImg = bmp;
		}
		else
		{
			profileImg = null;
		}
			// set the image
		/*if (bmp != null)
		{
			if (iv == null)
			{
				iv = (ImageView) findViewById(R.id.profilePhoto);
	
			}
			iv.setImageBitmap(bmp);
			img = null;
			decodedString = null;
		}*/
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
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * 
	 * 
	 */
	public int fetchUserInfo() throws InterruptedException, ExecutionException, TimeoutException
	{
		
       AsyncTask<String, Void, String> task = new getUserInfoTask()
		.execute("http://68.59.162.183/android_connect/get_profile.php");
        
       task.get(10000, TimeUnit.MILLISECONDS);
	
		
		return 1;
	}

	private class getUserInfoTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			//Global global = ((Global) getApplicationContext());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("email", getEmail()));
			return readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				//getting json object from the result string
				JSONObject jsonObject = new JSONObject(result);
				//gotta make a json array
				//JSONArray jsonArray = jsonObject.getJSONArray("userInfo");
				
				
				//json fetch was successful
				if (jsonObject.getString("success").toString().equals("1"))
				{
					JSONArray jsonArray = jsonObject.getJSONArray("profile");
					Log.d("getUserInfoOnPost", "success1");

					//at each iteration set to hashmap friendEmail -> 'first last'
					String fName = (String) jsonArray.get(0);

					//set first name
					// fName = o.);
					Log.d("getUserInfoOnPost", "after sgrabbinging fname to: " + fName);
					setFirstName(fName);
					Log.d("getUserInfoOnPost", "after set first name");
					
					//set last name
					String lName = (String) jsonArray.get(1);
					setLastName(lName);
					
					//set bio
					String bio = (String) jsonArray.get(3);
					setBio(bio);
					
					//set location
					String location = (String) jsonArray.get(4);
					setLocation(location);
					
					//set birthday (not yet implemented)
					//for now do age
					int age = (Integer) jsonArray.get(2);
					setAge(age);//panda
					Log.d("getUserInfoOnPost", "after set age");
					//String fName = jsonObject.getString("fName").toString();
					//setBirthday(fName); 
					
					//get that image niggi
					String image = (String) jsonArray.get(5);
					setImage(image);
				} 
				//unsuccessful
				else
				{
					// failed
					Log.d("UserFetchInfoOnPost", "FAILED");
				}
			} 
			catch (Exception e)
			{
				Log.d("atherjsoninuserpost", "here");
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
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
	public int fetchFriends() throws InterruptedException, ExecutionException, TimeoutException
	{
		AsyncTask<String, Void, String> task = new getFriendsTask()
				.execute("http://68.59.162.183/android_connect/get_friends.php?email="
						+ getEmail());
		
		task.get(10000, TimeUnit.MILLISECONDS);
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
	 * Should be getting the friendRequest key->vals here
	 */
	public int fetchFriendRequests() throws InterruptedException, ExecutionException, TimeoutException
	{
		AsyncTask<String, Void, String> task = new getFriendRequestsTask()
		.execute("http://68.59.162.183/android_connect/get_friend_requests.php?receiver="
				+ getEmail());

		task.get(10000, TimeUnit.MILLISECONDS);

		
		return 1;
	}

	private class getFriendRequestsTask extends AsyncTask<String, Void, String>
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
					JSONArray jsonArray = jsonObject.getJSONArray("friendRequests");
					
					//looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						//at each iteration set to hashmap friendEmail -> 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						//function adds friend to the friends map=
						Log.d("fetchFriendRequestsPost", "array length: " + jsonArray.length() + ", email: " + o.getString("email"));
						addToFriendRequests(o.getString("email"));
					}
				}
				// user has no friend requests
				if (jsonObject.getString("success").toString().equals("2"))
				{
					//no friend requests
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	
	
	
	/*
	 * should be getting the groups key->vals here
	 */
	public int fetchGroups() throws InterruptedException, ExecutionException, TimeoutException
	{
		AsyncTask<String, Void, String> task = new getGroupsTask()
		.execute("http://68.59.162.183/android_connect/get_groups.php?email="
				+ getEmail());
		task.get(20000, TimeUnit.MILLISECONDS);

		
		return 1;
	}

	private class getGroupsTask extends AsyncTask<String, Void, String>
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

				//need to get gid, gname for each and put them in hashmap
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					//gotta make a json array
					JSONArray jsonArray = jsonObject.getJSONArray("groups");
					
					
					//clearing old groups
					if (groups != null)
					{
						System.out.println("Clearing groups of user.");
						groups.clear();
					}
					
					
					//looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						//at each iteration set to hashmap friendEmail -> 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						//function adds friend to the friends map
						System.out.println("HERE WE ARE ABOUT TO ADD A GROUP TO THE GROUPS TABLE");
						addToGroups(o.getString("gid"), o.getString("gname"));
					}

				}
				// user has no friends
				if (jsonObject.getString("success").toString().equals("2"))
				{
					Log.d("getGroups", "ERROR WITH JSON");
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
	 * should be getting the groupInvites key->vals here
	 * 
	 */
	// Get numFriends, TODO: work on returning the integer
	public int fetchGroupInvites() throws InterruptedException, ExecutionException, TimeoutException
	{
		AsyncTask<String, Void, String> task = new getGroupInvitesTask()
				.execute("http://68.59.162.183/android_connect/get_group_invites.php?email="
						+ getEmail());
		task.get(20000, TimeUnit.MILLISECONDS);
		return 1;
	}

	private class getGroupInvitesTask extends AsyncTask<String, Void, String>
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
					JSONArray jsonArray = jsonObject.getJSONArray("invites");
					
					//looping thru array
					for (int i = 0; i < jsonArray.length(); i++)
					{
						//at each iteration set to hashmap friendEmail -> 'first last'
						JSONObject o = (JSONObject) jsonArray.get(i);
						//function adds friend to the friends map
						addToGroupInvites(o.getString("gid"), o.getString("gname"), o.getString("sender"));
					}

				}
				
				// user has no group invites
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
