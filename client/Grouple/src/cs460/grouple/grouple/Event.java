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
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONObject;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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


public class Event extends Activity
{
	private String email; //primary key (only necessary component of a user)
	private String fName;
	//private imagething profileImg;?
	//birthday?
	private String name;
	private String about;
	private String location;
	private int age;
	//date
	
	/*
	 * Constructor for User class
	 */
	public Event(String email)
	{

		System.out.println("Initializing new event.");
	}
	


	private void setImage(String img)
	{
		//String img = null;//jsonProfileArray.getString(5);
	
		// decode image back to android bitmap format
		/*byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
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
		
       
       //task.get(10000, TimeUnit.MILLISECONDS);
	
		
		return 1;
	}

	private class getUserInfoTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			//Global global = ((Global) getApplicationContext());
			return readJSONFeed(urls[0], null);
		}

		@Override
		protected void onPostExecute(String result)
		{
			try
			{
				//getting json object from the result string
				JSONObject jsonObject = new JSONObject(result);
				//gotta make a json array
				JSONArray jsonArray = jsonObject.getJSONArray("userInfo");
				
				
				//json fetch was successful
				if (jsonObject.getString("success").toString().equals("1"))
				{
					Log.d("getUserInfoOnPost", "success1");

					//at each iteration set to hashmap friendEmail -> 'first last'
					JSONObject o = (JSONObject) jsonArray.get(0);

					//set first name
					String fName = o.getString("first");
					Log.d("getUserInfoOnPost", "after sgrabbinging fname to: " + fName);
					//setFirstName(fName);
					Log.d("getUserInfoOnPost", "after set first name");
					
					//set last name
					String lName = o.getString("last");
					//setLastName(lName);
					
					//set bio
					String bio = o.getString("bio");
					//setBio(bio);
					
					//set location
					String location = o.getString("location");
					//setLocation(location);
					
					//set birthday (not yet implemented)
					//for now do age
				//	int age = Integer.parseInt(o.getString("age"));
					//setAge(age);
					//Log.d("getUserInfoOnPost", "after set age");
					//String fName = jsonObject.getString("fName").toString();
					//setBirthday(fName); 
					
					//get that image niggi
					//String image = o.getString("image");
					//setImage(json string for it);
				} 
				//unsuccessful
				else
				{
					// failed
				}
			} 
			catch (Exception e)
			{
				Log.d("atherjsoninuserpost", "here");
				//Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
			//do next thing here
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
