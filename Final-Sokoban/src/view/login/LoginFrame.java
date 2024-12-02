/*package view.login;

import view.FrameUtil;
import view.level.LevelFrame;

import javax.swing.*;
import java.awt.*;

//import view.FrameUtil;
//import view.level.LevelFrame;


public class LoginFrame extends JFrame {
    private JTextField username;
    private JTextField password;
    private JButton submitBtn;
    private JButton resetBtn;
    private LevelFrame levelFrame;


    public LoginFrame(int width, int height) {
        this.setTitle("Login Frame");
        this.setLayout(null);
        this.setSize(width, height);
        JLabel userLabel = FrameUtil.createJLabel(this, new Point(50, 20), 70, 40, "username:");
        JLabel passLabel = FrameUtil.createJLabel(this, new Point(50, 80), 70, 40, "password:");
        username = FrameUtil.createJTextField(this, new Point(120, 20), 120, 40);
        password = FrameUtil.createJTextField(this, new Point(120, 80), 120, 40);

        submitBtn = FrameUtil.createButton(this, "Confirm", new Point(40, 140), 100, 40);
        resetBtn = FrameUtil.createButton(this, "Reset", new Point(160, 140), 100, 40);

        submitBtn.addActionListener(e -> {
            System.out.println("Username = " + username.getText());
            System.out.println("Password = " + password.getText());
            if (this.levelFrame != null) {
                this.levelFrame.setVisible(true);
                this.setVisible(false);
            }
            //todo: check login info

        });
        resetBtn.addActionListener(e -> {
            username.setText("");
            password.setText("");
        });

        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void setLevelFrame(LevelFrame levelFrame) {
        this.levelFrame = levelFrame;
    }
}
 */
package view.login;

import view.FrameUtil;
import view.level.LevelFrame;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField username;
    private JPasswordField password;
    private JButton submitBtn;
    private JButton resetBtn;
    private LevelFrame levelFrame;

    public LoginFrame(int width, int height) {
        // Frame Setup
        this.setTitle("Stardew Valley Login");
        this.setSize(width, height);
        this.setLayout(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);

        // Set background image by overriding the content pane
        JLabel background = new JLabel(new ImageIcon("Final-sokoban/Final-Sokoban/stardewbackground.jpg")); // Replace with your image path
        background.setBounds(380, 200, width, height);
        this.setContentPane(background);

        // Stardew Valley Styled Font
        Font customFont = new Font("Serif", Font.BOLD, 18);

        // Username Label
        JLabel userLabel = FrameUtil.createJLabel(this, new Point(50, 50), 100, 30, "Username:");
        userLabel.setFont(customFont);
        userLabel.setForeground(new Color(167, 151, 175)); // Indigo color
        this.add(userLabel);

        // Password Label
        JLabel passLabel = FrameUtil.createJLabel(this, new Point(50, 100), 100, 30, "Password:");
        passLabel.setFont(customFont);
        passLabel.setForeground(new Color(144, 113, 229));
        this.add(passLabel);

        // Username Text Field
        username = FrameUtil.createJTextField(this, new Point(160, 50), 150, 30);
        username.setFont(customFont);
        this.add(username);

        // Password Text Field
        password = new JPasswordField();
        password.setBounds(160, 100, 150, 30);
        password.setFont(customFont);
        this.add(password);

        // Submit Button
        submitBtn = FrameUtil.createButton(this, "Login", new Point(50, 160), 120, 40);
        styleButton(submitBtn);
        this.add(submitBtn);

        // Reset Button
        resetBtn = FrameUtil.createButton(this, "Reset", new Point(190, 160), 120, 40);
        styleButton(resetBtn);
        this.add(resetBtn);

        // Submit Button Action
        submitBtn.addActionListener(e -> {
            System.out.println("Username = " + username.getText());
            System.out.println("Password = " + new String(password.getPassword()));
            if (this.levelFrame != null) {
                this.levelFrame.setVisible(true);
                this.setVisible(false);
            }
            // TODO: Validate login credentials
        });

        // Reset Button Action
        resetBtn.addActionListener(e -> {
            username.setText("");
            password.setText("");
        });

        // Frame Settings
        this.setLocationRelativeTo(null);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(255, 223, 186)); // Peach-like color
        button.setForeground(new Color(75, 0, 130)); // Indigo text
        button.setFont(new Font("Serif", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(139, 69, 19), 2)); // Brown border
    }

    public void setLevelFrame(LevelFrame levelFrame) {
        this.levelFrame = levelFrame;

    }
}
