package cs460.grouple.grouple;

import java.util.ArrayList;

public class Group
{
	private String name; //name for the group
	private String bio; //bio for the group
	private ArrayList<User> members; //array of all of the current members of the group

	/*
	 * Constructor for Group class
	 */
	public Group(String name)
	{
		this.name = name;
		System.out.println("Initializing new group.");
	}
	
	/*
	 * Setters for group class below
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	public void setBio(String bio)
	{
		this.bio = bio;
	}
	public void addMember(User u)
	{
		members.add(u);
	}
	
	/*
	 * Getters for group class below
	 */
	public String getName()
	{
		return name;
	}
	public String getBio()
	{
		return bio;
	}
	public User getMember(int idx)
	{
		return members.get(idx);
	}
	public ArrayList<User> getMembers()
	{
		return members;
	}
	
	/*
	 * To delete group and all arrays within
	 */
	public int delete()
	{
		//delete code here
		
		return 1; //successful
	}
}
