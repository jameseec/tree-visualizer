module io.github.jameseec.treevisualize {
    requires javafx.controls;
    requires javafx.fxml;


    opens io.github.jameseec.treevisualize to javafx.fxml;
    exports io.github.jameseec.treevisualize;
    exports io.github.jameseec.treevisualize.controller;
    opens io.github.jameseec.treevisualize.controller to javafx.fxml;
}