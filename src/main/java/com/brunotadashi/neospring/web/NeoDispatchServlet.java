package com.brunotadashi.neospring.web;

import com.brunotadashi.neospring.datastructures.ControllersInstances;
import com.brunotadashi.neospring.datastructures.ControllersMap;
import com.brunotadashi.neospring.datastructures.RequestControllerData;
import com.brunotadashi.neospring.util.NeoLogger;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class NeoDispatchServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        if (request.getRequestURL().toString().endsWith("favicon.ico")) {
            return;
        }

        PrintWriter out = new PrintWriter(response.getWriter());
        Gson gson = new Gson();

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

            if (controllerMethod.getParameterCount() > 0) {
                NeoLogger.log("NeoDispatchServlet", "Method " + controllerMethod.getName() + " has parameters");
                Object arg;
                Parameter parameter = controllerMethod.getParameters()[0];
                if (parameter.getAnnotations()[0].annotationType().getName().equals("com.brunotadashi.neospring.annotations.NeoBody")) {
                    String body = readBytesFromRequest(request);
                    NeoLogger.log("", "    Found Parameter from request of type " + parameter.getType().getName());
                    NeoLogger.log("", "    Parameter content: " + body);
                    arg = gson.fromJson(body, parameter.getType());
                    out.println(gson.toJson(controllerMethod.invoke(controller, arg)));
                }
            } else {
                out.println(gson.toJson(controllerMethod.invoke(controller)));
            }

            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String readBytesFromRequest(HttpServletRequest request) throws Exception {
        StringBuilder str = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        while ((line = br.readLine()) != null) {
            str.append(line);
        }

        return str.toString();
    }
}
