module com.example.learningjavafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.learningjavafx to javafx.fxml;
    exports com.example.learningjavafx;
}