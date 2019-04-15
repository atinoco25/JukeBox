package tests;

import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Pair;
import model.*;

/**
 * This program contains all of the GUI and model objects needed to test
 * functionality for iteration 1. It is an event driven program that
 * uses buttons to imitate how an account would log in and pick songs.
 * All iteration 1 tasks are completed and tested.
 * 
 * @author David Wang
 */


public class FunctionalSpike extends BorderPane{
	//Model fields needed for spike functionality.
	private LoginLogoutSystem log;
	private JukeboxAccountCollection accounts;
	private Account currUser;
	private String s1, s2;
	private SongSelector songSelector;
	
	private ObservableList<String> queue;
	
	//GUI Nodes
	private Button song1, song2, logIn, logOut;
	private Label accLabel, passLabel,  info;
	private TextField acc;
	private PasswordField pass;
	private HBox songBar;
	private GridPane layout;
	
	/**
	 * The constructor initializes all model pieces needed for the spike.
	 * A separate method is called to initialize GUI nodes.
	 */
	public FunctionalSpike() {
		accounts = new JukeboxAccountCollection(false);
		accounts.populateList();
		//Check model for available accounts during spike
		//'Merlin' is the only admin for this spike. 
		log = new LoginLogoutSystem(accounts);
		//The two songs we are using are these two five second songs.
		s1 = "Capture";
		s2 = "Loping Sting";
		queue = FXCollections.observableArrayList();
		songSelector = new SongSelector(queue, false);
		
		InitGUI();
	}
	
	/*
	 * The GUI nodes are initialized. These are initialized to mimic the
	 * look of the functional spike. The HBox and GridPane are used to structure the
	 * nodes. The button listeners are also initialized here.
	 */
	private void InitGUI() {
		song1 = new Button("Select song 1");
		song2 = new Button("Select song 2");
		
		songBar = new HBox();
		songBar.getChildren().addAll(song1, song2);
		songBar.setPadding(new Insets(0, 0, 0, 50));
		songBar.setSpacing(20);
		this.setTop(songBar);
		
		accLabel = new Label("Account Name");
		passLabel = new Label("Password");
		info = new Label("Login first");
		
		acc = new TextField();
		//Password field is used to hide string input
		pass = new PasswordField();
		
		logIn = new Button("Login");
		logOut = new Button("Log out");
		
		layout = new GridPane();
		layout.add(accLabel, 0, 0);
		layout.add(passLabel, 0, 1);
		layout.add(acc, 1, 0);
		layout.add(pass, 1, 1);
		layout.add(logIn, 1, 2);
		layout.add(info, 1, 3);
		layout.add(logOut, 1, 4);
		this.setCenter(layout);
		this.setMargin(layout, new Insets(0, 0, 0, 20));
		layout.setPadding(new Insets(10, 10, 10, 10));
		layout.setHgap(10);
		layout.setVgap(10);
		
		//One button handler is used to handle all the buttons.
		EventHandler<ActionEvent> buttonHandler = new ButtonHandler();
		song1.setOnAction(buttonHandler);
		song2.setOnAction(buttonHandler);
		logIn.setOnAction(buttonHandler);
		logOut.setOnAction(buttonHandler);
	}
	
	/*
	 * updateInfo is called to update the info label whenever certain
	 * events occur. It makes sure to display how many times the user had played a song
	 * and how much more time the user has to play songs.
	 */
	private void updateInfo() {
		acc.clear();
		pass.clear();
		int userPlays = currUser.numberSongPlayedByUserToday();
		//90000 is the number of seconds left (1500 minutes * 60 seconds per min)
		int timeLeft = 90000 - currUser.getTimePlayed();
		int hoursLeft = timeLeft / 3600;
		timeLeft = timeLeft % 3600;
		int minLeft = timeLeft / 60;
		String minSpace = "";
		if (minLeft < 10) {
			minSpace = "0";
		}
		int secLeft = timeLeft % 60;
		String secSpace = "";
		if (secLeft < 10) {
			secSpace = "0";
		}
		info.setText(userPlays+" selected, "+hoursLeft+":" +minSpace+minLeft+":"+secSpace+secLeft);
	}
	
	/*
	 * addUser, as the name suggests, adds a user to the list of accounts.
	 * It is called whenever an admin is logged in. It uses an alert to force
	 * the user to to give a yes/no answer.
	 * It uses the addNewUser method provided by Rick Mercer.
	 */
	private void addUser() {
		Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
	    confirmAlert.setHeaderText("Add a new account?");
	    confirmAlert.setContentText("Click cancel to select songs");
	    Optional<ButtonType> confirmResult = confirmAlert.showAndWait();

	    if (confirmResult.get() == ButtonType.OK) {
	      System.out.println("AlertType.CONFIRMATION, Clicked OK");
	      addNewUser();
	    }

	    if (confirmResult.get() == ButtonType.CANCEL) {
	      System.out.println("AlertType.CONFIRMATION, Clicked Cancel");
	    }
	}
	
