package model;

/**
 * This class will determine what is the current user that is logged in.
 * It can also log in an user, if no user is currently logged in.
 * It can also log out the current user.
 * 
 * Created by Alexis Tinoco (March 15, 2018 @ 3:30)
 * Modified: David Wang
 */

public class LoginLogoutSystem {
	private Account currentLoggedIn;
	private JukeboxAccountCollection accountCollection;
	
	/**
	 * Constructor of the class
	 * @param newCollection the collection of jukebox accounts
	 */
	public LoginLogoutSystem(JukeboxAccountCollection newCollection){
		currentLoggedIn = null;
		accountCollection = newCollection;
	}
	
	/**
	 * This method checks if someone is logged in
	 * 
	 * @return true if someone is logged in, false if not
	 */
	public boolean isThereSomeoneLoggedIn() {
		if(currentLoggedIn == null)
			return false;
		else return true;
	}
	
	/**
	 * This method logs in an user
	 * 
	 * @param username the username to log in
	 * @param password the password of the user to log in
	 * @return true if able to log in, false if not
	 */
	public boolean logIn(String username, String password) {
		if(accountCollection.doesAccountExists(username)) {
			if(accountCollection.getAccount(username).checkPassword(password)) {
				currentLoggedIn = accountCollection.getAccount(username);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * This method logs out an user
	 * Return false if there is not any user logged in
	 * 
	 * @return true if able to log out, false if not
	 */
	public boolean logOut() {
		if (isThereSomeoneLoggedIn() == true) {
			currentLoggedIn = null;
			return true;
		}
		
		return false;
	}
	
	/**
	 * This method gets the accounts name of the user logged in
	 * 
	 * @return the accounts name of user logged in, or empty string if nobody logged in
	 */
	public String getLoggedInUserAccount() {
		if (isThereSomeoneLoggedIn() == true) {
			return currentLoggedIn.getAccountName();
		}
		else {
			return "";
		} 
	}
}
