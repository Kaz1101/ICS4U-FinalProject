import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Title {

    /**
     * Written by Luka
     * sets up window, displays title screen (or the current lack thereof)
     */
    public Title(){
        Main.gameState = Main.GameState.TITLE;
        Main.input = new KeyInput();
        Main.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Main.window.setTitle("aaaaaaaaa");
        Main.window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        Main.window.setUndecorated(true);
        JLabel test = new JLabel(new ImageIcon(LoadedSprites.pullTexture("TestDummy")));
        Main.window.addKeyListener(Main.input);
        Main.window.add(test);
        tick.start();
        Main.window.setVisible(true);
    }

    /**
     * Written by Luka
     * Basic Swing timer that currently does almost nothing except stop itself and create a game frame when the game is started
     * This is for future us when we make an actual title screen
     */
    Timer tick = new Timer(10, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //add stuff for selecing options
            if (Main.input.start){
                try {
                    new GameFrame();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                Main.gameState = Main.GameState.PLAY;
                tick.stop();
            }
        }
    });
}
