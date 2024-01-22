import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Main {
    public enum GameState {TITLE, HOWTO, OPTIONS, PLAY, PAUSED, INVENTORY, BOSSFIGHT}
    public static GameState gameState;
    public static JFrame window = new JFrame();
    public static KeyInput input;
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public static int x = (int)screenSize.getWidth();
    public static int y = (int)screenSize.getHeight();
    public int ScaleFactor = (int)Math.sqrt(x*x + y*y) / 10;
    public static BGM bgm;

    /**
     * Written by Luka
     * Starts up the program
     * Loads required assets and creates a title window
     * @param args
     */
    public static void main(String[] args) {
        Setup setup = new Setup("data/map/", "data/assets/", 2);
        setup.load();
        bgm = new BGM();
        GameObject.getWindowSize(x, y);
        new Title();
    }
}