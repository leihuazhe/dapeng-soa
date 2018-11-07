package com.github.dapeng.impl.plugins;

import com.github.dapeng.api.AppListener;
import com.github.dapeng.api.Container;
import com.github.dapeng.api.Plugin;
import com.github.dapeng.api.events.AppEvent;
import com.github.dapeng.core.helper.SoaSystemEnvProperties;
import com.github.dapeng.doc.ApiWebSite;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.Controller;

import java.util.ArrayList;
import java.util.List;

public class ApiDocPlugin implements AppListener, Plugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiDocPlugin.class);
    private Container container;
    private Server server = null;

    List<Controller> controllers = new ArrayList<>();

    public ApiDocPlugin(Container container) {
        this.container = container;
        this.container.registerAppListener(this);

    }

    @Override
    public void appRegistered(AppEvent event) {
        //TODO:
        LOGGER.info(getClass().getSimpleName() + "::appRegistered event:[" + event.getSource() + "]");
    }

    @Override
    public void appUnRegistered(AppEvent event) {
        LOGGER.info(getClass().getSimpleName() + "::appUnRegistered event:[" + event.getSource() + "]");
    }

    @Override
    public void start() {
        LOGGER.warn("Plugin::" + getClass().getSimpleName() + "::start");
        Thread thread = new Thread("api-doc-thread") {
            @Override
            public void run() {
                try {

                    server = ApiWebSite.createServer(SoaSystemEnvProperties.SOA_APIDOC_PORT);

                    server.start();
                    System.out.println("api-doc server started at port: " + SoaSystemEnvProperties.SOA_APIDOC_PORT);

                    //server.join();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        };
        thread.setContextClassLoader(ApiDocPlugin.class.getClassLoader());
        thread.start();
    }

    @Override
    public void stop() {
        LOGGER.warn("Plugin::" + getClass().getSimpleName() + "::stop");
        try {
            server.stop();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
