package interpolator;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class VideoPlayer {
	
	private double play_bar_height;
	private StackPane assembled_player;
	private ImageView viewport;
	private Image c_image;
	private int c_frame;
	private ArrayList<Image> cached_images;
	private Controller play_bar;
	
	private double w = 720;
	private double h = 405;
	
	/* Factory to create video players
	   Encapsulates all the components required for the videos, such as previously cached images as well as a store
	   for the current image
	 */
	public VideoPlayer() {
		
		this.play_bar_height = 30;
		this.assembled_player = new StackPane();
		this.assembled_player.setPrefWidth(w + (w * 0.02));
		this.assembled_player.setPrefHeight(h + (h * 0.02) + this.play_bar_height);
		
		play_bar = new Controller();
		Group controller = play_bar.getController();
		controller.setTranslateY(198.5);
		
		build_background();
		build_im_view();

		this.assembled_player.getChildren().add(controller);

		this.cached_images = new ArrayList<Image>();
		
	}
	
	private void build_background() {
		
		Rectangle bg = new Rectangle();
		bg.setWidth(this.w + (this.w * 0.02));
		bg.setHeight(this.h + (this.h * 0.02) + this.play_bar_height);
		System.out.println("Width: "+bg.getWidth()+"\n"+"Height: "+bg.getHeight());
		bg.getStyleClass().add("video_background");
		this.assembled_player.getChildren().add(bg);
		
	}
	
	private void build_im_view() {
		
		this.viewport = new ImageView();
		Rectangle2D v_p_rectangle = new Rectangle2D(0, 0, this.w, this.h);
		this.viewport.setFitHeight(this.h);
		this.viewport.setPreserveRatio(true);
		this.viewport.getStyleClass().add("test");
		this.assembled_player.getChildren().add(this.viewport);
		this.assembled_player.setMargin(this.viewport, new Insets(0, 0, this.play_bar_height, 0));
		
	}
	
	// Returns the stack pane: the visual representation of the video player
	public StackPane getPlayer() {
		
		return this.assembled_player;
	
	}
	
	// Videos are played frame by frame, this loads the frame into the video player object one at a time.
	public Image loadFrame(BufferedImage image) {
		
		Image conv_im = SwingFXUtils.toFXImage(image, null);
		
		boolean[] pushed = check_pushed();

		if (pushed[1]) {
			return this.c_image;
		}
		
		this.cached_images.add(conv_im);
		
		// Fit the image either by height or by width, by whichever difference from the native viewbox is greater
		double w_ratio = this.w / image.getWidth();
		double h_ratio = this.h / image.getHeight();
		if (w_ratio > h_ratio) {
			this.viewport.setFitWidth(this.w);
		} else {
			this.viewport.setFitHeight(this.h);
		}
		
		this.viewport.setImage(conv_im);
		return conv_im;
		
	}
	
	// Called inside of a timed loop to check for user interaction
	private boolean[] check_pushed() {
		
		boolean[] pushed = play_bar.getPressed();
		
		// These buttons can be pushed sequentially multiple times, so need to reset after being pushed
		if (pushed[2]) {
			this.play_bar.resetButton(2);
		}
		if (pushed[3]) {
			this.play_bar.resetButton(3);
		}
		if (pushed[4]) {
			this.play_bar.resetButton(4);
			
			// Set video to be paused after resetting
			this.play_bar.resetButton(0);
			pushed[0] = false;
			this.play_bar.pushButton(1);
			pushed[1] = true;
		}
		
		return pushed;
		
	}
	
	public int which_frame(int fps) {
		this.c_frame = this.play_bar.interpolate_what(fps);
		return this.c_frame;
	}
	
	public void establish_controller_vars(int t_frames) {
		this.play_bar.setTFrames(t_frames);
	}
	

}