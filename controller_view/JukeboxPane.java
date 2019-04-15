package controller_view;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Pair;
import model.*;

/**
 * This program contains a jukebox that allows you to pick up to 3 songs per day per account
 * and the same songs up to 3 times a day for everybody
 * 
 * @author David Wang
 */

public class JukeboxPane extends BorderPane{
	//Model fields needed for spike functionality.
	private LoginLogoutSystem log;
	private JukeboxAccountCollection accounts;
	private Account currUser;
	private SongSelector songSelector;
	
	private boolean persist;
	private final static String songQ = "SongQueue";
	
	//Song table view
	private ObservableList<Song> playsObservableList;
	private TableView<Song> tableView;
	private TableColumn<Song, String> titleColumn, authorColumn;
	private TableColumn<Song, Integer> playsColumn;
	private TableColumn<Song, String> durationColumn;
	
	//Queue table view
	private ObservableList<String> queueObservableList;
	private ListView<String> queueListView;
	
	//GUI Nodes
	private Button addToQueue, logIn, logOut;
	private Label accLabel, passLabel,  info;
	private TextField acc;
	private PasswordField pass;
	private GridPane layout;
	
	private ProgressBar pb;
	private PBUpdate pbUpdate;
	
	/*
	 * The constructor initializes all model pieces needed for the spike.
	 * A separate method is called to initialize GUI nodes.
	 */
	public JukeboxPane(boolean persistence) {
		persist = persistence;
		accounts = new JukeboxAccountCollection(persist);
		//Check model for available accounts during spike
		//'Merlin' is the only admin for this spike. 
		log = new LoginLogoutSystem(accounts);
		
		setUpQueueView(persist);
		songSelector = new SongSelector(queueObservableList, persist);
		setUpSongTableView();
		InitGUI();
	}
	
	/*
	 * This method sets up the queue view
	 */
	private void setUpQueueView(boolean persist) {
		if (persist) {
			List<String> songPersistent = readPersistentData();
			queueObservableList = FXCollections.observableArrayList(songPersistent);
		}
		else {
			queueObservableList = FXCollections.observableArrayList();
		}
		
		queueListView = new ListView<>();
		queueListView.setItems(queueObservableList);
		
		VBox queue = new VBox();
		Text songText = new Text("Song Queue");
		songText.setStyle("-fx-text-fill: grey;" +
						  "-fx-font-size: 16pt;" +
						  "-fx-font-family: Sans Serif;" +
						  "-fx-font-weight: bolder;");
		Text songProgress = new Text("Song Progress");
		songProgress.setStyle("-fx-text-fill: grey;" +
				  "-fx-font-size: 16pt;" +
				  "-fx-font-family: Sans Serif;" +
				  "-fx-font-weight: bolder;");
		pb = new ProgressBar(0);
		pb.setPrefWidth(250);
		
		pbUpdate = new PBUpdate();
		pbUpdate.start();
		
		queue.getChildren().addAll(songText, queueListView, songProgress, pb);
		queue.setSpacing(10);
		this.setMargin(queue, new Insets(20, 20, 0, 0));
		queueListView.setPrefHeight(1000);
		this.setRight(queue);
	}

	/*
	 * This method sets up the song table view
	 */
	@SuppressWarnings("unchecked")
	private void setUpSongTableView() {
		setSongTableColumns();
		setSongTablePlaylist();
		tableView = new TableView<>();
		tableView.setItems(playsObservableList);
		tableView.getColumns().addAll(playsColumn, titleColumn, authorColumn, durationColumn);
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		tableView.setPrefHeight(1000);
		VBox table = new VBox();
		table.setSpacing(10);
		Text songList = new Text("Song List");
		songList.setStyle("-fx-text-fill: grey;" +
				  "-fx-font-size: 16pt;" +
				  "-fx-font-family: Sans Serif;" +
				  "-fx-font-weight: bolder;");
		table.getChildren().addAll(songList, tableView);
		table.setPadding(new Insets(20, 0, 0, 20));
		this.setLeft(table);
		
		songSelector.refreshTimesPlayedToday();
	}

	/*
	 * The GUI nodes are initialized. These are initialized to mimic the
	 * look of the functional spike. The HBox and GridPane are used to structure the
	 * nodes. The button listeners are also initialized here.
	 */
	private void InitGUI() {
		addToQueue = new Button("Add ->");
		this.setCenter(addToQueue);
		
		accLabel = new Label("Account Name");
		passLabel = new Label("Password");
		info = new Label("Login first");
		
		acc = new TextField();
		//Password field is used to hide string input
		pass = new PasswordField();
		
		logIn = new Button("Log in");
		logOut = new Button("Log out");
		
		layout = new GridPane();
		layout.add(accLabel, 0, 1);
		layout.add(passLabel, 0, 2);
		layout.add(acc, 1, 1);
		layout.add(pass, 1, 2);
		layout.add(logIn, 2, 1);
		layout.add(info, 1, 0);
		layout.add(logOut, 2, 2);
		this.setBottom(layout);
		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(10, 10, 10, 10));
		layout.setHgap(10);
		layout.setVgap(10);
		
