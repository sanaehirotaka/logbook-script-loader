package logbook.plugin.scriptloader.gui;

import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logbook.internal.LoggerHolder;
import logbook.internal.gui.Tools;
import logbook.internal.gui.WindowController;
import logbook.plugin.PluginContainer;
import logbook.plugin.gui.MainExtMenu;

public class ScriptLoaderMenu implements MainExtMenu {

    @Override
    public MenuItem getContent() {
        MenuItem menu = new MenuItem("ユーザースクリプト");
        menu.setOnAction(e -> {
            try {
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                URL url = ScriptLoaderMenu.class.getClassLoader()
                        .getResource("scriptloader/gui/script_main.fxml");
                FXMLLoader loader = new FXMLLoader(url);
                loader.setClassLoader(PluginContainer.getInstance().getClassLoader());
                Parent root = loader.load();
                stage.setScene(new Scene(root));
                WindowController controller = loader.getController();
                controller.setWindow(stage);

                stage.initOwner(menu.getParentPopup().getOwnerWindow());
                stage.setTitle("ユーザースクリプト");
                Tools.Windows.setIcon(stage);
                stage.show();
            } catch (Exception ex) {
                LoggerHolder.get().warn("ユーザースクリプトを開けませんでした", ex);
            }
        });
        return menu;
    }

}
