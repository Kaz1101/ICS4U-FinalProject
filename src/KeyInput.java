import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInput implements KeyListener {

    public boolean up, left, down, right, atk_up, atk_left, atk_right, atk_down, ability, start, interact;

    /**
     * Written by Luka (things were added by both Christina and Graham)
     * @param e the key event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (Main.gameState) {
            case TITLE -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER -> {
                        start = true;
                    }
                    case KeyEvent.VK_END -> System.exit(0);
                }
            }
            case PLAY -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_END:
                        System.exit(0);
                        break;
                    case KeyEvent.VK_W:
                        up = true;
                        break;
                    case KeyEvent.VK_A:
                        left = true;
                        break;
                    case KeyEvent.VK_S:
                        down = true;
                        break;
                    case KeyEvent.VK_D:
                        right = true;
                        break;
                    case KeyEvent.VK_UP:
                        System.out.println("atk up");
                        atk_up = true;
                        break;
                    case KeyEvent.VK_LEFT:
                        System.out.println("atk left");
                        atk_left = true;
                        break;
                    case KeyEvent.VK_DOWN:
                        System.out.println("atk down");
                        atk_down = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                        System.out.println("atk right");
                        atk_right = true;
                        break;
                    case KeyEvent.VK_E:
                        System.out.println("interact");
                        interact = true;
                        break;
                }
            }
        }
    }


    /**
     * Written by Luka (things were added by both Christina and Graham)
     * @param e the key event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (Main.gameState == Main.GameState.PLAY) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W:
                    up = false;
                    break;
                case KeyEvent.VK_A:
                    left = false;
                    break;
                case KeyEvent.VK_S:
                    down = false;
                    break;
                case KeyEvent.VK_D:
                    right = false;
                    break;
                case KeyEvent.VK_UP:
                    System.out.println("atk up");
                    atk_up = false;
                    break;
                case KeyEvent.VK_LEFT:
                    System.out.println("atk left");
                    atk_left = false;
                    break;
                case KeyEvent.VK_DOWN:
                    System.out.println("atk down");
                    atk_down = false;
                    break;
                case KeyEvent.VK_RIGHT:
                    System.out.println("atk right");
                    atk_right = false;
                    break;
                case KeyEvent.VK_E:
                    System.out.println("interact");
                    interact = false;
                    break;
            }
        }
    }

    //useless
    @Override
    public void keyTyped(KeyEvent e) {

    }
}
