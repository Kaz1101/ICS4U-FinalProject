import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public abstract class GameFrame extends JFrame implements KeyListener {

    private Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
    private final int screen_width = (int)screen_size.getWidth();
    private final int screen_height = (int)screen_size.getHeight();
    private Timer tick;

    /*
        The arraylists below are still tbd if in use or not
     */
    private static ArrayList<GameCharacter> players = new ArrayList<GameCharacter>();
    private static ArrayList<GameCharacter> enemies = new ArrayList<GameCharacter>();

    //only one to call ticks then?
    private static ArrayList<GameCharacter> characters = new ArrayList<GameCharacter>();
    private static ArrayList<Attack> attack = new ArrayList<Attack>(); //set variable to compare with player and enemy, if not same then do damage, put all attack in this arraylist

    /**
     * true if 'A' is held down
     */
    private boolean p1L = false;

    /**
     * true if 'D' is held down
     */
    private boolean p1R = false;

    /**
     * true if 'U' is held down
     */
    private boolean p1U = false;

    /**
     * true if 'S' is held down
     */
    private boolean p1D = false;

    /**
     * true if up arrow is held down
     */
    private boolean p2U = false;

    /**
     * true if down arrow is held down
     */
    private boolean p2D = false;

    /**
     * true if left arrow is held down
     */
    private boolean p2L = false;

    /**
     * true if right arrow is held down
     */
    private boolean p2R = false;

    /**
     * true if 'J' is held down
     */
    private boolean p1_attack = false;

    /**
     * true if 'K' is held down
     */
    private boolean p1_ability = false;

    /**
     * true if game is in-game and paused
     */
    private boolean game_paused = false;


    public GameFrame(){
        //im thinking of small frame for menu (and choosing characters) and fullscreen for game
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(screen_size);

        addKeyListener(this);
//        addMouseListener(this);
//        addMouseMotionListener(this);
        tick = new Timer(10, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                do_tick();
                for(GameCharacter c : characters){ //to change to gameobject
                    c.repaint();
                }
            }

        });
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("IDK WHAT TO CALL THIS");

        setup();


    }

    public static void add_character(GameCharacter c){
        characters.add(c);
    }

    public static void remove_character(GameCharacter c){
        characters.remove(c);
    }

    //tbd will prob make new gameobject animation
    public static void add_attack(Attack a){
        attack.add(a);
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch(key){
            //player 2 controls
            case KeyEvent.VK_UP: p2U = true; break;
            case KeyEvent.VK_DOWN: p2D = true; break;
            case KeyEvent.VK_LEFT: p2L = true; break;
            case KeyEvent.VK_RIGHT: p2R = true; break;
            //player 1 controls
            case KeyEvent.VK_W: p1U = true; break;
            case KeyEvent.VK_S: p1D = true; break;
            case KeyEvent.VK_A: p1L = true; break;
            case KeyEvent.VK_D: p1R = true; break;
            case KeyEvent.VK_J: p1_attack = true; break;
            case KeyEvent.VK_K: p1_ability = true; break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }





    public boolean isP1D() {
        return p1D;
    }

    public boolean isP1U() {
        return p1U;
    }

    public boolean isP1L() {
        return p1L;
    }

    public boolean isP1R() {
        return p1R;
    }

    public boolean isP2D() {
        return p2D;
    }

    public boolean isP2U() {
        return p2U;
    }

    public boolean isP2L() {
        return p2L;
    }

    public boolean isP2R() {
        return p2R;
    }

    public boolean isPaused() {
        return game_paused;
    }


    public abstract void do_tick();
    public abstract void setup();
}
