package interpolator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class Options {

	private StackPane option_assembly;
	private ChoiceBox<String> targ_res;
	private ChoiceBox<String> max_frame_rate;
	
	private double w;
	private double h;
	
	public Options() {
		
		this.w = 360;
		this.h = 246;
		
		this.option_assembly = new StackPane();
		build_background();
		build_options();
		
	}
	
	private void build_background() {
		
		Rectangle background = new Rectangle();
		background.setWidth(this.w);
		background.setHeight(this.h);
		background.getStyleClass().add("boxing");
		this.option_assembly.getChildren().add(background);
		
	}
	
	/* Extra options for use when running algorithm. This list could theoretically be extended for any future
	   options */
	private void build_options() {
		
		VBox option_list = new VBox();
		option_list.setTranslateX(15);
		option_list.setTranslateY(15);
		
		Text t_r = new Text();
		t_r.setText("Target resolution:");
		t_r.getStyleClass().add("option_text");
		option_list.getChildren().add(t_r);
		
		this.targ_res = new ChoiceBox<String>();
		List<String> choices = new ArrayList<String>();
		choices.add("Option 1");
		choices.add("Option 2");
		choices.add("Option 3");
		ObservableList<String> ob_choices = FXCollections.observableList(choices);
		this.targ_res.setTranslateX(255);
		this.targ_res.setTranslateY(25);
		this.targ_res.setItems(ob_choices);
		
		option_list.getChildren().add(targ_res);
		
		Text m_f_r = new Text();
		m_f_r.setText("Maximum frame rate:");
		m_f_r.getStyleClass().add("option_text");
		m_f_r.setTranslateY(30);
		
		option_list.getChildren().add(m_f_r);
		
		this.max_frame_rate = new ChoiceBox<String>();
		choices = new ArrayList<String>();
		choices.add("Option 1");
		choices.add("Option 2");
		choices.add("Option 3");
		ob_choices = FXCollections.observableList(choices);
		this.max_frame_rate.setTranslateX(255);
		this.max_frame_rate.setTranslateY(55);
		this.max_frame_rate.setItems(ob_choices);
		option_list.getChildren().add(max_frame_rate);
		
		this.option_assembly.getChildren().add(option_list);
		
	}
	
	public StackPane getOptions() {
		return this.option_assembly;
	}
	
}
