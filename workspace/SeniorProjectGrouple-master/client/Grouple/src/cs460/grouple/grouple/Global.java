package cs460.grouple.grouple;

import android.app.Application;

public class Global extends Application
{
	private String currentUser;
	private String acceptEmail;
	private String declineEmail;
	
	public String getCurrentUser()
	{
		return currentUser;
	}
	public void setCurrentUser(String email)
	{
		currentUser = email;
	}
	
	/*PANDA*/
	public void setDeclineEmail(String email)
	{
		declineEmail = email;
	}
	
	public String getDeclineEmail()
	{
		return declineEmail;
	}
	public void setAcceptEmail(String email)
	{
		acceptEmail = email;
	}
	
	public String getAcceptEmail()
	{
		return acceptEmail;
	}
}
