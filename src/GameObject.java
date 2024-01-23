import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class GameObject extends JComponent {
    private enum Direction {LEFT, RIGHT, UP, DOWN}
    private enum ObjectType {PLAYER, NPC, ENEMY, DOOR_IN, DOOR_OUT, POTION}
    public enum Action {IDLE, MOV, ATK, DMG, INTERACT}

    private static ArrayList<GameObject> players = new ArrayList<>(2);
    private static ArrayList<GameObject> enemies = new ArrayList<>(30);
    //    private static ArrayList<GameObject> interactables = new ArrayList<>(30);
    private static ArrayList<GameObject> npcs = new ArrayList<>(5); //testing testing! may become object or that could be another arraylist
    private static ArrayList<GameObject> worldObj = new ArrayList<>(15); //testing testing! may become object or that could be another arraylist
    private static int window_width, window_length;
    private  Direction cur_direction = Direction.UP; //characters spawn looking up
    public Action cur_action = Action.IDLE;
    private ObjectType type;
    private boolean is_dead = false;
    //public String cur_tile;
    private static double move_spd;
    private double ms; //movement speed for this object
    private boolean speed;
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
    private double levelWidth = Setup.colMax[GameFrame.curMap] * 100;//the 100 is scale of tiles, temp
    private double levelHeight = Setup.rowMax[GameFrame.curMap] * 100;//the 100 is scale of tiles, temp
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
    private int enemyCap = 5;
    private int enemyCount = 0;
    private String ability_name; //we might not need this (update. we need this to read animation)
    private int ability_range; //range for ability
    public int object_type; //0 = player, 1 = enemy, 2 = npc, 3 = static object, 4 = attack
    private String object_id; //for reading sprite images
    private int mapLevel;
    private int npcType;
    private boolean interactable;
    public boolean collected;
    public Inventory inventory;
    public boolean canUseItem = true;
    public PathfindAI pathfind = new PathfindAI();
    public boolean pathfinding = false;
    private int counter = 0;
    private static Random r = new Random();
    private Rectangle hitbox = new Rectangle(0, 0, 0, 0);
    private double cur_xp;
    private double[] xps = {100, 500, 1200, 2000, 2800, 3600, 4500, 20000000}; //havent finalized xp amount and gain
    private double next_xp = xps[0];
    private int level = 1;
    private double dmg_boost = 1; //multiplier
    private boolean speed_lock = true;


    /**
     * Sets up and initializes variables based on object data given from a csv file
     * @param temp a String array consisting of data for specific object being loaded
     * @author Christina, adjustments by Luka
     */
    public GameObject(String[] temp) {
        object_type = Integer.parseInt(temp[0]);

        switch (object_type) {
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
                interactable = Boolean.parseBoolean(temp[16]);
                mapLevel = Integer.parseInt(temp[17]);
                if (object_type == 0) {
                    cur_xp = Double.parseDouble(temp[18]);
                    enemyCount = Integer.parseInt(temp[19]);
                }

                hitbox = new Rectangle((int) xPos, (int) yPos, xScale, yScale);
//                if (Boolean.parseBoolean(temp[16])) {
//                    interactables.add(this);
//                }
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
                interactable = Boolean.parseBoolean(temp[7]);
//                if (Boolean.parseBoolean(temp[7])) {
//                    interactables.add(this);
//                }
            }
            case 3 -> {
                object_id = temp[1];
                xPos = Double.parseDouble(temp[2]);
                yPos = Double.parseDouble(temp[3]);
                xScale = Integer.parseInt(temp[4]);
                yScale = Integer.parseInt(temp[5]);
                interactable = Boolean.parseBoolean(temp[6]);
                if (object_id.equals("house1")) hitbox = new Rectangle((int) xPos + 50, (int) yPos - 50, xScale, yScale);
//                if (Boolean.parseBoolean(temp[6])) {
//                    interactables.add(this);
//                }
                collected = Boolean.parseBoolean(temp[7]);
            }
        }

        //add this game character to corresponding arraylist
        switch(object_type) {
            case 0:
                type = ObjectType.PLAYER;
                hitboxL = 10; //temp, the hitbox stuff is all currently under testing
                hitboxR = xScale - 10;
                hitboxU = yScale - 50;
                hitboxD = yScale;
                ms = move_spd;
                GameFrame.curMap = mapLevel;
                inventory = new Inventory();
                if (Main.input.startOld){
                    String[] inv = RWFile.readData("inventory");
                    if (inv != null) {
                        for (int i = 0; i < inv.length; i++){
                            inventory.inventorySpace.add(new GameObject(RWFile.readData(inv[i])));
                        }
                    }

                }
                players.add(this);
                break;
            case 1:
                type = ObjectType.ENEMY;
                //test
                pathfinding = true;

                enemies.add(this);

                if (this.characterCollision()){
                    xPos += 150;
                    yPos -= 150;
                }

                if(atk_type == 0){
                    ms = 0;
                } else {
                    ms = move_spd * ((r.nextDouble(40) + 20) / 100);
                }
                if(object_id.contains("boss")){
                    ms = move_spd;
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
                worldObj.add(this);
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
        object_type = 4;
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
    public void doTick(){
        switch(object_type) {
            case 0:
                refreshCD();
                refreshXP();
                //useAbility();
//                System.out.println(enemyCount);
                if (speed) spdBoost();
                die();
                break;
            case 1:
                GameObject player = players.get(0);
                if (pathfinding && getDistance(this, player) < 750) {
                    searchPath((int)(player.yPos + player.yScale / 2) / 100, (int)(player.xPos + player.xScale / 2) / 100);
                }
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
                        lrMove();
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
        max_hp += 20;
        cur_hp += 20;
        next_xp = xps[level];
        if(level == 7){
            speed_lock = false;
        }
    }


    /**
     * Kills the object calling this method by taking away max hp
     * @author Christina
     */
    private void kill() {
        if(getDistance(this, players.get(0)) > scrX * 2 ||! collisionCheck()){
            cur_hp -= max_hp;
            die();
        } if (object_type == 4 &&  !collisionCheck() || (xPos + xScale + ms >= levelWidth || xPos - ms <= 0 || yPos + yScale + ms >= levelHeight || yPos - ms <= 0)){
            cur_hp -= max_hp;
            die();
            Main.bgm.playSFX(1);
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
        Main.bgm.playSFX(1);
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
     * @author Luka
     */
    private void lrMove(){
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
            if (object_type == 1){
                players.get(0).enemyCount--;
            }
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
        for(GameObject obj : worldObj){
            if(obj != this && touches(obj) && GameFrame.curMap == 0){
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
            switch(object_type) {
                case 0, 1, 2:
                    hitbox.setLocation((int) xPos, (int) (yPos - ms));
                    if (!characterCollision()) {
                        yPos -= ms;
                    } else {
                        hitbox.setLocation((int) xPos, (int) yPos);
                        if (object_type == 0){
                            Main.bgm.playSFX(2);
                        }
                    }
                    break;
                case 4:
                    yPos -= ms;
                    hitbox.setLocation((int) xPos, (int) yPos);
                    break;
            }
        } else if (object_type == 0) {
            Main.bgm.playSFX(2);
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
            switch(object_type) {
                case 0, 1, 2:
                    hitbox.setLocation((int) xPos, (int) (yPos + ms));
                    if (!characterCollision()) {
                        yPos += ms;
                    } else {
                        hitbox.setLocation((int) xPos, (int) yPos);
                        if (object_type == 0){
                            Main.bgm.playSFX(2);
                        }
                    }
                    break;
                case 4:
                    yPos += ms;
                    hitbox.setLocation((int) xPos, (int) yPos);
                    break;
            }
        } else if (object_type == 0){
            Main.bgm.playSFX(2);
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
            switch(object_type) {
                case 0, 1, 2:
                    hitbox.setLocation((int) (xPos - ms), (int) (yPos));
                    if (!characterCollision()) {
                        xPos -= ms;
                    } else {
                        hitbox.setLocation((int) xPos, (int) yPos);
                        if (object_type == 0){
                            Main.bgm.playSFX(2);
                        }
                    }
                    break;
                case 4:
                    xPos -= ms;
                    hitbox.setLocation((int) xPos, (int) yPos);
                    break;
            }
        } else if (object_type == 0){
            Main.bgm.playSFX(2);
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
            switch(object_type) {
                case 0, 1, 2:
                    hitbox.setLocation((int) (xPos + ms), (int) yPos);
                    if (!characterCollision()) {
                        xPos += ms;
                    } else {
                        hitbox.setLocation((int) xPos, (int) yPos);
                        if (object_type == 0){
                            Main.bgm.playSFX(2);
                        }
                    }
                    break;
                case 4:
                    xPos += ms;
                    hitbox.setLocation((int) xPos, (int) yPos);
                    break;
            }
        } else if (object_type == 0){
            Main.bgm.playSFX(2);
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
            if(!speed_lock){
                last_atkcd = cur_atkcd;
            } else {
                last_atkcd = System.currentTimeMillis();
            }
            Main.bgm.playSFX(0);
        }
//        last_atkcd = cur_atkcd;
//        System.out.println(cur_atkcd);
//        System.out.println(last_atkcd);
    }

    /**
     * Attack for enemies, checks for if player is within attack range and attacks if true
     * attacks are in the direction that the enemies are facing
     * @author Christina
     */
    private void attack() {
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
                    if(!speed_lock){
                        last_atkcd = cur_atkcd;
                    } else {
                        last_atkcd = System.currentTimeMillis();
                    }
                    Main.bgm.playSFX(0);
                    break;
                case 0:
                    if (players.get(0).enemyCount < players.get(0).enemyCap){
                    Scanner s = null;
                    try {
//                        int spawnerNum = Integer.parseInt(object_id.replaceAll("\\D", ""));
                        int idk = (int) (r.nextDouble(7)) + 1;
//                        if (spawnerNum == 2) idk = r.nextInt(7) + 1;
                        s = new Scanner(new File("data/objectData/enemy" + idk + ".csv"));
                    } catch (FileNotFoundException e) {
                        System.out.println("no enemy for you :D");
                        //                        throw new RuntimeException(e);
                    }

                    String[] data = s.nextLine().split(",");
                    s.close();

                        GameFrame.addObject(data, GameFrame.curMap);
                        players.get(0).enemyCount++;
                        last_atkcd = System.currentTimeMillis();
                    }
                    break;

            }
            Main.bgm.playSFX(0);
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
            case 0: atk_range = 800; break;
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
        if (GameFrame.curMap == 0) {
            for (GameObject obj : GameFrame.game_objects) {
                if (obj.interactable) {
                    if (getDistance(obj, this) <= 75) {
                        obj.doInteract();
                        break;
                    }
                }
            }
        } if (GameFrame.curMap == 1){
            for (GameObject obj : GameFrame.sub_game_objects) {
                if (obj.interactable) {
                    if (getDistance(obj, this) <= 75) {
                        obj.doInteract();
                        break;
                    }
                }
            }
        }
    }

    /**
     *completing object specific interactions depending on object type
     * @author Graham, edited by Luka
     */
    private void doInteract() {
        String obj = object_id.replaceAll("\\d", "");
        switch (obj) {
            case "door_in":
                GameFrame.curMap = 1;
                Main.bgm.playSFX(4);
                Main.bgm.changeTrack(2);
                break;
            case "door_out":
                if(GameFrame.curMap >= 1) GameFrame.curMap --;
                Main.bgm.playSFX(4);
                Main.bgm.changeTrack(1);
                break;
            case "potion":
                if (!collected) {
                    players.get(0).inventory.inventorySpace.add(this);
                    collected = true;
                }
                break;
            case "spdBoost":
                Main.bgm.playSFX(6);
                players.get(0).speed = true;
                collected = true;
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



    public void useItem(){
        if (inventory.getCurItemIDX() < inventory.inventorySpace.size()) {
            GameObject obj = inventory.getCurObj();
            if (canUseItem) {
                String object = obj.object_id.replaceAll("\\d", "");
                switch (object) {
                    case "potion" -> {
                        Main.bgm.playSFX(5);
                        if (cur_hp < max_hp - 20){
                            cur_hp += 20;
                        } else {
                            cur_hp += max_hp - cur_hp;
                        }
                        canUseItem = false;
                        inventory.inventorySpace.remove(obj);
                    }
                    case "asdf" -> {
                        System.out.println("yeeee");
                    }
                }
            }
        }
    }

    private void spdBoost(){
        if (counter == 0) ms *= 2;
        counter++;
        System.out.println("spd!");
        if (counter == 500){
            ms /= 2;
            speed = false;
            counter = 0;
            System.out.println("slow");
        }
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
        System.out.println("[" + y + "," + x + "]");
        return Setup.textureData[GameFrame.curMap][y][x];
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
                return !Setup.collisionData[GameFrame.curMap][toTouch][(int) (xPos + hitboxL) / 100] &&
                        !Setup.collisionData[GameFrame.curMap][toTouch][(int) (xPos + (hitboxR - hitboxL)) / 100];
            } case LEFT -> {
                toTouch = (int) (xPos + hitboxL - move_spd) / 100;
                return !Setup.collisionData[GameFrame.curMap][(int) (yPos + hitboxU) / 100][toTouch] &&
                        !Setup.collisionData[GameFrame.curMap][(int) (yPos + (hitboxD - hitboxU)) / 100][toTouch];
            } case DOWN -> {
                toTouch = (int) (yPos + hitboxD + move_spd) / 100;
                return !Setup.collisionData[GameFrame.curMap][toTouch][(int) (xPos + hitboxL) / 100] &&
                        !Setup.collisionData[GameFrame.curMap][toTouch][(int) (xPos + (hitboxR - hitboxL)) / 100];
            } case RIGHT -> {
                toTouch = (int) (xPos + hitboxR + move_spd) / 100;
                return !Setup.collisionData[GameFrame.curMap][(int) (yPos + hitboxU) / 100][toTouch] &&
                        !Setup.collisionData[GameFrame.curMap][(int) (yPos + (hitboxD - hitboxU)) / 100][toTouch];
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
        switch(object_type) {
            case 0: gr.drawImage(LoadedSprites.pullTexture(object_id + "_" + cur_direction + "_" + cur_action), scrX, scrY, xScale, yScale, new Color(0, 0, 0, 0), null);break;
            case 1, 2: gr.drawImage(LoadedSprites.pullTexture(object_id.replaceAll("\\d" ,"") + "_" + cur_direction + "_" + cur_action), drawX, drawY, xScale, yScale, null);break;
            case 3: gr.drawImage(LoadedSprites.pullTexture(object_id), drawX, drawY, xScale, yScale, null); break;
            case 4: gr.drawImage(LoadedSprites.pullTexture(object_id.replaceAll("\\d" ,"") + "_attack"), drawX, drawY, xScale, yScale, null); break;
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

        if (xPos - 400 < playerX + playerScrX && xPos + 400 > playerX - playerScrX
                && yPos - 400 < playerY + playerScrY && yPos + 400 > playerY - playerScrY) {
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
        gr.fillRect(20, Main.y - 180,400, 170);
        gr.setColor(Color.black);
        gr.fillRect(25, Main.y - 175,390, 160);
        gr.setFont(new Font("Calibri Bold", Font.PLAIN, 26));
        gr.setColor(Color.white);
        gr.drawString("HP", 50, Main.y - 90);
        gr.drawString("XP", 50, Main.y - 45);
        gr.drawString("LEVEL == " + level, 50, Main.y - 135);
        gr.setColor(Color.gray);
        gr.fillRect(100, Main.y - 110, 200, 25);
        gr.fillRect(100, Main.y - 65, 200, 25);
        gr.setColor(Color.blue);
        gr.fillRect(100, Main.y - 65, (int) (cur_xp / next_xp * 200), 25);
        gr.setColor(Color.red);
        gr.fillRect(100, Main.y - 110, (int) (cur_hp / max_hp * 200), 25);
        gr.setColor(Color.white);
        gr.drawString(cur_xp + "/" + next_xp, 110, Main.y - 45);
        gr.drawString(cur_hp + "/" + max_hp, 110, Main.y - 90);
//        System.out.println(cur_hp);


    }

    /**
     * Saves all data for this object.
     * @return a double array that contains all field data of this object.
     * @author Graham or Luka idk
     */
    public String[] saveData() {
        switch (object_type){
            case 0 -> {
                RWFile.writeInventory(this.inventory);
                saveInventory();
                return new String[]{Integer.toString(object_type), Double.toString(max_hp), Double.toString(cur_hp),
                        Integer.toString(atk_dmg), Integer.toString(atk_spd), Integer.toString(atk_type), Double.toString(ap),
                        Long.toString(cd), Long.toString(cur_cd), ability_name, Integer.toString(ability_range), object_id,
                        Double.toString(xPos), Double.toString(yPos), Integer.toString(xScale), Integer.toString(yScale), Boolean.toString(interactable),
                        Integer.toString(GameFrame.curMap), Double.toString(cur_xp), Integer.toString(enemyCount)};
            }
            case 1 -> {
                return new String[]{Integer.toString(object_type), Double.toString(max_hp), Double.toString(cur_hp),
                        Integer.toString(atk_dmg), Integer.toString(atk_spd), Integer.toString(atk_type), Double.toString(ap),
                        Long.toString(cd), Long.toString(cur_cd), ability_name, Integer.toString(ability_range), object_id,
                        Double.toString(xPos), Double.toString(yPos), Integer.toString(xScale), Integer.toString(yScale), Boolean.toString(interactable), Integer.toString(GameFrame.curMap)};
            }
            case 2 -> {
                return new String[]{Integer.toString(object_type), object_id, Double.toString(xPos), Double.toString(yPos),
                        Integer.toString(xScale), Integer.toString(yScale), Integer.toString(npcType), Boolean.toString(interactable)};
            }
            case 3 -> {
                return new String[]{Integer.toString(object_type), object_id, Double.toString(xPos), Double.toString(yPos),
                        Integer.toString(xScale), Integer.toString(yScale), Boolean.toString(interactable), Boolean.toString(collected)};
            }
        }
        return null;
    }

    private void saveInventory(){
        for (GameObject obj : inventory.inventorySpace){
            RWFile.writeData(obj);
        }
    }

    /**
     * @return the object ID of this object
     */
    public String getObjectID() {
        return object_id;
    }

    public int getLevel(){
        return level;
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

    public void changeForm(int stage){
        switch(stage){
            case 2 -> {
                max_hp = (int)(max_hp * 1.5);
                cur_hp = max_hp;
                atk_dmg = (int)((double)atk_dmg * 1.5);
                atk_spd = 1;
                break;
            }
            case 3 -> {
                max_hp *= 2;
                cur_hp = max_hp;
                atk_dmg *= 2;
                speed_lock = false;
                break;
            }
        }
    }

    /**
     * @return the status of the current object, if it is dead or not, and adds to player's xp bar if an enemy is defeated
     * @author Christina
     */
    public boolean died() {
        if(is_dead && object_type == 1){
            players.get(0).cur_xp += 50;
        }
        if(is_dead){
            hitbox.setLocation(0, 0); //throw into waste corner
        }
        return is_dead;
    }

    public static void clearArray(){
        players.removeAll(players);
        npcs.removeAll(npcs);
        enemies.removeAll(enemies);
    }



}
