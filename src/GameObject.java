import javax.swing.*;
import java.awt.*;
import java.util.*;


public abstract class GameObject extends JComponent {
    private enum Direction {LEFT, RIGHT, UP, DOWN}
    public enum Action {IDLE, MOV, ATK, DMG}
    private static ArrayList<GameObject> players = new ArrayList<>(2);
    private static ArrayList<GameObject> enemies = new ArrayList<>(30);
    private static int window_width, window_length;
    private Color transparent = new Color(0,0,0,0);
    private  Direction cur_direction = Direction.UP; //characters spawn looking up
    public Action cur_action = Action.IDLE;
    public String cur_tile;
    private static double move_spd;
    public double xPos = 1100;
    public double yPos = 10;
    public int xHitbox = 57; //distance to top right corner of character
    public int yHitbox = 86; //distance to bottom left corner of character
    private double levelWidth = Setup.colMax * 100;//the 100 is scale of tiles, temp
    private double levelHeight = Setup.rowMax * 100;//the 100 is scale of tiles, temp
    public int scrX = window_width/2 - xHitbox/2;
    public int scrY = window_length/2 - yHitbox/2;
    private double max_hp; //max health points
    private double cur_hp; //current health points
    private double atk_dmg, atk_spd;
    private int atk_range;
    private double ap;  //ability power that is used as base effectiveness of ability
    private int ability_type;// -1 = dmg, 1 = heal   (edit. should we only have aoe?)
    private long cur_cd, cur_atkcd; //current cd countdown
    private long cd; //fixed value for this character's ability cd time
    private String ability_name; //we might not need this (update. we need this to read animation)
    private int character_type; //0 = player, 1 = enemy
    private String character_id; //for reading sprite images
    private int ability_range; //range for ability

//    private ImageIcon still, left, right, move;

    public GameObject(String[] temp) {
        character_type = Integer.parseInt(temp[0]);
        max_hp = Double.parseDouble(temp[1]);
        cur_hp = Double.parseDouble(temp[2]);
        atk_dmg = Double.parseDouble(temp[3]);
        atk_spd = Double.parseDouble(temp[4]);
        atk_range = Integer.parseInt(temp[5]);
        ap = Double.parseDouble(temp[6]);
        ability_type = Integer.parseInt(temp[7]);
        cd = Long.parseLong(temp[8]);
        cur_cd = Long.parseLong(temp[9]);
        ability_name = temp[10];
        ability_range = Integer.parseInt(temp[11]);
        character_id = temp[12];

//        still = new ImageIcon(character_id + "_still.gif");
//        left = new ImageIcon(character_id + "_left.gif");
//        right = new ImageIcon(character_id + "_right.gif");
//        move = new ImageIcon(character_id + "_move.gif");


        //character_type, max_hp, cur_hp, atk_dmg, atk_spd, atk_range, ap, ability_type, cd, cur_cd, ability_name, ability_range, character_id





        //add this game character to corresponding arraylist
        switch(character_type){
            case 0:
                players.add(this);
                break;
            case 1:
                enemies.add(this);
                break;
        }
    }


    public static void getWindowSize(int x, int y) {
        window_width = x;
        System.out.println(window_width);
        window_length = y;
        System.out.println(window_length);
        move_spd = (double) y / 200; //for now takes 5 seconds to move across screen from bottom to top, can scale later
        System.out.println(move_spd);
    }


    public void doTick(){
        //Checks for if on cooldown, takes away tick time from current cooldown
        if (cur_cd - 50 > 0) {
            cur_cd -= 50;
        } else {
            cur_cd = 0;
        }

        if(cur_atkcd - 50 > 0){
            cur_atkcd -= 50;
        } else {
            cur_atkcd = 0;
        }

    }

    public int getTile(){
        int x = 0;
        int y = 0;


        if (cur_direction == Direction.LEFT || cur_direction == Direction.UP) {
            x = (int) xPos / 100;
            y = (int) yPos / 100; //100 temp for scaling
        } if (cur_direction == Direction.DOWN){
            x = (int) xPos / 100;
            y = (int) (yPos + yHitbox) / 100;
        } if (cur_direction == Direction.RIGHT){
            x = (int) (xPos + xHitbox) / 100;
            y = (int) yPos / 100;
        }
//        System.out.println("[" + x + "," + y + "]");
        return Integer.parseInt(Setup.textureData[x][y]);
        //should create a tile collision checker so we dont have to manually input which tiles are solid
    }

