package logbook.plugin.scriptloader.api;

import javax.json.JsonObject;

import logbook.api.APIListenerSpi;
import logbook.plugin.scriptloader.data.ScriptLoaderContext;
import logbook.proxy.RequestMetaData;
import logbook.proxy.ResponseMetaData;
import lombok.val;

public class APIListener implements APIListenerSpi {

    @Override
    public void accept(JsonObject obj, RequestMetaData req, ResponseMetaData res) {
        val context = ScriptLoaderContext.get();
        context.execute(req.getRequestURI(), obj, req.getParameterMap());
    }
}
