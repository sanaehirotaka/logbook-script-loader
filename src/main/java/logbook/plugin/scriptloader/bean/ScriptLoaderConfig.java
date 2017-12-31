package logbook.plugin.scriptloader.bean;

import java.util.ArrayList;
import java.util.List;

import logbook.internal.Config;
import lombok.Data;

/**
 * ユーザースクリプトの設定
 *
 */
@Data
public class ScriptLoaderConfig {

    /** スクリプト */
    private List<String> scripts = new ArrayList<>();

    /** 有効 */
    private boolean enable = true;

    /**
     * アプリケーションのデフォルト設定ディレクトリから{@link ScriptLoaderConfig}を取得します、
     * これは次の記述と同等です
     * <blockquote>
     *     <code>Config.getDefault().get(ScriptLoaderBean.class, ScriptLoaderBean::new)</code>
     * </blockquote>
     *
     * @return {@link ScriptLoaderConfig}
     */
    public static ScriptLoaderConfig get() {
        return Config.getDefault().get(ScriptLoaderConfig.class, ScriptLoaderConfig::new);
    }
}
