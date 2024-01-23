import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInput implements KeyListener {

    public boolean up, left, down, right, atk_up, atk_left, atk_right, atk_down, ability, interact, startNew, startOld, options, inventory, useItem, paused, saving, restart;

    /**
     * Written by Luka (things were added by both Christina and Graham)
     * @param e the key event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (Main.gameState) {
            case TITLE -> {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_K -> {
                        options = true;
                        Main.gameState = Main.GameState.HOWTO;
                    }
                    case KeyEvent.VK_ENTER ->{
                        if (!options){
                            startNew = true;
                        }
                    }
                    case KeyEvent.VK_SPACE ->{
                        if (!options){
                            startOld = true;
                        }
                    }
                    case KeyEvent.VK_END -> System.exit(0);
                }
            }
            case HOWTO -> {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_K) {
                    options = false;
                    Main.gameState = Main.GameState.TITLE;
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
                    case KeyEvent.VK_ESCAPE:
                        paused = true;
                        Main.gameState = Main.GameState.PAUSED;
                        break;
                    case KeyEvent.VK_Q:
                        inventory = true;
                        Main.gameState = Main.GameState.INVENTORY;
                        break;
                }
            }
            case PAUSED -> {
                switch (e.getKeyCode()){
                    case KeyEvent.VK_SPACE -> {
                        paused = false;
                        Main.gameState = Main.GameState.PLAY;
                    }
                    case KeyEvent.VK_ENTER -> saving = true;
                }
            }
            case INVENTORY -> {
                switch (e.getKeyCode()){
                    case KeyEvent.VK_Q -> {
                        inventory = false;
                        Main.gameState = Main.gameState.PLAY;
                    }
                    case KeyEvent.VK_UP -> {
                        if (Inventory.slotRow > 0){
                            Inventory.slotRow--;
                        }
                    }
                    case KeyEvent.VK_LEFT -> {
                        if (Inventory.slotCol > 0){
                            Inventory.slotCol--;
                        }
                    }
                    case KeyEvent.VK_DOWN -> {
                        if (Inventory.slotRow < 3){
                            Inventory.slotRow++;
                        }
                    }
                    case KeyEvent.VK_RIGHT -> {
                        if (Inventory.slotCol < 9){
                            Inventory.slotCol++;
                        }
                    }
                    case KeyEvent.VK_ENTER -> {
                        useItem = true;
                    }
                }
            }
            case DEAD -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    restart = true;
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
        if (Main.gameState == Main.GameState.INVENTORY){

            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                useItem = false;
            }
        }
    }

    //useless
    @Override
    public void keyTyped(KeyEvent e) {

    }
}
