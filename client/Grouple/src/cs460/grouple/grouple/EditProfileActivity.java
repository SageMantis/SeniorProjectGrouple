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
import org.json.JSONArray;
import org.json.JSONObject;

//import cs460.grouple.grouple.RegisterActivity.getRegisterTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class EditProfileActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		
		//START KILL SWITCH LISTENER
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("CLOSE_ALL");
		BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		 @Override
		 public void onReceive(Context context, Intent intent)
		 {
			 // close activity
			 if(intent.getAction().equals("CLOSE_ALL"))
			 {
				 //Log.d("app666","we killin the login it");
				 //System.exit(1);
				 finish();
			  }
					  
		 }
	};
		registerReceiver(broadcastReceiver, intentFilter);
		//End Kill switch listener
				
		//execute php script, using the current users email address to populate the textviews
		new getProfileTask().execute("http://98.213.107.172/android_connect/get_profile.php");
	
}

// public void loginButton(View view)
// {
// Create helper and if successful, will bring the correct home activity.
// EditText usernameEditText = (EditText)
// findViewById(R.id.emailEditTextRA);
// EditText passwordEditText = (EditText)
// findViewById(R.id.passwordEditText);

// new
// getRegisterTask().execute("http://98.213.107.172/android_connect/get_login.php?email="+usernameEditText.getText().toString()+"&password="+passwordEditText.getText().toString());
// }

/*
 * Get profile executes get_profile.php. It uses the current users email address to retrieve the users name, age, and bio. 
 */
private class getProfileTask extends AsyncTask<String, Void, String>
{

	protected String doInBackground(String... urls)
	{

		return readJSONFeed(urls[0]);
	}
	public String readJSONFeed(String URL)
	{

		StringBuilder stringBuilder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(URL);
		try
		{
			Global global = ((Global) getApplicationContext());
			String email = global.getCurrentUser();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
			nameValuePairs.add(new BasicNameValuePair("email", email));
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

	protected void onPostExecute(String result)
	{
		try
		{
			JSONObject jsonObject = new JSONObject(result);
			System.out.println(jsonObject.getString("success"));
			if (jsonObject.getString("success").toString().equals("1"))
			{
				//Success
				JSONArray jsonProfileArray = (JSONArray)jsonObject.getJSONArray("profile");
				
				String name = jsonProfileArray.getString(0)+" "+jsonProfileArray.getString(1);
				String age = jsonProfileArray.getString(2);
				String bio = jsonProfileArray.getString(3);
				String location = jsonProfileArray.getString(4);
				
				TextView nameTextView = (TextView) findViewById(R.id.nameEditTextEPA);
				TextView ageTextView = (TextView) findViewById(R.id.ageEditTextEPA);
				TextView locationTextView = (TextView) findViewById(R.id.locationEditTextEPA);
				TextView bioTextView = (TextView) findViewById(R.id.bioEditTextEPA);
				//JSONObject bioJson = jsonProfileArray.getJSONObject(0);
				nameTextView.setText(name);
				ageTextView.setText(age);
				bioTextView.setText(bio);
				locationTextView.setText(location);
				
				
				
			} 
			else
			{
				//Fail
			}
		} catch (Exception e)
		{
			Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
		}
	}
}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//Button Listener for submit changes. It the profile in the database.
	//This executes the 
	public void submitButton(View view)
	{
		new setProfileTask().execute("http://98.213.107.172/android_connect/update_profile.php");
		
		Intent intent = new Intent(this,UserActivity.class);
		startActivity(intent);
		
		finish();
	}
	
	/*
	 * Set profile executes update_profile.php. It uses the current users email address to update the users name, age, and bio. 
	 */
	private class setProfileTask extends AsyncTask<String, Void, String>
	{

		protected String doInBackground(String... urls)
		{

			return readJSONFeed(urls[0]);
		}
		public String readJSONFeed(String URL)
		{

			StringBuilder stringBuilder = new StringBuilder();
			HttpClient httpClient = new DefaultHttpClient();
			//kaboom
			HttpPost httpPost = new HttpPost(URL);
			try
			{
				Global global = ((Global) getApplicationContext());
				String email = global.getCurrentUser();
				TextView nameTextView = (TextView) findViewById(R.id.nameEditTextEPA);
				TextView ageTextView = (TextView) findViewById(R.id.ageEditTextEPA);
				TextView bioTextView = (TextView) findViewById(R.id.bioEditTextEPA);
				TextView locationTextView = (TextView) findViewById(R.id.locationEditTextEPA);
				
				String name = nameTextView.getText().toString();
				//Split name by space because sleep.
				String[] splited = name.split("\\s+");
				String firstName = splited[0];
				String lastName = splited[1];
				
				String age = ageTextView.getText().toString();
				
				String bio = bioTextView.getText().toString();
				
				String location = locationTextView.getText().toString();
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("first", firstName));
				nameValuePairs.add(new BasicNameValuePair("last", lastName));
				nameValuePairs.add(new BasicNameValuePair("age", age));
				nameValuePairs.add(new BasicNameValuePair("bio", bio));
				nameValuePairs.add(new BasicNameValuePair("location", location));
				nameValuePairs.add(new BasicNameValuePair("email", email));
				
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

		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				//System.out.println(jsonObject.getString("success"));
				if (jsonObject.getString("success").toString().equals("1"))
				{
					//Success
					TextView resultTextView = (TextView) findViewById(R.id.resultTextViewEPA);
					resultTextView.setText("Success");
				} 
				else
				{
					//Fail
					TextView resultTextView = (TextView) findViewById(R.id.resultTextViewEPA);
					resultTextView.setText("Fail");
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}
	

}