package com.brunotadashi.neospring.web;

import com.brunotadashi.neospring.datastructures.ControllersInstances;
import com.brunotadashi.neospring.datastructures.ControllersMap;
import com.brunotadashi.neospring.datastructures.RequestControllerData;
import com.brunotadashi.neospring.util.NeoLogger;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

public class NeoDispatchServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        if (request.getRequestURL().toString().endsWith("favicon.ico")) {
            return;
        }

        String url = request.getRequestURI();
        String httpMethod = request.getMethod().toUpperCase();
        String key = httpMethod + url;
        RequestControllerData data = ControllersMap.values.get(key);
        NeoLogger.log("NeoDispatcherServlet", "URL: " + url + " (" + httpMethod + ") - Handler " + data.getControllerClass() + "." + data.getControllerMethod());

        Object controller;
        NeoLogger.log("DispatcherServlet", "Searching for Controller Instance");
        try {
            controller = ControllersInstances.instances.get(data.controllerClass);
            if (controller == null) {
                NeoLogger.log("DispatcherServlet", "Creating new Controller Instance");
                controller = Class.forName(data.controllerClass).getDeclaredConstructor().newInstance();
                ControllersInstances.instances.put(data.controllerClass, controller);
            }

            Method controllerMethod = null;
            for (Method method : controller.getClass().getMethods()) {
                if (method.getName().equals(data.controllerMethod)) {
                    controllerMethod = method;
                    break;
                }
            }

            NeoLogger.log("DispatcherServlet", "Invoking method " + controllerMethod.getName() + " to handle request");
            PrintWriter out = new PrintWriter(response.getWriter());
            // Invoca o método a partir da instância desse Controller
            out.println(controllerMethod.invoke(controller));
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
