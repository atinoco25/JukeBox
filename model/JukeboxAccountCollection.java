package model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class keeps a collection of accounts and
 * which accounts are administrators.
 * 
 * @author Alexis Tinoco
 * Modified: David Wang
 */

public class JukeboxAccountCollection {
	private HashMap<String, Account> collectionAccounts;
	private List<String> admins;
	private final static String userList = "ListOfUsers";
	
	/**
	 * Constructor of the class
	 * @param persist let the class know if the current system is persistant
	 */
	public JukeboxAccountCollection(boolean persist) {
		collectionAccounts = new HashMap<>();
		admins = new ArrayList<String>();
		admins.add("Merlin");
		if (persist) {
			readPersistentData();
		}
		else {
			this.populateList();
		}
	}
	
	/**
	 * This populates the account list with initial accounts
	 * This method is used primarily by the spike.
	 */
	public void populateList() {
		this.addAccount("Chris", "1");
		this.addAccount("Devon", "22");
		this.addAccount("River", "333");
		this.addAccount("Ryan", "4444");
		this.addAccount("Merlin", "7777777");
	}
	
	/**
	 * This method adds an account to the collection.
	 * 
	 * @param username the username of the new account
	 * @param password the password of the new account
	 * 
	 * @return true if account succesfully created, false if not
	 */
	public boolean addAccount(String username, String password) {
		if (!collectionAccounts.containsKey(username)) {
			Account newAccount = new Account(username, password);
			collectionAccounts.put(username, newAccount);
			return true;
		}
		
		return false;
	}
	
	/**
	 * This method removes an account
	 * 
	 * @param username the username of the account to be removed
	 * @return true if able to remove, false if not
	 */
	public boolean removeAccount(String username) {
		if (collectionAccounts.containsKey(username)) {
			collectionAccounts.remove(username);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if an account exists
	 * 
	 * @param username username to be checked
	 * @return true if account exists, false if not
	 */
	public boolean doesAccountExists(String username) {
		if(collectionAccounts.containsKey(username))
			return true;
		else 
			return false;
	}
	
	/**
	 * Returns the account being asked for
	 * 
	 * @param username the name of account to be retrieved
	 * @return the account being asked for, or null if it does not exists
	 */
	public Account getAccount(String username) {
		if(collectionAccounts.containsKey(username)) {
			return collectionAccounts.get(username);
		}
		else 
			return null;
	}
	
	/**
	 * Check if a username is an administrator
	 * 
	 * @param user the account to be checked
	 * @return true if it is an admin, false if not
	 */
	public boolean admin(Account user) {
		return admins.contains(user.getAccountName());
	}
	
	/*
	 * This method will read the data from previous programs
	 */
	@SuppressWarnings("unchecked")
	private void readPersistentData() {
		try {
		      FileInputStream fileOutput = new FileInputStream(userList);
		      ObjectInputStream in = new ObjectInputStream(fileOutput);
		      try {
				collectionAccounts = (HashMap<String, Account>) in.readObject();
				in.close();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				this.populateList();
			}
	    } catch (IOException e) {
	      e.printStackTrace();
	      this.populateList();
	    }
	}
	
	/**
	 * This method will write data
	 */
	public void writePersistentData() {
		try {
		      FileOutputStream fileOutput = new FileOutputStream(userList);
		      ObjectOutputStream out = new ObjectOutputStream(fileOutput);
		      out.writeObject(collectionAccounts);
		      out.close();
		    
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
	}
}
