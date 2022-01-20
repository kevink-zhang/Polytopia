package org.cis120.polytopia;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class Main implements Runnable {
    public void run() {
        // NOTE : recall that the 'final' keyword notes immutability even for
        // local variables.

        // Top-level frame in which game components live.
        // Be sure to change "TOP LEVEL FRAME" to the name of your game
        final JFrame frame = new JFrame("Polytopia (KZ2025)");
        frame.setLocation(300, 300);

        // Status panel
        // final JPanel status_panel = new JPanel();
        // frame.add(status_panel, BorderLayout.SOUTH);
        // final JLabel status = new JLabel("Running...");
        // status_panel.add(status);

        // Main playing area

        // loads in font
        JPanel j = new JPanel();

        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(
                    Font.createFont(
                            Font.TRUETYPE_FONT, new File("files/assets/JosefinSans-Light.ttf")
                    )
            );
            ge.registerFont(
                    Font.createFont(Font.BOLD, new File("files/assets/JosefinSans-BoldItalic.ttf"))
            );

        } catch (IOException | FontFormatException e) {
            // Handle exception
        }

        // use the font
        final GameBody game = new GameBody(j);
        frame.add(game, BorderLayout.CENTER);

        // Reset button
        // final JPanel control_panel = new JPanel();
        // frame.add(control_panel, BorderLayout.NORTH);

        // Note here that when we add an action listener to the reset button, we
        // define it as an anonymous inner class that is an instance of
        // ActionListener with its actionPerformed() method overridden. When the
        // button is pressed, actionPerformed() will be called.
        final JButton reset = new JButton("Reset");
        // reset.addActionListener(new ActionListener() {
        //// public void actionPerformed(ActionEvent e) {
        //// game.reset();
        //// }
        // });
        // control_panel.add(reset);

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Start game
        game.run();
    }

}
