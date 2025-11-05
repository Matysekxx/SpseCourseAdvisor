package org.spse_course_advisor;

public class Main {
    public static void main(String[] args) {
        final String projectDir = (args.length == 0 ? "config" : args[0]);
        Window.launchFromJson(projectDir);
    }
}