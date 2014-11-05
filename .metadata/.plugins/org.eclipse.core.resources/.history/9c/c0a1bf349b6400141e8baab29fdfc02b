package cs460.grouple.grouple;

import cs460.grouple.grouple.R;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import android.support.v4.app.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


public class UserActivity extends ActionBarActivity implements View.OnClickListener
{

	private Button b;
	private ImageView iv;
	private final static int CAMERA_DATA = 0;
	private Bitmap bmp;
	private Intent i;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);

		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		ab.setCustomView(R.layout.actionbar);
		ab.setDisplayHomeAsUpEnabled(true);
		TextView actionbarTitle = (TextView)findViewById(R.id.actionbarTitleTextView);

		Global global = ((Global)getApplicationContext());
		actionbarTitle.setText(global.getName()+"'s Profile");
		View user = findViewById(R.id.userLayout);
		
		try {
			global.fetchNumFriendRequests();
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		global.setNotifications(user);
		
		System.out.println("What the heck is going on now?");
		
		//START KILL SWITCH LISTENER
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("CLOSE_ALL");
		BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
		    // close activity
			  if(intent.getAction().equals("CLOSE_ALL"))
			  {
				  Log.d("app666","we killin the login it");
				  //System.exit(1);
				  finish();
			  }
			  
		  }
		};
		registerReceiver(broadcastReceiver, intentFilter);
		//End Kill switch listener
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation_actions, menu);
		
		//Set up the edit button and image view
		b = (Button) findViewById(R.id.editProfilePhotoButton);
		iv = (ImageView) findViewById(R.id.profilePhoto);
		b.setOnClickListener(this);
		iv.setOnClickListener(this);
		
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
			Intent intent = new Intent("CLOSE_ALL");
			this.sendBroadcast(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            startHomeActivity(null);
        }
        return false;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.editProfilePhotoButton:
			i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(i, CAMERA_DATA);
			break;
		}
	}

	@Override
	protected void onActivityResult(int reqCode, int resCode, Intent d) {
		// TODO Auto-generated method stub
		super.onActivityResult(reqCode, resCode, d);
		if(resCode == RESULT_OK){
			Bundle extras = d.getExtras();
			bmp = (Bitmap) extras.get("data");
			iv.setImageBitmap(bmp);
		}
	}

	/*Start activity functions for going back to home and logging out*/
	public void startHomeActivity(View view)
	{
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
	}
	public void startLoginActivity(View view)
	{
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}	
}
