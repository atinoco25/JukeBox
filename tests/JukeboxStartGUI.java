package tests;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * This program is a functional spike to determine the interactions are 
 * actually working. It is an event-driven program with a graphical user
 * interface to affirm the functionality all Iteration 1 tasks have been 
 * completed and are working correctly. This program will be used to 
 * test your code for the first 100 points of the JukeBox project.
 */

public class JukeboxStartGUI extends Application {
	
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
  	FunctionalSpike spike = new FunctionalSpike();
    BorderPane all = new BorderPane();
    //The functional spike was separated into a BorderPane object.
    all.setBottom(spike);
    
    Scene scene = new Scene(all, 300, 250);
    primaryStage.setScene(scene);
    primaryStage.show();

  }
}