package cs460.grouple.grouple;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * GroupCreateActivity allows a user to create a new group.
 */
@SuppressLint("UseSparseArrays")
public class GroupCreateActivity extends ActionBarActivity
{
	BroadcastReceiver broadcastReceiver;
	private Map<Integer, Boolean> isAdmin = new HashMap<Integer, Boolean>();
	private Map<Integer, String> alreadyAdded = new HashMap<Integer, String>();
	private Map<Integer, String> added = new HashMap<Integer, String>();
	private Map<Integer, Boolean> role = new HashMap<Integer, Boolean>();
	private Map<String, String> theseFriends = new HashMap<String, String>();
	User user;
	private ArrayList<HttpResponse> response = new ArrayList<HttpResponse>();
	int firstEntry = 0;
	private int increment = 0;
	private String email = null;
	private String g_id = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_create);
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionBarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		actionBarTitle.setText("Groups");
		
		Global global = ((Global) getApplicationContext());
		email = global.getCurrentUser().getEmail();
		user = global.loadUser(email);
		//friend email -> full names
		theseFriends = user.getFriends();
		
		ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);
		upButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View view)
			{
				System.out.println("Image button onClick was clicked!");
				startParentActivity(view);
			}
		});
		
		/* beginning building the interface */
		LayoutInflater inflater = getLayoutInflater();
		LinearLayout membersToAdd = (LinearLayout) findViewById(R.id.linearLayoutNested1);
		
		if(theseFriends.size() == 0)
		{
			View row = inflater.inflate(
					R.layout.listitem_groupcreateadded, null);

			((Button) row.findViewById(R.id.friendNameButtonNoAccess))
					.setText("You don't have any friends yet!");
			row.findViewById(R.id.removeFriendButtonNoAccess)
					.setVisibility(1);
			membersToAdd.addView(row);
		}
		
		Iterator iterator = theseFriends.entrySet().iterator();
		
		//for each friend
		for(int i=0; i<theseFriends.size(); i++)
		{
			
			GridLayout rowView;
			rowView = (GridLayout) inflater.inflate(
					R.layout.listitem_groupcreateadded, null);
			final Button makeAdminButton = (Button) rowView
					.findViewById(R.id.removeFriendButtonNoAccess);

			makeAdminButton
					.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View view)
						{
							System.out.println("makeAdminButton onClick was clicked");
							if (makeAdminButton.getText()
									.toString().equals("-"))
							{
								makeAdminButton.setText("A");
								isAdmin.put(view.getId(), true);
								makeAdminButton
										.setTextColor(getResources()
												.getColor(
														R.color.light_green));
							} else
							{
								makeAdminButton.setText("-");
								isAdmin.put(view.getId(), false);
								makeAdminButton
										.setTextColor(getResources()
												.getColor(
														R.color.orange));
							}
						}
					});
			makeAdminButton.setId(i);
			
			final Button friendNameButton = (Button) rowView.findViewById(R.id.friendNameButtonNoAccess);
			final CheckBox cb = (CheckBox) rowView
					.findViewById(R.id.addToGroupBox);
			cb.setId(makeAdminButton.getId());
			isAdmin.put(cb.getId(), false);
			
			//Note: the following onCheckedChanged's code could still be rewritten (01/21/2015)
			
			cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(CompoundButton view, boolean isChecked)
				{
					System.out.println("checkbox was clicked!");
					String text = friendNameButton.getLayout()
							.getText().toString();
				
					if (!view.isChecked()
							&& (alreadyAdded.keySet().size() == 1)
							&& (firstEntry == view.getId()))
					{
						System.out.println("Entering first");
						alreadyAdded.clear();
						added.clear();
						role.clear();
						firstEntry = 0;
						Log.d("Close Attention",
								"The mapsize is: "
										+ alreadyAdded.keySet()
												.size());
					} else if (alreadyAdded.keySet().isEmpty())
					{
						System.out.println("Entering second");
						if (view.isChecked())
						{
							System.out.println("Entering third");
							Log.d("here2?", " <=here we are?");
							added.put(view.getId(), text);
							role.put(view.getId(),
									isAdmin.get(view.getId()));
							alreadyAdded.put(view.getId(), text);
							Log.d("Close Attention",
									"The mapsize is: "
											+ alreadyAdded
													.keySet()
													.size());
							firstEntry = view.getId();
						}
					} else if (view.isChecked())
					{
						System.out.println("Entering fourth");
						boolean flag = false;
						Log.d("here2?", " <=outtheloop?");
						Iterator iterate = alreadyAdded
								.entrySet().iterator();
						while (iterate.hasNext())
						{
							Log.d("here2?", " <=intheloop?");
							Map.Entry pair = (Map.Entry) iterate
									.next();
							if (pair.getValue().equals(text))
							{
								flag = true;
								Log.d("here?", "flag <=here?");
							}
						}

						if (!flag)
						{
							System.out.println("Entering fifth");
							if (view.isChecked())
							{
								System.out.println("Entering sixth");
								Log.d("herefromunselected?",
										" <=here?");
								added.put(view.getId(), text);
								role.put(view.getId(), isAdmin
										.get(view.getId()));
								alreadyAdded.put(view.getId(),
										text);
								Log.d("Close Attention",
										"The mapsize is: "
												+ alreadyAdded
														.keySet()
														.size());
							}
						}
					} else if (!view.isChecked())
					{
						System.out.println("Entering seventh");
						Log.d("2herefromselected?", " <=here?");
						added.remove(view.getId());
						role.remove(view.getId());
						alreadyAdded.remove(view.getId());
						Log.d("Close Attention",
								"The mapsize is: "
										+ alreadyAdded.keySet()
												.size());
					}
				}
			});
			
			Entry thisEntry = (Entry) iterator.next();
			friendNameButton.setText(thisEntry.getValue().toString());
			friendNameButton.setId(i);
			rowView.setId(i);
			membersToAdd.addView(rowView);	
		}
	}
	
	// Create Group pops up a confirm box to make sure the user wants to create the group.
	public void createGroupButton(View view)
	{		
		
		//first check to make sure a group name has been typed by the user
		EditText groupNameEditText = (EditText) findViewById(R.id.groupName);
		EditText groupBioEditText = (EditText) findViewById(R.id.groupBio);

		String groupname = groupNameEditText.getText().toString();
		//if empty string, display error box
		if(groupname.compareTo("") == 0)
		{
			new AlertDialog.Builder(this)
			.setMessage("Please give your group a name before creating.")
			.setCancelable(true)
			.setNegativeButton("Ok", null).show();
		}
		//otherwise display confirmation box
		else
		{
			new AlertDialog.Builder(this)
			.setMessage("Are you sure you want to create this group?")
			.setCancelable(true)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int id)
				{
					new CreateGroupTask().execute("http://68.59.162.183/"
					+ "android_connect/create_group.php");
				}
			}).setNegativeButton("Cancel", null).show();
		}
	}
	
	public void addToGroupTable(View view)
	{
		
	}
	
	private class CreateGroupTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			EditText groupNameEditText = (EditText) findViewById(R.id.groupName);
			EditText groupBioEditText = (EditText) findViewById(R.id.groupBio);

			String groupname = groupNameEditText.getText().toString();
			String groupbio = groupBioEditText.getText().toString();	
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("g_name", groupname));
			nameValuePairs.add(new BasicNameValuePair("descript", groupbio));
			nameValuePairs.add(new BasicNameValuePair("creator", email));

			return global.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);

				// group has been successfully created
				if (jsonObject.getString("success").toString().equals("1"))
				{
					//now we can set the newly created g_id returned from the server
					g_id = jsonObject.getString("g_id").toString();
					
					System.out.println("g_id of newly created group is: "+g_id);
					
					//add yourself to the group as Creator
					new AddGroupMembersTask().execute("http://68.59.162.183/"
							+ "android_connect/add_groupmember.php", email, email, "C", g_id);
					
					//now add all the additional users to the group
					Iterator iterate = added.entrySet().iterator();
					Iterator iterate2 = role.entrySet().iterator();
					while (iterate.hasNext())
					{
						String role;
						Map.Entry tadded = (Map.Entry) iterate.next();
						Map.Entry trole = (Map.Entry) iterate2.next();
						
						int index = (Integer) tadded.getKey();
						Iterator iterate3 = theseFriends.entrySet().iterator();
						for(int i=0; i<index; i++)
						{
							iterate3.next();
						}
						Map.Entry temail = (Map.Entry) iterate3.next();
						String friendsEmail = (String) temail.getKey();
						
						System.out.println(trole.toString());
						if(trole.getValue().toString().equals("false"))
						{
							role = "M";
						}
						else
						{
							role = "A";
						}
						new AddGroupMembersTask().execute("http://68.59.162.183/"
								+ "android_connect/add_groupmember.php", friendsEmail, email, role, g_id);
					}
					
					//Now take the user to a new activity (the newly create group's profile page)					
					//Note: until groupprofileactivity is coded, just take the user back to main groups page.
					finish();
				} 
				else if (jsonObject.getString("success").toString().equals("0"))
				{	
					//group failed to create. display something to user here.
				}
			} catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}
		}
	}
	
	private class AddGroupMembersTask extends AsyncTask<String,Void,String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", urls[1]));
			nameValuePairs.add(new BasicNameValuePair("sender", urls[2]));
			nameValuePairs.add(new BasicNameValuePair("role", urls[3]));
			nameValuePairs.add(new BasicNameValuePair("g_id", urls[4]));

			return global.readJSONFeed(urls[0], nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);

				// member has been successfully added
				if (jsonObject.getString("success").toString().equals("1"))
				{
					//all working correctly, continue on.
				} 
				else if (jsonObject.getString("success").toString().equals("0"))
				{	
					//something went wrong... display something to user here...
				}
			} catch (Exception e)
			{
				Log.d("readJSONFeed", e.getLocalizedMessage());
			}
		}		
	}
	
	public void startParentActivity(View view)
	{
		Bundle extras = getIntent().getExtras();

		String className = extras.getString("ParentClassName");
		Intent newIntent = null;
		try
		{
			newIntent = new Intent(this, Class.forName("cs460.grouple.grouple."
					+ className));
			if (extras.getString("ParentEmail") != null)
			{
				newIntent.putExtra("email", extras.getString("ParentEmail"));
			}
			// newIntent.putExtra("email", extras.getString("email"));
			System.out.println("delete");
			// newIntent.putExtra("ParentEmail", extras.getString("email"));
			newIntent.putExtra("ParentClassName", "GroupCreateActivity");
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		newIntent.putExtra("up", "true");
		startActivity(newIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_logout)
		{
			Global global = ((Global) getApplicationContext());
			Intent login = new Intent(this, LoginActivity.class);
			startActivity(login);
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		if (id == R.id.action_home)
		{
			Intent intent = new Intent(this, HomeActivity.class);
			intent.putExtra("up", "false");
			intent.putExtra("ParentClassName", "GroupCreateActivity");
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
}
