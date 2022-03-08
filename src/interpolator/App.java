package interpolator;

import org.apache.commons.io.FilenameUtils;

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

import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public class App extends Application {
	
	// Establishing some constant values for the application
	private int w_b = 1280;
	private int h_b = 800;
	private double ratio = 0.7;
	
	// This is the video player
	private VideoPlayer vp;
	private StackPane v_outlet;
	
	// This is the diagnostics
	private Diagnostic diag;
	private StackPane diag_outlet;
	
	// This is the video importing section
	private VideoImport vid_import;
	private StackPane vid_imp_outlet;
	
	// This is the algorithm importing section
	private AlgorithmImport alg_import;
	private StackPane alg_outlet;
	
	// This is any additional options added
	private Options options;
	private StackPane opt_outlet;
	
	
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
		InputStream stream1 = new FileInputStream(p1);
		InputStream stream2 = new FileInputStream(p2);
		display_frame(vp, stream1);
		
		// Test video
		File vid_file = new File("src/sample-mp4-file-small.mp4");
		Media test_vid = new Media(vid_file.toURI().toURL().toString());
		MediaPlayer player = new MediaPlayer(test_vid);
		player.setAutoPlay(true);
		MediaView video_playback = new MediaView(player);
		
		AlgCompiler ac = new AlgCompiler();
		ac.setAlg("src/Interpolate.java");
		try {
			ac.run_command_line();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		l_col.getChildren().add(this.v_outlet);
		l_col.getChildren().add(vid_diag_separator);
		l_col.getChildren().add(this.diag_outlet);
		r_col.getChildren().add(internal_col);
		
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
	
	
	
	private InputStream get_frame(File video) {
		
		/*IContainer stream_container = IContainer.make();
		
		if (stream_container.open(video.getName(), IContainer.Type.READ, null) < 0)
		      System.out.println("could not open file: " + video.getName());
		
		
		// Get correct video stream and it's decoder
		int videoStreamIndex;
		IStreamCoder videoCoder = null;
		for (int i = 0 ; i < stream_container.getNumStreams() ; i++) {
			
			IStream stream = stream_container.getStream(i);
			IStreamCoder stream_coder = stream.getStreamCoder();
			
			if (stream_coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO)
		      {
		        videoStreamIndex = i;
		        videoCoder = stream_coder;
		        break;
		      }
			
		}
		
		if (videoCoder == null) {
			System.out.println("Video stream cannot be found");
		}
		
		return null;
		*/
		
		IMediaReader reader = ToolFactory.makeReader(video.getName());
		reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
		return null;
		
	}
	
	private static class ImageSnapListener {
		 
        /*public void onVideoPicture(IVideoPictureEvent event) {
 
            if (event.getStreamIndex() != mVideoStreamIndex) {
                // if the selected video stream id is not yet set, go ahead an
                // select this lucky video stream
                if (mVideoStreamIndex == -1)
                    mVideoStreamIndex = event.getStreamIndex();
                // no need to show frames from this video stream
                else
                    return;
            }
 
            // if uninitialized, back date mLastPtsWrite to get the very first frame
            if (mLastPtsWrite == Global.NO_PTS)
                mLastPtsWrite = event.getTimeStamp() - MICRO_SECONDS_BETWEEN_FRAMES;
 
            // if it's time to write the next frame
            if (event.getTimeStamp() - mLastPtsWrite >= 
                    MICRO_SECONDS_BETWEEN_FRAMES) {
                                 
                String outputFilename = dumpImageToFile(event.getImage());
 
                // indicate file written
                double seconds = ((double) event.getTimeStamp()) / 
                    Global.DEFAULT_PTS_PER_SECOND;
                System.out.printf(
                        "at elapsed time of %6.3f seconds wrote: %s\n",
                        seconds, outputFilename);
 
                // update last write time
                mLastPtsWrite += MICRO_SECONDS_BETWEEN_FRAMES;
            }
 
        }
         
        private String dumpImageToFile(BufferedImage image) {
            try {
                String outputFilename = outputFilePrefix + 
                     System.currentTimeMillis() + ".png";
                ImageIO.write(image, "png", new File(outputFilename));
                return outputFilename;
            } 
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
 
 	*/
    } 

	// Load a frame and then output it into the video player
	private void display_frame(VideoPlayer vp, InputStream image_file) {
		
		/*vp.loadFrame(image_file);
		v_outlet.getChildren().remove(1);
		
		v_outlet.getChildren().add(new ImageView(c_frame));*/
		
	}
	
	private void run_cycle() {
		
		
		
	}
	
	/*private double display_average_framerate(double frame_speed) {
		
		
		
	}*/
	
	
	
}