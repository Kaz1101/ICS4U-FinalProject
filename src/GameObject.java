import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


public class GameObject extends JComponent {
    private enum Direction {LEFT, RIGHT, UP, DOWN}
    private enum ObjectType {PLAYER, NPC, ENEMY, DOOR_IN, DOOR_OUT, INVENTORY}
    public enum Action {IDLE, MOV, ATK, DMG, INTERACT}
    private static ArrayList<GameObject> players = new ArrayList<>(2);
    private static ArrayList<GameObject> enemies = new ArrayList<>(30);
    private static ArrayList<GameObject> interactables = new ArrayList<>(30);
    private static ArrayList<GameObject> npcs = new ArrayList<>(5); //testing testing! may become object or that could be another arraylist
    private static int window_width, window_length;
    private  Direction cur_direction = Direction.UP; //characters spawn looking up
    public Action cur_action = Action.IDLE;
    private ObjectType type;
    private boolean is_dead = false;
    //public String cur_tile;
    private static double move_spd;
    private double ms; //movement speed for this object
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
    public double max_hp; //max health points
    public double cur_hp; //current health points
    private int atk_dmg, atk_spd;
    private int atk_type; //-1 = melee, 1 = ranged, 0 = spawner
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
    public PathfindAI pathfind = new PathfindAI();
    public boolean pathfinding = false;
    private int counter = 0;
    private static Random r = new Random();
    private Rectangle hitbox = new Rectangle(0, 0, 0, 0);
    private double cur_xp = 0;
    private double[] xps = {0, 100, 500, 1200, 2000, 2800, 3600, 4500}; //havent finalized xp amount and gain
    private double next_xp = xps[1];
    private int level = 1;
    private double dmg_boost = 1; //multiplier


