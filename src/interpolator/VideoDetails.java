package interpolator;

import java.util.ArrayList;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class VideoDetails {

	private StackPane detail_assembly;
	private double w;
	private double h;

	private VBox detail_list;
	private ArrayList<Text> details;

	public VideoDetails() {

		this.w = 350;
		this.h = 60;

		this.details = new ArrayList<Text>();

	}

	private void build_detail_box() {
		
		this.detail_assembly = new StackPane();
		this.detail_assembly.setPrefSize(this.h, this.w);

		this.detail_list = new VBox();

		Text title = new Text();
		title.setText("Video details:");
		title.getStyleClass().add("text");
		title.setId("vid_det_text");
		this.detail_list.getChildren().add(title);

		// Add in the details
		for (int i = 0 ; i < details.size(); i++) {
			this.detail_list.getChildren().add(details.get(i));
		}
		
		this.detail_assembly.getChildren().add(this.detail_list);

	}

	public StackPane getDetails() {
		return this.detail_assembly;
	}

	public void addDetail(int ind, String text) {

		Text new_detail = new Text();
		new_detail.setText(text);
		new_detail.getStyleClass().add("details");
		new_detail.setTranslateX(5);
		new_detail.setTranslateY(20);
		this.details.add(ind, new_detail);

		build_detail_box();

	}

	public void addDetail(String text) {

		Text new_detail = new Text();
		new_detail.setText(text);
		new_detail.getStyleClass().add("details");
		new_detail.setTranslateX(5);
		this.details.add(new_detail);

		build_detail_box();

	}

	public void setDetail(int ind, String text) {

		this.details.get(ind).setText(text);

	}

}
