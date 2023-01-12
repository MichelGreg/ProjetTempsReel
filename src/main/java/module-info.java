module fr.uha.ensisa.gm.projet {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    opens fr.uha.ensisa.gm.projet to javafx.fxml;
    exports fr.uha.ensisa.gm.projet;
}