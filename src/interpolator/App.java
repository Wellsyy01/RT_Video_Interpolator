package interpolator;

import org.apache.commons.io.FilenameUtils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;

public class App extends Application {
	
	public static FileWriter log;
	
	private final static File LOGFILE = new File("logs/log.txt");
	
	// Establishing some constant values for the application
	private int w_b = 1280;
	private int h_b = 800;
	private double ratio = 0.7;
	
	// This is the video player
	private static VideoPlayer vp;
	private StackPane v_outlet;
	
	// This is the diagnostics
	private Diagnostic diag;
	private StackPane diag_outlet;
	
	// This is the video importing section
	private static VideoImport vid_import;
	private StackPane vid_imp_outlet;
	private static int vid_width;
	private static int vid_height;
	
	// This is the algorithm importing section
	private AlgorithmImport alg_import;
	private StackPane alg_outlet;
	
	// This is any additional options added
	private static Options options;
	private StackPane opt_outlet;

	private static int frame_rate;
	private static boolean vid_ready = false;
	private static boolean alg_ready = false;
	
	
	public static void main(String[] args) {
		launch(args);
		
	}

	// Need to remove file not found exception throws for final implementation and fix with try catch.
	@Override
	public void start(Stage primaryStage) throws MalformedURLException, FileNotFoundException, IOException {
		
		try {
			this.log = new FileWriter(LOGFILE);
			this.log.write("Opening log file" + "\n");
			this.log.close();
		} catch (IOException e1) {
			System.err.print("Error opening log file");
		}

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
		this.vp = new VideoPlayer();
		this.v_outlet = this.vp.getPlayer();
		v_outlet.setTranslateY(20);
		
		// Separating video player from diagnostics
		Separator vid_diag_separator = new Separator();
		vid_diag_separator.getStyleClass().add("separator");
		vid_diag_separator.setTranslateY(60);
		
		// Creating diagnostics
		this.diag = new Diagnostic(primaryStage);
		this.diag_outlet = this.diag.getDiagnostic();
		this.diag_outlet.setTranslateY(80);
		
		VBox internal_col = new VBox();
		internal_col.setTranslateY(6);
		internal_col.setPrefSize(759, 240);
		internal_col.setPadding(new Insets(0, 0, 5, 0));
		
		// Importing main video
		this.vid_import = new VideoImport(primaryStage);
		this.vid_imp_outlet = this.vid_import.getVideoImporter();
		internal_col.getChildren().add(this.vid_imp_outlet);
		
		// Importing interpolation algorithm
		this.alg_import = new AlgorithmImport(primaryStage);
		this.alg_outlet = this.alg_import.getAlgImporter();
		internal_col.getChildren().add(this.alg_outlet);
		
		// Additional options
		this.options = new Options();
		this.opt_outlet = this.options.getOptions();
		internal_col.getChildren().add(this.opt_outlet);

		// Test images
		File p1 = new File("src/pic1.jpg");
		File p2 = new File("src/pic2.jpg");
		
		// Test video
		/*File vid_file = new File("src/sample-mp4-file-small.mp4");
		Media test_vid = new Media(vid_file.toURI().toURL().toString());
		MediaPlayer player = new MediaPlayer(test_vid);
		player.setAutoPlay(true);
		MediaView video_playback = new MediaView(player);*/
		
		
		// Setting up the GUI
		l_col.getChildren().add(this.v_outlet);
		l_col.getChildren().add(vid_diag_separator);
		l_col.getChildren().add(this.diag_outlet);
		r_col.getChildren().add(internal_col);
		
		GridPane root = new GridPane();
		l_col.setStyle("-fx-background-color: darkgray;");
		r_col.setStyle("-fx-background-color: darkgray;");
		root.add(l_col, 0, 0);
		root.add(r_col, 1, 0);
		
		Scene scene = new Scene(root, w_b, h_b);
		scene.getStylesheets().add("style.css");
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
		
	}
	
	public static void start_video_process(double fr) {
		System.out.println("howdy");

		 Timeline r_vid = new Timeline(new KeyFrame(Duration.seconds(1/fr), (ActionEvent event) ->
	        {
	        	try {
					run_cycle();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }));
        r_vid.setCycleCount(Timeline.INDEFINITE);
        r_vid.play(); 
        
	}
	
	public static void prep_vid_to_start() throws IOException {
		
		vid_width = vid_import.getWidth();
		vid_height = vid_import.getHeight();
		frame_rate = vid_import.getFrameRate();
		System.out.println(vid_import.getFrameCount());
		vp.establish_controller_vars(vid_import.getFrameCount());
		BufferedImage test_im = vid_import.getTIM();
		display_frame(vp, test_im);
		vid_ready = true;
		if (alg_ready) {
			start_video_process(frame_rate);
		}
		
		
	}
	
	public static void prep_alg_to_start(String dir) throws IOException {
		
		create_native_link_file(System.getProperty("os.name"), dir);
		alg_ready = true;
		if (vid_ready) {
			start_video_process(frame_rate);
		}
		
	}

	// Load a frame and then output it into the video player
	private static void display_frame(VideoPlayer vp, BufferedImage image) {
		vp.loadFrame(image); 
		
	}
	
	private static void run_cycle() throws IOException {
		
		int index = vp.which_frame(frame_rate);
		int[] frame = vid_import.getWorkingFrame(index);
		int[] targ_res = options.getTR();
		
		int[] n_frame = Interpolate.link(frame, vid_width, vid_height, targ_res[0], targ_res[1]);
		
		BufferedImage dis_image = new BufferedImage(vid_import.getWidth(), vid_import.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		dis_image.setRGB(0, 0, vid_width, vid_height, n_frame, 0, vid_width);
		display_frame(vp, dis_image);
		
	}
	
	// Create header file for native files to use
	private static void create_native_link_file(String os, String dir) throws IOException {
		
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec("javac -h native/"+dir+" src/interpolator/Interpolate.java");
		log("javac -h native Interpolate.java");
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			log("JNI header creation failed");
		}
	}
	
	// Log the entered text
	public static void log(String log_text) throws IOException {
		
		log = new FileWriter(LOGFILE, true);
		log.append(log_text + "\n");
		log.close();
		
	}
	
	/*private double display_average_framerate(double frame_speed) {
		
		
		
	}*/
	
	
	
}