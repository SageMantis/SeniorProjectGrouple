package cs460.grouple.grouple;

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
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

/*
 * HomeActivity is Launcher activity and allows the user to log in to his/her acccount.
 */
public class LoginActivity extends Activity
{
	Button loginButton;
	BroadcastReceiver broadcastReceiver;
	ProgressBar progBar;
	TextView loginFail;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// sets up an progress bar spinner that will appear when user hits
		// login.
		progBar = (ProgressBar) findViewById(R.id.progressBarLA);
		progBar.setVisibility(View.GONE);

		// sets up error message that will appear if user enters invalid
		// login/pass.
		loginFail = (TextView) findViewById(R.id.loginFailTextViewLA);


		Log.d("app666", "we created");
		initKillswitchListener();
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	public void startRegisterActivity(View view)
	{
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}

	public void startHomeActivity(String email)
	{
		Intent intent = new Intent(this, HomeActivity.class);
		System.out.println("Email: " + email);
		intent.putExtra("email", email);
		startActivity(intent);
	}

	public void loginButton(View view)
	{
		// Create helper and if successful, will bring the correct home
		// activity.

		// Removes any previous error message from previous failed login
		loginFail.setVisibility(View.INVISIBLE);

		// Makes progress bar visible during processing of login
		progBar.setVisibility(View.VISIBLE);

		EditText emailEditText = (EditText) findViewById(R.id.emailEditTextLA);
		EditText passwordEditText = (EditText) findViewById(R.id.passwordEditTextLA);
		String email = emailEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		// String email = "test001@gmail.com";
		// String password="password";
		Global global = ((Global) getApplicationContext());

		new getLoginTask()
					.execute("http://98.213.107.172/android_connect/get_login.php?email="
							+ email + "&password=" + password);

	}

	private class getLoginTask extends AsyncTask<String, Void, String>
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
				//validated for login
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// successful
					Global global = ((Global) getApplicationContext());

					
					// Login processing finished: progress bar disappear again
					progBar.setVisibility(View.VISIBLE);
					// display message from json (successful login message)
					loginFail.setText(jsonObject.getString("message"));
					loginFail.setTextColor(getResources().getColor(
							R.color.light_green));
					loginFail.setVisibility(View.VISIBLE);

					//get the email
					EditText emailEditText = (EditText) findViewById(R.id.emailEditTextLA);
					String email = emailEditText.getText().toString();

					//load the user into the system
					User user = global.loadUser(email);
					System.out.println("user name in login is currently " + user.getFullName());
					//can't do below here
					user.setIsCurrentUser(true);//hopefully it is pass-by-reference PANDA
					global.addToUsers(user);
					
					Log.d("LoginActivity", "after adding to users in global");
					// Sets this users name.
					
					//starting the home activity with the current users email
					startHomeActivity(email);
					Log.d("LoginActivity", "after startHomeActivity");
					//finish(); // Finishing login (possibly save some memory)
					Log.d("LoginActivity", "after finish");
				} 
				else
				{
					//login validation failed
					// Login processing finished: progress bar disappear again
					progBar.setVisibility(View.GONE);
					System.out.println("failed");
					// display message from json (failed login reason)
					loginFail.setText(jsonObject.getString("message"));
					loginFail.setTextColor(Color.RED);
					loginFail.setVisibility(View.VISIBLE);
				}
			} catch (Exception e)
			{
				Log.d("ReadatherJSONFeedTask", e.getLocalizedMessage());
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			System.exit(0);
		}
		return false;
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
