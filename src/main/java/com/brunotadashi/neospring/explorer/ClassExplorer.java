package com.brunotadashi.neospring.explorer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ClassExplorer {

    public static List<String> retrieveAllClasses(Class<?> sourceClass) {
        return packageExplorer(sourceClass.getPackageName());
    }

    public static List<String> packageExplorer(String packageName) {
        ArrayList<String> classNames = new ArrayList<String>();

        try {
            InputStream stream = ClassLoader.getSystemClassLoader().getSystemResourceAsStream(packageName.replaceAll("\\.", File.separator));

            BufferedReader br = new BufferedReader(new InputStreamReader(stream));

            String line;
            while((line = br.readLine()) != null) {
                if (line.endsWith(".class")) {
                    // Concatena o packageName com o nome da classe e remove a extensão `.class`.
                    classNames.add(packageName + "." + line.substring(0, line.indexOf(".class")));
                } else {
                    classNames.addAll(packageExplorer(packageName + "." + line));
                }
            }

            return classNames;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
