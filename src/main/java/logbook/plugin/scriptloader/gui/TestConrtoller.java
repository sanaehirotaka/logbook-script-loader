package logbook.plugin.scriptloader.gui;

import java.io.StringReader;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logbook.internal.gui.Tools;
import logbook.internal.gui.WindowController;
import logbook.plugin.scriptloader.data.ScriptLoaderContext;

public class TestConrtoller extends WindowController {

    @FXML
    private TextField uri;

    @FXML
    private TextArea req;

    @FXML
    private TextArea res;

    @FXML
    void run(ActionEvent event) {
        try {
            JsonObject obj = Json.createReader(new StringReader(res.getText())).readObject();
            Map<?, ?> parameterMap = new ObjectMapper().readValue(req.getText(), Map.class);

            ScriptLoaderContext.get()
                    .execute(uri.getText(), obj, parameterMap);
        } catch (Exception e) {
            Tools.Conrtols.alert(AlertType.WARNING, "テストで例外が発生しました", "テストで例外が発生しました", e, this.getWindow());
        }
    }
}
