package interpolator;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;

public class App extends Application {
	
	// Establishing some constant values for the application
	private int w_b = 1200;
	private int h_b = 800;
	private double ratio = 0.7;
	
	// This is the video player
	private StackPane v_outlet;
	
	
	public static void main(String[] args) {
		launch(args);
	}

	// Need to remove file not found exception throws for final implementation and fix with try catch.
	@Override
	public void start(Stage primaryStage) throws MalformedURLException, FileNotFoundException {

		primaryStage.setTitle("Video Interpolator");
		primaryStage.setWidth(w_b);
		primaryStage.setHeight(h_b);

		// Two columns for each section of the app. Sizes are adjusted to the stage size.
		VBox l_col = new VBox();
		VBox r_col = new VBox();
		l_col.prefWidthProperty().bind(primaryStage.widthProperty().multiply(ratio));
		r_col.prefWidthProperty().bind(primaryStage.widthProperty().multiply(1 - ratio));
		l_col.prefHeightProperty().bind(primaryStage.heightProperty());
		r_col.prefHeightProperty().bind(primaryStage.heightProperty());
		
		// Creating the video player
		VideoPlayer vp = new VideoPlayer(400.0, 300.0);
		v_outlet = vp.getPlayer();
		
		// Test images
		File p1 = new File("src/pic1.jpg");
		File p2 = new File("src/pic2.jpg");
		InputStream stream1 = new FileInputStream(p1);
		InputStream stream2 = new FileInputStream(p2);
		display_frame(vp, stream1);
		
		// Test video
		File vid_file = new File("src/sample-mp4-file-small.mp4");
		Media test_vid = new Media(vid_file.toURI().toURL().toString());
		MediaPlayer player = new MediaPlayer(test_vid);
		player.setAutoPlay(true);
		MediaView video_playback = new MediaView(player);
		
		// Button for testing
		Button btn = new Button();
		btn.setText("Say 'Hello World'");
		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				display_frame(vp, stream2);
			}
		});
		
		
		// Setting up the GUI
		l_col.getChildren().add(v_outlet);
		r_col.getChildren().add(btn);
		l_col.getStyleClass().add("background");
		
		GridPane root = new GridPane();
		l_col.setStyle("-fx-background-color: green;");
		r_col.setStyle("-fx-background-color: red;");
		root.add(l_col, 0, 0);
		root.add(r_col, 1, 0);
		
		Scene scene = new Scene(root, w_b, h_b);
		scene.getStylesheets().add("style.css");
		
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	// Load a frame and then output it into the video player
	private void display_frame(VideoPlayer vp, InputStream image_file) {
		
		Image c_frame = vp.loadFrame(image_file);
		v_outlet.getChildren().remove(1);
		v_outlet.getChildren().add(new ImageView(c_frame));
		
	}
	
	
	
}