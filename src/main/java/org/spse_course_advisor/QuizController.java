package org.spse_course_advisor;

public class QuizController {
    private final QuizModel model;
    private final Window view;

    public QuizController(QuizModel model, Window view) {
        this.model = model;
        this.view = view;
    }

    public void startQuiz() {
        view.showQuestionnairePanel();
        model.start();
    }

    public void answerQuestion(boolean isYes) {
        model.answerCurrentQuestion(isYes);
        nextQuestion();
    }

    public void nextQuestion() {
        if (model.getCurrentQuestionIndex() < model.getTotalQuestions() - 1) {
            model.nextQuestion();
        } else {
            view.updateResult();
            view.showResultPanel();
        }
    }

    public void previousQuestion() {
        model.previousQuestion();
    }

    public void restartQuiz() {
        model.reset();
        view.showWelcomePanel();
    }
}