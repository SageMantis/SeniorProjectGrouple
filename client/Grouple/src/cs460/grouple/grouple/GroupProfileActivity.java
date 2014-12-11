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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import cs460.grouple.grouple.R;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GroupProfileActivity extends ActionBarActivity 
{

	private ImageView iv;
	private Bitmap bmp;
	private final static int CAMERA_DATA = 0;
	private Intent i;
	private BroadcastReceiver broadcastReceiver;
	private String gname = "";
	private String bio = "";
	private int gcount = 0;
	private int index = 0;
	private LayoutInflater inflater;
	private LinearLayout membersToAdd;
	Intent upIntent;
	Intent parentIntent;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_profile);

		View groupProfile = findViewById(R.id.groupProfileContainer);
		load(groupProfile);
	}
	
	
	public void initActionBar()
	{

		/*Action bar*/
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(false);
		TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitleTextView);
		Global global = ((Global) getApplicationContext());
		Bundle extras = getIntent().getExtras();
		gname = extras.getString("gname");
		actionbarTitle.setText(gname);
		ImageButton upButton = (ImageButton) findViewById(R.id.actionbarUpButton);
		upButton.setOnClickListener(new OnClickListener() {

	
			public void onClick(View view) {
				startActivity(upIntent);
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			startActivity(upIntent);
		}
		return false;
	}

	public void load(View view)
	{
		Log.d("message", "00000000000000001");
		
		inflater = getLayoutInflater();
		membersToAdd = (LinearLayout) findViewById(R.id.linearLayoutNested2);
		
		Global global = ((Global) getApplicationContext());
		//backstack of intents
		//each class has a stack of intents lifo method used to execute them at start of activity
		//intents need to include everything like ParentClassName, things for current page (email, ...)
		//if check that friends
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		//do a check that it is not from a back push

		
		getGroupCount();
		upIntent = new Intent(this, GroupsCurrentActivity.class);
		upIntent.putExtra("up", "true");
		upIntent.putExtra("mod", "true");
		//startActivity(upIntent);
		initActionBar();
		initKillswitchListener();
		
	}
	
	public void getGroupCount(){
		new getProfileTask().execute("http://98.213.107.172/" +
				"android_connect/count_group_members.php");
	}
	
	public void getGroupContents(){
		if(index < gcount){//for(; index < gcount; index++){
			Log.d("hello", "hellohello1 " + ";index = " + index + ";gcount = " + gcount);
			new getProfileTask().execute("http://98.213.107.172/" +
					"android_connect/get_group_contents.php");
			//index++;
		}
		else{

			TextView tv = (TextView)findViewById(R.id.bioTextView);
			tv.setText(bio);
		}
	}
	
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation_actions, menu);

		// Set up the image view
		if (iv == null)
		{
			iv = (ImageView) findViewById(R.id.profilePhoto);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Global global = ((Global) getApplicationContext());
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_logout)
		{
			global.setAcceptEmail("");
			global.setCurrentUser("");
			global.setDeclineEmail("");
			Intent login = new Intent(this, LoginActivity.class);
			startActivity(login);
			bmp = null;
			iv = null;
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		if (id == R.id.action_home)
		{
			Intent intent = new Intent(this, HomeActivity.class);
			intent.putExtra("ParentClassName", "GroupProfileActivity");
			intent.putExtra("up", "false");
			//add to stack
			global.addToParentStackGroupProfile(intent);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}


	/* Start activity functions for going back to home and logging out */
	public void startHomeActivity(View view)
	{
		Intent intent = new Intent(this, HomeActivity.class);
		intent.putExtra("ParentClassName", "GroupProfileActivity");
		intent.putExtra("up", "false");
		Global global = ((Global) getApplicationContext());
		global.addToParentStackGroupProfile(parentIntent);
		startActivity(intent);
		bmp = null;
		iv = null;
		finish();
	}

	public void startEditProfileActivity(View view)
	{
		Intent intent = new Intent(this, EditProfileActivity.class);
		intent.putExtra("up", "false");
		Global global = ((Global) getApplicationContext());
		global.addToParentStackGroupProfile(parentIntent);
		intent.putExtra("ParentClassName", "GroupProfileActivity");
		startActivity(intent);
		bmp = null;
		iv = null;
	}

	public String readGetFriendsJSONFeed(String URL)
	{

		Log.d("message", "00000000000000002");
		StringBuilder stringBuilder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(URL);
		try
		{
			if(URL.equals("http://98.213.107.172/android_connect/count_group_members.php")){
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("gname", gname));
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			}
			else if(URL.equals("http://98.213.107.172/android_connect/get_group_contents.php")){
				Log.d("hello", "hellohello2");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("gname", gname));
				nameValuePairs.add(new BasicNameValuePair("index", "" + index));
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				index++;
			}
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
						Log.d("whatis", "The response is: " + line);
						stringBuilder.append(line);
					}
					inputStream.close();
					reader.close();
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
	
	/*
	 * Get profile executes get_profile.php.
	 */
	private class getProfileTask extends AsyncTask<String, Void, String>
	{

		protected String doInBackground(String... urls)
		{
			Log.d("message", "00000000000000003 " + urls[0]);
			return readGetFriendsJSONFeed(urls[0]);
		}

		protected void onPostExecute(String result)
		{
			try
			{
				Log.d("message", "00000000000000004");
				JSONObject jsonObject = new JSONObject(result);
				
				if (jsonObject.getString("success").toString().equals("1"))
				{
					String gnumTemp = (jsonObject.getString("gcount"));
					gcount = Integer.parseInt(gnumTemp);
					Log.d("count group members", "There are " + gnumTemp + " gmembers.");
					getGroupContents();
				}
				else if(jsonObject.getString("success").toString().equals("2")){
					String grow = (jsonObject.getString("grow"));
					//JSONArray grow = (JSONArray) jsonObject.getJSONArray("grow");
					String tokens = ",";
					String[] contents = grow.split(tokens);
					for(int i = 0; i < contents.length; i++){
						if(contents[i].contains("\"")){
							contents[i] = contents[i].replaceAll("\"", "");
						}
					}
					
					bio = contents[2];
					String role = contents[5];
					String member = contents[7];
					Log.d("count group members", "The contents of this row is:\n" + 
							contents[7] + ".");
					
					//<<<<<<<<<< here >>>>>>>>>>//
					GridLayout rowView = (GridLayout) inflater.inflate(R.layout.listitem_groupprofile, null);
					Button removeFriendButton = (Button) rowView.findViewById(R.id.removeFriendButtonNoAccess);
					Button friendNameButton = (Button) rowView.findViewById(R.id.friendNameButtonNoAccess);
					friendNameButton.setText(member);
					if(role.equals("C")){
						removeFriendButton.setText("C");
						removeFriendButton.setTextColor(getResources().getColor(R.color.black));
					}
					else if(role.equals("A")){
						removeFriendButton.setText("A");
						removeFriendButton.setTextColor(getResources().getColor(R.color.light_green));
					}
					else{
						removeFriendButton.setText("-");
						removeFriendButton.setTextColor(getResources().getColor(R.color.light_blue));
					}
					removeFriendButton.setId(index);
					friendNameButton.setId(index);
					rowView.setId(index);
					membersToAdd.addView(rowView);
					
					getGroupContents();
				}
					/*
					// Success
					JSONArray jsonProfileArray = (JSONArray) jsonObject
							.getJSONArray("profile");

					//String name = jsonProfileArray.getString(0) + " "
						//	+ jsonProfileArray.getString(1);
					String bio = jsonProfileArray.getString(3);
					String img = jsonProfileArray.getString(5);

					// decode image back to android bitmap format
					byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
					if (decodedString != null)
					{
						bmp = BitmapFactory.decodeByteArray(decodedString, 0,
								decodedString.length);
					}
					// set the image
					if (bmp != null)
					{
						if (iv == null)
						{
							iv = (ImageView) findViewById(R.id.profilePhoto);

						}
						iv.setImageBitmap(bmp);
						img = null;
						decodedString = null;
					}

					TextView bioTextView = (TextView) findViewById(R.id.bioTextView);
					bioTextView.setText(bio);
					*/
				else
				{
					// Fail
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	
	
	public void toggleAdmin(View view){
		
	}
	
	public void editGroupProfileButton(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.editGroupProfilePhotoButton:
			i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(i, CAMERA_DATA);
			break;

		}
	}

	@Override
	protected void onActivityResult(int reqCode, int resCode, Intent d)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(reqCode, resCode, d);
		if (resCode == RESULT_OK)
		{
			Bundle extras = d.getExtras();
			bmp = (Bitmap) extras.get("data");
			iv.setImageBitmap(bmp);
		}
	}
	
	public void initKillswitchListener()
	{
		// START KILL SWITCH LISTENER
				IntentFilter intentFilter = new IntentFilter();
				intentFilter.addAction("CLOSE_ALL");
				broadcastReceiver = new BroadcastReceiver()
				{
					@Override
					public void onReceive(Context context, Intent intent)
					{
						// close activity
						if (intent.getAction().equals("CLOSE_ALL"))
						{
							Log.d("app666", "we killin the login it");
							// System.exit(1);
							finish();
						}

					}
				};
				registerReceiver(broadcastReceiver, intentFilter);
				// End Kill switch listener
	}
}
