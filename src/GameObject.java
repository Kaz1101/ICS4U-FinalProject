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
    public PathfindAI pathfind = new PathfindAI();
    public boolean pathfinding = false;
    private int counter = 0;
    private int moveBackCount = 0;
    private static Random r = new Random();


    /**
     * @author Christina, adjusted by Luka
     * Sets up and initializes variables based on object data given from a csv file
     * @param temp a String array consisting of data for specific object being loaded
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
                ms = move_spd * r.nextDouble() + 0.5;
                if(ms > move_spd){
                    ms -= 0.7;
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
     * @author Christina
     * Constructor for attack objects
     * @param atk_dmg the damage that this object deals
     * @param damage_type the type of character that released this attack
     * @param atk_type the range of the attack
     * @param character_id the specific character that released this attack
     * @param xPos x position of the character that released this attack
     * @param yPos the y position of the character that released this attack
     * @param dir the direction the character is facing when releasing this attack
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
     * @author Christina, Luka
     * The method that gets called for each timer tick, checks for and refreshes object status
     */
    public void doTick(){
        switch(character_type) {
            case 0:
                refreshCD();
                //useAbility();
                die();
                break;
            case 1:
                if (pathfinding) searchPath((int)players.get(0).yPos / 100, (int)players.get(0).xPos / 100);
                attack();
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
     * @author Christina
     * Kills the object calling this method by taking away max hp
     */
    private void kill() {
        if(getDistance(this, players.get(0)) > scrX * 2 ||! collisionCheck()){
            cur_hp -= max_hp;
            die();
        } if (character_type == 4 && (xPos + xScale + move_spd >= levelWidth || xPos - move_spd <= 0 || yPos + yScale + move_spd >= levelHeight || yPos - move_spd <= 0)){
            cur_hp -= max_hp;
            die();
        }
    }

    private void kill(GameObject o){
        o.cur_hp -= max_hp;
        o.die();
    }

    /**
     * @author Christina
     * Movement for attack objects that keeps them moving in a straight line
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
     * @author Luka
     * Basic code for idle npcs where they move slightly every approx 1 sec
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
     * @author Luka
     * Basic code that makes npcs move back and forth
     * @param spd the npc's speed (quarter of player speed)
     */
    private void lrMove(double spd){
            if (cur_direction == Direction.LEFT) {
                if (xPos >= originX - 400) {
                    xPos -= spd;
                } else {
                    cur_direction = Direction.RIGHT;
                }
            }
            if (cur_direction == Direction.RIGHT) {
                if (xPos <= originX) {
                    xPos += spd;
                } else {
                    cur_direction = Direction.LEFT;
                }
            }
            moveBackCount = 0;
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
     *
     * @param goalRow the y coordinate of the destination
     * @param goalCol the x coordinate of the destination
     */
    private void searchPath(int goalRow, int goalCol){
        int startRow = (int) yPos / 100;
        int startCol = (int) xPos / 100;

        pathfind.setNode(startRow, startCol, goalRow, goalCol);

        if (pathfind.search()) {
            int nextX = pathfind.path.get(0).col * 100;
            int nextY = pathfind.path.get(0).row * 100;

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

//            int nextRow = pathfind.path.get(0).row;
//            int nextCol = pathfind.path.get(0).col;
//            if (nextCol == goalCol && nextRow == goalRow){
//                pathfinding = false;
//            }
        }
    }

    /**
     * Checks for if the opposite character type from the attacker is within range, and deals damage if collides
     */
    private void do_damage() {
        switch(damage_type){
            case 1:
                for(GameObject enemy : enemies){
                    if(getDistance(this, enemy) < 10){
                        enemy.takeDamage(atk_dmg);
                        kill(this);
                    }
                }
                break;
            case -1:
                for(GameObject player : players){
                    if(getDistance(this, player) < 10){
                        player.takeDamage(atk_dmg);
                        kill(this);
                    }
                }
        }
    }

//    /**
//     * Checks for if this object intersects with another object
//     * @param o the other object to check collision with
//     * @return true if the two objects intersect
//     */
//    private boolean touches(GameObject o) {
//        return this.getBounds().intersects(o.getBounds());
//    }

    /**
     * Checks for if the current object is dead or not (is hp below 0), and sets is_dead to true if is dead
     */
    private void die() {
        if(cur_hp <= 0){
            is_dead = true;
        }
    }


    //Movement methods created by Christina(?) and edited by Luka
    /**
     * Checks for current y position, if within map boarders from the top and is not moving into an object with collision, moves up.
     */
    public void moveUp(){
        cur_direction = Direction.UP;
        cur_action = Action.MOV;
        if(yPos - ms > 0 && collisionCheck()){
                yPos -= ms;
        }
    }
    /**
     * Checks for current y position, if within boarders from the bottom and is not moving into an object with collision, moves up.
     */
    public void moveDown(){
        cur_direction = Direction.DOWN;
        cur_action = Action.MOV;
        if(yPos + yScale + ms < levelHeight && collisionCheck()){
                yPos += ms;
        }
    }

    /**
     * Checks for current x position, if within boarders from the left side and is not moving into an object with collision, moves left.
     */
    public void moveLeft(){
        cur_direction = Direction.LEFT;
        cur_action = Action.MOV;
        if(xPos - ms > 10 && collisionCheck()){
            xPos -= ms;
        }
    }
    /**
     * Checks for current x position, if within boarders from the right side and is not moving into an object with collision, moves right.
     */
    public void moveRight(){
        cur_direction = Direction.RIGHT;
        cur_action = Action.MOV;
        if(xPos + xScale + ms < levelWidth && collisionCheck()){
            if(xPos - ms > 0 && collisionCheck()){
                    xPos += ms;
            }
        }
    }

    /**
     * Attack for player, checks for attack key input direction and creates an attack object facing the same direction
     * also checks for if time between attacks is long enough
     * @param dir direction of the attack
     */
    public void attack(String dir){
        cur_atkcd = System.currentTimeMillis() - last_atkcd;
        if (cur_atkcd / 1000 > atk_spd) {
            switch (dir) {
                case "U":
                    GameFrame.addObject(atk_dmg, damage_type, atk_type, object_id, xPos, yPos, "u");
                    last_atkcd = System.currentTimeMillis();
                    break;
                case "D":
                    GameFrame.addObject(atk_dmg, damage_type, atk_type, object_id, xPos, yPos, "d");
                    last_atkcd = System.currentTimeMillis();
                    break;
                case "L":
                    GameFrame.addObject(atk_dmg, damage_type, atk_type, object_id, xPos, yPos, "l");
                    last_atkcd = System.currentTimeMillis();
                    break;
                case "R":
                    GameFrame.addObject(atk_dmg, damage_type, atk_type, object_id, xPos, yPos, "r");
                    last_atkcd = System.currentTimeMillis();
                    break;
            }
        }
        System.out.println(cur_atkcd);
        System.out.println(last_atkcd);
    }

    /**
     * Attack for enemies, checks for if player is within attack range and attacks if true
     * attacks are in the direction that the enemies are facing
     */
    private void attack(){
        cur_atkcd = System.currentTimeMillis() - last_atkcd;
        if (cur_atkcd / 1000 > atk_spd && withinAttackRange()) {
            switch (cur_direction) {
                case UP:
                    GameFrame.addObject(atk_dmg, damage_type, atk_type, object_id, xPos, yPos, "u");
                    last_atkcd = System.currentTimeMillis();
                    break;
                case DOWN: GameFrame.addObject(atk_dmg, damage_type, atk_type, object_id, xPos, yPos, "d");
                    last_atkcd = System.currentTimeMillis();
                    break;
                case LEFT: GameFrame.addObject(atk_dmg, damage_type, atk_type, object_id, xPos, yPos, "l");
                    last_atkcd = System.currentTimeMillis();
                    break;
                case RIGHT: GameFrame.addObject(atk_dmg, damage_type, atk_type, object_id, xPos, yPos, "r");
                    last_atkcd = System.currentTimeMillis();
                    break;
            }
        }
    }

    /**
     * Checks for if player is within the attack range of the enemy character
     * @return true if player is within attack range of this enemy object
     */
    private boolean withinAttackRange() {
        double atk_range = 0;
        switch(atk_type){
            case -1: atk_range = 50; break;
            case 1: atk_range = 500; break;
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
     */
    private boolean getRange(double atk_range){
        GameObject player = players.get(0);
        switch(cur_direction){
            case UP:
                if(Math.abs(xPos - player.xPos) < 20){
                    return yPos - player.yPos < atk_range;
                }
                break;
            case DOWN:
                if(Math.abs(xPos - player.xPos) < 20){
                    return player.yPos - yPos < atk_range;
                }
                break;
            case LEFT:
                if(Math.abs(yPos - player.yPos) < 20){
                    return xPos - player.xPos < atk_range;
                }
                break;
            case RIGHT:
                if(Math.abs(yPos - player.yPos) < 20){
                    return player.yPos - yPos < atk_range;
                }
                break;
        }
        return false;
    }

    /**
     * @author Graham, edited by Luka
     * getting objects in range and then triggering the doInteract() function
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
     * @author Graham
     * completing object specific interactions depending on object type
     */
    private void doInteract() {
        switch (type) {
            case DOOR_IN:
                Setup.curMap = 1;
                break;
            case DOOR_OUT:
                if(Setup.curMap >= 1) Setup.curMap --;
                break;
            default:
                System.out.println("poo poo"); //????? LMAO
                break;
        }
    }

    /**
     * Takes away from this object's current hp
     * @param dmg the amount of damage the other object deals
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


    public void showAttackAnimation(){

    }


    /**
     * Written by Luka
     * Find the tile the player is currently on, as of now only used for testing and debugging
     * @return the tile type the player is on
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
     * Written by Luka
     * Checks for tile in front of player to see if it has collision or not
     * @return the collision status of tile in front of player
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
     * Written by Luka
     * Draws any on-screen objects including the player
     * @param gr Graphics2D component passed from paintComponent
     * @param drawX x position of non-player objects on map
     * @param drawY y position of non-player objects on map
     */
    public void draw(Graphics2D gr, int drawX, int drawY){ //to be draw camera
        switch(character_type) {
            case 0: gr.drawImage(LoadedSprites.pullTexture(object_id + "_" + cur_direction + "_" + cur_action), scrX, scrY, xScale, yScale, null); break;
            case 1, 2: gr.drawImage(LoadedSprites.pullTexture(object_id + "_" + cur_direction + "_" + cur_action), drawX, drawY, xScale, yScale, null); break;
            case 3: gr.drawImage(LoadedSprites.pullTexture(object_id), drawX, drawY, xScale, yScale, null); break;
            case 4: gr.drawImage(LoadedSprites.pullTexture(object_id + "_attack"), drawX, drawY, xScale, yScale, null); break;
        }

    }

    /**
     * Written by Luka
     * Checks to see if object is within visible bounds of screen
     * @param gr Graphics2d component passed from paintComponent - to be passed to "draw" method
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
     * Saves all data for this object.
     * @return a double array that contains all field data of this object.
     */
    public String[] saveData() {
        return new String[]{Integer.toString(character_type), Double.toString(max_hp), Double.toString(cur_hp), Double.toString(atk_dmg), Double.toString(atk_spd), Integer.toString(atk_type), Double.toString(ap), Long.toString(cd), Long.toString(cur_cd), ability_name, Integer.toString(ability_range), object_id};
    }


    /**
     *
     * @return the object ID of this object
     */
    public String getObjectID() {
        return object_id;
    }

    //we might not need these
    public void setxPos(double x){
        xPos = x;
    }

    public void setyPos(double y){
        yPos = y;
    }

    /**
     * @author Graham
     * checking distance between objects for interactions and other things
     */
    public double getDistance(GameObject x, GameObject y) {
        return Math.sqrt(Math.pow((x.xPos - y.xPos), 2) + Math.pow((x.yPos - y.yPos), 2));
    }

    /**
     *
     * @return the status of the current object, if it is dead or not
     */
    public boolean died() {
        return is_dead;
    }




}
