import javax.swing.*;
import java.awt.*;

public class Main {
    public enum GameState {TITLE, OPTIONS, PLAY}
    public static GameState gameState;
    public static JFrame window = new JFrame();
    public static KeyInput input;
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public static int x = (int)screenSize.getWidth();
    public static int y = (int)screenSize.getHeight();
    public int ScaleFactor = (int)Math.sqrt(x*x + y*y) / 10;

    public static void main(String[] args) {
        Setup setup = new Setup("data/map/", "data/assets/", 1);
        setup.load();
        GameObject.getWindowSize(x, y);
        new Title();
    }
}