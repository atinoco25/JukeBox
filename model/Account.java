package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;

/**
 * This class will keep the information of a Jukebox account
 * 
 * Created by Alexis Tinoco
 * Modified: David Wang
 */
public class Account implements Serializable {
	private final String password;
	private final String accountName;
	private int userSongsPlayed;
	private int timePlayed;
	private LocalDate localDate;
	
	/**
	 * Constructor of the class, sets up the account's name and the password
	 * 
	 * @param newAccountName name of the account
	 * @param newPassword password of the account
	 */
	public Account(String newAccountName, String newPassword) {
		password = newPassword;
		accountName = newAccountName;
		userSongsPlayed = 0;
		timePlayed = 0;
		localDate = LocalDate.now();
	}
	
	/**
	 * This returns the number of songs played that day by this user.
	 * 
	 * @return number song played by user today
	 */
	public int numberSongPlayedByUserToday() {
		checkIfNewDay();
		
		return userSongsPlayed;
	}
	
	/**
	 * This method checks if the date is the same.
	 * It resets the number of songs played if it is a new day.
	 */
	private void checkIfNewDay() {
		if (!localDate.equals(LocalDate.now())) {
			userSongsPlayed = 0;
			localDate = LocalDate.now();
		}
	}

	/**
	 * This method takes a String containing a password, 
	 * 	and checks if it matched the current account password
	 * 
	 * @param passwordToBeChecked The password that will be compare to the correct account's password
	 * @return true if password is correct, false if incorrect
	 */
	public boolean checkPassword(String passwordToBeChecked) {
		if (password.equals(passwordToBeChecked))
			return true;
		else
			return false;
	}
	
	/**
	 * This method increments the number of times a user has played
	 * a song. It also increments the time the user has played a song
	 * by the duration of the song.
	 * 
	 * @param songLength The duration of a song
	 */
	public void incPlays(int songLength) {
		userSongsPlayed++;
		timePlayed += songLength;
	}
	
	/**
	 * This method returns the account name
	 * @return account's name
	 */
	public String getAccountName() {
		return accountName;
	}
	
	/**
	 * This methods returns the total amount of time played by user today
	 * @return total time played today
	 */
	public int getTimePlayed() {
		return timePlayed;
	}
}
