package interpolator;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class Controller {

	private boolean[] pressed = {false, false, false, false, false};
	private Group playbar;
	private HBox controller_assembly;
	
	// This is effectively a collection of the buttons used to control the interpolated video.
	public Controller() {
		
		controller_assembly = new HBox();
		playbar = new Group();
		build_play_bar();
		
		Button play = createButton(0);
		controller_assembly.getChildren().add(play);
		
		Button pause = createButton(1);
		controller_assembly.getChildren().add(pause);
		
		Button skip = createButton(2);
		controller_assembly.getChildren().add(skip);
		
		Button rewind = createButton(3);
		controller_assembly.getChildren().add(rewind);
		
		Button restart = createButton(4);
		controller_assembly.getChildren().add(restart);
		
		controller_assembly.setTranslateX(295);
		
		playbar.getChildren().add(controller_assembly);
		
	}
	
	private void build_play_bar() {
		
		Rectangle playbar = new Rectangle();
		playbar.setWidth(720);
		playbar.setHeight(30);
		playbar.getStyleClass().add("boxing");
		this.playbar.getChildren().add(playbar);
		
	}
	
	// Small factory method for creating buttons, for modularity adjustments as well as code clarity
	private Button createButton(int ind) {
		
		Button btn = new Button();
		btn.setPrefWidth(30);
		btn.setPrefHeight(30);
		btn.getStyleClass().add(".button");
		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				pushButton(ind);
			}
		});
		return btn;
		
	}
	
	public Group getController() {
		return this.playbar;
	}
	
	public boolean[] getPressed() {
		return this.pressed;
	}
	
	public boolean getPressedInd(int ind) {
		return this.pressed[ind];
	}
	
	// Adjusts the boolean array to show that a button has been pushed
	// The object as such also is an abstraction of what actions should be enacted on the video
	public void pushButton(int ind) {
		if (!this.pressed[ind]) {
			this.pressed[ind] = true;
		}
	}
	
	public void resetButton(int ind) {
		if (!this.pressed[ind]) {
			this.pressed[ind] = false;
		}
	}
	
	
	
}
