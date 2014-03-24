package com.test.demo.listener;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.test.demo.filter.EncodingFilter;
import com.test.demo.servlet.HelloServlet;

import javax.servlet.ServletContextEvent;

/**
 * guice listener
 * User: xinge
 * Date: 14-3-17
 * Time: pm 9:51
 */
public class GuiceListener extends GuiceServletContextListener{

    private Injector injector;

    @Override
    protected Injector getInjector() {
        injector = Guice.createInjector(new CustomModule(), new ServletModule(){
            @Override
            protected void configureServlets() {
                super.configureServlets();
                // filter
                filter("/*").through(EncodingFilter.class);
                // servlet
                //serve("/*").with(DispatchServlet.class);
                serve("/hello/hi/*").with(HelloServlet.class);

            }
        });
        return injector;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        super.contextInitialized(servletContextEvent);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        super.contextDestroyed(servletContextEvent);
    }

    private class CustomModule implements Module {
        @Override
        public void configure(Binder binder) {
            //binder.bind(TaskService.class).toProvider(new TaskServiceProvider());     // task by avro nettyServer
            //binder.bind(TaskHttpClient.class).toInstance(new TaskHttpClient());       // task by avro httpServer
        }
    }
}
