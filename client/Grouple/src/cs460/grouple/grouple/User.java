package cs460.grouple.grouple;

import java.util.ArrayList;

public class User
{
	private String email;
	private String fName;
	private String lName;
	private String bio;
	private ArrayList<User> friends;
	//birthday?
	
	/*
	 * Constructor for User class
	 */
	public User(String email)
	{
		this.email = email;
		System.out.println("Initializing new user.");
	}
	
	/*
	 * Setters for user class below
	 */
	public void setEmail(String email)
	{
		this.email = email;
	}
	public void setFirstName(String fName)
	{
		this.fName = fName;
	}
	public void setLastName(String lName)
	{
		this.lName = lName;
	}
	public void setBio(String bio)
	{
		this.bio = bio;
	}
	
	/*
	 * Getters for user class below
	 */
	public String getEmail()
	{
		return email;
	}
	public String getFirstName()
	{
		return fName;
	}
	public String getLastName()
	{
		return lName;
	}
	public String getFullName()
	{
		return fName + " " + lName;
	}
	public String getBio()
	{
		return bio;
	}
	public int getNumFriends()
	{
		return friends.size(); //check if needs -1
	}
	public int getNumFriendRequests()
	{
		return 1;//not sure yet
	}
	
	/*
	 * To delete the user out of memory and clear all arrays
	 */
	public int delete()
	{
		//delete code here
		
		return 1; //successful
	}
}
