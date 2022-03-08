module VideoInterpolator {
	
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.media;
	requires javafx.graphics;
	requires org.apache.commons.io;
	requires javafx.base;
	requires xuggle.xuggler.noarch;
	requires java.desktop;

    opens interpolator;
} 