		//One button handler is used to handle all the buttons.
		EventHandler<ActionEvent> buttonHandler = new ButtonHandler();
		addToQueue.setOnAction(buttonHandler);
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
	private void maxSongsReached(Song song) {
		Alert maxSong = new Alert(AlertType.INFORMATION);
		if (currUser.numberSongPlayedByUserToday() == 3) {
			maxSong.setHeaderText(currUser.getAccountName() + " has reached the limit today");
		}
		else if (currUser.getTimePlayed() + song.getSongDuration() > 90000) {
			maxSong.setHeaderText(currUser.getAccountName() + " has reached free song limit");
		}
		else {
			maxSong.setHeaderText(song.getSongName() + " max plays reached");
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
	 * this method sets up the columns of the table view from the variables that the song class has
	 */
	private void setSongTableColumns() {
		playsColumn = new TableColumn<>("Plays");
		playsColumn.setMinWidth(30);
		playsColumn.setCellValueFactory(new PropertyValueFactory<>("timesPlayedToday"));
		
		titleColumn = new TableColumn<>("Title");
		titleColumn.setMinWidth(150);
		titleColumn.setCellValueFactory(new PropertyValueFactory<>("songName"));
		
		authorColumn = new TableColumn<>("Artist");
		authorColumn.setMinWidth(150);
		authorColumn.setCellValueFactory(new PropertyValueFactory<>("songName"));
		
		durationColumn = new TableColumn<Song, String>("Time");
		durationColumn.setMinWidth(30);
		durationColumn.setCellValueFactory(new Callback<CellDataFeatures<Song, String>, ObservableValue<String>>() {
		    @Override 
			public ObservableValue<String> call(CellDataFeatures<Song, String> p) {
		         int duration = p.getValue().getSongDuration();
		         int minutes = duration / 60;
		         int seconds = duration % 60;
		         return new SimpleStringProperty(String.valueOf(minutes) + ":" + String.valueOf(seconds));
		     }
		  });
		//durationColumn.setCellValueFactory(new PropertyValueFactory<>("songDuration"));
	}
	
	/*
	 * This method returns the song selector
	 */
	private synchronized SongSelector getSelector() {
		return songSelector;
	}

	/*
	 * This method returns the current list selection
	 */
	public Song getSelected() {
		return tableView.getSelectionModel().getSelectedItem();
	}
	
	/*
	 * this method will add the songs to the table view
	 */
	private void setSongTablePlaylist(){
		playsObservableList = FXCollections.observableArrayList();
		
		for(String key: songSelector.getSongCollection().keySet()) {
			playsObservableList.add(songSelector.getSong(key));
		}
	}
	
	/**
	 * This method will write data into a file for future jukebox systems
	 */
	public void writePersistent() {
		accounts.writePersistentData();
		songSelector.writePersistentData();
		
		List<String> writeQ = new ArrayList<String>();
		try {
		      FileOutputStream fileOutput = new FileOutputStream(songQ);
		      ObjectOutputStream out = new ObjectOutputStream(fileOutput);
		      for(String s : queueObservableList) {
		    	  writeQ.add(s);
		      }
		      out.writeObject(writeQ);
		      out.close();
		    
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
	}
	
	/*
	 * This method will read data
	 */
	@SuppressWarnings("unchecked")
	private List<String> readPersistentData() {
		List<String> readQ;
		try {
		      FileInputStream fileOutput = new FileInputStream(songQ);
		      ObjectInputStream in = new ObjectInputStream(fileOutput);
		      try {
				readQ = (ArrayList<String>) in.readObject();
				in.close();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				readQ = new ArrayList<String>();
			}
	    } catch (IOException e) {
	      e.printStackTrace();
	      readQ = new ArrayList<String>();
	    }
		return readQ;
	}
	
	/**
	 * This method closes all the threads
	 */
	public void closeAll() {
		pbUpdate.stopWork();
		songSelector.closeAll();
	}
	
	/*
	 * This method will update the number of time every song has played today in the table
	 */
	private void refreshTable() {
		songSelector.refreshTimesPlayedToday();
		tableView.refresh();
	}
	
	/*
	 * This class is a listener for all of the buttons.
	 * It allows the user to log in and out.
	 * It also allows the user to pick songs to add to the queue. 
	 */
	private class ButtonHandler implements EventHandler<ActionEvent> {

	  	@Override
	  	public void handle(ActionEvent event) {
	  		
	  		if(event.getSource() == addToQueue) {
	  			if(currUser != null && tableView.getSelectionModel().getSelectedItem() != null) {
	  				if(songSelector.addToQueue(tableView.getSelectionModel().getSelectedItem().getSongName(), currUser, false)) {
	  					//On successful addition, info is updated
	  					refreshTable();
	  					updateInfo();
	  				}
	  				else {
	  					//Otherwise a descriptive alert is displayed
	  					maxSongsReached(tableView.getSelectionModel().getSelectedItem());
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
	
	/*
	 * This private class contains the code to show the bar progression
	 */
	private class PBUpdate extends Thread {
		
		private boolean done = false;
		
		/*
		 * While the bar is not done (full) keep showing the progression
		 */
		@Override
		public void run() {
			while(!done) {
				if (getSelector() != null) {
					pb.setProgress(songSelector.songPercentage());
				}
			}
		}
		
		/*
		 * This method will set the progression bar to full, which will stop the thread
		 */
		public void stopWork() {
			done = true;
		}
	}
}
