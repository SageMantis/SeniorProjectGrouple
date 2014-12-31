package cs460.grouple.grouple;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
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
import org.json.JSONObject;
import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/*
 * Global stores user values needed for notifications.
 */
public class Global extends Application
{
	private String currentUser;
	private String currentName;
	private String acceptEmail;
	private String declineEmail;
	private String name;
	private LinkedList<Intent> parentStackFriendsCurrent = new LinkedList<Intent>();
	private LinkedList<Intent> parentStackFriendProfile = new LinkedList<Intent>();
	private LinkedList<Intent> parentStackGroupsCurrent = new LinkedList<Intent>();
	private LinkedList<Intent> parentStackGroupProfile = new LinkedList<Intent>();
	private LinkedList<Intent> parentStackUser = new LinkedList<Intent>();
	private LinkedList<Intent> parentStackFriends = new LinkedList<Intent>();
	private LinkedList<Intent> parentStackGroups = new LinkedList<Intent>();
	private LinkedList<Intent> parentStackGroupInvites = new LinkedList<Intent>();
	private ArrayList<User> users; //contains all the users that have been loaded into the current run of the program
	private int numFriendRequests;
	private int numFriends;
	private int numGroupInvites;
	private int numGroups;

	// TODO: Add getParentFriendsActivity

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
	public void addToUsers(User u)
	{
		//Only add user if they are not in the array
		//probably check for this in the loadUser
		users.add(u);
	}
	//takes in an email and returns that user if user is in the system
	public User getUser(String email)
	{
		User u = null;
		//searching through the users to find the 1 that matches given email
		for (int i = 0; i < users.size(); i++)
		{
			if (users.get(i).getEmail().equals(email))
			{
				u = users.get(i);
			}
		}
		return u; //returns null if user was not found
	}
	//using the email of user, load them up into our array of pertinent users
	public User loadUser(String email)
	{	
		//check that user is not already loaded
		User user = checkGetUser(email);//null if user not loaded
		if (user == null)
		{
			//user was not previously loaded
			
			//instantiate a new user
			user = new User(email); //changes that null to something fresh
			//json call using email to fetch users fName, lName, bio, location, birthday, profileImage
			//this is next
			
			int success;
			//there is currently no wait for this to complete and it returns a user before these get set
			success = user.fetchUserInfo();
			
			if (success == 1)
				Log.d("loadUser", "after fetchUserInfo()");
			
			//json call to populate users friendKeys / friendNames
			//this is implemented so do it and test
			success = user.fetchFriends();
			if (success == 1)
				Log.d("loadUser","after fetchFriends()");
			
			//json call to populate users groupKeys / groupNames
			
			//json call to populate users friendRequestKeys / names
					
			//json call to populate users groupInviteKeys / names
		}
		else
		{
			//user is already loaded
			//it is already set to what it needs
		}
		//set isCurrentUser to false unless the OG user
		return user;
	}
	
	//takes in user email, if found in users -> returns true, else false
	private User checkGetUser(String email)
	{
		User user = null;
		
		//if users has not yet been made, initialize it
		if (users == null)
		{
			users = new ArrayList<User>();
		}
		
		//loop through users
		for (User u : users)
		{
			//if emails match
			if (u.getEmail().equals(email))
				user = u; //makes return statement the user
		}
		
		//return null if user was not found
		return user;
	}
	/*
	 * Old code for most things we are going to update

	public int getNumFriendRequests()
	{
		return numFriendRequests;
	}

	public int getNumGroupInvites()
	{
		return numGroupInvites;
	}

	public int getNumFriends()
	{
		return numFriends;
	}

	public int getNumGroups()
	{
		return numGroups;
	}

	public String getCurrentName()
	{
		return currentName;
	}

	public void setCurrentName(String name)
	{
		currentName = name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
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

	public void setNumFriends(int numFriends)
	{
		this.numFriends = numFriends;
	}

	*/

	/*
	 * Adding an intent to the stack of parents for a specific activity (differentiated using its view)
	 */
	public void addToParentStack(View view, Intent intent)
	{
		switch (view.getId())
		{
			case R.id.currentFriendsContainer:
				parentStackFriendsCurrent.push(intent);
				break;
			case R.id.friendProfileContainer:
				parentStackFriendProfile.push(intent);
				break;
			case R.id.userContainer:
				parentStackUser.push(intent);
				break;
			case R.id.groupsCurrentContainer:
				parentStackGroupsCurrent.push(intent);
				break;
			case R.id.groupProfileContainer:
				parentStackGroupProfile.push(intent);
				break;
			case R.id.friendsContainer:
				parentStackFriends.push(intent);
				break;
			case R.id.groupsContainer:
				parentStackGroups.push(intent);
			case R.id.groupInvitesContainer:
				parentStackGroupInvites.push(intent);
				break;
		}
	}

