package cs460.grouple.grouple;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

//import cs460.grouple.grouple.RegisterActivity.getRegisterTask;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EditProfileActivity extends ActionBarActivity implements
		View.OnClickListener
{
	private Button b;
	private ImageView iv;
	private final static int CAMERA_DATA = 0;
	private Bitmap bmp;
	byte[] decodedString;
	private Intent i;
	BroadcastReceiver broadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);

		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(true);

		// Resetting error text view
		TextView errorTextView = (TextView) findViewById(R.id.errorTextViewEPA);
		errorTextView.setVisibility(1);

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
					// Log.d("app666","we killin the login it");
					// System.exit(1);
					finish();
				}
			}
		};

		registerReceiver(broadcastReceiver, intentFilter);
		// End Kill switch listener

		// execute php script, using the current users email address to populate
		// the textviews
		new getProfileTask()
				.execute("http://98.213.107.172/android_connect/get_profile.php");

	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	/*
	 * Get profile executes get_profile.php. It uses the current users email
	 * address to retrieve the users name, age, and bio.
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
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						4);
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

		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				System.out.println(jsonObject.getString("success"));
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// Success
					JSONArray jsonProfileArray = (JSONArray) jsonObject
							.getJSONArray("profile");

					String name = jsonProfileArray.getString(0) + " "
							+ jsonProfileArray.getString(1);
					String age = jsonProfileArray.getString(2);
					String bio = jsonProfileArray.getString(3);
					String location = jsonProfileArray.getString(4);
					String img = jsonProfileArray.getString(5);

					// decode image back to android bitmap format
					decodedString = Base64.decode(img, Base64.DEFAULT);

					Log.d("scott", "1st");

					if (decodedString != null)
					{
						Log.d("scott", "2nd");
						bmp = BitmapFactory.decodeByteArray(decodedString, 0,
								decodedString.length);
					}
					Log.d("scott", "3rd");
					// set the image
					Log.d("scott", "4th");
					if (bmp != null)
					{
						Log.d("scott", "5th");
						img = null;
						decodedString = null;
					}
					Log.d("scott", "6th");

					TextView nameTextView = (TextView) findViewById(R.id.nameEditTextEPA);
					TextView ageTextView = (TextView) findViewById(R.id.ageEditTextEPA);
					TextView locationTextView = (TextView) findViewById(R.id.locationEditTextEPA);
					TextView bioTextView = (TextView) findViewById(R.id.bioEditTextEPA);
					if (iv == null)
					{
						Log.d("scott", "7th");
						iv = (ImageView) findViewById(R.id.profilePhoto);
					}
					// JSONObject bioJson = jsonProfileArray.getJSONObject(0);
					nameTextView.setText(name);
					ageTextView.setText(age);
					bioTextView.setText(bio);
					locationTextView.setText(location);
					iv.setImageBitmap(bmp);
				} else
				{
					// Fail
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation_actions, menu);
		// Set up the edit button and image view
		b = (Button) findViewById(R.id.editProfilePhotoButton);
		b.setOnClickListener(this);
		if (iv == null)
		{
			iv = (ImageView) findViewById(R.id.profilePhoto);
		}

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
			global.setAcceptEmail("");
			global.setCurrentUser("");
			global.setDeclineEmail("");
			Intent login = new Intent(this, LoginActivity.class);
			startActivity(login);
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		if (id == R.id.action_home)
		{
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	// Button Listener for submit changes. It the profile in the database.
	// This executes the
	public void submitButton(View view)
	{
		// error checking

		// bio no more than
		TextView bioTextView = (TextView) findViewById(R.id.bioEditTextEPA);
		String bio = bioTextView.getText().toString();
		if (bio.length() > 100)
		{
			TextView errorTextView = (TextView) findViewById(R.id.errorTextViewEPA);
			errorTextView.setText("Bio is too many characters.");
			errorTextView.setVisibility(0);
		} else
		{
			new setProfileTask()
					.execute("http://98.213.107.172/android_connect/update_profile.php");
			Intent intent = new Intent(this, UserActivity.class);
			startActivity(intent);
			finish();
		}

	}

	/*
	 * Set profile executes update_profile.php. It uses the current users email
	 * address to update the users name, age, and bio.
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
			// kaboom
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
				// Split name by space because sleep.
				String[] splitted = name.split("\\s+");
				String firstName = splitted[0];
				String lastName = splitted[1];

				String age = ageTextView.getText().toString();

				String bio = bioTextView.getText().toString();

				String location = locationTextView.getText().toString();

				MultipartEntityBuilder builder = MultipartEntityBuilder
						.create();

				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

				byte[] data;

				// process photo if set and add it to builder
				if (bmp != null)
				{
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bmp.compress(CompressFormat.JPEG, 100, bos);
					data = bos.toByteArray();
					ByteArrayBody bab = new ByteArrayBody(data, ".jpg");
					builder.addPart("profilepic", bab);
					data = null;
					bab = null;
					bos.close();
				}

				// add remaining fields to builder, then execute
				builder.addTextBody("first", firstName, ContentType.TEXT_PLAIN);
				builder.addTextBody("last", lastName, ContentType.TEXT_PLAIN);
				builder.addTextBody("age", age, ContentType.TEXT_PLAIN);
				builder.addTextBody("bio", bio, ContentType.TEXT_PLAIN);
				builder.addTextBody("location", location,
						ContentType.TEXT_PLAIN);
				builder.addTextBody("email", email, ContentType.TEXT_PLAIN);

				httpPost.setEntity(builder.build());

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
					// cleanup
					inputStream.close();
					reader.close();
					bmp = null;
					builder = null;
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
				// System.out.println(jsonObject.getString("success"));
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// Success
					System.out.println("Success");
				} else
				{
					// Fail
					System.out.println("Fail");
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.editProfilePhotoButton:
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

}
