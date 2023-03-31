module com.example.hissefx {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;

    opens com.isikenes.hissefx to javafx.fxml;
    exports com.isikenes.hissefx;
}