	public Intent getNextParentIntent(View view)
	{
		Intent parentIntent = null;
		switch (view.getId())
		{
		case R.id.currentFriendsContainer:
			if (parentStackFriendsCurrent.size() >= 1)
			{
				System.out.println("In next parent get of current friends");
				parentIntent = parentStackFriendsCurrent.pop();
			} else
			{
				parentIntent = new Intent(this, FriendsActivity.class);
				startActivity(parentIntent);
			}
			break;
		case R.id.friendProfileContainer:
			if (parentStackFriendProfile.size() >= 1)
			{
				parentIntent = parentStackFriendProfile.pop();
			}
			break;
		case R.id.groupsCurrentContainer:
			if (parentStackGroupsCurrent.size() >= 1)
			{
				System.out.println("we are in the switch for current groups");
				System.out.println("Groups current container");
				parentIntent = parentStackGroupsCurrent.pop();
			}
			break;
		case R.id.groupProfileContainer:
			if (parentStackGroupProfile.size() >= 1)
			{
				parentIntent = parentStackGroupProfile.pop();
			}
			break;
		case R.id.friendsContainer:
			if (parentStackFriends.size() >= 1)
			{
				parentIntent = parentStackFriends.pop();
			}
			break;
		case R.id.userContainer:
			if (parentStackUser.size() >= 1)
			{
				parentIntent = parentStackUser.pop();
			}
			break;
		case R.id.groupsContainer:
			if (parentStackGroups.size() >= 1)
			{
				parentIntent = parentStackGroups.pop();
			}
		case R.id.groupInvitesContainer:
			if (parentStackGroupInvites.size() >= 1)
			{
				parentIntent = parentStackGroupInvites.pop();
			}
			break;
		default:
			parentIntent = new Intent(this, HomeActivity.class);
			parentIntent.putExtra("ParentClassName", "HomeActivity");
			break;
		}

		return parentIntent;

	}

	//think: may need to move this to the user class
		/*
		 * would look like
		 * 	user u = global.getUser("email");
		 * 	u.setNotifications();
		 */
	//or could take in email and grab the user through global and do this
		/*
		 * 	would look like
		 * 		global.setNotifications("view", "email");
		 */
	//gotta be consistent thorughout so,... same on loading each friendprofile... either grab user or go to global first
	//I like grabbing it first, makes it like the user is really there
	public int setNotifications(View view, User user)
	{
		// todo: If I can pass an email in here and skip setting current user
		int numFriendRequests = user.getNumFriendRequests();
		int numFriends = user.getNumFriends();
		int numGroupInvites = user.getNumGroupInvites();
		int numGroups = user.getNumGroups();

		// Home Activity
		if (numFriendRequests > 0
				&& view.findViewById(R.id.friendsButtonHA) != null)
		{
			if (numFriendRequests == 1)
			{
				((Button) view.findViewById(R.id.friendsButtonHA))
						.setText("Friends \n(" + numFriendRequests
								+ " request)");
			} else
			{
				((Button) view.findViewById(R.id.friendsButtonHA))
						.setText("Friends \n(" + numFriendRequests
								+ " requests)");
			}
		} else if (numFriendRequests == 0
				&& view.findViewById(R.id.friendsButtonHA) != null)
		{
			((Button) view.findViewById(R.id.friendsButtonHA))
					.setText("Friends");
		}
		if (numGroupInvites > 0
				&& view.findViewById(R.id.groupsButtonHA) != null)
		{
			if (numFriendRequests == 1)
			{
				((Button) view.findViewById(R.id.groupsButtonHA))
						.setText("Groups \n(" + numGroupInvites + " invites)");
			} else
			{
				((Button) view.findViewById(R.id.groupsButtonHA))
						.setText("Groups \n(" + numGroupInvites + " invites)");
			}
		} else if (numFriendRequests == 0
				&& view.findViewById(R.id.groupsButtonHA) != null)
		{
			((Button) view.findViewById(R.id.groupsButtonHA)).setText("Groups");
		}

		// Friends Activity
		if (view.findViewById(R.id.friendRequestsButtonFA) != null
				&& view.findViewById(R.id.currentFriendsButtonFA) != null)
		{
			Button friendRequestsButton = (Button) view
					.findViewById(R.id.friendRequestsButtonFA);
			friendRequestsButton.setText("Friend Requests ("
					+ numFriendRequests + ")");
			Button currentFriendsButton = (Button) view
					.findViewById(R.id.currentFriendsButtonFA);
			currentFriendsButton
					.setText("My Friends (" + user.getNumFriends() + ")"); //PANDA
		}

		// User Profile Buttons
		if ((view.findViewById(R.id.friendsButtonUPA) != null)
				&& (view.findViewById(R.id.groupsButtonUPA) != null)
				&& (view.findViewById(R.id.eventsButtonUPA) != null))
		{
			((Button) view.findViewById(R.id.friendsButtonUPA))
					.setText("Friends\n(" + numFriends + ")");
			((Button) view.findViewById(R.id.groupsButtonUPA))
					.setText("Groups\n(" + numGroups + ")");
			// set numfriends, numgroups, and numevents
		}

		// Friend Profile Buttons
		if ((view.findViewById(R.id.friendsButtonFPA) != null)
				&& (view.findViewById(R.id.groupsButtonFPA) != null)
				&& (view.findViewById(R.id.eventsButtonFPA) != null))
		{
			// set numfriends, numgroups, and numevents
			((Button) view.findViewById(R.id.friendsButtonFPA))
					.setText("Friends\n(" + numFriends + ")");
			((Button) view.findViewById(R.id.groupsButtonFPA))
					.setText("Groups\n(" + numGroups + ")");
		}

		// Groups activity
		if (view.findViewById(R.id.pendingGroupsButton) != null)
		{
			System.out.println("Pending groups setting text to what it is");
			((Button) view.findViewById(R.id.pendingGroupsButton))
					.setText("Group Invites (" + numGroupInvites + ")");
		}
		if (view.findViewById(R.id.yourGroupsButton) != null)
		{
			((Button) view.findViewById(R.id.yourGroupsButton))
					.setText("My Groups (" + numGroups + ")");
		}
		return 1; // successful
		// else do nothing, keep that invisible
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
