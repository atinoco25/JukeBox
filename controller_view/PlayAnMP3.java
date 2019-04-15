package controller_view;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import model.Song;

/**
 * This program is initialized inside of a javafx application.
 * It simulates a mp3 player and plays any songs that have been
 * added to the queue in FIFO order.
 * Notably, the setOnEndOfMedia method for the MediaPlayer is not functioning
 * correctly so another thread is used to check the duration of each song
 * to manually stop each song. We are also looking into using the autoplay method.
 * 
 * @author David Wang adapter from code proved by Rick Mercer
 */

public class PlayAnMP3 {
	
	private static String path;
	private static List<String> next;
	private boolean playing;
	private MediaPlayer mediaPlayer;
	private ObservableList<String> observableList;
	private HashMap<String, Song> songCollection;
	private TimeCheat timeCount;
	private double percentagePlayed;

	/**
	 * Constructor of the class
	 * @param newObservableList an observable list that can be modified
	 * @param songs a hashmap containing all the available songs
	 */
  public PlayAnMP3(ObservableList<String> newObservableList, HashMap<String, Song> songs){
	percentagePlayed = 0.0;
  	playing = false;
  	mediaPlayer = null;
  	
  	observableList = newObservableList;
  	songCollection = songs;
  	
  	timeCount = new TimeCheat();
  	Thread songTimer = new Thread(timeCount);
  	songTimer.start();
  }
  
  /**
   * This method is used to add a song to the queue.
   * 
   * @param song the song to be added to the queue
   */
  public synchronized void addNext(Song song) {
	observableList.add(song.getSongName());
  }
  
  /*
   * this method returns the observable list
   */
  private synchronized ObservableList<String> getList() {
	  return observableList;
  }
  
  /**
   * Get the percentage of the current progression of the song
   * @return the percentage that the song has played
   */
  public synchronized double getPercentage() {
	  if (getPlay()) {
		  return percentagePlayed;  
	  }
	  else {
		  return 0.0;
	  }
  }
  
  /**
   * This method will set the queue to play
   * @param play true/false should the play start
   */
  public synchronized void setPlaying(boolean play) {
	  playing = play;
  }
  
  /*
   * Get current playing status
   */
  private synchronized boolean getPlay() {
	  return playing;
  }
  
  /*
   * Get the media player
   */
  private synchronized MediaPlayer getPlayer() {
	  return mediaPlayer;
  }
  
  /*
   * Set the media player to a new media player
   */
  private synchronized void setPlayer(MediaPlayer player) {
	  mediaPlayer = player;
  }
  
  /*
   * Set the current percentage of the song
   */
  private synchronized void setPercentage(double percent) {
	  percentagePlayed = percent;
  }
  
  /*
   * Get the song path
   */
  private synchronized String getSongPath(String songName) {
	  return songCollection.get(songName).getPath();
  }
  
  /**
   * Stop the current song from playing
   */
  public synchronized void close() {
	  timeCount.stopWork();
  }
  
  /**
   * This method plays any songs in the queue. Another thread constantly
   * calls this method (from SongSelector).
   */
  public synchronized void play() {
	  //System.out.println("Playing is: " + getPlay() + " media is: " + getPlayer());
	if (!getList().isEmpty() && !getPlay() && getPlayer() == null) {
		path = getSongPath(getList().get(0));
		// Need a File and URI object so the path works on all OSs
	    File file = new File(path);
	    URI uri = file.toURI();
	    // Play one mp3 and and have code run when the song ends
	    Media media = new Media(uri.toString());
	    mediaPlayer = new MediaPlayer(media);
	    getPlayer().setOnEndOfMedia(new EndOfSongHandler());
	    
	    setPlaying(true);
	    getPlayer().play();
	    //System.out.println(mediaPlayer.getOnEndOfMedia());
	    System.out.println("You may need to shut this App down");
	}
  }
  
  /*
   * If there is another song in the queue, play it.
   */
  private class EndOfSongHandler implements Runnable {
    @Override
    public void run() {
      setPlaying(false);
      // This Runnable apparently does not get called all the time.
      // However, I have the same code in my Jukebox and it works.
      // This question "setOnEndOfMedia does not work" is unanswered on the web.
      System.out.println("Song ended");
      getList().remove(0);
      setPlayer(null);
    }
  }
  
  /*
   * On our computers, the end of media never occurs. THe media player status is stuck
   * on playing. Instead, I made a "cheat" class to use song duration to check if a song
   * has finished. If it does, the song is manually stopped. 
   */
  private class TimeCheat implements Runnable {
	  
	  private boolean done = false;
	  
	  /*
	   * This method will allow us to keep the song playing
	   */
	  @Override
	  public void run() {
		  while (!done) {
			  if (getPlayer() != null && getPlay()) {
				  setPercentage(getPlayer().getCurrentTime().toMillis()/getPlayer().getTotalDuration().toMillis());
				  if (!getPlayer().getCurrentTime().lessThanOrEqualTo(getPlayer().getTotalDuration())
					  && getPlayer().getStatus().equals(MediaPlayer.Status.PLAYING)) {
					  setPlaying(false);
					  mediaPlayer.stop();
				  }
			  } 
		  }
	  }
	  
	  /*
	   * Set the current song to finish
	   */
	  public synchronized void stopWork() {
		  done = true;
	  }
  }

}