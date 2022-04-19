 package interpolator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;

import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.Global;
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
	private static int stream_index;
	private static long mLastPtsWrite;
	private static long seconds_per_frame;
	private int frame_count;
	private int frame_rate;
	private static int vid_height;
	private static int vid_width;
	private StackPane import_assembly;
	private boolean valid_file;
	private Spinner<Integer> frames_held;
	private VideoDetails details;
	private String meta_data;
	private static ArrayList<int[]> video_decomp;
	private static BufferedImage test_im = null;
	private static int count = 0;

	private UserAckPopup popup;

	private double w;
	private double h;
	private boolean ready;
	private static String[] acceptable_extensions = {"mp4", "mp3", "wav", "fxm", "flv", "m4a", "m4v"};

	public VideoImport(Stage stage) throws MalformedURLException {

		this.w = 360;
		this.h = 246;

		this.import_assembly = new StackPane();
		this.frame_count = 0;
		this.frame_rate = 0;

		this.popup = new UserAckPopup();
		
		this.vid_height = 0;
		this.vid_width = 0;
		this.stream_index = -1;
		this.mLastPtsWrite = Global.NO_PTS;
		
		this.ready = false;

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

				try {
					App.prep_vid_to_start();
				} catch (IOException e) {
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


		this.current_video = new Media(c_vid.toURI().toURL().toString());
		this.current_video_file = c_vid;
		
		this.video_decomp = new ArrayList<int[]>();

		this.details.clearDetails();
		this.details.addDetail(load_meta_data());

		return (FilenameUtils.getBaseName(c_vid.toString()) + "." + FilenameUtils.getExtension(c_vid.toString()));

	}

	// Some methodology for getting metadata taken from https://gist.github.com/ThomasBassa/f7c20ba4b6341a05cd2375f24f63e6c5


	private String load_meta_data() throws IOException {

		IContainer container = IContainer.make();
		int validate = container.open(this.current_video_file.getAbsolutePath(), IContainer.Type.READ, null);
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

		App.log("\n"+ "Video Height - "+vid_coder.getHeight());
		App.log("Video Width - "+vid_coder.getWidth());
		App.log("Video FrameRate - "+vid_coder.getFrameRate() + "\n");
		
		this.vid_height = vid_coder.getHeight();
		this.vid_width = vid_coder.getWidth();
		this.frame_rate = (int) vid_coder.getFrameRate().getDouble();
		this.frame_count = (int) (container.getDuration() / 1000) * this.frame_rate;
		
		this.stream_index = vid_stream_ID;
		update_time_between_frames(1.0 / this.frame_rate);
		decompose_video();
		
		return ("Video Resolution - "+vid_coder.getHeight()+"x"+vid_coder.getWidth() +
				"\n" + "Video Frame Rate - " + vid_coder.getFrameRate().getDouble() + " frames per second");

	}

	public Media getVideo() {
		return this.current_video;
	}

	public File getVideoFile() {
		return this.current_video_file;
	}
	
	public int getFrameRate() {
		return this.frame_rate;
	}
	
	public int getFrameCount() {
		return this.video_decomp.size();
	}

	public int[] getWorkingFrame(int ind) {

		return this.video_decomp.get(ind);
	}
	
	private void update_time_between_frames(double time) {
		
		this.seconds_per_frame = (long) (Global.DEFAULT_PTS_PER_SECOND * time);
		
	}
	
	private void decompose_video() {
		
		IMediaReader mediaReader = ToolFactory.makeReader(this.current_video_file.getAbsolutePath());

        // stipulate that we want BufferedImages created in BGR 24bit color space
        mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);

        mediaReader.addListener(new ImageSnapListener());

        // read out the contents of the media file and
        // dispatch events to the attached listener
        while (mediaReader.readPacket() == null) ;
        
        this.ready = true;
        
	}
	
	 private static class ImageSnapListener extends MediaListenerAdapter {

			public void onVideoPicture(IVideoPictureEvent event) {

	            if (event.getStreamIndex() != stream_index) {
	                // if the selected video stream id is not yet set, go ahead an
	                // select this lucky video stream
	                if (stream_index == -1)
	                	stream_index = event.getStreamIndex();
	                // no need to show frames from this video stream
	                else
	                    return;
	            }

	            // if uninitialized, back date mLastPtsWrite to get the very first frame
	            if (mLastPtsWrite == Global.NO_PTS)
	                mLastPtsWrite = event.getTimeStamp() - seconds_per_frame;

	            // if it's time to write the next frame
	            if (event.getTimeStamp() - mLastPtsWrite >= 
	            		seconds_per_frame) {
	            	
	            	count++;
	                BufferedImage im = event.getImage();
	                if (test_im == null); 
	                	test_im = im;
	                
	                int[] pixel_vals = new int[vid_width * vid_height];
	                im.getRGB(0, 0, vid_width, vid_height, pixel_vals, 0, vid_width);
	                video_decomp.add(pixel_vals);

	                // update last write time
	                mLastPtsWrite += seconds_per_frame;
	            }

	        }

	 }
	 
	 public ArrayList<int[]> get_decomp(){
		 return this.video_decomp;
	 }

	public int getWidth() {
		return this.vid_width;
	}
	
	public int getHeight() {
		return this.vid_height;
	}
	
	public boolean getReady() {
		return this.ready;
	}
	
	public BufferedImage getTIM() {
		return this.test_im;
	}
	
}
