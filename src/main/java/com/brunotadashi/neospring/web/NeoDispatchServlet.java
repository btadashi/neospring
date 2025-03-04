package com.brunotadashi.neospring.web;

import com.brunotadashi.neospring.datastructures.*;
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
import java.lang.reflect.Field;
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

                injectDependencies(controller);
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

    private void injectDependencies(Object client) throws Exception {
        for (Field attr : client.getClass().getDeclaredFields()) {
            String attrType = attr.getType().getName();
            NeoLogger.log("IsiDispatcherServlet", " Injected "+ attr.getName() + " Field has type " + attrType);
            Object serviceImpl;
            if (DependencyInjectionMap.objects.get(attrType) == null) {
                // Tem pela declaração da interface?
                NeoLogger.log("DependencyInjection", "Could not find Instance for " + attrType);
                String implType = ServiceImplementationMap.implementations.get(attrType);

                // Precisamos bucscar pela declaração da implementação.
                if (implType != null) {
                    NeoLogger.log("DependencyInjection", "Found Instance for " + implType);

                    // Se encontramos a declaração da implementação, precisamos ver se tem alguma instância.
                    serviceImpl = DependencyInjectionMap.objects.get(implType);

                    // Se não tiver, criamos um novo objeto.
                    if (serviceImpl == null) {
                        NeoLogger.log("DependencyInjection", "Injecting new object");
                        serviceImpl = Class.forName(implType).getDeclaredConstructor().newInstance();
                        DependencyInjectionMap.objects.put(implType, serviceImpl);
                    }

                    // Precisamos atribuir essa instância ao atributo.
                    attr.setAccessible(true);
                    attr.set(client, serviceImpl);
                    NeoLogger.log("DependencyInjection", "Object injected successfully");
                }
            }
        }
    }
}
