package tests;

import static org.junit.Assert.*;
import org.junit.Test;
import model.*;

/**
 * The JUnit test
 *
 */
public class JUnitTest {

	/**
	 * This tests the log in and out system
	 */
	@Test
	public void testLogInAndOut() {
		JukeboxAccountCollection accountCollection = new JukeboxAccountCollection(false);
		LoginLogoutSystem logInOut = new LoginLogoutSystem(accountCollection);
		assertFalse(logInOut.isThereSomeoneLoggedIn());
		assertFalse(logInOut.logIn("alexis", "tinoco"));
		accountCollection.addAccount("alexis", "tinoco");
		assertFalse(logInOut.logOut());
		assertFalse(logInOut.logIn("alexis", "tinocoooo"));
		assertTrue(logInOut.logIn("alexis", "tinoco"));
		assertTrue(logInOut.isThereSomeoneLoggedIn());
		
		accountCollection.addAccount("david", "wang");
		assertTrue(logInOut.logIn("david", "wang"));
		assertTrue(logInOut.getLoggedInUserAccount().equals("david"));
		assertTrue(logInOut.logOut());
		assertFalse(logInOut.isThereSomeoneLoggedIn());
		assertTrue(logInOut.getLoggedInUserAccount().equals(""));
	}
	
	/**
	 * This tests the song selector class
	 */
	@Test
	public void testSongSelector() {
		SongSelector songSelector = new SongSelector(null, false);
		assertTrue(songSelector.getSong("Capture").getSongName().equals("Capture"));
		assertTrue(songSelector.getSong("Captureeeee") == null);
		
		Account user = new Account("username", "password");
		assertTrue(songSelector.addToQueue("Capture", user, true) == true);
		assertTrue(songSelector.addToQueue("Captureeee", user, true) == false);
		assertTrue(songSelector.addToQueue("Capture", user, true) == true);
		assertTrue(songSelector.addToQueue("Capture", user, true) == true);
		assertTrue(songSelector.addToQueue("Capture", user, true) == false);
		
		Account user2 = new Account("second", "2");
		songSelector.addToQueue("Capture", user2, true);
	}
	
	/**
	 * This tests the account class
	 */
	public void testAccount() {
		Account user = new Account("username", "password");
		assertTrue(user.getTimePlayed() == 0);
	}
	
	/**
	 * This test the jukebox account collections
	 */
	@Test
	public void testJukeboxAccountCollection() {
		JukeboxAccountCollection collection = new JukeboxAccountCollection(false);
		assertTrue(collection.addAccount("Merlin", "1234567") == false);
		assertTrue(collection.removeAccount("Ryan") == true);
		assertTrue(collection.removeAccount("Ryan") == false);
		assertTrue(collection.getAccount("Ryan") == null);
		Account user = new Account("username", "password");
		assertTrue(collection.admin(user) == false);
	}
}
