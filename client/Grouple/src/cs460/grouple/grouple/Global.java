package cs460.grouple.grouple;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
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
	private String acceptEmail;
	private String declineEmail;

	private LinkedList<Intent> parentStackFriendsCurrent = new LinkedList<Intent>();
	private LinkedList<Intent> parentStackGroupsCurrent = new LinkedList<Intent>();
	private LinkedList<Intent> parentStackGroupProfile = new LinkedList<Intent>();
	private LinkedList<Intent> parentStackUserProfile = new LinkedList<Intent>();
	private LinkedList<Intent> parentStackFriends = new LinkedList<Intent>();
	private LinkedList<Intent> parentStackGroups = new LinkedList<Intent>();
	private LinkedList<Intent> parentStackGroupInvites = new LinkedList<Intent>();
	private ArrayList<User> users; //contains all the users that have been loaded into the current run of the program

	/*
	 * Adds a user to the users arraylist
	 */
	public void addToUsers(User u)
	{
		//if users has not yet been made, initialize it
		if (users == null)
		{
			users = new ArrayList<User>();
		}
		
		users.add(u);
	}
	
	
	//using the email of user, load them up into our array of pertinent users
	public User loadUser(String email)
	{	
		//check that user is not already loaded
		User user = checkGetUser(email);//returns null, if user not loaded//user, if user was loaded
		if (user == null) 
		{
			//user was not previously loaded
			//need to set a flag to be sure to add this to the users array
			
			//instantiate a new user
			user = new User(email); //changes that null to something fresh
			
			//check if current user
			if (users.size() == 0)
			{
				user.setIsCurrentUser(true);
			}
			
			//initialize success
			int success = 0;
			try
			{
				//json call using email to fetch users fName, lName, bio, location, birthday, profileImage
				success = user.fetchUserInfo();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			} catch (ExecutionException e)
			{
				e.printStackTrace();
			} catch (TimeoutException e)
			{
				e.printStackTrace();
			}
			
			//was successful in fetching user info
			if (success == 1)
				Log.d("loadUser", "success after fetchUserInfo()");

			//reset success
			success = 0;
			try
			{
				//json call to populate users friendKeys / friendNames
				success = user.fetchFriends();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			} catch (ExecutionException e)
			{
				e.printStackTrace();
			} catch (TimeoutException e)
			{
				e.printStackTrace();
			}
			
			//was successful in fetching friends
			if (success == 1)
				Log.d("loadUser","success after fetchFriends()");
			
			
			/*
			//reset success
			success = 0;
			//json call to populate users groupKeys / groupNames
			try
			{
				success = user.fetchGroups();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//was successful in fetching groups
			if (success == 1)
				Log.d("loadUser","success after fetchGroups()");
			*/
			
			//json call to populate users friendRequestKeys / names
			//reset success
			success = 0;
			try
			{
				success = user.fetchFriendRequests();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//was successful in fetching groups
			if (success == 1)
				Log.d("loadUser","success after fetchFriendRequests()");
			
			//json call to populate users groupInviteKeys / names\
			
			//put user in users
			addToUsers(user);
			
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
	 * Adding an intent to the stack of parents for a specific activity (differentiated using its view)
	 */
	public void addToParentStack(View view, Intent intent)
	{
		switch (view.getId())
		{
			case R.id.currentFriendsContainer:
				parentStackFriendsCurrent.push(intent);
				break;
			case R.id.userProfileContainer:
				parentStackUserProfile.push(intent);
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
		case R.id.userProfileContainer:
			if (parentStackUserProfile.size() >= 1)
			{
				parentIntent = parentStackUserProfile.pop();
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
	
	//may be outdated, can either update notifications here or in each activity itself
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

	

	//probably not going to use this as much, 
	//maybe none if groups / users both have their own and everything goes through those
	//wouldn't let me use global from them, could change that too i suppose, maybe
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
				} 
				else
				{
					Log.d("JSON", "Failed to download file");
				}
			} 
			catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}
		}
		return stringBuilder.toString();
	}//end readJSONFeed
}//end Global class
