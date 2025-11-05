package org.spse_course_advisor;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Optional;

public class Window extends JFrame implements PropertyChangeListener {
    private static final Color BRAND_BLUE = new Color(148, 185, 239);
    private static final Color BRAND_RED_ACCENT = Color.RED;
    private static final Color BACKGROUND_GRAY = new Color(240, 240, 240);
    private static final String WELCOME_PANEL = "WELCOME_PANEL" ;
    private static final String QUESTIONNAIRE_PANEL = "QUESTIONNAIRE_PANEL" ;
    private static final String RESULT_PANEL = "RESULT_PANEL" ;
    private final String projectDir;
    private final QuizModel model;
    private QuizController controller;
    private final JPanel mainCardPanel;
    private JPanel questionContainer;
    private JPanel answerButtonsPanel;
    private JButton prevButton;
    private JLabel imageLabel;
    private JButton nextButton;
    private JLabel resultLabel;

    public Window(QuizModel model, String projectDir) {
        this.projectDir = projectDir;
        this.model = model;
        this.model.addPropertyChangeListener(this);
        setTitle("SPŠE Course Advisor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        final JRootPane rootPane = this.getRootPane();
        final InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        final ActionMap actionMap = rootPane.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ESCAPE_ACTION");
        actionMap.put("ESCAPE_ACTION", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        final JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(BACKGROUND_GRAY);

        mainCardPanel = new JPanel(new CardLayout());
        mainCardPanel.setOpaque(false);
        mainCardPanel.add(buildWelcomePanel(), WELCOME_PANEL);
        mainCardPanel.add(buildQuestionnairePanel(), QUESTIONNAIRE_PANEL);
        mainCardPanel.add(buildResultPanel(), RESULT_PANEL);

        mainContainer.add(mainCardPanel, BorderLayout.CENTER);

        final JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.add(buildHeader(), BorderLayout.NORTH);
        rootPanel.add(mainContainer, BorderLayout.CENTER);
        add(rootPanel);
    }

    public void setController(QuizController controller) {
        this.controller = controller;
    }

    private JComponent buildHeader() {
        final JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BRAND_BLUE);
        header.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        final JPanel logoWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        logoWrapper.setOpaque(false);

        final JPanel logoPanel = new CenteredLogoPanel();
        logoWrapper.add(logoPanel);
        header.add(logoWrapper, BorderLayout.CENTER);
        return header;
    }

    private JComponent buildWelcomePanel() {
        final JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setOpaque(false);

        final JPanel centerContent = new JPanel();
        centerContent.setOpaque(false);
        centerContent.setLayout(new GridBagLayout());

        final JLabel welcomeLabel = new JLabel("Vítejte v dotazníku pro výběr oboru!");
        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(Font.BOLD, 28f));

        final JButton startButton = new JButton("Začít formulář");
        stylePrimary(startButton);
        startButton.setFont(startButton.getFont().deriveFont(20f));
        startButton.addActionListener(e -> controller.startQuiz());

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(0, 0, 20, 0);
        centerContent.add(welcomeLabel, gbc);
        centerContent.add(startButton, gbc);

        welcomePanel.add(centerContent, BorderLayout.CENTER);
        return welcomePanel;
    }

    private JComponent buildQuestionnairePanel() {
        final JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        questionContainer = new JPanel(new BorderLayout());
        questionContainer.setOpaque(false);
        mainPanel.add(questionContainer, BorderLayout.NORTH);

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setOpaque(false);
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        mainPanel.add(imagePanel, BorderLayout.CENTER);

        final JPanel buttonContainer = new JPanel(new BorderLayout());
        buttonContainer.setOpaque(false);

        answerButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        answerButtonsPanel.setOpaque(false);

        prevButton = new JButton("Zpět");
        styleSecondary(prevButton);
        prevButton.addActionListener(e -> controller.previousQuestion());

        nextButton = new JButton("Další");
        stylePrimary(nextButton);
        nextButton.addActionListener(e -> controller.nextQuestion());

        buttonContainer.add(prevButton, BorderLayout.LINE_START);
        buttonContainer.add(answerButtonsPanel, BorderLayout.CENTER);
        buttonContainer.add(nextButton, BorderLayout.LINE_END);

        mainPanel.add(buttonContainer, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JComponent buildResultPanel() {
        final JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setOpaque(false);

        final JPanel centerMessagePanel = new JPanel(new GridBagLayout());
        centerMessagePanel.setOpaque(false);
        resultLabel = new JLabel("", SwingConstants.CENTER);
        resultLabel.setFont(resultLabel.getFont().deriveFont(Font.BOLD, 28f));
        centerMessagePanel.add(resultLabel);

        resultPanel.add(centerMessagePanel, BorderLayout.CENTER);

        final JPanel centerContent = new JPanel(new GridBagLayout());
        centerContent.setOpaque(false);

        final JButton restartButton = new JButton("Zkusit znovu");
        stylePrimary(restartButton);
        restartButton.setFont(restartButton.getFont().deriveFont(20f));
        restartButton.addActionListener(e -> controller.restartQuiz());

        centerContent.add(restartButton, new GridBagConstraints());
        resultPanel.add(centerContent, BorderLayout.SOUTH);
        resultPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));

        return resultPanel;
    }

