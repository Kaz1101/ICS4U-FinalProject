import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Random;

public class Title {



    private ImageIcon[] title = new ImageIcon[4];
    private JLabel label = new JLabel();
    private Random r = new Random();
    private int loadTime;
    private int elapsedTime = 0;
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
        boolean titleScreen = r.nextBoolean();
        if (titleScreen) title[0] = new ImageIcon(LoadedSprites.pullTexture("main_title"));
        else title[0] = new ImageIcon(LoadedSprites.pullTexture("main_titleALT"));
        title[1] = new ImageIcon(LoadedSprites.pullTexture("main_howto"));
        title[2] = new ImageIcon((LoadedSprites.pullTexture("loadScreen")));
        Main.window.add(label);
        titleDisplay(0);
        Main.window.addKeyListener(Main.input);
        try {
            Main.bgm.setMusic(0);
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

            if (Main.gameState != Main.GameState.PLAY) {
                if (Main.input.options) {
                    titleDisplay(1);
                    System.out.println("options");
                }
                if (!Main.input.options) {
                    titleDisplay(0);
                }
            }

            if (Main.input.startNew || Main.input.startOld){
                Main.bgm.stop();
                load();
            }
        }
    });

    /**
     * displays a random title from title screens we have so far
     * @param i a random integer representing the index of the title screen to be displayed
     */
    private void titleDisplay(int i){
        label.setIcon(title[i]);

    }

    /**
     * A random loading screen that makes players feel like they are waiting for game to load
     * generates random integer to randomize wait time
     */
    private void load(){
        if (elapsedTime == 0){
            Main.bgm.playSFX(8);
            label.setIcon(title[2]);
            loadTime = r.nextInt(500 - 200) + 200;
            Main.gameState = Main.GameState.PLAY;

        }
        elapsedTime ++;
        if (elapsedTime >= loadTime){
            try {
                tick.stop();
                new GameFrame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
