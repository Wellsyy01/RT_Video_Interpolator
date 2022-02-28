package interpolator;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import java.io.File;
import java.net.MalformedURLException;

public class App extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {



		primaryStage.setTitle("CAAAAANT");
		Button btn = new Button();
		btn.setText("Say 'Hello World'");
		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				System.out.println("Hello World!");
			}
		});

		VBox l_col = new VBox();
		VBox r_col = new VBox();
		
		MediaView video_playback = new MediaView();
		l_col.getChildren().add(video_playback);
		
		
		StackPane root = new StackPane();

		root.getChildren().addAll(l_col, r_col);
		
		primaryStage.setScene(new Scene(root, 300, 250));
		primaryStage.show();

	}

	private int load_video(File video) throws MalformedURLException {

		Media m_vid = new Media(video.toURI().toURL().toString());

		// Check for loading successfully
		if (true) {
			return 1;
		} else {
			return 0;
		}

	}
	
	
	
	
}
