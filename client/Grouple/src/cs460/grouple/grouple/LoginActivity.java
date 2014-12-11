package cs460.grouple.grouple;

import org.json.JSONObject;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity
{
	Button loginButton;
	BroadcastReceiver broadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ActionBar ab = getActionBar();
		ab.hide();
		Log.d("app666", "we created");
		//todo auto capitalize first / last names.
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

	public void startHomeActivity()
	{
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
	}



	public void loginButton(View view)
	{
		// Create helper and if successful, will bring the correct home
		// activity.
		EditText emailEditText = (EditText) findViewById(R.id.emailEditTextLA);
		EditText passwordEditText = (EditText) findViewById(R.id.passwordEditTextLA);
		String email = emailEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		//String email = "test001@gmail.com";
		//String password="password";
		Global global = ((Global) getApplicationContext());
		global.setCurrentUser(email);
		global.fetchName(email);
		try
		{
			Thread.sleep(500);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new getLoginTask()
				.execute("http://98.213.107.172/android_connect/get_login.php?email="
						+ email + "&password=" + password);
	}

	private class getLoginTask extends AsyncTask<String, Void, String>
	{
		protected String doInBackground(String... urls)
		{
			Global global = ((Global) getApplicationContext());
			return global.readJSONFeed(urls[0], null);
		}

		protected void onPostExecute(String result)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").toString().equals("1"))
				{
					// successful
					Global global = ((Global) getApplicationContext());
					// check for current number of friend requests
					global.fetchNumFriendRequests(global.getCurrentUser());
					global.fetchNumFriends(global.getCurrentUser());
					global.fetchNumGroups(global.getCurrentUser());
					global.fetchNumGroupInvites(global.getCurrentUser());
					// Sets this users name.

					global.setCurrentName(global.getName());
					System.out.println("Setting current name to " + global.getName());
					Thread.sleep(1000); // Sleeping to let home activity start up
					startHomeActivity();
					finish(); // Finishing login (possibly save some memory)
				} else
				{
					// failed
					System.out.println("failed");
					TextView loginFail = (TextView) findViewById(R.id.loginFailTextViewLA);
					loginFail.setText(jsonObject.getString("message"));
					loginFail.setVisibility(0);
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
