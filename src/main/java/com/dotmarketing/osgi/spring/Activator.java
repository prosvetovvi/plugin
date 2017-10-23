package com.dotmarketing.osgi.spring;

import com.dotcms.repackage.org.apache.logging.log4j.LogManager;
import com.dotcms.repackage.org.apache.logging.log4j.core.LoggerContext;
import com.dotmarketing.loggers.Log4jUtil;
import com.dotmarketing.osgi.GenericBundleActivator;
import org.apache.felix.http.api.ExtHttpService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.web.servlet.DispatcherServlet;


public class Activator extends GenericBundleActivator {

    private LoggerContext pluginLoggerContext;

    @SuppressWarnings("unchecked")
    public void start(BundleContext context) throws Exception {

        LoggerContext dotcmsLoggerContext = Log4jUtil.getLoggerContext();
        pluginLoggerContext = (LoggerContext) LogManager
                .getContext(this.getClass().getClassLoader(),
                        false,
                        dotcmsLoggerContext,
                        dotcmsLoggerContext.getConfigLocation());
        initializeServices(context);
        ClassLoader currentContextClassLoader = Thread.currentThread().getContextClassLoader();
        ServiceReference sRef = context.getServiceReference(ExtHttpService.class.getName());
        if (sRef != null) {
            overrideClasses(context);
            ExtHttpService httpService = (ExtHttpService) context.getService(sRef);
            try {
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                DispatcherServlet dispatcherServlet = new DispatcherServlet();
                dispatcherServlet.setContextConfigLocation("spring/content-servlet.xml");
                httpService.registerServlet("/api", dispatcherServlet, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Thread.currentThread().setContextClassLoader(currentContextClassLoader);
            }
        }
    }

    public void stop(BundleContext context) throws Exception {
        unregisterServices(context);
        Log4jUtil.shutdown(pluginLoggerContext);
    }

}