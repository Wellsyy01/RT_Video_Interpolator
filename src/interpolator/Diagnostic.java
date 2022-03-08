package interpolator;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Diagnostic {
	
	private String[] acceptable_extensions = {"mp4", "mp3", "wav", "fxm", "flv", "m4a", "m4v"};
	private double w;
	private double h;
	
	private double average_fps;
	private double overall_accuracy;
	private ArrayList<Double> fps_vals;
	private ArrayList<Double> acc_vals;
	private File comp_vid;
	private StackPane diagnostic_assembly;
	private boolean valid_file;
	
	// Showing the average speed and accuracy of the algorithm when run with the supplied file.
	public Diagnostic(Stage stage) {
		
		this.average_fps = 0.0;
		this.overall_accuracy = 0.0;
		this.fps_vals = new ArrayList<Double>();
		this.acc_vals = new ArrayList<Double>();
		
		this.w = 800;
		this.h = 210;
		
		this.diagnostic_assembly = new StackPane();
		build_background();
		build_diagnostics(stage);
		
	}
	
	private void build_background() {
		
		
		Rectangle bg = new Rectangle();
		bg.setWidth(this.w);
		bg.setHeight(this.h);
		bg.getStyleClass().add("boxing");
		
		this.diagnostic_assembly.getChildren().add(bg);
		
	}
	
	private void build_diagnostics(Stage stage) {
		
		HBox diagnostic_box = new HBox();
		diagnostic_box.setTranslateX(75);
		diagnostic_box.setTranslateY(30);
		
		// This section displays the average speed of the algorithm
		Text afps_display = new Text();
		afps_display.getStyleClass().add("text");
		afps_display.setId("afps_text");
		afps_display.setText("Averaging a speed" + "\n" + "of: " + "\n" + "      " + this.average_fps + " frames per second");
		afps_display.setTranslateY(27);
		
		diagnostic_box.getChildren().add(afps_display);
		
		// Vertical separator between speed and accuracy sections
		Separator separate = new Separator();
		separate.getStyleClass().add("separator");
		separate.setRotate(90);
		separate.setTranslateX(-70);
		separate.setTranslateY(73);
		separate.setPrefWidth(this.h);
		diagnostic_box.getChildren().add(separate);
		
		// This section displays the average accuracy of the algorithm
		VBox accuracy_diag = new VBox();
		
		Text acc_title = new Text();
		acc_title.setText("Import comparison video file:");
		acc_title.getStyleClass().add("text");
		acc_title.setId("acc_title");
		acc_title.setTranslateX(-150);
		accuracy_diag.getChildren().add(acc_title);
		
		HBox file_import = new HBox();
		file_import.setTranslateX(-120);
		file_import.setTranslateY(12);
		
		FileChooser comp_vid_chooser = new FileChooser();
		
		TextField comp_vid_file_name = new TextField();
		comp_vid_file_name.setEditable(false);
		comp_vid_file_name.setPrefSize(300, 30);
		comp_vid_file_name.setText("Video File Name");
		file_import.getChildren().add(comp_vid_file_name);
		
		Button open_file_finder = new Button();
		open_file_finder.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				File comp_video = comp_vid_chooser.showOpenDialog(stage);
				String file_name = load_comp_video(comp_video);
				if (file_name != null) {
					valid_file = true;
					comp_vid_file_name.setText(file_name);
				}
			}
		});
		open_file_finder.setPrefSize(30, 30);
		
		file_import.getChildren().add(open_file_finder);
		accuracy_diag.getChildren().add(file_import);
		
		Text aacc = new Text();
		aacc.setText("Average accuracy of:" + "\n"
					+ "                          "
					+ this.overall_accuracy + " for each frame");
		aacc.getStyleClass().add("text");
		aacc.setId("accuracy_text");
		aacc.setTranslateX(-100);
		aacc.setTranslateY(30);
		accuracy_diag.getChildren().add(aacc);
		diagnostic_box.getChildren().add(accuracy_diag);
		
		this.diagnostic_assembly.getChildren().add(diagnostic_box);
		
	}
	
	public StackPane getDiagnostic() {
		return this.diagnostic_assembly;
	}
	
	/* Checks the supplied file to check if it is a valid format, and returns the name and extension of the file
	   without the path
	 */
	public String load_comp_video(File c_vid) {
		
		String extension = FilenameUtils.getExtension(c_vid.toString());
		boolean flag = false;
		for (String ext : acceptable_extensions) {
			if (ext.equals(extension)) {
				flag = true;
			}
		}
		
		if (!flag) {
			// TODO Pop-up to tell that the video format is not accepted
			return null;
		}
		
		this.comp_vid = c_vid;
		
		return (FilenameUtils.getBaseName(c_vid.toString()) + "." + FilenameUtils.getExtension(c_vid.toString()));
	
	}
	
	// Updated using common formula
	public void update_average(double n_speed) {
		
		double new_total = (this.average_fps * this.fps_vals.size()) + n_speed;
		this.fps_vals.add(n_speed);
		this.average_fps = new_total / this.fps_vals.size();
		
		// TODO Update text to reflect new average
		
	}

}
