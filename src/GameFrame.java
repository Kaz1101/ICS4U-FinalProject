import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class GameFrame extends JPanel{
    GameObject testSubject1;
    private static ArrayList<GameObject> game_objects = new ArrayList<>(50);
    private boolean game_over = false;
    GameObject testSubject2;

    public GameFrame() throws IOException {
        Main.window.getContentPane().removeAll();
        Main.window.add(this);
        Setup.curMap = 0;
        this.setBackground(Color.black);
        testSubject1 = new BooperDooper(RWCharacterFile.readInitialFile("booperdooper"));
        testSubject2 = new Dogepro(RWCharacterFile.readInitialFile("enemyTest"));
        this.add(testSubject1);
        this.add(testSubject2);
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
                    testSubject1.cur_action = GameObject.Action.IDLE;
                }
                if (Main.input.up) {
                    testSubject1.cur_action = GameObject.Action.MOV;
                    testSubject1.moveUp();
                }
                if (Main.input.left) {
                    testSubject1.cur_action = GameObject.Action.MOV;
                    testSubject1.moveLeft();
                }
                if (Main.input.down) {
                    testSubject1.cur_action = GameObject.Action.MOV;
                    testSubject1.moveDown();
                }
                if (Main.input.right) {
                    testSubject1.cur_action = GameObject.Action.MOV;
                    testSubject1.moveRight();
                } if (testSubject1.getTile().equals("woodwalldoor")){
                System.out.println("AAAAAAAAAAAAAAA");
                    Setup.curMap = 1;
            }
                testSubject2.cur_action = GameObject.Action.IDLE;
                repaint();

            if (testSubject1.died()) {
                game_over = true;
            }
            for(GameObject o : game_objects){
                o.doTick();
            }

        }
    });

//a
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
            double paintX = tileX - testSubject1.xPos + testSubject1.scrX;
            double paintY = tileY - testSubject1.yPos + testSubject1.scrY;

            if (tileX - 200 < testSubject1.xPos + testSubject1.scrX && tileX + 200 > testSubject1.xPos - testSubject1.scrX
                    && tileY - 200 < testSubject1.yPos + testSubject1.scrY && tileY + 200 > testSubject1.yPos - testSubject1.scrY) {
                gr.drawImage(LoadedSprites.pullTexture(tileType), (int) paintX, (int) paintY, 100, 100, null);
            }
            col++;

            if (col == Setup.colMax[Setup.curMap]) {
                col = 0;
                row++;
            }
        } //end of map tile painting

        //npc stuff painting
        double paintX = testSubject2.xPos - testSubject1.xPos + testSubject1.scrX;
        double paintY = testSubject2.yPos - testSubject1.yPos + testSubject1.scrY;

        if (testSubject2.xPos - 200 < testSubject1.xPos + testSubject1.scrX && testSubject2.xPos + 200 > testSubject1.xPos - testSubject1.scrX
                && testSubject2.yPos - 200 < testSubject1.yPos + testSubject1.scrY && testSubject2.yPos + 200 > testSubject1.yPos - testSubject1.scrY) {
            testSubject2.drawPlayer(gr, (int) paintX, (int) paintY); //game object array for these?, also for things like doors, maybe make it so their collisioin changes from true to false when block ahead of player is the door

        }
        //player painting
        testSubject1.drawPlayer(gr, 0, 0);
        gr.dispose();

    }

    public static void addObject(GameObject o){
        game_objects.add(o);
    }

    public static void removeObject(GameObject o){
        game_objects.remove(o);
    }

}
