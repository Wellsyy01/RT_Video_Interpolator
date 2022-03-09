package interpolator;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.io.FilenameUtils;

import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.ICodec;

import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class VideoImport {

	private File current_video_file;
	private Media current_video;
	private int frame_count;
	private int frame_rate;
	private StackPane import_assembly;
	private boolean valid_file;
	private Spinner<Integer> frames_held;
	private VideoDetails details;
	private String meta_data;

	private UserAckPopup popup;

	private double w;
	private double h;
	private static String[] acceptable_extensions = {"mp4", "mp3", "wav", "fxm", "flv", "m4a", "m4v"};

	public VideoImport(Stage stage) throws MalformedURLException {

		this.w = 360;
		this.h = 246;

		this.import_assembly = new StackPane();
		this.frame_count = 0;
		this.frame_rate = 0;

		this.popup = new UserAckPopup();

		build_background();
		build_import(stage);

	}

	private void build_background() {

		Rectangle background = new Rectangle();
		background.setWidth(this.w);
		background.setHeight(this.h);
		background.getStyleClass().add("boxing");
		this.import_assembly.getChildren().add(background);

	}

	private void build_import(Stage stage) throws MalformedURLException {

		VBox importer = new VBox();
		importer.setTranslateX(15);
		importer.setTranslateY(15);

		Text i_v = new Text();
		i_v.setText("Import video:");
		i_v.getStyleClass().add("text");
		i_v.setId("vid_imp_title");
		importer.getChildren().add(i_v);

		// Importing the video file to be interpolated, and displaying its name in an associated text box.

		HBox file_import = new HBox();
		file_import.setTranslateX(10);
		file_import.setTranslateY(4);

		FileChooser vid_chooser = new FileChooser();

		TextField vid_file_name = new TextField();
		vid_file_name.setEditable(false);
		vid_file_name.setText("Main Video File");
		vid_file_name.setPrefSize(300, 30);


		file_import.getChildren().add(vid_file_name);

		Button open_file_finder = new Button();
		open_file_finder.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				File comp_video = vid_chooser.showOpenDialog(stage);
				if (comp_video == null) {
					valid_file = false;
					vid_file_name.setText("No file selected");
					current_video_file = null;
					return;
				}
				String file_name;
				try {
					file_name = load_video(comp_video, stage);
				} catch (IOException e) {
					valid_file = false;
					vid_file_name.setText("No file selected");
					current_video_file = null;
					return;
				}
				if (file_name != null) {
					valid_file = true;
					vid_file_name.setText(file_name);
					current_video_file = comp_video;
				} else {
					valid_file = false;
					vid_file_name.setText("Invalid file");
					current_video_file = null;
				}


			}
		});
		open_file_finder.setPrefSize(30, 30);

		file_import.getChildren().add(open_file_finder);
		importer.getChildren().add(file_import);

		// Details about the imported video
		this.details = new VideoDetails();
		this.details.addDetail(this.meta_data);
		StackPane details_assembly = this.details.getDetails();
		details_assembly.setTranslateY(20);
		importer.getChildren().add(details_assembly);


		// If the user wants to limit the amount of frames per second they can adjust this.
		// This would only be necessary if the algorithm provided can run faster than the frame rate of the video.

		Text no_f = new Text();
		no_f.setText("Number of frames held:");
		no_f.getStyleClass().add("Text");
		no_f.setTranslateX(145);
		no_f.setTranslateY(-40);
		importer.getChildren().add(no_f);

		this.frames_held = new Spinner<Integer>();
		SpinnerValueFactory<Integer> fh_fac = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 8, 1);
		this.frames_held.setValueFactory(fh_fac);
		this.frames_held.setTranslateX(255);
		this.frames_held.setTranslateY(-40);
		this.frames_held.setPrefSize(80, 30);
		importer.getChildren().add(this.frames_held);

		this.import_assembly.getChildren().add(importer);


	}

	public StackPane getVideoImporter() {
		return this.import_assembly;
	}

	public int getFramesHeld() {
		return this.frames_held.getValue();
	}

	// Checks video to ensure it is in a valid format
	private String load_video(File c_vid, Stage stage) throws MalformedURLException, IOException {

		String extension = FilenameUtils.getExtension(c_vid.toString());
		boolean flag = false;
		for (String ext : this.acceptable_extensions) {
			if (ext.equals(extension)) {
				flag = true;
			}
		}

		if (!flag) {

			String prep_msg = "The file type submitted is currently not accepted" + "\n" +
					"The acceptable file types are:"+"\n" + "'mp4' 'mp3' 'wav' 'fxm' 'flv' 'm4a' 'm4v'";
			this.popup.trigger_popup(prep_msg, stage);
			return null;
		}

		// this.comp_vid = c_vid;
		Path file_path = Paths.get(c_vid.getAbsolutePath());
		BasicFileAttributes vid_details;
		try {
			vid_details = Files.readAttributes(file_path, BasicFileAttributes.class);
		}
		catch (IOException e) {
			String prep_msg = "Error occurred when loading video";
			this.popup.trigger_popup(prep_msg, stage);
			return null;
		}

		this.current_video = new Media(c_vid.toURI().toURL().toString());

		System.out.println(load_meta_data());

		return (FilenameUtils.getBaseName(c_vid.toString()) + "." + FilenameUtils.getExtension(c_vid.toString()));

	}

	// Some methodology for getting metadata taken from https://gist.github.com/ThomasBassa/f7c20ba4b6341a05cd2375f24f63e6c5


	public String load_meta_data() throws IOException {

		IContainer container = IContainer.make();
		int validate = container.open(this.current_video_file.getName(), IContainer.Type.READ, null);
		if (validate < 0) 
			System.out.println("Failed to open video file");

		int vid_stream_ID = -1;
		IStreamCoder vid_coder = null;
		for (int i = 0 ; i < container.getNumStreams() ; i++) {
			IStream stream = container.getStream(i);
			IStreamCoder stream_coder = stream.getStreamCoder();

			if (stream_coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				vid_stream_ID = i;
				vid_coder = stream_coder;
				break;
			}
		
		}
		
		if (vid_stream_ID == -1) {
			App.log("No video stream found in container");
			return "ERROR";
		} else if (!vid_coder.isOpen()){
			App.log("Could not open video decoder");
		}

		App.log("Video Height - "+vid_coder.getHeight());
		App.log("Video Width - "+vid_coder.getWidth());
		App.log("Video FrameRate - "+vid_coder.getFrameRate());
		
		return "hi";

	}

	public Media getVideo() {
		return this.current_video;
	}

	public File getVideoFile() {
		return this.current_video_file;
	}

	public void skip_forward() {
		this.frame_count = this.frame_count + (10 * this.frame_rate);
	}

	public void skip_forward(int amount) {
		this.frame_count = this.frame_count + (amount * this.frame_rate);
	}

	public void skip_back() {
		this.frame_count = this.frame_count - (10 * this.frame_rate);
		if (this.frame_count < 0)
			this.frame_count = 0;
	}

	public void skip_back(int amount) {
		this.frame_count = this.frame_count - (amount * this.frame_rate);
		if (this.frame_count < 0)
			this.frame_count = 0;
	}

	public Image getWorkingFrame() {

		return null;
	}

}
