package logbook.plugin.scriptloader.data;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.json.JsonObject;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import logbook.internal.LoggerHolder;
import logbook.plugin.scriptloader.bean.ScriptLoaderConfig;
import lombok.Getter;
import lombok.val;

/**
 * スクリプトエンジンを管理する
 *
 */
public class ScriptLoaderContext {

    private static ScriptLoaderContext INSTANCE;

    static {
        INSTANCE = new ScriptLoaderContext();
        if (ScriptLoaderConfig.get().isEnable()) {
            INSTANCE.reload();
        }
    }

    @Getter
    private Map<String, ScriptEngine> engines = new LinkedHashMap<>();

    /**
     * 初期化します
     */
    public void reload() {
        val config = ScriptLoaderConfig.get();
        this.engines.clear();
        for (String str : config.getScripts()) {
            initScript(Paths.get(str));
        }
    }

    /**
     * スクリプトファイルを初期化します
     * @param path スクリプトファイル
     */
    public void initScript(Path path) {
        val manager = new ScriptEngineManager();
        try {
            String ext;
            String filename = path.getFileName().toString();
            int idx = filename.lastIndexOf('.');
            if (idx != -1) {
                ext = filename.substring(idx + 1, filename.length());
            } else {
                ext = "js";
            }
            ScriptEngine engine = manager.getEngineByExtension(ext);

            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                engine.eval(reader);
                if (((Invocable) engine).getInterface(Runnable.class) == null) {
                    throw new IllegalStateException("runメソッドが実装されていません");
                }
            }
            this.engines.put(path.toString(), engine);
        } catch (Exception e) {
            LoggerHolder.get().warn("ユーザースクリプトの初期化で例外[file=" + path + "]", e);
        }
    }

    /**
     * スクリプトを呼び出します
     *
     * @param uri リクエストURI
     * @param obj Json
     * @param parameterMap リクエストパラメーター
     */
    public void execute(String uri, JsonObject obj, Map<?, ?> parameterMap) {
        if (ScriptLoaderConfig.get().isEnable()) {
            for (val entry : this.engines.entrySet()) {
                try {
                    val engine = entry.getValue();
                    engine.put("uri", uri);
                    engine.put("res", obj);
                    engine.put("req", parameterMap);
                    ((Invocable) engine).getInterface(Runnable.class).run();
                } catch (Throwable t) {
                    LoggerHolder.get().warn("ユーザースクリプト実行中に例外[file=" + entry.getKey() + "]", t);
                }
            }
        }
    }

    public static ScriptLoaderContext get() {
        return INSTANCE;
    }
}
