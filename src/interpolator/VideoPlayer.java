package interpolator;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class VideoPlayer {
	
	private int play_bar_height = 30;
	private StackPane assembled_player;
	private Image c_image;
	private ArrayList<InputStream> cached_images;
	
	/* Factory to create video players
	   Encapsulates all the components required for the videos, such as previously cached images as well as a store
	   for the current image
	 */
	public VideoPlayer(double width, double height) {
		
		this.assembled_player = new StackPane();
		
		build_background(width, height);
		build_im_view(width, height);
		
		this.cached_images = new ArrayList<InputStream>();
		
	}
	
	private void build_background(double w, double h) {
		
		Rectangle bg = new Rectangle();
		bg.setWidth(w + (w * 0.1));
		bg.setHeight(h + (h * 0.1) + play_bar_height);
		bg.getStyleClass().add("background");
		this.assembled_player.getChildren().add(bg);
		
	}
	
	private void build_im_view(double w, double h) {
		
		ImageView outlet = new ImageView();
		outlet.getStyleClass().add("test");
		this.assembled_player.getChildren().add(outlet);
		this.assembled_player.getChildren().get(1).toFront();
		this.assembled_player.setMargin(outlet, new Insets(0, 0, play_bar_height, 0));
		
	}
	
	// Returns the stack pane: the visual representation of the video player
	public StackPane getPlayer() {
		
		return this.assembled_player;
	
	}
	
	// Videos are played frame by frame, this loads the frame into the video player object one at a time.
	public Image loadFrame(InputStream image_stream) {

		this.cached_images.add(image_stream);
		this.c_image = new Image(image_stream);

		return this.c_image;
		
	}
	
	

}