package com.brunotadashi.neospring.web;

import com.brunotadashi.neospring.explorer.ClassExplorer;
import com.brunotadashi.neospring.util.NeoLogger;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.util.List;

public class NeoSpringWebApplication {
    public static void run(Class<?> sourceClass) {

        java.util.logging.Logger.getLogger("org.apache").setLevel(java.util.logging.Level.OFF);
        long start, finish;

        NeoLogger.showBanner();
        try {
            start = System.currentTimeMillis();
            NeoLogger.log("Embedded Web Container", "Starting NeoSpringApplication");

            /* Class Explorer */
            List<String> allClasses = ClassExplorer.retrieveAllClasses(sourceClass);

            for (String s : allClasses) {
                NeoLogger.log("Class Explorer", "Class Found" + s);
            }
            /* End of Class Explorer */

            Tomcat tomcat = new Tomcat();
            Connector connector = new Connector();
            connector.setPort(8080);
            tomcat.setConnector(connector);
            NeoLogger.log("Embedded Web Container", "Web Container started on port 8080");

            Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());

            Tomcat.addServlet(ctx, "NeoDispatchServlet", new NeoDispatchServlet());

            ctx.addServletMappingDecoded("/*", "NeoDispatchServlet");

            tomcat.start();
            finish = System.currentTimeMillis();
            NeoLogger.log("Embedded Web Container", "NeoSpring Web Application started in " + ((double) (finish - start) / 100)  + " seconds");
            tomcat.getServer().await();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