	/*
	 * maxSongsReached is called whenever the user tries to queue a song and it
	 * fails. Since there are two ways that this can happen, it figures out why
	 * the queue failed and shows an alert describing it.
	 */
	private void maxSongsReached(String songName) {
		Alert maxSong = new Alert(AlertType.INFORMATION);
		if (currUser.numberSongPlayedByUserToday() == 3) {
			maxSong.setHeaderText(currUser.getAccountName() + " has reached the limit");
		}
		else {
			maxSong.setHeaderText(songName + " max plays reached");
		}
		maxSong.show();
	}
	
	// Note: This code snippet is a modified version of the Custom Login Dialog 
	// example found at: http://code.makery.ch/blog/javafx-dialogs-official/.
	// Modifications by Rick Mercer.
	//
	// Rick is providing this to use "as is" for your Jukebox project
	// and long as you in the above attribution.
	private void addNewUser() {
	  // Create a custom dialog with two input fields
	  Dialog<Pair<String, String>> dialog = new Dialog<>();
	  dialog.setTitle("Adding new user");
      dialog.setHeaderText("Enter the new user ID and password");

	  // Set the button types
	  ButtonType loginButtonType = new ButtonType("Add new user", ButtonData.OK_DONE);
	  dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

	  // Create the Account Name and password labels and fields
	  GridPane grid = new GridPane();
	  grid.setHgap(10);
	  grid.setVgap(10);
	  grid.setPadding(new Insets(20, 150, 10, 10));

	  TextField username = new TextField();
	  username.setPromptText("Account Name");
	  PasswordField password = new PasswordField();
	  password.setPromptText("Password");

	  grid.add(new Label("Account Name:"), 0, 0);
	  grid.add(username, 1, 0);
	  grid.add(new Label("Password:"), 0, 1);
	  grid.add(password, 1, 1);

	  dialog.getDialogPane().setContent(grid);

	  // Convert the result to a username-password-pair when the Add user button is clicked.
	  // This is lambda instead of an instance of a new event handler: shorter code.
	  dialog.setResultConverter(dialogButton -> {
	    if (dialogButton == loginButtonType) {
	      return new Pair<>(username.getText(), password.getText());
	    }
	    return null;
	  });

	  Optional<Pair<String, String>> result = dialog.showAndWait();

	  result.ifPresent(usernamePassword -> {
	    System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
	    accounts.addAccount(usernamePassword.getKey(), usernamePassword.getValue());
	  });

	}

	/*
	 * This class is a listener for all of the buttons.
	 * It allows the user to log in and out.
	 * It also allows the user to pick songs to add to the queue. 
	 */
	private class ButtonHandler implements EventHandler<ActionEvent> {

	  	@Override
	  	public void handle(ActionEvent event) {
	  		
	  		//Lets the user pick "Capture"
	  		if(event.getSource() == song1) {
	  			if(currUser != null) {
	  				if(songSelector.addToQueue(s1, currUser, false)) {
	  					queue.add(s1);
	  					//On successful addition, info is updated
	  					updateInfo();
	  				}
	  				else {
	  					//Otherwise a descriptive alert is displayed
	  					maxSongsReached(s1);
	  				}
	  			}
	  		}
	  		
	  		//Lets the user pick "Loping Sting"
	  		if(event.getSource() == song2) {
	  			if(currUser != null) {
	  				if(songSelector.addToQueue(s2, currUser, false)) {
	  					queue.add(s2);
	  					updateInfo();
	  				}
	  				else {
	  					maxSongsReached(s2);
	  				}
	  			}
	  		}
	  		
	  		//Allows the user to log in
	  		if(event.getSource() == logIn) {
	  			String accountName = acc.getText();
	  			String password = pass.getText();
	  			if (log.logIn(accountName, password)) {
  					currUser = accounts.getAccount(accountName);
	  				
  					//admins are allowed to add users.
  					if (accounts.admin(currUser)) {
  						addUser();
  					}
  					updateInfo();
	  			}
  				else {
  					//unsuccessful log in attempts
  					info.setText("Invalid Credentials");
  					acc.clear();
  					pass.clear();
  				}
	  		}
	  		
	  		//Allows the user to log out
	  		if(event.getSource() == logOut) {
	  			currUser = null;
	  			info.setText("Login first");
	  		}
	  	}
	}
}