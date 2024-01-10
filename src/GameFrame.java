import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class GameFrame extends JPanel{
    GameObject p1;
    GameObject testSubject2;
    private static ArrayList<GameObject> game_objects = new ArrayList<>();
    private boolean game_over = false;

    public GameFrame() throws IOException {
        Main.window.getContentPane().removeAll();
        Main.window.add(this);
        Setup.curMap = 0;
        this.setBackground(Color.black);
        p1 = new GameObject(RWCharacterFile.readInitialFile("booperdooper"));
        addObject(RWCharacterFile.readInitialFile("enemyTest"));
        this.revalidate();
        this.repaint();
        this.setVisible(true);

        System.out.println("game start");
        tick.start();
    }


    Timer tick = new Timer(10, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            //for collision: keep track of what tile the character is on, only call movement if character is not on said tile?
//            System.out.println(testSubject1.getTile());
//              testSubject1.collisionCheck();
                if (!(Main.input.up && Main.input.left && Main.input.down && Main.input.right)) {
                    p1.cur_action = GameObject.Action.IDLE;
                }
                if (Main.input.up) {
                    p1.cur_action = GameObject.Action.MOV;
                    p1.moveUp();
                }
                if (Main.input.left) {
                    p1.cur_action = GameObject.Action.MOV;
                    p1.moveLeft();
                }
                if (Main.input.down) {
                    p1.cur_action = GameObject.Action.MOV;
                    p1.moveDown();
                }
                if (Main.input.right) {
                    p1.cur_action = GameObject.Action.MOV;
                    p1.moveRight();
                } if (p1.getTile().equals("woodwalldoor")){
                System.out.println("AAAAAAAAAAAAAAA");
                    Setup.curMap = 1;
            }
                for (int i = 0; i < game_objects.size(); i++){
                    GameObject obj = game_objects.get(i);
                    obj.cur_action = GameObject.Action.IDLE;
                    obj.doTick();
                }

            if (p1.died()) {
                game_over = true;
            }
            repaint();
        }
    });
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

        for (GameObject o : game_objects) {

            //npc stuff painting
            double paintX = o.xPos - p1.xPos + p1.scrX;
            double paintY = o.yPos - p1.yPos + p1.scrY;

            if (o.xPos - 200 < p1.xPos + p1.scrX && o.xPos + 200 > p1.xPos - p1.scrX
                    && o.yPos - 200 < p1.yPos + p1.scrY && o.yPos + 200 > p1.yPos - p1.scrY) {
                o.drawPlayer(gr, (int) paintX, (int) paintY); // for things like doors, maybe make it so their collisioin changes from true to false when block ahead of player is the door

            }
        }
        //player painting
        p1.drawPlayer(gr, 0, 0);
        gr.dispose();

    }

    /**
     * Creates and adds a character GameObject to the GameObject arraylist
     * @param s a string array that contains information of a character
     */
    public static void addObject(String[] s){
        game_objects.add(new GameObject(s));
    }

    /**
     * Creates and adds an attack GameObject to the GameObject arraylist
     * @param atk_dmg the damage of this attack
     * @param damage_type which character type this attack is useful against
     * @param atk_type ranged or melee attack
     * @param character_id which character's attack animation to pull
     */
    public static void addObject(int atk_dmg, int damage_type, int atk_type, String character_id){
        game_objects.add(new GameObject(atk_dmg, damage_type, atk_type, character_id));
    }

    public static void removeObject(GameObject o){
        game_objects.remove(o);
    }

}
