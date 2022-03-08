package interpolator;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class UserAckPopup {

	private String content;
	private Popup popup;
	private StackPane popup_assembly;
	
	private Text popup_content;
	
	private double w;
	private double h;
	
	public UserAckPopup() {
		
		this.w = 150;
		this.h = 100;
		
		this.popup = new Popup();
		this.content = "Default Popup Message";
		
		build_popup();
		
	}
	
	private void build_popup() {
		
		this.popup_assembly = new StackPane();
		
		Rectangle background = new Rectangle();
		background.setWidth(300);
		background.setHeight(180);
		background.getStyleClass().add("boxing");
		this.popup_assembly.getChildren().add(background);
		
		VBox popup_col = new VBox();
		popup_col.setTranslateX(22);
		popup_col.setTranslateY(35);
		
		this.popup_content = new Text();
		this.popup_content.setTextAlignment(TextAlignment.CENTER);
		this.popup_content.setText("Default Popup Message");
		this.popup_content.getStyleClass().add("error_text");
		this.popup_content.setId("error_message");
		popup_col.getChildren().add(this.popup_content);
		
		Button close_popup = new Button();
		close_popup.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				popup.hide();
				
			}
		});
		close_popup.setPrefSize(50, 30);
		close_popup.setTranslateX(105);
		close_popup.setTranslateY(44);
		close_popup.setText("Ok");
		popup_col.getChildren().add(close_popup);
		this.popup_assembly.getChildren().add(popup_col);
		this.popup.getContent().add(popup_assembly);
		
	}
	
	public void trigger_popup(String error_message, Stage stage) {
		
		this.popup_content.setText(error_message);
		
		if (!popup.isShowing()) {
			this.popup.show(stage);
		}
		
	}
	
	
}