    public void updateResult() {
        final Optional<QuizModel.FieldStats> bestField = model.calculateResult();

        final double itPercentage = model.getFieldStats().getOrDefault("Informační technologie", new QuizModel.FieldStats("", 0)).getPercentage();
        final double elePercentage = model.getFieldStats().getOrDefault("Elektrotechnika a robotika", new QuizModel.FieldStats("", 0)).getPercentage();

        final String resultMessage;
        if (itPercentage == 100.0 && elePercentage == 100.0) {
            resultMessage = "<html>" + "<div style='text-align: center;'>Gratulujeme, jsi všestranný talent!<br>" +
                    "Máš skvělé předpoklady pro <b style='color: " + toHex(BRAND_RED_ACCENT) + ";'>oba obory (IT i elektrotechniku)</b>.<br></div></html>" ;
        } else if (bestField.isPresent() && bestField.get().score > 0) {
            final QuizModel.FieldStats winner = bestField.get();
            resultMessage = String.format(
                    "<html><div style='text-align: center;'>Nejvíce ti sedí obor:<br><h1 style='color: " + toHex(BRAND_RED_ACCENT) + ";'>%s</h1><br>Shoda: %.0f %%</div></html>",
                    winner.name, winner.getPercentage()
            );
        } else {
            resultMessage = "<html><div style='text-align: center;'>Nepodařilo se určit vhodný obor.<br>Zkus to znovu!</div></html>" ;
        }

        resultLabel.setText(resultMessage);
    }

    public void showWelcomePanel() {
        final CardLayout cl = (CardLayout) mainCardPanel.getLayout();
        cl.show(mainCardPanel, WELCOME_PANEL);
    }

    public void showQuestionnairePanel() {
        final CardLayout cl = (CardLayout) mainCardPanel.getLayout();
        cl.show(mainCardPanel, QUESTIONNAIRE_PANEL);
    }

    public void showResultPanel() {
        final CardLayout cl = (CardLayout) mainCardPanel.getLayout();
        cl.show(mainCardPanel, RESULT_PANEL);
    }

    private void stylePrimary(AbstractButton b) {
        styleButton(b, BRAND_BLUE, Color.BLACK);
    }

