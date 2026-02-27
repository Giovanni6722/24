module org.gp._4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.gp._4 to javafx.fxml;
    exports org.gp._4;
}