package org.spse_course_advisor;

import org.json.JSONArray;
import org.json.JSONTokener;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonLoader {
    public record Question(String prompt, String fieldForYes, String image) {}
    public record Questionnaire(String title, List<Question> questions) { }
    public static Questionnaire loadFromInputStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IOException("InputStream is null. Resource not found?");
        }
        final var content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        return loadFromString(content);
    }

    public static Questionnaire loadFromString(String content) {
        final JSONObject root = new JSONObject(new JSONTokener(content));
        final String title = root.optString("title", "Dotazník");
        final JSONArray qs = root.getJSONArray("questions");
        final List<Question> questions = new ArrayList<>();
        for (int i = 0; i < qs.length(); i++) {
            final JSONObject q = qs.getJSONObject(i);
            final String prompt = q.getString("prompt");
            final String fieldForYes = q.optString("fieldForYes", null);
            final String image = q.optString("image", null);
            questions.add(new Question(prompt, fieldForYes, image));
        }
        Collections.shuffle(questions);
        return new Questionnaire(title, questions);
    }
}
