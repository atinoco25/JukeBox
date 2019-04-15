
package model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import controller_view.PlayAnMP3;
import javafx.collections.ObservableList;

/**
 * This class will keep track of all the songs that can be played.
 * It will also determine if a given song can be played and add it
 * to the mp3 player to play. A thread is used so that any songs in
 * the queue will automatically be played.
 * 
 * @author David Wang
 * Modified: Alexis Tinoco
 */
public class SongSelector {
	private HashMap<String, Song> songCollection;
	private PlayAnMP3 player;
	private songPlay songRun;
	private final static String songList = "ListOfSongs";
	
	/**
	 * Constructor the of the class
	 * @param observableList an observable list that can be changed
	 * @param persist if the current system depends on previus systems
	 */
	public SongSelector(ObservableList<String> observableList, boolean persist) {
		if (persist) {
			readSongData();
		}
		else {
			createAvailableSongs();
		}
		
		player = new PlayAnMP3(observableList, songCollection);
		songRun = new songPlay();
		Thread songThread = new Thread(songRun);
		songThread.start();
	}

	/*
	 * Helper method
	 * This method creates the available songs
	 */
	private void createAvailableSongs() {
		songCollection = new HashMap<String, Song>();
		
		Song captureSong = new Song("Capture", "songfiles/Capture.mp3", 5);
		Song lopingStingSong = new Song("Loping Sting", "songfiles/LopingSting.mp3", 5);
		Song danseMacabreSong = new Song("Dance Macabre Violin Hook", "songfiles/DanseMacabreViolinHook.mp3", 34);
		Song determinedTumbaoSong = new Song("Determined Tumbao", "songfiles/DeterminedTumbao.mp3", 20);
		Song swingCheeseSong = new Song("Swing Cheese", "songfiles/SwingCheese.mp3", 15);
		Song theCurtainRisesSong = new Song("The Curtain Rises", "songfiles/TheCurtainRises.mp3", 28);
		Song untameableFireSong = new Song("Untameable Fire", "songfiles/UntameableFire.mp3", 282);
		
		songCollection.put(captureSong.getSongName(), captureSong);
		songCollection.put(lopingStingSong.getSongName(), lopingStingSong);
		songCollection.put(danseMacabreSong.getSongName(), danseMacabreSong);
		songCollection.put(determinedTumbaoSong.getSongName(), determinedTumbaoSong);
		songCollection.put(swingCheeseSong.getSongName(), swingCheeseSong);
		songCollection.put(theCurtainRisesSong.getSongName(), theCurtainRisesSong);
		songCollection.put(untameableFireSong.getSongName(), untameableFireSong);
	}
	
	/**
	 * This method returns the asked song
	 * 
	 * @param name the name of the song asked
	 * @return the song asked, or null if it doesnt exists
	 */
	public synchronized Song getSong(String name) {
		if(songCollection.containsKey(name))
			return songCollection.get(name);
		
		else
			return null;
	}
	
	/**
	 * This method will only add to Queue if the user is allowed to add the given song to queue
	 * And if the song has not been played 3 times today
	 * 
	 * @param name the name of the song
	 * @param user the user that asked for the song to be played
	 * @param isJUnitTest determines if current run is a j unit test
	 * @return true if able to add to queue, false if not
	 */
	public boolean addToQueue(String name, Account user, boolean isJUnitTest) {
		if(meetsAllRequirements(name,user)) {
			System.out.println(name + " added to queue");
			
			if(!isJUnitTest)
				player.addNext(songCollection.get(name));
			
			songCollection.get(name).addATimePlayed();
			user.incPlays(songCollection.get(name).getSongDuration());
			return true;
		}
		else
			return false;
	}
	
	/*
	 * Helper function
	 * And if the user has played less than 3 songs today
	 */
	private boolean meetsAllRequirements(String name, Account user) {
		if(user.numberSongPlayedByUserToday()< 3) {
			if(songCollection.containsKey(name)) {
				if (songCollection.get(name).canBePlayed()) {
				 //added new constraint on total time played.
					int timePlayed = user.getTimePlayed() + songCollection.get(name).getSongDuration();
					if (timePlayed > 90000) {
						return false;
					}
					return true;
				}
			}
		}
			
		return false;
	}
	
	/**
	 * Get the song collection
	 * @return the song collection
	 */
	public synchronized HashMap<String, Song> getSongCollection(){
		return songCollection;
	}
	
	/**
	 * Get the song percentage
	 * @return the song percentage
	 */
	public double songPercentage() {
		return player.getPercentage();
	}
	
	/**
	 * This method will determine if the media player should start
	 * @param play if the music should start
	 */
	public void setPlaying(boolean play) {
		player.setPlaying(play);
	}
	
	@SuppressWarnings("unchecked")
	private void readSongData() {
		try {
		      FileInputStream fileOutput = new FileInputStream(songList);
		      ObjectInputStream in = new ObjectInputStream(fileOutput);
		      try {
				songCollection = (HashMap<String, Song>) in.readObject();
				in.close();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				this.createAvailableSongs();
			}
	    } catch (IOException e) {
	      e.printStackTrace();
	      this.createAvailableSongs();
	    }
	}
	
	/**
	 * This method will write the data for future systems to use
	 */
	public void writePersistentData() {
		try {
		      FileOutputStream fileOutput = new FileOutputStream(songList);
		      ObjectOutputStream out = new ObjectOutputStream(fileOutput);
		      out.writeObject(songCollection);
		      out.close();
		    
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
	}
	
	/**
	 * This method will close all the song threads
	 */
	public synchronized void closeAll() {
		songRun.stopWork();
		player.close();
	}
	
	public void refreshTimesPlayedToday() {
		for(String key: songCollection.keySet()) {
			songCollection.get(key).checkIfNewDay();
		}
	}
	
	/*
	 * This is a thread that constantly tries to run songs.
	 * This was added mainly since setonendofmedia was not functioning
	 * and we needed another way to play songs one after another.
	 * Maybe edited later.
	 */
	private class songPlay implements Runnable {
		
		private boolean done = false;

		@Override
		public void run() {
			while(!done) {
				player.play();
			}
		}
		
		public synchronized void stopWork() {
			done = true;
		}
		
	}
}