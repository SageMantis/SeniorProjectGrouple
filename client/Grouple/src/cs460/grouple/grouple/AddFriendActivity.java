package cs460.grouple.grouple;

import java.io.BufferedReader;
import android.graphics.Color;
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
import org.json.JSONObject;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class AddFriendActivity extends ActionBarActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friend);
		if (savedInstanceState == null)
		{
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		ActionBar ab = getActionBar();
		ab.setTitle("");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		ab.setIcon(Color.TRANSPARENT);
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
			Global global = ((Global)getApplicationContext());
			global.setAcceptEmail("");
			global.setCurrentUser("");
			global.setDeclineEmail("");
			startLoginActivity(null);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment
	{

		public PlaceholderFragment()
		{
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_add_friend,
					container, false);
			return rootView;
		}
	}

	public void addFriendButton(View view)
	{
		EditText emailEditTextAFA = (EditText) findViewById(R.id.emailEditTextAFA);
		String email = emailEditTextAFA.getText().toString();
		Global global = ((Global)getApplicationContext());
		String senderEmail = global.getCurrentUser();
		System.out.println("Email:" + email
				+"\nSender Email:" + senderEmail);
		
		// write the mf
		new getAddFriendTask()
				.execute("http://98.213.107.172/android_connect/add_friend.php");
	}

	public String readJSONFeed(String URL)
	{
		// Get all the fields and store locally
		EditText emailEditText = (EditText) findViewById(R.id.emailEditTextAFA);
		Global global = ((Global)getApplicationContext());
		String sender = global.getCurrentUser();
		String receiver = emailEditText.getText().toString();
	

		StringBuilder stringBuilder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(URL);
		try
		{
			// Add your data
			System.out.println("Receiver Email: " + receiver + "Sender Email: " + sender);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("sender", sender));
			nameValuePairs.add(new BasicNameValuePair("receiver", receiver));
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
		return stringBuilder.toString();
	}
	
	private class getAddFriendTask extends AsyncTask<String, Void, String>
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
				System.out.println(jsonObject.getString("success"));
				
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// successful
					System.out.println("success!");
					//Add popup
					EditText emailEditText = (EditText) findViewById(R.id.emailEditTextAFA);
					emailEditText.setText("");
					TextView addFriendMessage = (TextView) findViewById(R.id.addFriendMessageTextViewAFA);
					addFriendMessage.setText("User was invited!");
					addFriendMessage.setTextColor(Color.GREEN);
					addFriendMessage.setVisibility(0);
					// startLoginActivity();
				} 
				else if (jsonObject.getString("success").toString().equals("2"))
				{
					// failed
					TextView addFriendMessage = (TextView) findViewById(R.id.addFriendMessageTextViewAFA);
					addFriendMessage.setText("You are already friends with that user.");
					addFriendMessage.setTextColor(Color.RED);
					addFriendMessage.setVisibility(0);
				}
				else if (jsonObject.getString("success").toString().equals("3"))
				{
					// failed
					System.out.println("fail!");
					TextView addFriendMessage = (TextView) findViewById(R.id.addFriendMessageTextViewAFA);
					addFriendMessage.setText("Friend request is already pending with that user.");
					addFriendMessage.setTextColor(Color.RED);
					addFriendMessage.setVisibility(0);
				}
				else 
				{
					// failed
					TextView addFriendMessage = (TextView) findViewById(R.id.addFriendMessageTextViewAFA);
					addFriendMessage.setText("Error connecting to the server.");
					addFriendMessage.setTextColor(Color.RED);
					addFriendMessage.setVisibility(0);
				}

			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	public void startUserActivity(View view)
	{
		Intent intent = new Intent(this, UserActivity.class);
		startActivity(intent);
	}
	public void startHomeActivity(View view)
	{
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
	}
	public void startEventsActivity(View view)
	{
		Intent intent = new Intent(this, EventsActivity.class);
		startActivity(intent);
	}
	public void startGroupsActivity(View view)
	{
		Intent intent = new Intent(this, GroupsActivity.class);
		startActivity(intent);
	}
	public void startLoginActivity(View view)
	{
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
	
	public void startFriendsActivity(View view)
	{
		Intent intent = new Intent(this, FriendsActivity.class);
		startActivity(intent);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	    if(keyCode == KeyEvent.KEYCODE_BACK)
	    {
	        startFriendsActivity(null);
	    }
	    return false;
	}
}
