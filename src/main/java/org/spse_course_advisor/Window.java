package org.spse_course_advisor;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
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
    private static final String WELCOME_PANEL = "WELCOME_PANEL" ;
    private static final String QUESTIONNAIRE_PANEL = "QUESTIONNAIRE_PANEL" ;
    private static final String RESULT_PANEL = "RESULT_PANEL" ;
    private final String projectDir;
    private final QuizModel model;
    private QuizController controller;
    private final JPanel mainCardPanel;
    private JPanel questionContainer;
    private JLabel imageLabel;
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

        mainCardPanel = new JPanel(new CardLayout());
        mainCardPanel.add(buildWelcomePanel(), WELCOME_PANEL);
        mainCardPanel.add(buildQuestionnairePanel(), QUESTIONNAIRE_PANEL);
        mainCardPanel.add(buildResultPanel(), RESULT_PANEL);

        final JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.add(Box.createRigidArea(
                new Dimension(0, 140)
        ), BorderLayout.NORTH);
        rootPanel.add(mainCardPanel, BorderLayout.CENTER);
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
        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(Font.BOLD, 35f));

        final JButton startButton = new JButton("Začít formulář");
        stylePrimary(startButton);
        startButton.setFont(startButton.getFont().deriveFont(Font.BOLD, 26f));
        startButton.setPreferredSize(new Dimension(300, 90));
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

        final JButton resetButton = new JButton("↻");
        resetButton.setContentAreaFilled(false);
        resetButton.setBorderPainted(false);
        resetButton.setOpaque(false);
        resetButton.setFocusPainted(false);
        resetButton.setForeground(Color.BLACK);
        resetButton.setFont(resetButton.getFont().deriveFont(Font.BOLD, 36f));
        resetButton.setPreferredSize(new Dimension(100, 60));
        resetButton.addActionListener(e -> controller.restartQuiz());

        final JPanel resetWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        resetWrapper.setOpaque(false);
        resetWrapper.add(resetButton);

        questionContainer = new JPanel(new BorderLayout());
        questionContainer.setOpaque(false);
        
        topPanel.add(resetWrapper, BorderLayout.NORTH);
        topPanel.add(questionContainer, BorderLayout.CENTER);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
        mainPanel.add(imageLabel, BorderLayout.CENTER);

        final JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.setOpaque(false);
        final JButton prevButton = new JButton("Zpět");
        stylePrimary(prevButton);
        prevButton.setFont(prevButton.getFont().deriveFont(Font.BOLD, 26f));
        prevButton.setPreferredSize(new Dimension(200, 60));
        prevButton.addActionListener(e -> controller.previousQuestion());

        final JPanel prevWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 35));
        prevWrapper.setOpaque(false);
        prevWrapper.add(prevButton);

        final JButton nextButton = new JButton("Přeskočit");
        stylePrimary(nextButton);
        nextButton.setFont(nextButton.getFont().deriveFont(Font.BOLD, 26f));
        nextButton.setPreferredSize(new Dimension(200, 60));
        nextButton.addActionListener(e -> controller.nextQuestion());

        final JPanel nextWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 35));
        nextWrapper.setOpaque(false);
        nextWrapper.add(nextButton);

        final JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        buttonContainer.setOpaque(false);

        final JButton yesBtn = createChoiceButton("Ano", true);
        final JButton noBtn = createChoiceButton("Ne", false);

        buttonContainer.add(yesBtn);
        buttonContainer.add(noBtn);

        bottomWrapper.add(prevWrapper, BorderLayout.WEST);
        bottomWrapper.add(buttonContainer, BorderLayout.CENTER);
        bottomWrapper.add(nextWrapper, BorderLayout.EAST);

        mainPanel.add(bottomWrapper, BorderLayout.SOUTH);

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
                    "Máš skvělé předpoklady pro <b style='color: " + toHex(Color.RED) + ";'>oba obory (IT i elektrotechniku)</b>.<br></div></html>" ;
        } else if (bestField.isPresent() && bestField.get().score > 0) {
            final QuizModel.FieldStats winner = bestField.get();
            resultMessage = String.format(
                    "<html><div style='text-align: center;'>Nejvíce ti sedí obor:<br><h1 style='color: " + toHex(Color.RED) + ";'>%s</h1><br>Shoda: %.0f %%</div></html>",
                    winner.name, winner.getPercentage()
            );
        } else resultMessage =
                "<html><div style='text-align: center;'>Nepodařilo se určit vhodný obor.<br>Zkus to znovu!</div></html>" ;
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

    private void stylePrimary(JButton b) {
        styleButton(b, BRAND_BLUE, Color.BLACK);
    }

    private void styleButton(JButton b, Color background, Color foreground) {
        b.setBackground(background);
        b.setForeground(foreground);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent evt) {
                b.setBackground(background.brighter());
            }

            @Override public void mouseExited(MouseEvent evt) {
                b.setBackground(background);
                b.setForeground(foreground);
            }
        });
    }

    private void refreshQuestion() {
        questionContainer.removeAll();

        final JsonLoader.Question q = model.getCurrentQuestion();

        final JLabel prompt = new JLabel(
                "<html><div style='text-align: center;'>" + q.prompt() + "</div></html>",
                SwingConstants.CENTER
        );
        prompt.setFont(prompt.getFont().deriveFont(Font.BOLD, 30f));
        prompt.setForeground(Color.BLACK);
        prompt.setBorder(BorderFactory.createEmptyBorder(8, 0, 50, 0));
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
    }

    private JButton createChoiceButton(String text, boolean isYes) {
        final JButton button = new JButton(text);
        stylePrimary(button);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 26f));
        button.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        button.setPreferredSize(new Dimension(220, 110));
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

    private class LogoGlassPane extends JComponent {
        private final Icon logo;
        private final int padding = 5;

        {
            final File logoFile = new File(projectDir, "SPSE-Jecna_Logotyp.svg");
            logo = new FlatSVGIcon(logoFile);
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
