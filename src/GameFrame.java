import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class GameFrame extends JPanel{
    GameObject p1;
    private static ArrayList<GameObject> game_objects = new ArrayList<>();
    private static ArrayList<GameObject> sub_game_objects = new ArrayList<>();
    private boolean game_over = false;
    private JLabel pauseScreen = new JLabel(new ImageIcon(LoadedSprites.pullTexture("tempPause")));
    private int pauseDisplay = 0;

    /**
     * Refreshes window to now initialize and display the game with any necessary objects
     * @throws IOException
     */
    public GameFrame() throws IOException {
        Main.window.getContentPane().removeAll();
        Main.window.add(this);
        Setup.curMap = 0;
        this.setBackground(Color.black);
        p1 = new GameObject(RWFile.readInitialFile("booperdooper"));
        game_objects.add(p1);
        sub_game_objects.add(p1);
        loadLooseObj();
        this.revalidate();
        this.repaint();
        this.setVisible(true);

        Main.bgm.changeTrack(1);

        System.out.println("game start");
        tick.start();
    }


    /**
     * Written by Luka, with things added by Graham and Christina
     * Timer that calls corresponding methods for all player inputs and calls doTick() for every object
     * Runs through every 10 milliseconds and performs appropriate actions
     */
    Timer tick = new Timer(10, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (Main.gameState) {
                case PLAY -> {
                    if (pauseDisplay == 1) {
                        Main.window.remove(pauseScreen);
                        Main.bgm.play();
                        Main.bgm.loop();
                        pauseDisplay--;
                    }
                    if (!(Main.input.up && Main.input.left && Main.input.down && Main.input.right)) {
                        p1.cur_action = GameObject.Action.IDLE;
                    }
                    if (Main.input.up) {
                        p1.moveUp();
                    }
                    if (Main.input.left) {
                        p1.moveLeft();
                    }
                    if (Main.input.down) {
                        p1.moveDown();
                    }
                    if (Main.input.right) {
                        p1.moveRight();
                    }
                    if (Main.input.atk_up) {
                        p1.attack("U");
                    }
                    if (Main.input.atk_down) {
                        p1.attack("D");
                    }
                    if (Main.input.atk_left) {
                        p1.attack("L");
                    }
                    if (Main.input.atk_right) {
                        p1.attack("R");
                    }
                    if (Main.input.interact) {
                        p1.cur_action = GameObject.Action.INTERACT;
                        p1.interact();
                    }
                    if (Setup.curMap == 0) {
                        ticks(game_objects);
                    }
                    if (Setup.curMap == 1) {
                        ticks(sub_game_objects);
                    }


                    if (p1.died()) {
                        game_over = true;
                    }
                    repaint();
                }
            }
        }
    });

    private void ticks(ArrayList<GameObject> list){
        for (int i = 0; i < list.size(); i++) {
            GameObject obj = list.get(i);
            obj.cur_action = GameObject.Action.IDLE;
            obj.doTick();
            if (obj.died()) {
                list.remove(obj);
                i--;
            }
        }
    }


    /**
     * Written by Luka
     * The thing that controlls graphics and draws everything on screen
     * Houses the map painting functions and calculations (only paints within a certain distance around players for efficient painting)
     * Sends Graphics component to respective GameObject methods that require them to draw objects and player
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D gr = (Graphics2D)g;

        //for map tiles
        int col = 0;
        int row = 0;

        while (col < Setup.colMax[Setup.curMap] && row < Setup.rowMax[Setup.curMap]) {
            int tileX = col * 100;//the 100 value will change based on scale, temp - location of tile within whole level map
            int tileY = row * 100;
            String tileType = Setup.textureData[Setup.curMap][row][col];
            double paintX = tileX - p1.xPos + p1.scrX;
            double paintY = tileY - p1.yPos + p1.scrY;

            if (tileX - 200 < p1.xPos + p1.scrX && tileX + 200 > p1.xPos - p1.scrX
                    && tileY - 200 < p1.yPos + p1.scrY && tileY + 200 > p1.yPos - p1.scrY) {
                gr.drawImage(LoadedSprites.pullTexture(tileType), (int) paintX, (int) paintY, 100, 100, null);
            }
            col++;

            if (col == Setup.colMax[Setup.curMap]) {
                col = 0;
                row++;
            }
        } //end of map tile painting

//        ArrayList<Integer> testX = new ArrayList<Integer>();
//        ArrayList<Integer> testY = new ArrayList<Integer>();
//        //temp debug
//        gr.setColor(new Color(255,0,0,70));
//        for(int i = 0; i < game_objects.get(1).pathfind.path.size(); i++){
//            int tileX = game_objects.get(1).pathfind.path.get(i).col * 100;//the 100 value will change based on scale, temp - location of tile within whole level map
//            int tileY = game_objects.get(1).pathfind.path.get(i).row * 100;
//            double paintX = tileX - p1.xPos + p1.scrX;
//            double paintY = tileY - p1.yPos + p1.scrY;
//
//            if (tileX - 200 < p1.xPos + p1.scrX && tileX + 200 > p1.xPos - p1.scrX
//                    && tileY - 200 < p1.yPos + p1.scrY && tileY + 200 > p1.yPos - p1.scrY) {
//                gr.fillRect((int)paintX, (int)paintY, 100, 100);
//                testX.add(game_objects.get(1).pathfind.path.get(i).col * 100);
//                testY.add(game_objects.get(1).pathfind.path.get(i).row * 100);
//            }
//        }

        if (Setup.curMap == 0) {
            for (GameObject o : game_objects) {
                o.renderCheck(gr);
            }
        } if (Setup.curMap == 1) {
            for (GameObject o : sub_game_objects) {
                o.renderCheck(gr);
            }
        }

        p1.drawOverlay(gr);

        //player painting
        p1.draw(gr, 0, 0);

        if (pauseDisplay == 1){
            gr.setColor(new Color(30,30,30, 95));
            gr.fillRect(0, 0, Main.x, Main.y);
            gr.drawImage(LoadedSprites.pullTexture("tempPause"), (Main.x / 3), (Main.y / 3), 650, 400, null);
        }
        gr.dispose();

    }

    /**
     * Creates and adds a GameObject to the GameObject arraylist
     * @param s a string array that contains information of the object that is created
     * @param map the specific map that this object should be added to
     */
    public static void addObject(String[] s, int map) {
        switch (map) {
            case 0: game_objects.add(new GameObject(s)); break;
            case 1: sub_game_objects.add(new GameObject(s)); break;
        }
    }

    /**
     * Creates and adds an attack GameObject to the GameObject arraylist
     * @param atk_dmg the damage of this attack
     * @param damage_type which character type this attack is useful against
     * @param atk_type ranged or melee attack
     * @param character_id which character's attack animation to pull
     */
    public static void addObject(int atk_dmg, int damage_type, int atk_type, String character_id, double x, double y, String d){
        switch (Setup.curMap) {
            case 0: game_objects.add(new GameObject(atk_dmg, damage_type, atk_type, character_id, x, y, d)); break;
            case 1: sub_game_objects.add(new GameObject(atk_dmg, damage_type, atk_type, character_id, x, y, d)); break;
        }
    }


    /**
     * Written by Luka
     * Takes objects to be initialized from a csv, then initializes them and adds them to the GameObject arrays
     * @throws IOException
     */
    private void loadLooseObj() throws IOException {
        String[] loose = RWFile.readGeneral("data/objectData/objectList");
        for (int i = 0; i < loose.length - 1; i += 2){
            addObject(RWFile.readInitialFile(loose[i]), Integer.parseInt(loose[i+1]));
        }
    }
}