    public boolean collisionCheck(){
        int toTouch;

        //create boolean collision 2d array
        switch (cur_direction){
            case UP -> {
                toTouch = (int) (yPos - move_spd) / 100;
                return !Setup.collisionData[toTouch][(int) xPos / 100] &&
                        !Setup.collisionData[toTouch][(int) (xPos + xHitbox) / 100];
            } case LEFT -> {
                toTouch = (int) (xPos - move_spd) / 100;
                return !Setup.collisionData[(int) yPos / 100][toTouch] &&
                        !Setup.collisionData[(int) (yPos + yHitbox) / 100][toTouch];
            } case DOWN -> {
                toTouch = (int) (yPos + yHitbox + move_spd) / 100;
                return !Setup.collisionData[toTouch][(int) xPos / 100] &&
                        !Setup.collisionData[toTouch][(int) (xPos + xHitbox) / 100];
            } case RIGHT -> {
                toTouch = (int) (xPos + xHitbox + move_spd) / 100;
                return !Setup.collisionData[(int) yPos / 100][toTouch] &&
                        !Setup.collisionData[(int) (yPos + yHitbox) / 100][toTouch];
            }
        }
        return false;
    }


    //Movement and attack

    /**
     * Checks for current x position, if within boarders from the left side, moves left.
     */
    public void moveLeft(){
        cur_direction = Direction.LEFT;
        if(xPos - move_spd > 0 && collisionCheck()){
            xPos -= move_spd;
        }

    }

    /**
     * Checks for current x position, if within boarders from the right side, moves right.
     */
    public void moveRight(){
        cur_direction = Direction.RIGHT;
        if(xPos + xHitbox + move_spd < levelWidth && collisionCheck()){
            System.out.println("MOVING RIGHT");
            xPos += move_spd;
        }
    }

    /**
     * Checks for current y position, if within boarders from the top, moves up.
     */
    public void moveUp(){
        cur_direction = Direction.UP;
        if(yPos - move_spd > 0 && collisionCheck()){
            yPos -= move_spd;
        }
    }
    public void moveDown(){
        cur_direction = Direction.DOWN;
        if(yPos + yHitbox + move_spd < levelHeight && collisionCheck()){
            yPos += move_spd;
        }
    }

    public void takeDamage(double dmg){
        if(cur_hp - dmg >= 0){
            cur_hp -= dmg;
        } else {
            cur_hp = 0;
        }
    }

    public void useAbility(){
        //somehow get range here
        switch(ability_type){
            case -1:
                //switch again for current character type

            case 1:
        }

    }

    /**
     * Each subclass should override to show corresponding ability animation
     */
    public abstract void showAbilityAnimation();

    /**
     * Each subclass should override to show corresponding attack animation
     */
    public abstract void showAttackAnimation();

    /**
     * Attacks --> need to change way of calculating attack range and way of calling attack
     */
    public void attack(){
        double x_range = 0, y_range = 0;
        //set attack range according to current facing direction.
        switch(cur_direction){
            //x and y pos is top left corner of object, xHitbox is top right corner, yHitbox is bottom left corner.
            case RIGHT:
                x_range = xPos + xHitbox + atk_range;
                y_range = yPos + yHitbox;
                break;

            case LEFT:
                x_range = xPos - atk_range;
                y_range = yPos + yHitbox;
                break;

            case UP:
                x_range = xPos + xHitbox;
                y_range = yPos - atk_range;
                break;

            case DOWN:
                x_range = xPos + xHitbox;
                y_range = yPos + yHitbox + atk_range;
                break;
        }

        //idk either this or keep whatever is below, only use this for projectiles instead
//        GameFrame.add_attack(new Attack(x_range, y_range, xPos, yPos));

        /*
            Checks for character type to determine which type of characters the attack is effective on
            Loops through all opponent objects and checks for if their coordinates is within attack range
            If within attack range then deduct corresponding hp
         */
        switch(character_type) {
            case 0:
                for(GameObject enemy: enemies){
                    if(enemy.xPos <= x_range && enemy.xPos >= this.xPos && enemy.yPos <= y_range && enemy.yPos >= this.yPos){
                        enemy.takeDamage(this.atk_dmg);
                    }
                }
                break;

            case 1:
                for(GameObject player: players){
                    if(player.xPos <= x_range && player.xPos >= this.xPos && player.yPos <= y_range && player.yPos >= this.yPos){
                        player.takeDamage(this.atk_dmg);
                    }
                }
                break;
        }

    }

    public void drawPlayer(Graphics2D gr){ //to be draw camera
        gr.drawImage(LoadedSprites.pullTexture(character_id + "_" + cur_direction + "_" + cur_action), scrX, scrY, xHitbox, yHitbox, transparent, null);
    }

    /**
     * Saves all data for this object.
     * @return a double array that contains all field data of this object.
     */
    public String[] saveData() {
        return new String[]{Integer.toString(character_type), Double.toString(max_hp), Double.toString(cur_hp), Double.toString(atk_dmg), Double.toString(atk_spd), Integer.toString(atk_range), Double.toString(ap), Integer.toString(ability_type), Long.toString(cd), Long.toString(cur_cd), ability_name, Integer.toString(ability_range), character_id};
    }


    //getters and setters
    public String getCharacterID() {
        return character_id;
    }

    public void setCharacterID(String s){
        character_id = s;
    }


}