package logbook.plugin.scriptloader.gui;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import logbook.internal.LoggerHolder;
import logbook.internal.gui.Tools;
import logbook.internal.gui.WindowController;
import logbook.plugin.PluginContainer;
import logbook.plugin.scriptloader.bean.ScriptLoaderConfig;
import logbook.plugin.scriptloader.data.ScriptLoaderContext;
import lombok.val;

/**
 * ユーザースクリプトのコントローラー
 *
 */
public class ScriptLoaderController extends WindowController {

    @FXML
    private ListView<String> scriptList;

    @FXML
    private Label extTypes;

    @FXML
    private CheckBox enableScript;

    private List<String> extensions;

    @FXML
    void initialize() {
        this.scriptList.setItems(FXCollections.observableList(ScriptLoaderConfig.get().getScripts()));
        this.extensions = new ScriptEngineManager().getEngineFactories()
                .stream()
                .map(ScriptEngineFactory::getExtensions)
                .flatMap(List::stream)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        this.extTypes.setText(this.extensions.stream().collect(Collectors.joining(",")));
        this.enableScript.setSelected(ScriptLoaderConfig.get().isEnable());
        // 有効のチェックが変更された時
        this.enableScript.selectedProperty().addListener((ob, o, n) -> {
            if (n) {
                // 有効なら再読込み
                ScriptLoaderContext.get().reload();
            } else {
                // 無効ならスクリプトコンテキストを破棄
                ScriptLoaderContext.get().getEngines().clear();
            }
            // 設定に保存
            ScriptLoaderConfig.get().setEnable(n);
        });
    }

    /**
     * 全てのスクリプトを再読込み
     * @param event
     */
    @FXML
    void reloadContext(ActionEvent event) {
        ScriptLoaderContext.get()
                .reload();
    }

    /**
     * テスト
     * @param event
     */
    @FXML
    void test(ActionEvent event) {
        try {
            Stage stage = new Stage();
            URL url = ScriptLoaderMenu.class.getClassLoader()
                    .getResource("scriptloader/gui/script_test.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            loader.setClassLoader(PluginContainer.getInstance().getClassLoader());
            Parent root = loader.load();
            WindowController controller = loader.getController();
            controller.setWindow(stage);
            stage.setScene(new Scene(root));
            stage.initOwner(this.getWindow());
            stage.setTitle("ユーザースクリプトのテスト");
            Tools.Windows.setIcon(stage);
            stage.show();
        } catch (Exception ex) {
            LoggerHolder.get().warn("ユーザースクリプトのテストを開けませんでした", ex);
        }
    }

    /**
     * 追加
     * @param event
     */
    @FXML
    void addScript(ActionEvent event) {
        val dialog = new FileChooser();
        dialog.getExtensionFilters().add(new ExtensionFilter("ユーザースクリプト", this.extensions.stream()
                .map(e -> "*." + e)
                .toArray(String[]::new)));
        val list = dialog.showOpenMultipleDialog(this.getWindow());
        if (list != null) {
            for (File file : list) {
                Path path = file.toPath();
                ScriptLoaderContext.get()
                        .getEngines()
                        .remove(path.toString());
                if (!ScriptLoaderConfig.get()
                        .getScripts()
                        .contains(path.toString())) {
                    ScriptLoaderConfig.get().getScripts().add(path.toString());
                }
                ScriptLoaderContext.get()
                        .initScript(path);
            }
        }
        this.scriptList.setItems(FXCollections.observableList(ScriptLoaderConfig.get().getScripts()));
    }

    /**
     * 削除
     * @param event
     */
    @FXML
    void removeScript(ActionEvent event) {
        for (String selected : this.scriptList.getSelectionModel().getSelectedItems()) {
            this.scriptList.getItems().remove(selected);
            ScriptLoaderConfig.get()
                    .getScripts()
                    .remove(selected);
            ScriptLoaderContext.get()
                    .getEngines()
                    .remove(selected);
        }
    }
}
