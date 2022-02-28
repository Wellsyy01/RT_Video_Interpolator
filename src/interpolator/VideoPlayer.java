package interpolator;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class VideoPlayer {
	
	private ImageView outlet;
	private Image c_image;
	private ArrayList<InputStream> cached_images;
	
	public VideoPlayer(Group root, double x, double y) {
		
		this.outlet = new ImageView();
		this.outlet.setLayoutX(x);
		this.outlet.setLayoutY(y);
		root.getChildren().add(this.outlet);
		
	}
	
	public int loadFrame(FileInputStream image_file) {
		
		InputStream im_stream = image_file;
		this.cached_images.add(im_stream);

		this.c_image = new Image(im_stream);
		this.outlet.setImage(this.c_image);

		return 1;
	}
	
	

}
