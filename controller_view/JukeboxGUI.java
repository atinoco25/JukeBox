package controller_view;

import java.util.Optional;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * This GUI allows us to run our Jukebox program
 * It will also read the data from previus Jukebox programs.
 */

public class JukeboxGUI extends Application{
	
	private JukeboxPane all;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
	    primaryStage.setTitle("JukeBox Sprint 2");
	    
	    all = null;
	    
	    Alert readData = new Alert(AlertType.CONFIRMATION);
	    readData.setHeaderText("Read saved data?");
	    readData.setContentText("Press cancel while system testing.");
	    Optional<ButtonType> confirmResult = readData.showAndWait();

	    if (confirmResult.get() == ButtonType.OK) {
	      System.out.println("Reading data");
	      all = new JukeboxPane(true);
	    }

	    else if (confirmResult.get() == ButtonType.CANCEL) {
	      System.out.println("Setting default data");
	      all = new JukeboxPane(false);
	    }
	    
	    Scene scene = new Scene(all, 850, 700);
	    scene.getStylesheets().add("file:controller_view/JukeboxStyle.css");
	    
	    primaryStage.setScene(scene);
	    primaryStage.show();
	    
	    primaryStage.setOnCloseRequest(new PersistOrNot());
	}
	
	private class PersistOrNot implements EventHandler<WindowEvent> {

		@Override
		public void handle(WindowEvent event) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
		    alert.setTitle("Shut Down Option");
		    alert.setHeaderText("Press ok to write persistent object(s)");
		    alert.setContentText("Press cancel while system testing.");
		    Optional<ButtonType> result = alert.showAndWait();

		    if (result.get() == ButtonType.OK) {
		      all.writePersistent();
		    }
			
		    all.closeAll();
		}
		
	}
}