    /**
     * Sets up and initializes variables based on object data given from a csv file
     * @param temp a String array consisting of data for specific object being loaded
     * @author Christina, adjustments by Luka
     */
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
                hitbox = new Rectangle((int) xPos, (int) yPos, xScale, yScale);
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
                hitbox = new Rectangle((int) xPos, (int) yPos, xScale, yScale);
                if(npcType == 3){
                    pathfinding = true;
                }
                if (Boolean.parseBoolean(temp[7])) {
                    interactables.add(this);
                }
            }
            case 3 -> {
                object_id = temp[1];
                xPos = Double.parseDouble(temp[2]);
                yPos = Double.parseDouble(temp[3]);
                xScale = Integer.parseInt(temp[4]);
                yScale = Integer.parseInt(temp[5]);
                if (Boolean.parseBoolean(temp[6])) {
                    interactables.add(this);
                }
                collected = Boolean.parseBoolean(temp[7]);
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
                ms = move_spd;
                players.add(this);
                break;
            case 1:
                type = ObjectType.ENEMY;
                //test
                pathfinding = true;

                enemies.add(this);
                if(atk_type == 0){
                    ms = 0;
                } else {
                    ms = move_spd * 0.75;
                }
                break;
            case 2:
                type = ObjectType.NPC;
                npcs.add(this);
                originX = xPos;
                originY = yPos;
                ms = move_spd * 0.3;
                hitboxL = 10; //temp, the hitbox stuff is all currently under testing
                hitboxR = xScale - 10;
                hitboxU = yScale - 50;
                hitboxD = yScale;
                ms = move_spd * 0.25;
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

    /**
     * Constructor for attack objects
     * @param atk_dmg the damage that this object deals
     * @param damage_type the type of character that released this attack
     * @param atk_type the range of the attack
     * @param character_id the specific character that released this attack
     * @param xPos x position of the character that released this attack
     * @param yPos the y position of the character that released this attack
     * @param dir the direction the character is facing when releasing this attack
     * @author Christina
     */
    public GameObject(int atk_dmg, int damage_type, int atk_type, String character_id, double xPos, double yPos, String dir){
        this.atk_dmg = atk_dmg;
        this.damage_type = damage_type;
        this.atk_type = atk_type;
        this.object_id = character_id;
        this.xPos = xPos;
        this.yPos = yPos - 20;
        ms = move_spd * 2;
        character_type = 4;
        max_hp = 1; //single hit
        cur_hp = 1;
        xScale = 50;
        yScale = 50;
        switch(dir){
            case "u": cur_direction = Direction.UP; break;
            case "d": cur_direction = Direction.DOWN; break;
            case "l": cur_direction = Direction.LEFT; break;
            case "r": cur_direction = Direction.RIGHT; break;
        }
        hitbox = new Rectangle((int) xPos, (int) yPos, xScale, yScale);
    }


    /**
     * Calculates movement speed based on screen size
     * @param x the width of the screen
     * @param y the length of the screen
     */
    public static void getWindowSize(int x, int y) {
        window_width = x;
        System.out.println(window_width);
        window_length = y;
        System.out.println(window_length);
        move_spd = (double) y / 150;
        System.out.println(move_spd);
    }

    /**
     * The method that gets called for each timer tick, checks for and refreshes object status
     * @author Christina, Luka
     */
    public void doTick() throws FileNotFoundException {
        switch(character_type) {
            case 0:
                refreshCD();
                refreshXP();
                //useAbility();
                die();
                break;
            case 1:
                if (pathfinding) searchPath((int)players.get(0).yPos / 100, (int)players.get(0).xPos / 100);
                attack();
                die();
                break;
            case 2:
                switch (npcType) {
                    case 0:
                        idle();
                        break;
                    case 1:
                        cur_action = Action.MOV;
                        lrMove(ms);
                        break;
                    case 2:
                        randomMove();
                        break;
                    case 3:
                        if (pathfinding) searchPath((int)players.get(0).yPos / 100, (int)players.get(0).xPos / 100);
                        break;

                }
                break;
            case 4:
                moveForward();
                do_damage();
                kill();
                break;

        }
    }

    /**
     * Refreshes to see if current xp meets the need to level up
     * @author Christina
     */
    private void refreshXP() {
        if(cur_xp >= next_xp){
            levelUp();
        }
    }

    /**
     * Increases character level, also adds to damage scale and refreshes next amount of xp needed
     * @author Christina
     */
    private void levelUp(){
        level += 1;
        dmg_boost += 0.1;
        max_hp += 70;
        cur_hp += 70;
        next_xp = xps[level];
    }


    /**
     * Kills the object calling this method by taking away max hp
     * @author Christina
     */
    private void kill() {
        if(getDistance(this, players.get(0)) > scrX * 2 ||! collisionCheck()){
            cur_hp -= max_hp;
            die();
        } if (character_type == 4 && (xPos + xScale + move_spd >= levelWidth || xPos - move_spd <= 20 || yPos + yScale + move_spd >= levelHeight || yPos - move_spd <= 0 || getDistance(this, players.get(0)) > scrX)){
            cur_hp -= max_hp;
            die();
        }
    }

    /**
     * Forcefully kills the object passed into parameter
     * @param o the object wanted to kill
     * @author Christina
     */
    private void kill(GameObject o){
        o.cur_hp -= max_hp;
        o.die();
    }

    /**
     * Movement for attack objects that keeps them moving in a straight line
     * @author Christina
     */
    private void moveForward() {
        switch(cur_direction){
            case UP: moveUp(); break;
            case DOWN: moveDown(); break;
            case LEFT: moveLeft(); break;
            case RIGHT: moveRight(); break;
        }
    }

    /**
     * Basic code for idle npcs where they move slightly every approx 1 sec
     * @author Luka
     */
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



    /**
     * Basic code that makes npcs move back and forth
     * @param spd the npc's speed (quarter of player speed)
     * @author Luka
     */
    private void lrMove(double spd){
            if (cur_direction == Direction.LEFT) {
                if (xPos >= originX - 400) {
                    moveLeft();
                } else {
                    cur_direction = Direction.RIGHT;
                }
            }
            if (cur_direction == Direction.RIGHT) {
                if (xPos <= originX) {
                    moveRight();
                } else {
                    cur_direction = Direction.LEFT;
                }
            }

    }


    int moveTimer = 0;
    int lastDir = 0;

    //Written by Luka, beginnings of a random motion class for npc's
    private void randomMove(){
        if (moveTimer == 50) {
            int motion = r.nextInt(5) + 1;
            if (motion == 1) {
                lastDir = 1;
                moveLeft();
            }
            if (motion == 2) {
                lastDir = 2;
                moveRight();
            }
            if (motion == 3) {
                lastDir = 3;
                moveUp();
            }
            if (motion == 4) {
                lastDir = 4;
                moveDown();
            } if (motion == 5){
                lastDir = 0;
                cur_action = Action.IDLE;
            }
            moveTimer = 0;
        }
        if (lastDir == 1){
            moveLeft();
        } if (lastDir == 2){
            moveRight();
        } if (lastDir == 3){
            moveUp();
        } if (lastDir == 4){
            moveDown();
        }
        moveTimer ++;
    }

    private void updatePos(){
        if (cur_action == Action.MOV) {
            if (cur_direction == Direction.UP) {
                moveUp();
            }
            if (cur_direction == Direction.LEFT) {
                moveLeft();
            }
            if (cur_direction == Direction.DOWN) {
                moveDown();
            }
            if (cur_direction == Direction.RIGHT) {
                moveRight();
            }
        }
    }

    /**
     * Pathfinding that searches for shortest path to reach destination
     * @param goalRow the y coordinate of the destination
     * @param goalCol the x coordinate of the destination
     */
    private void searchPath(int goalRow, int goalCol){
        int startRow = (int) yPos / 100;
        int startCol = (int) xPos / 100;

        pathfind.setNode(startRow, startCol, goalRow, goalCol);

        if (pathfind.search()) {
            int nextX = pathfind.path.get(0).col * 100 + 50;
            int nextY = pathfind.path.get(0).row * 100 + 50;

            int left = (int) xPos;
            int right = (int) xPos + xScale;
            int up = (int) yPos;
            int down = (int) yPos + yScale;

            if (up > nextY && left >= nextX && right < nextX + 100) {
                moveUp();
            } else if (up < nextY && left >= nextX && right < nextX + 100) {
                moveDown();
            } else if (up >= nextY && down < nextY + 100){
                if (left > nextX){
                    moveLeft();
                } if (left < nextX){
                    moveRight();
                }
            } else if (up > nextY && left > nextX){
                moveUp();
                if (!collisionCheck()){
                    moveLeft();
                }
            } else if (up > nextY && left < nextX){
                moveUp();
                if (!collisionCheck()){
                    moveRight();
                }
            } else if (up < nextY && left > nextX){
                moveDown();
                if (!collisionCheck()){
                    moveLeft();
                }
            } else if (up < nextY && left < nextX){
                moveDown();
                if (!collisionCheck()){
                    moveRight();
                }
            }
        }
    }

    /**
     * For attacks only:
     * Checks for if the opposite character type from the attacker is within range, and deals damage if collides
     * @author Christina
     */
    private void do_damage() {
        switch(damage_type){
            case 1:
                for(GameObject enemy : enemies){
                    if(touches(enemy)){
                        enemy.takeDamage(atk_dmg);
                        kill(this);
                    }
                }
                break;
            case -1:
                for(GameObject player : players){
                    if(touches(player)){
                        player.takeDamage(atk_dmg);
                        kill(this);
                    }
                }
        }
    }

    /**
     * Checks for if this object's hitbox intersects with another object's hitbox
     * @param o the other object to check collision with
     * @return true if the two objects intersect
     * @author Christina
     */
    private boolean touches(GameObject o) {
        return hitbox.getBounds().intersects(o.hitbox.getBounds());
    }

    /**
     * Checks for if the current object is dead or not (is hp below 0), and sets is_dead to true if is dead
     * @author Christina
     */
    private void die() {
        if(cur_hp <= 0){
            is_dead = true;
        }
    }

    /**
     * Checks for if this object's hitbox collides with another object's hitbox
     * @return true if the two collides
     * @author Christina
     */
    private boolean characterCollision(){
        for(GameObject enemy : enemies){
            if(enemy != this && touches(enemy)){
                return true;
            }
        }
        for(GameObject player : players){
            if(player != this && touches(player)){
                return true;
            }
        }
        for(GameObject npc : npcs){
            if(npc != this && touches(npc)){
                return true;
            }
        }
        return false;
    }


    //Movement methods created by Christina(?) and edited by Luka
    /**
     * Checks for current y position, if within map boarders from the top and is not moving into an object with collision, moves up.
     * @author Christina
     */
    public void moveUp(){
        cur_direction = Direction.UP;
        cur_action = Action.MOV;
        if(yPos - ms > 0 && collisionCheck()){
            switch(character_type) {
                case 0, 1, 2:
                    hitbox.setLocation((int) xPos, (int) (yPos - ms));
                    if (!characterCollision()) {
                        yPos -= ms;
                    } else {
                        hitbox.setLocation((int) xPos, (int) yPos);
                    }
                    break;
                case 4:
                    yPos -= ms;
                    hitbox.setLocation((int) xPos, (int) yPos);
                    break;
            }
        }
    }
    /**
     * Checks for current y position, if within boarders from the bottom and is not moving into an object with collision, moves up.
     * @author Christina
     */
    public void moveDown(){
        cur_direction = Direction.DOWN;
        cur_action = Action.MOV;
        if(yPos + yScale + ms < levelHeight && collisionCheck()){
            switch(character_type) {
                case 0, 1, 2:
                    hitbox.setLocation((int) xPos, (int) (yPos + ms));
                    if (!characterCollision()) {
                        yPos += ms;
                    } else {
                        hitbox.setLocation((int) xPos, (int) yPos);
                    }
                    break;
                case 4:
                    yPos += ms;
                    hitbox.setLocation((int) xPos, (int) yPos);
                    break;
            }
        }
    }

    /**
     * Checks for current x position, if within boarders from the left side and is not moving into an object with collision, moves left.
     * @author Christina
     */
    public void moveLeft(){
        cur_direction = Direction.LEFT;
        cur_action = Action.MOV;
        if(xPos - ms > 10 && collisionCheck()){
            switch(character_type) {
                case 0, 1, 2:
                    hitbox.setLocation((int) (xPos - ms), (int) (yPos));
                    if (!characterCollision()) {
                        xPos -= ms;
                    } else {
                        hitbox.setLocation((int) xPos, (int) yPos);
                    }
                    break;
                case 4:
                    xPos -= ms;
                    hitbox.setLocation((int) xPos, (int) yPos);
                    break;
            }
        }
    }
    /**
     * Checks for current x position, if within boarders from the right side and is not moving into an object with collision, moves right.
     * @author Christina
     */
    public void moveRight(){
        cur_direction = Direction.RIGHT;
        cur_action = Action.MOV;
        if(xPos + xScale + ms < levelWidth && collisionCheck()){
            switch(character_type) {
                case 0, 1, 2:
                    hitbox.setLocation((int) (xPos + ms), (int) yPos);
                    if (!characterCollision()) {
                        xPos += ms;
                    } else {
                        hitbox.setLocation((int) xPos, (int) yPos);
                    }
                    break;
                case 4:
                    xPos += ms;
                    hitbox.setLocation((int) xPos, (int) yPos);
                    break;
            }
        }
    }

    /**
     * Attack for player, checks for attack key input direction and creates an attack object facing the same direction
     * also checks for if time between attacks is long enough
     * @param dir direction of the attack
     * @author Christina
     */
    public void attack(String dir){
        cur_atkcd = System.currentTimeMillis() - last_atkcd;
        if (cur_atkcd / 1000 > atk_spd) {
            int ad = (int) (atk_dmg * dmg_boost);
            switch (dir) {
                case "U":
                    GameFrame.addObject(ad, 1, atk_type, object_id, xPos, yPos, "u");
                    last_atkcd = System.currentTimeMillis();
                    break;
                case "D":
                    GameFrame.addObject(ad, 1, atk_type, object_id, xPos, yPos, "d");
                    last_atkcd = System.currentTimeMillis();
                    break;
                case "L":
                    GameFrame.addObject(ad, 1, atk_type, object_id, xPos, yPos, "l");
                    last_atkcd = System.currentTimeMillis();
                    break;
                case "R":
                    GameFrame.addObject(ad, 1, atk_type, object_id, xPos, yPos, "r");
                    last_atkcd = System.currentTimeMillis();
                    break;
            }
        }
        last_atkcd = cur_atkcd;
        System.out.println(cur_atkcd);
        System.out.println(last_atkcd);
    }

    /**
     * Attack for enemies, checks for if player is within attack range and attacks if true
     * attacks are in the direction that the enemies are facing
     * @author Christina
     */
    private void attack() throws FileNotFoundException {
        cur_atkcd = System.currentTimeMillis() - last_atkcd;
        if (cur_atkcd / 1000 > atk_spd && withinAttackRange()) {
            switch (atk_type) {
                case -1, 1:
                    switch (cur_direction) {
                        case UP:
                            GameFrame.addObject(atk_dmg, -1, atk_type, object_id, xPos, yPos, "u");
                            last_atkcd = System.currentTimeMillis();
                            break;
                        case DOWN:
                            GameFrame.addObject(atk_dmg, -1, atk_type, object_id, xPos, yPos, "d");
                            last_atkcd = System.currentTimeMillis();
                            break;
                        case LEFT:
                            GameFrame.addObject(atk_dmg, -1, atk_type, object_id, xPos, yPos, "l");
                            last_atkcd = System.currentTimeMillis();
                            break;
                        case RIGHT:
                            GameFrame.addObject(atk_dmg, -1, atk_type, object_id, xPos, yPos, "r");
                            last_atkcd = System.currentTimeMillis();
                            break;
                    }
                    break;
                case 0:
                    Scanner s = new Scanner(new File("data/objectData/enemyTest.csv"));
                    String[] data = s.nextLine().split(",");
                    s.close();
                    GameFrame.addObject(data, 0);
                    last_atkcd = System.currentTimeMillis();
                    break;

            }
        }

    }

    /**
     * Checks for if player is within the attack range of the enemy character
     * @return true if player is within attack range of this enemy object
     * @author Christina
     */
    private boolean withinAttackRange() {
        double atk_range = 0;
        switch(atk_type){
            case -1: atk_range = 50; break;
            case 1: atk_range = 500; break;
            case 0: atk_range = 2000; break;
        }
        for(GameObject player : players) {
            return getRange(atk_range);
        }
        return false;
    }


    /**
     * Gets the range of the player compared to this enemy object and compares to attack range
     * @param atk_range the range of the attack
     * @return true if player is within attack range
     * @author Christina
     */
    private boolean getRange(double atk_range){
        GameObject player = players.get(0);
        if(atk_type == 0){
            return getDistance(this, player) < atk_range;
        }
        switch(cur_direction){
            case UP:
                if(Math.abs(xPos - player.xPos) < 50){
                    return yPos - player.yPos < atk_range;
                }
                break;
            case DOWN:
                if(Math.abs(xPos - player.xPos) < 50){
                    return player.yPos - yPos < atk_range;
                }
                break;
            case LEFT:
                if(Math.abs(yPos - player.yPos) < 50){
                    return xPos - player.xPos < atk_range;
                }
                break;
            case RIGHT:
                if(Math.abs(yPos - player.yPos) < 50){
                    return player.yPos - yPos < atk_range;
                }
                break;
        }
        return false;
    }

    /**
     * getting objects in range and then triggering the doInteract() function
     * @author Graham, edited by Luka
     */
    public void interact() {
        for (GameObject intractable : interactables) {
            System.out.println(getDistance(intractable, this));
            if (cur_direction == Direction.UP) {
                if (getDistance(intractable, this) <= 100) {
                    intractable.doInteract();
                }
            } if (cur_direction == Direction.DOWN){
                if (getDistance(intractable, this) >= 100) {
                    intractable.doInteract();
                }
            }
        }
    }

    /**
     *completing object specific interactions depending on object type
     * @author Graham
     */
    private void doInteract() {
        switch (type) {
            case DOOR_IN:
                Setup.curMap = 1;
                Main.bgm.changeTrack(2);
                break;
            case DOOR_OUT:
                if(Setup.curMap >= 1) Setup.curMap --;
                Main.bgm.changeTrack(1);
                break;
            default:
                System.out.println("poo poo"); //????? LMAO
                break;
        }
    }

    /**
     * Takes away from this object's current hp
     * @param dmg the amount of damage the other object deals
     * @author Christina
     */
    public void takeDamage(double dmg){
        cur_action = Action.DMG;
        if(cur_hp - dmg >= 0){
            cur_hp -= dmg;
        } else {
            cur_hp = 0;
        }
    }

    /**
     * Uses this character's specific ability
     */
    public void useAbility(){
        //somehow get range here


    }

    /**
     * Updates all cooldowns for this object for each tick time
     * @author Christina
     */
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




    /**
     * Find the tile the player is currently on, as of now only used for testing and debugging
     * @return the tile type the player is on
     * @author Luka
     */
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

    /**
     * Checks for tile in front of player to see if it has collision or not
     * @return true if the tile in front has collision enabled
     * @author Luka
     */
    public boolean collisionCheck(){
        int toTouch;

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


    /**
     * Draws any on-screen objects including the player
     * @param gr Graphics2D component passed from paintComponent
     * @param drawX x position of non-player objects on map
     * @param drawY y position of non-player objects on map
     * @author Luka
     */
    public void draw(Graphics2D gr, int drawX, int drawY){ //to be draw camera
        switch(character_type) {
            case 0: gr.drawImage(LoadedSprites.pullTexture(object_id + "_" + cur_direction + "_" + cur_action), scrX, scrY, xScale, yScale, new Color(0, 0, 0, 0), null);break;
            case 1, 2: gr.drawImage(LoadedSprites.pullTexture(object_id + "_" + cur_direction + "_" + cur_action), drawX, drawY, xScale, yScale, new Color(0, 0, 0, 0), null);break;
            case 3: gr.drawImage(LoadedSprites.pullTexture(object_id), drawX, drawY, xScale, yScale, null); break;
            case 4: gr.drawImage(LoadedSprites.pullTexture(object_id + "_attack"), drawX, drawY, xScale, yScale, null); break;
        }
    }

    /**
     * Checks to see if object is within visible bounds of screen
     * @param gr Graphics2D component passed from paintComponent - to be passed to "draw" method
     * @author Luka
     */
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
     * Draws the player status overlay
     * @param gr the graphics thing
     * @author Luka
     */
    public void drawOverlay(Graphics2D gr){
        gr.setColor(Color.white);
        gr.fillRect(20, Main.y - 200,400, 170);
        gr.setColor(Color.black);
        gr.fillRect(25, Main.y - 195,390, 160);
        gr.setFont(new Font("Calibri Bold", Font.PLAIN, 26));
        gr.setColor(Color.white);
        gr.drawString("HP", 50, Main.y - 100);
        gr.drawString("XP", 50, Main.y - 55);
        gr.drawString("LEVEL == " + level, 50, Main.y - 145);
        gr.setColor(Color.gray);
        gr.fillRect(100, Main.y - 120, 200, 25);
        gr.fillRect(100, Main.y - 75, 200, 25);
        gr.setColor(Color.blue);
        gr.fillRect(100, Main.y - 75, (int) (cur_xp / next_xp * 200), 25);
        gr.setColor(Color.red);
        gr.fillRect(100, Main.y - 120, (int) (cur_hp / max_hp * 200), 25);
        gr.setColor(Color.white);
        gr.drawString(cur_xp + "/" + next_xp, 110, Main.y - 55);
        gr.drawString(cur_hp + "/" + max_hp, 110, Main.y - 100);
        System.out.println(cur_hp);


    }

    /**
     * Saves all data for this object.
     * @return a double array that contains all field data of this object.
     * @author Graham or Luka idk
     */
    public String[] saveData() {
        return new String[]{Integer.toString(character_type), Double.toString(max_hp), Double.toString(cur_hp), Double.toString(atk_dmg), Double.toString(atk_spd), Integer.toString(atk_type), Double.toString(ap), Long.toString(cd), Long.toString(cur_cd), ability_name, Integer.toString(ability_range), object_id};
    }


    /**
     * @return the object ID of this object
     */
    public String getObjectID() {
        return object_id;
    }

    //we might need these for setting location of randomly spawned enemies
    public void setxPos(double x){
        xPos = x;
    }

    public void setyPos(double y){
        yPos = y;
    }

    /**
     * checking distance between objects for interactions and other things
     * @author Graham
     */
    public double getDistance(GameObject x, GameObject y) {
        return Math.sqrt(Math.pow((x.xPos - y.xPos), 2) + Math.pow((x.yPos - y.yPos), 2));
    }


    /**
     * @return the status of the current object, if it is dead or not, and adds to player's xp bar if an enemy is defeated
     * @author Christina
     */
    public boolean died() {
        if(is_dead && character_type == 1){
            players.get(0).cur_xp += 50;
        }
        if(is_dead){
            hitbox.setLocation(0, 0); //throw into waste corner
        }
        return is_dead;
    }




}
