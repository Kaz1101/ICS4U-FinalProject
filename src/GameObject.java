import javax.swing.*;
import java.awt.*;
import java.util.*;


public class GameObject extends JComponent {


    private enum Direction {LEFT, RIGHT, UP, DOWN}
    private enum ObjectType {PLAYER, NPC, ENEMY, DOOR_IN, DOOR_OUT, INVENTORY}
    public enum Action {IDLE, MOV, ATK, DMG, INTERACT}
    private static ArrayList<GameObject> players = new ArrayList<>(2);
    private static ArrayList<GameObject> enemies = new ArrayList<>(30);
    private static ArrayList<GameObject> interactables = new ArrayList<>(30);
    private static ArrayList<GameObject> npc = new ArrayList<>(5); //testing testing! may become object or that could be another arraylist
    private static int window_width, window_length;
    private  Direction cur_direction = Direction.UP; //characters spawn looking up
    public Action cur_action = Action.IDLE;
    private ObjectType type;
    private boolean is_dead = false;
    //public String cur_tile;
    private static double move_spd;
    private double npcSpd;
    private double enemySpd;
    public double xPos;
    public double yPos;
    private double originX; //the origin stuff and dedicated speed is under testing!
    private double originY;
    private int xScale;//distance to top right corner of character - temp!
    private int yScale;//distance to bottom left corner of character - temp!
    private int hitboxL; //temp, the hitbox stuff is all currently under testing
    private int hitboxR;
    private int hitboxU;
    private int hitboxD;
    private double levelWidth = Setup.colMax[Setup.curMap] * 100;//the 100 is scale of tiles, temp
    private double levelHeight = Setup.rowMax[Setup.curMap] * 100;//the 100 is scale of tiles, temp
    public int scrX = window_width/2 - xScale/2;
    public int scrY = window_length/2 - yScale/2;
    private double max_hp; //max health points
    private double cur_hp; //current health points
    private int atk_dmg, atk_spd;
    private int atk_type; //-1 = melee, 1 = ranged
    private double ap;  //ability power that is used as base effectiveness of ability
    private int damage_type; // -1 = enemy's, 1 = player's
    private long cur_cd, cur_atkcd, last_atkcd; //current cd countdown
    private long cd; //fixed value for this character's ability cd time
    private String ability_name; //we might not need this (update. we need this to read animation)
    private int ability_range; //range for ability
    private int character_type; //0 = player, 1 = enemy, 2 = npc, 3 = static object, 4 = attack
    private String object_id; //for reading sprite images
    private int npcType;
    private boolean collected;

//    private ImageIcon still, left, right, move;

