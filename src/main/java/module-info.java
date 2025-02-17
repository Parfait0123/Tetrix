module tetrix.tetrix {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;
    requires javafx.graphics;
    requires javafx.base;


    opens tetrix.tetrix to javafx.fxml;
    exports tetrix.tetrix;
}