    private void styleSecondary(AbstractButton b) {
        styleButton(b, Color.WHITE, Color.BLACK);
        final Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(7, 19, 7, 19)
        );
        b.setBorder(border);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                b.setBorder(border);
            }
        });
    }

    private void styleAccent(AbstractButton b) {
        styleButton(b, BRAND_RED_ACCENT, Color.WHITE);
    }

    private void styleButton(AbstractButton b, Color background, Color foreground) {
        b.setBackground(background);
        b.setForeground(foreground);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                if (background.equals(Color.WHITE)) {
                    b.setForeground(BRAND_BLUE);
                    b.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BRAND_BLUE, 1),
                            BorderFactory.createEmptyBorder(7, 19, 7, 19)
                    ));
                    return;
                }
                b.setBackground(background.brighter());
            }

            public void mouseExited(MouseEvent evt) {
                b.setBackground(background);
                b.setForeground(foreground);
            }
        });
    }

    private void refreshQuestion() {
        questionContainer.removeAll();
        answerButtonsPanel.removeAll();

        final JsonLoader.Question q = model.getCurrentQuestion();

        final JLabel prompt = new JLabel(
                "<html><div style='text-align: center;'>" + q.prompt() + "</div></html>",
                SwingConstants.CENTER
        );
        prompt.setFont(prompt.getFont().deriveFont(Font.BOLD, 28f));
        prompt.setForeground(Color.BLACK);
        questionContainer.add(prompt, BorderLayout.CENTER);

        final String imageName = q.image();
        if (imageName != null && !imageName.isEmpty()) {
            try {
                final Icon icon;
                final File imageFile = new File(projectDir, imageName);
                final ImageIcon imageIcon = new ImageIcon(imageFile.toURI().toURL());

                final int maxWidth = 700;
                final int maxHeight = 500;
                if (imageIcon.getIconWidth() > maxWidth || imageIcon.getIconHeight() > maxHeight) {
                    final var widthRatio = (double) maxWidth / imageIcon.getIconWidth();
                    final var heightRatio = (double) maxHeight / imageIcon.getIconHeight();
                    final double ratio = Math.min(widthRatio, heightRatio);
                    final var newWidth = (int) (imageIcon.getIconWidth() * ratio);
                    final var newHeight = (int) (imageIcon.getIconHeight() * ratio);
                    final Image scaledImage = imageIcon.getImage().getScaledInstance(
                            newWidth, newHeight, Image.SCALE_SMOOTH
                    );
                    icon = new ImageIcon(scaledImage);
                } else {
                    icon = imageIcon;
                }

                imageLabel.setIcon(icon);
            } catch (Exception e) {
                imageLabel.setIcon(null);
                System.err.println("Error loading image '" + imageName + "': " + e.getMessage());
            }
        } else {
            imageLabel.setIcon(null);
        }

        buildSingleChoiceQuestion(answerButtonsPanel, q);

        questionContainer.revalidate();
        questionContainer.repaint();
        answerButtonsPanel.revalidate();
        answerButtonsPanel.repaint();

        int index = model.getCurrentQuestionIndex();
        prevButton.setEnabled(index > 0);
        nextButton.setText(index < model.getTotalQuestions() - 1 ? "Další" : "Dokončit");
        if (index < model.getTotalQuestions() - 1) {
            stylePrimary(nextButton);
        } else {
            styleAccent(nextButton);
        }
    }

    private void buildSingleChoiceQuestion(JPanel answerPanel, JsonLoader.Question q) {
        final GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.insets = new Insets(5, 15, 5, 15);
        answerPanel.add(createChoiceButton("Ano", true, q), btnGbc);
        answerPanel.add(createChoiceButton("Ne", false, q), btnGbc);
    }

    private JButton createChoiceButton(String text, boolean isYes, JsonLoader.Question question) {
        final JButton button = new JButton(text);
        stylePrimary(button);

        button.setFont(button.getFont().deriveFont(Font.BOLD, 18f));
        button.addActionListener(e -> controller.answerQuestion(isYes));
        return button;
    }

    public static void launchFromJson(String projectDir) {
        try {
            final String resourcePath = projectDir + File.separator + "questions.json" ;
            final InputStream is = new FileInputStream(resourcePath);
            final JsonLoader.Questionnaire q = JsonLoader.loadFromInputStream(is);
            SwingUtilities.invokeLater(() -> {
                final QuizModel model = new QuizModel(q);
                final Window view = new Window(model, projectDir);
                final QuizController controller = new QuizController(model, view);
                view.setController(controller);
                view.setVisible(true);
            });
        } catch (IOException e) {
            SwingUtilities.invokeLater(
                    () -> JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE)
            );
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (QuizModel.STATE_CHANGED.equals(evt.getPropertyName())) {
            refreshQuestion();
        }
    }

    private static String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    private class CenteredLogoPanel extends JPanel {
        private final Icon logo;

        {
            final File logoFile = new File(projectDir, "SPSE-Jecna_Logotyp_Cernobily.svg");
            logo = new FlatSVGIcon(logoFile);
        }

        CenteredLogoPanel() {
            setOpaque(false);
            final Dimension size = new Dimension(logo.getIconWidth(), logo.getIconHeight());
            setPreferredSize(size);
            setMinimumSize(size);
        }

        @Override
        protected final void paintComponent(Graphics g) {
            super.paintComponent(g);
            final int x = (getWidth() - logo.getIconWidth()) / 2;
            final int y = (getHeight() - logo.getIconHeight()) / 2;
            logo.paintIcon(this, g, x, y);
        }
    }
}
