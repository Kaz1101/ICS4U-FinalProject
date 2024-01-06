import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class GameFrame extends JPanel{
    GameObject testSubject1;

    public GameFrame() throws IOException {
        Main.window.getContentPane().removeAll();
        Main.window.add(this);
        this.setBackground(Color.black);
        testSubject1 = new BooperDooper(RWCharacterFile.readInitialFile("booperdooper"));
        this.add(testSubject1);
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
                    System.out.println("up");
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
                }
            repaint();

        }
    });


    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D gr = (Graphics2D)g;

        int col = 0;
        int row = 0;

        while (col < Setup.colMax && row < Setup.rowMax) {
            String tileType = Setup.textureData[row][col];
            int tileX = col * 100;//the 100 value will change based on scale, temp - location of tile within whole level map
            int tileY = row * 100;
            double paintX = tileX - testSubject1.xPos + testSubject1.scrX;
            double paintY = tileY - testSubject1.yPos + testSubject1.scrY;

            if (tileX - 200 < testSubject1.xPos + testSubject1.scrX && tileX + 200 > testSubject1.xPos - testSubject1.scrX
                    && tileY - 200 < testSubject1.yPos + testSubject1.scrY && tileY + 200 > testSubject1.yPos - testSubject1.scrY) {
                gr.drawImage(LoadedSprites.pullTexture(tileType), (int) paintX, (int) paintY, 100, 100, null);
            }
            col++;

            if (col == Setup.colMax) {
                col = 0;
                row++;
            }
        }

        testSubject1.drawPlayer(gr);
        gr.dispose();

    }

}