    public GameObject(String[] temp) {
        character_type = Integer.parseInt(temp[0]);

        switch (character_type) {
            case 0, 1 -> {
                max_hp = Double.parseDouble(temp[1]);
                cur_hp = Double.parseDouble(temp[2]);
                atk_dmg = Integer.parseInt(temp[3]);
                atk_spd = Integer.parseInt(temp[4]);
                atk_type = Integer.parseInt(temp[5]);
                ap = Double.parseDouble(temp[6]);
                cd = Long.parseLong(temp[7]);
                cur_cd = Long.parseLong(temp[8]);
                ability_name = temp[9];
                ability_range = Integer.parseInt(temp[10]);
                object_id = temp[11];
                xPos = Double.parseDouble(temp[12]);
                yPos = Double.parseDouble(temp[13]);
                xScale = Integer.parseInt(temp[14]);
                yScale = Integer.parseInt(temp[15]);

                if (Boolean.parseBoolean(temp[16])) {
                    interactables.add(this);
                }
            }
            case 2 -> {
                object_id = temp[1];
                xPos = Double.parseDouble(temp[2]);
                yPos = Double.parseDouble(temp[3]);
                xScale = Integer.parseInt(temp[4]);
                yScale = Integer.parseInt(temp[5]);
                npcType = Integer.parseInt(temp[6]);
                if (Boolean.parseBoolean(temp[7])) {
                    interactables.add(this);
                }
            }
            case 3 -> {
                object_id = temp[1];
                xPos = Double.parseDouble(temp[2]);
                yPos = Double.parseDouble(temp[3]);
                if (Boolean.parseBoolean(temp[4])) {
                    interactables.add(this);
                }
                collected = Boolean.parseBoolean(temp[5]);
            }
        }

        //add this game character to corresponding arraylist
        switch(character_type) {
            case 0:
                type = ObjectType.PLAYER;
                hitboxL = 10; //temp, the hitbox stuff is all currently under testing
                hitboxR = xScale - 10;
                hitboxU = yScale - 50;
                hitboxD = yScale;
                players.add(this);
                break;
            case 1:
                type = ObjectType.ENEMY;
                enemies.add(this);
                enemySpd = move_spd * 0.5;
                break;
            case 2:
                type = ObjectType.NPC;
                npc.add(this);
                originX = xPos;
                originY = yPos;
                npcSpd = move_spd * 0.25;
                if (npcType == 1) cur_direction = Direction.LEFT;
                break;
            case 3:
                if(object_id.equals("door_in")) {
                    type = ObjectType.DOOR_IN;
                } if(object_id.equals("door_out")) {
                    type = ObjectType.DOOR_OUT;
                }
                break;
        }
    }

    public GameObject(int atk_dmg, int damage_type, int atk_type, String character_id, double xPos, double yPos, String dir){
        this.atk_dmg = atk_dmg;
        this.damage_type = damage_type;
        this.atk_type = atk_type;
        this.object_id = character_id + "_atk";
        this.xPos = xPos;
        this.yPos = yPos - 20;
        character_type = 4;
        max_hp = 1;//if we want multiple hits or not
        cur_hp = 1;
        switch(dir){
            case "u": cur_direction = Direction.UP; break;
            case "d": cur_direction = Direction.DOWN; break;
            case "l": cur_direction = Direction.LEFT; break;
            case "r": cur_direction = Direction.RIGHT; break;
        }
        //set position and hitbox here
    }


    public static void getWindowSize(int x, int y) {
        window_width = x;
        System.out.println(window_width);
        window_length = y;
        System.out.println(window_length);
        move_spd = (double) y / 150; //for now takes 5 seconds to move across screen from bottom to top, can scale later
        System.out.println(move_spd);
    }


    public void doTick(){
        //refresh cooldown
        switch(character_type) {
            case 0:
                refreshCD();
                //attack();
                //useAbility();
                die();
                break;
            case 4:
                moveForward();
                do_damage();
                kill();
            case 1:
                //moveForward();
                //do_damage();
                trackPlayer(enemySpd);
                break;
            case 2:
                switch (npcType) {
                    case 0:
                        idle();
                        break;
                    case 1:
                        cur_action = Action.MOV;
                        lrMove(npcSpd);
                        break;

                }
                break;

        }
    }



    private void kill() {
        for(GameObject player : players){
            if(Math.abs(this.xPos - player.xPos) > (double) window_width / 2 + 300){
                cur_hp -= max_hp;
                die();
            }
        }
    }

    private void moveForward() {
        switch(cur_direction){
            case UP: moveUp(); break;
            case DOWN: moveDown(); break;
            case LEFT: moveLeft(); break;
            case RIGHT: moveRight(); break;
        }
    }

    private int counter = 0;
    private void idle(){
        counter ++;
        if (counter == 100){
            if (cur_direction == Direction.UP){
                cur_direction = Direction.DOWN;
            } else {
                cur_direction = Direction.UP;
            }
            counter = 0;
        }
    }

