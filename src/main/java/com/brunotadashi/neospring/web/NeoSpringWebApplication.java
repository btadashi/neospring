package com.brunotadashi.neospring.web;

import com.brunotadashi.neospring.annotations.NeoGetMethod;
import com.brunotadashi.neospring.annotations.NeoPostMethod;
import com.brunotadashi.neospring.datastructures.ControllersMap;
import com.brunotadashi.neospring.datastructures.RequestControllerData;
import com.brunotadashi.neospring.explorer.ClassExplorer;
import com.brunotadashi.neospring.util.NeoLogger;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class NeoSpringWebApplication {
    public static void run(Class<?> sourceClass) {

        java.util.logging.Logger.getLogger("org.apache").setLevel(java.util.logging.Level.OFF);
        long start, finish;

        NeoLogger.showBanner();
        try {
            start = System.currentTimeMillis();
            NeoLogger.log("Embedded Web Container", "Starting NeoSpringApplication");

            extractMetaData(sourceClass);

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

    private static void extractMetaData(Class<?> sourceClass) throws Exception {
        /* Class Explorer */
        List<String> allClasses = ClassExplorer.retrieveAllClasses(sourceClass);

        for (String neoClass : allClasses) {
            Annotation annotations[] = Class.forName(neoClass).getAnnotations();
            for (Annotation classAnnotation : annotations) {
                if (classAnnotation.annotationType().getName().equals("com.brunotadashi.neospring.annotations.NeoController")) {
                    NeoLogger.log("Metadata Explorer", "Found a Controller " + neoClass);
                    extractMethods(neoClass);
                }
            }
        }

        for (RequestControllerData item : ControllersMap.values.values()) {
            NeoLogger.log("", "    + " + item.httpMethod + ":" + item.url + " [" + item.controllerClass + "." + item.controllerMethod + "]");
        }
        /* End of Class Explorer */
    }

    private static void extractMethods(String className) throws Exception {
        String httpMethod = "";
        String path = "";
        for (Method method : Class.forName(className).getDeclaredMethods()) {
            for (Annotation annotation : method.getAnnotations()) {
                if (annotation.annotationType().getName().equals("com.brunotadashi.neospring.annotations.NeoGetMethod")) {
                    path = ((NeoGetMethod) annotation).value();
//                    NeoLogger.log("", " + method: " + method.getName() + " - URL GET = " + path);
                    httpMethod = "GET";

                } else if (annotation.annotationType().getName().equals("com.brunotadashi.neospring.annotations.NeoPostMethod")) {
                    path = ((NeoPostMethod) annotation).value();
//                    NeoLogger.log("", " + method: " + method.getName() + " - URL POST = " + path);
                    httpMethod = "POST";
                }

                RequestControllerData getData = new RequestControllerData(httpMethod, path, className, method.getName());
                ControllersMap.values.put(httpMethod + path, getData);
            }
        }
    }
}
