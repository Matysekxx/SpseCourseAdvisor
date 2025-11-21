package org.spse_course_advisor;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
        rootPanel.add(new CenteredLogoPanel(), BorderLayout.NORTH);
        rootPanel.add(mainContainer, BorderLayout.CENTER);
        add(rootPanel);
        setGlassPane(new LogoGlassPane());
        getGlassPane().setVisible(true);
    }

    public void setController(QuizController controller) {
        this.controller = controller;
    }

    private JPanel buildWelcomePanel() {
        final JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setOpaque(false);

        final JPanel centerContent = new JPanel();
        centerContent.setOpaque(false);
        centerContent.setLayout(new GridBagLayout());

        final JLabel welcomeLabel = new JLabel("Vítejte v dotazníku pro výběr oboru");
        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(Font.BOLD, 45f));

        final JButton startButton = new JButton("Začít formulář");
        stylePrimary(startButton);
        startButton.setFont(startButton.getFont().deriveFont(20f));
        startButton.addActionListener(e -> controller.startQuiz());

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(0, 0, 70, 0);
        centerContent.add(welcomeLabel, gbc);
        centerContent.add(startButton, gbc);

        welcomePanel.add(centerContent, BorderLayout.CENTER);
        return welcomePanel;
    }

    private JPanel buildQuestionnairePanel() {
        final JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 50, 24));


        final JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        final JButton resetButton = new JButton("Reset");
        stylePrimary(resetButton);
        resetButton.setFont(resetButton.getFont().deriveFont(Font.BOLD, 26f));
        resetButton.setPreferredSize(new Dimension(200, 90));
        resetButton.addActionListener(e -> controller.restartQuiz());

        questionContainer = new JPanel(new BorderLayout());
        questionContainer.setOpaque(false);
        topPanel.add(questionContainer, BorderLayout.CENTER);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setOpaque(false);
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        mainPanel.add(imagePanel, BorderLayout.CENTER);

        final JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        buttonContainer.setOpaque(false);

        answerButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        answerButtonsPanel.setOpaque(false);

        prevButton = new JButton("Zpět");
        stylePrimary(prevButton);
        prevButton.setFont(prevButton.getFont().deriveFont(Font.BOLD, 26f));
        prevButton.setPreferredSize(new Dimension(200, 90));
        prevButton.addActionListener(e -> controller.previousQuestion());

        nextButton = new JButton("Přeskočit");
        stylePrimary(nextButton);
        nextButton.setFont(nextButton.getFont().deriveFont(Font.BOLD, 26f));
        nextButton.setPreferredSize(new Dimension(200, 90));
        nextButton.addActionListener(e -> controller.nextQuestion());

        buttonContainer.add(prevButton);

        final JButton yesBtn = createChoiceButton("Ano", true);
        final JButton noBtn = createChoiceButton("Ne", false);

        buttonContainer.add(yesBtn);
        buttonContainer.add(nextButton);
        buttonContainer.add(noBtn);

        buttonContainer.add(resetButton);

        mainPanel.add(buttonContainer, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel buildResultPanel() {
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
        questionContainer.add(prompt, BorderLayout.NORTH);

        final String imageName = q.image();
        if (imageName != null && !imageName.isEmpty()) {
            try {
                final Icon icon;
                final File imageFile = new File(projectDir, imageName);
                final ImageIcon imageIcon = new ImageIcon(imageFile.toURI().toURL());

                final int maxWidth = Math.max(400, getWidth());
                final int maxHeight = Math.max(300, getHeight() - 450);

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
        questionContainer.revalidate();
        questionContainer.repaint();
        answerButtonsPanel.revalidate();
        answerButtonsPanel.repaint();
    }

    private JButton createChoiceButton(String text, boolean isYes) {
        final JButton button = new JButton(text);
        stylePrimary(button);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 26f));
        button.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        button.setPreferredSize(new Dimension(200, 90));
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

    private static class CenteredLogoPanel extends JPanel {
        private final int targetHeight = 130;
        private final int padding = 5;

        CenteredLogoPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(100, targetHeight + padding * 2));
            setMinimumSize(new Dimension(50, targetHeight + padding * 2));
        }
    }

    private class LogoGlassPane extends JComponent {
        private final Icon logo;
        private final int padding = 5;

        {
            final File logoFile = new File(projectDir, "SPSE-Jecna_Logotyp.svg");
            logo = new FlatSVGIcon(logoFile);
        }

        LogoGlassPane() {
            setOpaque(false);
        }

        @Override
        public void paintComponent(Graphics g) {
            if (logo == null) return;

            final Graphics2D g2 = (Graphics2D) g.create();
            try {
                final int targetWidth = 300;
                final int targetHeight = 300;

                int iconW = logo.getIconWidth();
                int iconH = logo.getIconHeight();

                if (iconW <= 0) iconW = targetWidth;
                if (iconH <= 0) iconH = 200;

                final double sx = targetWidth / (double) iconW;
                final double sy = targetHeight / (double) iconH;
                final double scale = Math.min(sx, sy);
                final int drawW = (int) Math.round(iconW * scale);

                final int x = getWidth() - drawW - padding;

                final AffineTransform old = g2.getTransform();
                g2.translate(x, padding);
                g2.scale(scale, scale);

                logo.paintIcon(this, g2, 0, 0);
                g2.setTransform(old);
            } finally {
                g2.dispose();
            }
        }
    }


}
