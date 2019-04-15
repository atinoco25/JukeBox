package model;

import java.io.Serializable;
/**
 * This class represents a Song object. It keeps track of its path, name and its duration.
 * The day and duration are tracked to determine if the song can be played.
 * 
 * @author Alexis Tinoco
 * Modified: David Wang
 */
import java.time.LocalDate;

/**
 * This class contains the object Song, and it is able to play a song
 * along with other methods
 */
// Added to allow package model to exist on GitHub
public class Song implements Serializable {
	private String path;
	private String songName;
	private LocalDate localDate;
	private Integer timesPlayedToday;
	private Integer songDuration;
	
	/**
	 * Constructor of the class
	 * @param newName the name of the song
	 * @param newPath the path of the song
	 * @param newDurationSeconds the duration of the song
	 */
	public Song(String newName, String newPath, int newDurationSeconds) {
		songName = newName;
		path = newPath;
		localDate = LocalDate.now();
		timesPlayedToday = 0;
		songDuration = newDurationSeconds;
	}
	
	/**
	 * Resets times played if it is a new day.
	 */
	public void checkIfNewDay() {
		if(!LocalDate.now().equals(localDate)) {
			timesPlayedToday = 0;
			localDate = LocalDate.now();
		}
	}
	
	/**
	 * Checks if song has been played 3 times today.
	 * 
	 * @return true if the song can be played today, false if not
	 */
	public boolean canBePlayed() {
		checkIfNewDay();
		
		if(timesPlayedToday >= 3) {
			return false;
		}
		return true;
	}
	
	public void addATimePlayed() {
		timesPlayedToday++;
	}
	
	/**
	 * Get the song name
	 * @return the song name
	 */
	public String getSongName() {
		return songName;
	}
	
	/**
	 * Get the song path
	 * @return the song path
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Get the number of times played today
	 * @return the number times played today
	 */
	public int getTimesPlayedToday() {
		return timesPlayedToday;
	}
	
	/**
	 * Get the song duration
	 * @return the song duration
	 */
	public int getSongDuration() {
		return songDuration;
	}
}