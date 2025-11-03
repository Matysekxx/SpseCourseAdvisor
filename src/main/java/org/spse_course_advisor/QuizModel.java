package org.spse_course_advisor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QuizModel {

    public static final String STATE_CHANGED = "stateChanged";

    private final JsonLoader.Questionnaire questionnaire;
    private int currentQuestionIndex = 0;
    private final Map<String, FieldStats> fieldStats;
    private final Map<String, Long> totalQuestionsPerField;
    private final Boolean[] answers;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public static class FieldStats {
        public final String name;
        public int score = 0;
        private final long totalQuestions;

        FieldStats(String name, long totalQuestions) {
            this.name = name;
            this.totalQuestions = totalQuestions;
        }

        public double getPercentage() {
            return totalQuestions > 0 ? ((double) score / totalQuestions) * 100 : 0;
        }
    }

    public QuizModel(JsonLoader.Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
        this.fieldStats = new HashMap<>();
        this.answers = new Boolean[questionnaire.questions().size()];
        this.totalQuestionsPerField = questionnaire.questions().stream()
                .map(JsonLoader.Question::fieldForYes)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    private void fireStateChange() {
        support.firePropertyChange(STATE_CHANGED, null, null);
    }

    public void start() {
        fireStateChange();
    }

    public JsonLoader.Question getCurrentQuestion() {
        return questionnaire.questions().get(currentQuestionIndex);
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public int getTotalQuestions() {
        return questionnaire.questions().size();
    }

    public void answerCurrentQuestion(boolean isYes) {
        final int questionIndex = getCurrentQuestionIndex();
        final String fieldName = getCurrentQuestion().fieldForYes();
        if (fieldName != null) {
            final long total = totalQuestionsPerField.getOrDefault(fieldName, 0L);
            final FieldStats stats = fieldStats.computeIfAbsent(fieldName, name -> new FieldStats(name, total));
            final Boolean previousAnswer = answers[questionIndex];

            if (previousAnswer != null && previousAnswer) {
                stats.score--;
            }

            if (isYes) {
                stats.score++;
            }
        }

        answers[questionIndex] = isYes;
        nextQuestion();
    }

    public void nextQuestion() {
        if (currentQuestionIndex < getTotalQuestions() - 1) {
            currentQuestionIndex++;
        }
        fireStateChange();
    }

    public void previousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            fireStateChange();
        }
    }

    public Optional<FieldStats> calculateResult() {
        return fieldStats.values().stream()
                .max(Comparator.comparingDouble(FieldStats::getPercentage));
    }

    public void reset() {
        currentQuestionIndex = 0;
        Arrays.fill(answers, null);
        for (FieldStats stats : fieldStats.values()) {
            stats.score = 0;
        }
        fireStateChange();
    }

    public Map<String, FieldStats> getFieldStats() {
        return fieldStats;
    }
}