    private void lrMove(double spd){
        if (cur_direction == Direction.LEFT){
            if (xPos >= originX - 400) {
                xPos -= spd;
            } else {
                cur_direction = Direction.RIGHT;
            }
        } if (cur_direction == Direction.RIGHT){
            if (xPos <= originX){
                xPos += spd;
            } else {
                cur_direction = Direction.LEFT;
            }
        }
    }


    private void randomMove(double spd){

    }

    private void trackPlayer(double spd){
        double playerX = players.get(0).xPos;
        double playerY = players.get(0).yPos;
        System.out.println(xPos);
        System.out.println(yPos);
        if (playerX > xPos - 800){
            xPos -= spd;
            System.out.println("aa");
        } else if (playerX < xPos + 800){
            xPos += spd;
            System.out.println("ee");
        } if (playerY > yPos - 800){
            yPos -= spd;
            System.out.println("hks");
        } else if (playerY < yPos + 800){
            yPos += spd;
            System.out.println("asdfkh");
        }
    }

    private void do_damage() {
        switch(damage_type){
            case 1:
                for(GameObject enemy : enemies){
                    if(this.getBounds().intersects(enemy.getBounds())){
                        enemy.takeDamage(atk_dmg);
                        cur_hp -= 1;
                    }
                    die();
                }
                break;
            case -1:
                for(GameObject player : players){
                    if(touches(player)){
                        player.takeDamage(atk_dmg);
                        cur_hp -= 1;
                    }
                    die();
                }
        }
    }

    private boolean touches(GameObject o) {
        return this.getBounds().intersects(o.getBounds());
    }

    private void die() {
        if(cur_hp <= 0){
            is_dead = true;
        }
    }

    /**
     * Checks for current y position, if within boarders from the top, moves up.
     */
    public void moveUp(){
        cur_direction = Direction.UP;
        cur_action = Action.MOV;
        if(yPos - move_spd > 0 && collisionCheck()){
            //if(!characterCollision()) {
                yPos -= move_spd;
            //} else {
                yPos += move_spd;
            //}
        }
    }

    public void moveDown(){
        cur_direction = Direction.DOWN;
        cur_action = Action.MOV;
        if(yPos + yScale + move_spd < levelHeight && collisionCheck()){
            //if(!characterCollision()) {
                yPos += move_spd;
            //} else {
                yPos -= move_spd;
           // }
        }
    }

    /**
     * Checks for current x position, if within boarders from the left side, moves left.
     */
    public void moveLeft(){
        cur_direction = Direction.LEFT;
        cur_action = Action.MOV;
        if(xPos - move_spd > 0 && collisionCheck()){
            xPos -= move_spd;
        }
    }
    /**
     * Checks for current x position, if within boarders from the right side, moves right.
     */
    public void moveRight(){
        cur_direction = Direction.RIGHT;
        cur_action = Action.MOV;
        if(xPos + xScale + move_spd < levelWidth && collisionCheck()){
            if(xPos - move_spd > 0 && collisionCheck()){
                //if(!characterCollision()) {
                    xPos += move_spd;
                //} else {
                    xPos -= move_spd;
                //}
            }
        }
    }

    public void attack(String dir){
        if (cur_atkcd > atk_spd / 1000 && withinAttackRange()) {
            switch (dir) {
                case "U":
                    GameFrame.addObject(atk_dmg, damage_type, atk_type, object_id, xPos, yPos, "u");
                    last_atkcd = cur_atkcd;
                    break;
                case "D":
                    GameFrame.addObject(atk_dmg, damage_type, atk_type, object_id, xPos, yPos, "d");
                    last_atkcd = cur_atkcd;
                    break;
                case "L":
                    GameFrame.addObject(atk_dmg, damage_type, atk_type, object_id, xPos, yPos, "l");
                    last_atkcd = cur_atkcd;
                    break;
                case "R":
                    GameFrame.addObject(atk_dmg, damage_type, atk_type, object_id, xPos, yPos, "r");
                    last_atkcd = cur_atkcd;
                    break;
            }
        }
    }

