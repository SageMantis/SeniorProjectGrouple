package cs460.grouple.grouple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

//import cs460.grouple.grouple.LoginActivity.getLoginTask;


import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Build;

public class RegisterActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		if (savedInstanceState == null)
		{
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		getActionBar().hide();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
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
			View rootView = inflater.inflate(R.layout.fragment_register,
					container, false);
			return rootView;
		}
	}

	public void registerButton(View view)
	{
		TextView errorMessageTextView = (TextView) findViewById(R.id.errorMessageTextView);
		EditText passwordEditText = (EditText) findViewById(R.id.passwordEditTextRA);
		EditText rePasswordEditText = (EditText) findViewById(R.id.rePasswordEditTextRA);
		String password = passwordEditText.getText().toString();
		String rePassword = rePasswordEditText.getText().toString();
		if (password.equals(rePassword))
		{
			// write the mf
			new getRegisterTask()
					.execute("http://98.213.107.172/android_connect/register_account.php");

		} else
		{
			errorMessageTextView.setVisibility(0);
		}
	}

	public String readJSONFeed(String URL)
	{
		// Get all the fields and store locally

		EditText emailEditText = (EditText) findViewById(R.id.emailEditTextRA);
		System.out.println("i made it1!");
		EditText passwordEditText = (EditText) findViewById(R.id.passwordEditTextRA);
		EditText fNameEditText = (EditText) findViewById(R.id.fNameEditText);
		EditText lNameEditText = (EditText) findViewById(R.id.lNameEditText);
		EditText rePasswordEditText = (EditText) findViewById(R.id.rePasswordEditTextRA);
		String email = emailEditText.getText().toString();
		String fName = fNameEditText.getText().toString();
		String lName = lNameEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		String rePassword = rePasswordEditText.getText().toString();

		StringBuilder stringBuilder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(URL);
		try
		{

			// Add your data
			System.out.println("email: " + email + " " + fName + " " + lName
					+ " pass:" + password);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
			nameValuePairs.add(new BasicNameValuePair("email", email));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("first", fName));
			nameValuePairs.add(new BasicNameValuePair("last", lName));
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

	private class getRegisterTask extends AsyncTask<String, Void, String>
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
					// account registered successfully
					System.out.println("success!");
					startLoginActivity();
				} else
				{
					/*TO DO:
					 * -add textview for a failed register attempt.
					 * -set text of textview to display message from jsonObject.getString("message")
					*/
					System.out.println("fail!");
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	public void loginButton(View view)
	{
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}

	public void startLoginActivity()
	{
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}

}
