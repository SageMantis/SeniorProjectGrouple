package cs460.grouple.grouple;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONObject;


import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class Group extends Activity
{
	private int id; //id of the group
	private String name; //name for the group
	private String bio; //bio for the group
	private Map<String, String> members; //members of the groups' email->name pair

	/*
	 * Constructor for Group class
	 */
	public Group(int id)
	{
		this.id = id;
		System.out.println("Initializing new group.");
	}
	
	/*
	 * Setters for group class below
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	public void setBio(String bio)
	{
		this.bio = bio;
	}
	
	/*
	 * Getters for group class below
	 */
	public int getID()
	{
		return id;
	}
	public String getName()
	{
		return name;
	}
	public String getBio()
	{
		return bio;
	}

	/*
	 * 
	 * will be fetching the members email->name pairs
	 * 
	 */
	// Get numFriends, TODO: work on returning the integer
	public int fetchMembers()
	{
		new getMembersTask()
				.execute("http://68.59.162.183/android_connect/get_group_members.php?gid="
						+ getID());
		return 1;
	}

	private class getMembersTask extends AsyncTask<String, Void, String>
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
	 * will be fetching the group info
	 * 
	 */
	// Get numFriends, TODO: work on returning the integer
	public int fetchGroupInfo()
	{
		new getGroupInfoTask()
				.execute("http://68.59.162.183/android_connect/get_group_info.php?gid="
						+ getID());
		return 1;
	}

	private class getGroupInfoTask extends AsyncTask<String, Void, String>
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
				//successful run
				if (jsonObject.getString("success").toString().equals("1"))
				{
					//set group name
					String name = jsonObject.getString("groupName").toString();
					setName(name);
					//set group bio
					String bio = jsonObject.getString("groupBio").toString();
					setBio(bio);

				}
				//unsuccessful
				if (jsonObject.getString("success").toString().equals("2"))
				{
					//shouldnt
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	/*
	 * To delete group and all arrays within
	 */
	public int delete()
	{
		//delete code here
		
		return 1; //successful
	}
}
