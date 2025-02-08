module org.example.warmachines {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.warmachines to javafx.fxml;
    exports org.example.warmachines;
    exports org.example.warmachines.havakaradeniz;
    opens org.example.warmachines.havakaradeniz to javafx.fxml;
    exports org.example.warmachines.SavasAraci;
    opens org.example.warmachines.SavasAraci to javafx.fxml;
    exports org.example.warmachines.Models;
    opens org.example.warmachines.Models to javafx.fxml;
}