package org.spse_course_advisor;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        final String filePath = (args.length == 0 ? "src/main/resources" : args[0]) + File.separator + "questions.json";
        Window.launchFromJson(filePath);
    }
}