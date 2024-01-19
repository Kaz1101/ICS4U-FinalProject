import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Title {



    private ImageIcon[] title = new ImageIcon[4];
    private JLabel label = new JLabel();
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
        title[0] = new ImageIcon(LoadedSprites.pullTexture("main_title"));
        title[1] = new ImageIcon(LoadedSprites.pullTexture("TestDummy"));
        Main.window.add(label);
        titleDisplay(0);
        Main.window.addKeyListener(Main.input);
        try {
            Main.bgm.set(0);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        Main.bgm.play();
        Main.bgm.loop();
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
            if (Main.input.options){
                titleDisplay(1);
                System.out.println("options");
            } if (!Main.input.options){
                titleDisplay(0);
            }
            //^ should we do joptionpane? or we can draw jbuttons
            if (Main.input.start){
                try {
                    Main.bgm.stop();
                    new GameFrame();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                Main.gameState = Main.GameState.PLAY;
                tick.stop();
            }
        }
    });

    private void titleDisplay(int i){
        label.setIcon(title[i]);

    }
}
