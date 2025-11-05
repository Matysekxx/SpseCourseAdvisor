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

    public void answerCurrentQuestion(boolean answer) {
        answers[currentQuestionIndex] = answer;
    }

    private void recalculateScores() {
        fieldStats.values().forEach(stats -> stats.score = 0);
        for (int i = 0; i < questionnaire.questions().size(); i++) {
            final Boolean answer = answers[i];
            if (answer != null && answer) {
                final String fieldName = questionnaire.questions().get(i).fieldForYes();
                if (fieldName != null) {
                    final long total = totalQuestionsPerField.getOrDefault(fieldName, 0L);
                    final FieldStats stats = fieldStats.computeIfAbsent(fieldName, name -> new FieldStats(name, total));
                    stats.score++;
                }
            }
        }
    }

    public void nextQuestion() {
        if (currentQuestionIndex < getTotalQuestions() - 1) {
            currentQuestionIndex++;
        }
        recalculateScores();
        fireStateChange();
    }

    public void previousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            recalculateScores();
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
        recalculateScores();
        fireStateChange();
    }

    public Map<String, FieldStats> getFieldStats() {
        return fieldStats;
    }
}