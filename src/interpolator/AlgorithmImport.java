package interpolator;

import java.io.File;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AlgorithmImport {
	
	private StackPane import_assembly;
	private double w;
	private double h;
	
	// Keep track of the driver file, as well as the sub-files needed for the driver file
	private File driver;
	private ArrayList<File> sub_files;
	private boolean valid_file;

	public AlgorithmImport(Stage stage) {
		
		this.w = 360;
		this.h = 246;
		
		this.sub_files = new ArrayList<File>();
		
		this.import_assembly = new StackPane();
		
		build_background();
		build_alg_import(stage);
		
	}
	
	private void build_background() {
		
		Rectangle background = new Rectangle();
		background.setWidth(this.w);
		background.setHeight(this.h);
		background.getStyleClass().add("boxing");
		this.import_assembly.getChildren().add(background);
		
	}
	
	// Build the structure of the Algorithm Importing section
	private void build_alg_import(Stage stage) {
		
		VBox importer = new VBox();
		importer.setTranslateX(15);
		importer.setTranslateY(15);
		
		Text a_d_f = new Text();
		a_d_f.setText("Algorithm driver file:");
		a_d_f.getStyleClass().add("text");
		importer.getChildren().add(a_d_f);
		
		HBox file_import = new HBox();
		file_import.setTranslateX(50);
		file_import.setTranslateY(6);
		
		// Choose a driver algorithm to use, and display it in an non-editable box
		FileChooser alg_chooser = new FileChooser();
		
		TextField alg_file_name = new TextField();
		alg_file_name.setEditable(false);
		alg_file_name.setPrefSize(230, 25);
		file_import.getChildren().add(alg_file_name);
		
		Button open_file_finder = new Button();
		open_file_finder.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				File alg = alg_chooser.showOpenDialog(stage);
				String file_name = load_alg(alg);
				if (file_name != null) {
					valid_file = true;
					alg_file_name.setText(file_name);
				}
			}
		});
		open_file_finder.setPrefSize(30, 25);
		file_import.getChildren().add(open_file_finder);
		
		// Open a separate window/pop-up that allows user input text to be used as the algorithm file
		// This saves to a chosen area on user's computer
		Button open_text_editor = new Button();
		open_text_editor.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				// TODO open a text editor that can be used to create an algorithm file to run
				
			}
		});
		open_text_editor.setPrefSize(30, 25);
		file_import.getChildren().add(open_text_editor);
		importer.getChildren().add(file_import);
		
		Text a_s_f = new Text();
		a_s_f.setText("Algorithm sub-files:");
		a_s_f.getStyleClass().add("text");
		a_s_f.setTranslateY(12);
		importer.getChildren().add(a_s_f);
		
		// Section for importing any sub-files that the algorithms may need to use
		HBox sub_files = new HBox();
		sub_files.setTranslateX(30);
		sub_files.setTranslateY(18);
		
		ListView<String> s_f_list = new ListView<String>();
		ObservableList<String> ob_s_f_list = FXCollections.observableArrayList();
		s_f_list.setItems(ob_s_f_list);
		s_f_list.setPrefSize(280, 120);
		
		sub_files.getChildren().add(s_f_list);
		
		// List of buttons that are needed for functionality purposes
		VBox buttons = new VBox();
		
		Button imp_sub_file = new Button();
		imp_sub_file.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				// TODO import algorithm sub-file and add to list
				
			}
		});
		imp_sub_file.setPrefSize(30, 26);
		buttons.getChildren().add(imp_sub_file);
		
		Button sub_file_text_editor = new Button();
		sub_file_text_editor.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				// TODO same as previous text editor button, but add to sub-file list
				// Can probably throw this in a function
				
			}
		});
		sub_file_text_editor.setPrefSize(30, 26);
		buttons.getChildren().add(sub_file_text_editor);
		
		Button remove_sub_file = new Button();
		remove_sub_file.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				// TODO remove the sub-file chosen in the list box
				
			}
		});
		remove_sub_file.setPrefSize(30, 26);
		buttons.getChildren().add(remove_sub_file);
		sub_files.getChildren().add(buttons);
		importer.getChildren().add(sub_files);
		this.import_assembly.getChildren().add(importer);
		

		
		
	}
	
	public StackPane getAlgImporter() {
		return this.import_assembly;
	}
	
	// Check the associated file is a valid file type (i.e the system can handle it)
	// Returns the file name and extension without the path
	private String load_alg(File alg) {
		
		// TODO Implement process to check algorithm extension / validity
		
		return "";
		
	}
	
}