    private void attack(){
        cur_atkcd = System.currentTimeMillis() - last_atkcd;
        switch(character_type) {
            //case 0:

            case 1:
                if (cur_atkcd > atk_spd / 1000 && withinAttackRange()) {
                    switch (cur_direction) {
                        case UP:
                            GameFrame.addObject(atk_dmg, damage_type, atk_type, object_id, xPos, yPos, "u");
                            last_atkcd = cur_atkcd;
                            break;
                        case DOWN:
                            GameFrame.addObject(atk_dmg, damage_type, atk_type, object_id, xPos, yPos, "d");
                            last_atkcd = cur_atkcd;
                            break;
                        case LEFT:
                            GameFrame.addObject(atk_dmg, damage_type, atk_type, object_id, xPos, yPos, "l");
                            last_atkcd = cur_atkcd;
                            break;
                        case RIGHT:
                            GameFrame.addObject(atk_dmg, damage_type, atk_type, object_id, xPos, yPos, "r");
                            last_atkcd = cur_atkcd;
                            break;
                    }
                }
        }
    }

    private boolean withinAttackRange() {
        double atk_range = 0;
        for(GameObject player : players) {
            switch(atk_type){
                case -1: atk_range = 50; break;
                case 1: atk_range = 500; break;
            }
            switch (cur_direction) {
                case UP:
                    if (yPos - player.yPos <= atk_range) {
                        return true;
                    }
                    break;
                case DOWN:
                    if(player.yPos - yPos <= atk_range){
                        return true;
                    }
                    break;
                case LEFT:
                    if(xPos - player.xPos <= atk_range){
                        return true;
                    }
                    break;
                case RIGHT:
                    if(player.xPos - xPos <= atk_range){
                        return true;
                    }
                    break;
            }
            return false;
        }
        return false;
    }

    public void interact() {
        for (GameObject interactable : interactables) {
            System.out.println(getDistance(interactable, this));
            if (cur_direction == Direction.UP) {
                if (getDistance(interactable, this) <= 100) {
                    interactable.doInteract();
                }
            } if (cur_direction == Direction.DOWN){
                if (getDistance(interactable, this) >= 100) {
                    interactable.doInteract();
                }
            }
        }
    }

    private void doInteract() {
        switch (type) {
            case DOOR_IN:
                Setup.curMap = 1;
                break;
            case DOOR_OUT:
                if(Setup.curMap >= 1) Setup.curMap --;
                break;
            default:
            System.out.println("poo poo");
                break;
        }
    }

    public void takeDamage(double dmg){
        cur_action = Action.DMG;
        if(cur_hp - dmg >= 0){
            cur_hp -= dmg;
        } else {
            cur_hp = 0;
        }
    }

    public void useAbility(){
        //somehow get range here


    }

    private void refreshCD(){
        if (cur_cd - 10 > 0) {
            cur_cd -= 10;
        } else {
            cur_cd = 0;
        }

        if (cur_atkcd - 10 > 0) {
            cur_atkcd -= 10;
        } else {
            cur_atkcd = 0;
        }
    }


    public void showAbilityAnimation(){

    }


    public void showAttackAnimation(){

    }



    public String getTile(){
        int x = 0;
        int y = 0;


        if (cur_direction == Direction.LEFT || cur_direction == Direction.UP) {
            x = (int) xPos / 100;
            y = (int) yPos / 100; //100 temp for scaling
        } if (cur_direction == Direction.DOWN){
            x = (int) xPos / 100;
            y = (int) (yPos + yScale) / 100;
        } if (cur_direction == Direction.RIGHT){
            x = (int) (xPos + xScale) / 100;
            y = (int) yPos / 100;
        }
//        System.out.println("[" + x + "," + y + "]");
        return Setup.textureData[Setup.curMap][y][x];
        //should create a tile collision checker so we dont have to manually input which tiles are solid
    }

