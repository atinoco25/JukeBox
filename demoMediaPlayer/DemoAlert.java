package demoMediaPlayer;

import java.util.Optional;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * This class is a demo that shows alerts
 */
public class DemoAlert extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  private TextField input;

  @Override
  public void start(Stage primaryStage) {
    
    // A confirm Dialog
    Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
    confirmAlert.setHeaderText("Add a new account?");
    confirmAlert.setContentText("Click cancel to select songs");
    Optional<ButtonType> confirmResult = confirmAlert.showAndWait();

    if (confirmResult.get() == ButtonType.OK) {
      System.out.println("AlertType.CONFIRMATION, Clicked OK");
    }

    if (confirmResult.get() == ButtonType.CANCEL) {
      System.out.println("AlertType.CONFIRMATION, Clicked Cancel");
    }
 
    BorderPane pane = new BorderPane(); 
    input = new TextField("Enter anything");
    pane.setCenter(input);
    
    // Live Code Demo
    // Rick: DELETE THIS WEDNESDAY MORNING !!!!!!!!
    // You may use a lambda instead of writing entire classes
    input.setOnAction(event -> {

      Alert alert = new Alert(AlertType.WARNING);
      alert.setHeaderText("You have selected the maximum songs today");
      alert.setContentText("Try again tomorrow . . . ");
      Optional<ButtonType> result = alert.showAndWait();

      if (result.get() == ButtonType.OK) {
        // This is the only result possible in AlertType.WARNING
        // There are other AlertType modal dialogs that pauses the
        // application until the user clicks a button or enters something
        System.out.println("AlertType.Warning, Clicked OK");
      }
    }); // end of lambda

    
    // KEEP THIS!!!!
    Scene scene = new Scene(pane, 300, 230);
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}