    public boolean collisionCheck(){
        int toTouch;

        //create boolean collision 2d array
        switch (cur_direction){
            case UP -> {
                toTouch = (int) (yPos + hitboxU - move_spd) / 100;
                return !Setup.collisionData[Setup.curMap][toTouch][(int) (xPos + hitboxL) / 100] &&
                        !Setup.collisionData[Setup.curMap][toTouch][(int) (xPos + (hitboxR - hitboxL)) / 100];
            } case LEFT -> {
                toTouch = (int) (xPos + hitboxL - move_spd) / 100;
                return !Setup.collisionData[Setup.curMap][(int) (yPos + hitboxU) / 100][toTouch] &&
                        !Setup.collisionData[Setup.curMap][(int) (yPos + (hitboxD - hitboxU)) / 100][toTouch];
            } case DOWN -> {
                toTouch = (int) (yPos + hitboxD + move_spd) / 100;
                return !Setup.collisionData[Setup.curMap][toTouch][(int) (xPos + hitboxL) / 100] &&
                        !Setup.collisionData[Setup.curMap][toTouch][(int) (xPos + (hitboxR - hitboxL)) / 100];
            } case RIGHT -> {
                toTouch = (int) (xPos + hitboxR + move_spd) / 100;
                return !Setup.collisionData[Setup.curMap][(int) (yPos + hitboxU) / 100][toTouch] &&
                        !Setup.collisionData[Setup.curMap][(int) (yPos + (hitboxD - hitboxU)) / 100][toTouch];
            }
        }
        return false;
    }


    //Movement and attack

    public void draw(Graphics2D gr, int drawX, int drawY){ //to be draw camera
        switch(character_type) {
            case 0: gr.drawImage(LoadedSprites.pullTexture(object_id + "_" + cur_direction + "_" + cur_action), scrX, scrY, xScale, yScale, null); break;
            case 1, 2: gr.drawImage(LoadedSprites.pullTexture(object_id + "_" + cur_direction + "_" + cur_action), drawX, drawY, xScale, yScale, null); break;
            case 4: gr.drawImage(LoadedSprites.pullTexture(object_id + "_attack"), drawX, drawY, xScale, yScale, null); break;
        }

    }

    public void renderCheck(Graphics2D gr){
        double playerX = players.get(0).xPos;
        double playerY = players.get(0).yPos;
        double playerScrX = players.get(0).scrX;
        double playerScrY = players.get(0).scrY;

        double paintX = xPos - playerX + playerScrX;
        double paintY = yPos - playerY + playerScrY;

        if (xPos - 200 < playerX + playerScrX && xPos + 200 > playerX - playerScrX
                && yPos - 200 < playerY + playerScrY && yPos + 200 > playerY - playerScrY) {
            draw(gr, (int) paintX, (int) paintY); // for things like doors, maybe make it so their collisioin changes from true to false when block ahead of player is the door

        }
    }

    /**
     * Saves all data for this object.
     * @return a double array that contains all field data of this object.
     */
    public String[] saveData() {
        return new String[]{Integer.toString(character_type), Double.toString(max_hp), Double.toString(cur_hp), Double.toString(atk_dmg), Double.toString(atk_spd), Integer.toString(atk_type), Double.toString(ap), Long.toString(cd), Long.toString(cur_cd), ability_name, Integer.toString(ability_range), object_id};
    }


    //getters and setters
    public String getObjectID() {
        return object_id;
    }


    public void setxPos(double x){
        xPos = x;
    }

    public void setyPos(double y){
        yPos = y;
    }

    public double getDistance(GameObject x, GameObject y) {
        System.out.println(x.xPos);
        System.out.println(x.yPos);
        System.out.println(y.xPos);
        System.out.println(y.yPos);
        return Math.sqrt(Math.pow((x.xPos - y.xPos), 2) + Math.pow((x.yPos - y.yPos), 2));
    }

    public boolean died() {
        return is_dead;
    }




}
