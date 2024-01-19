
import java.io.*;
import java.util.ArrayList;

public class Setup extends LoadedSprites {
    public static int curMap;
    public String textureDir;
//    private final File data;
//    private final File loose;
    public static String[][][] textureData;
    public static boolean[][][] collisionData;
    public static int[] colMax;
    public static int[] rowMax;
    private File[] processQueue;

    /**
     * Originally written by Graham, adjusted and implemented by Luka
     * Queues up map components to be read
     * @param mapDir directory of map
     * @param textureFolder directory of texture folder
     * @param mapCount number of maps to read
     */
    public Setup(String mapDir, String textureFolder, int mapCount){
        processQueue = new File[mapCount];
        colMax = new int[mapCount];
        rowMax = new int[mapCount];
        textureData = new String[mapCount][][];
         for (int i = 0; i < mapCount; i++) {
             processQueue[i] = new File(mapDir + i + ".csv");
 //            File mapLoose = new File(mapDir.getAbsolutePath() + "loose.csv");
         }
       // loose = mapLoose;
        textureDir = textureFolder;
    }


    //temp
    private final int scaleX = 100;
    private final int scaleY = 100;

    /**
     * Written by Graham, edited by Luka
     * Loads all required map textures
     * @throws IOException
     */
    public void loadMapTextures() throws IOException {
        for (int a = 0; a < processQueue.length; a++) {
            ArrayList<String[]> textData = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(processQueue[a]))) {
                String line;
                while ((line = br.readLine()) != null) {
                    textData.add(line.split(","));
                }
            }
            textureData[a] = textData.toArray(new String[textData.size()][]);
            for (int i = 0; i < textureData[a].length; i++) {
                for (int j = 0; j < textureData[a][0].length; j++) {
                    if (!tempText.contains(textureData[a][i][j])) {
                        load(textureDir + textureData[a][i][j] + ".gif", textureData[a][i][j], scaleX, scaleY);
                    }
                }
            }
            colMax[a] = textureData[a][0].length;
            rowMax[a] = textureData[a].length;
        }
        collisionTiles();
    }

    /**
     * Written by Luka
     * Reads csv that contains tile types that have collision
     * @throws IOException
     */
    public void collisionTiles() throws IOException{
        BufferedReader read = new BufferedReader(new FileReader("data/map/collisionTiles.csv"));
         mapCollision(read.readLine().split(","));
    }

    /**
     * Written by Luka
     * Creates a duplicate world map that houses collision booleans rather than texture names
     * @param collision Array of tile types with collision
     */
    private void mapCollision(String[] collision){
        collisionData = new boolean[textureData.length][textureData[0].length][textureData[0][0].length];
        for (int a = 0; a < textureData.length; a++) {
            for (int i = 0; i < textureData[0].length; i++) {
                for (int j = 0; j < textureData[0][0].length; j++) {
                    for (int k = 0; k < collision.length; k++) {
                        if (textureData[a][i][j].equals(collision[k])) {
                            collisionData[a][i][j] = true;
                            break;
                        }
                    }
                }
            }
        }
    }


    /**
     * Loads object sprites - these will change as more sprites are completed
     */
    public void load(){
        try {
            load("data/TestMovementPics/movinggif.gif", "TestDummy", 500, 500);
            load("data/assets/title.png", "main_title", Main.x, Main.y);
            load("data/assets/tempPause.png", "tempPause", 650, 400);

            load("data/assets/Hongcha1.png", "booperdooper_UP_IDLE", 58, 86);
            load("data/assets/Hongcha.png", "booperdooper_UP_MOV", 58, 86);
            load("data/assets/Hongcha.png", "booperdooper_UP_INTERACT", 58, 86);
            load("data/assets/booperdooper_idleL.png", "booperdooper_LEFT_IDLE", 58, 86);
            load("data/assets/booperdooper_runL.gif", "booperdooper_LEFT_MOV", 58, 86);
            load("data/assets/booperdooper_idleD.png", "booperdooper_DOWN_IDLE", 58, 86);
            load("data/assets/booperdooper_runD.gif", "booperdooper_DOWN_MOV", 58, 86);
            load("data/assets/Booperdooper1.png", "booperdooper_DOWN_INTERACT", 58, 86);
            load("data/assets/booperdooper_idleR.png", "booperdooper_RIGHT_IDLE", 58, 86);
            load("data/assets/booperdooper_runR.gif", "booperdooper_RIGHT_MOV", 58, 86);
            load("data/assets/booperdooper_attack.png", "booperdooper_attack", 100, 100);

            load("data/assets/booperdooper_runR.gif", "npc1_RIGHT_MOV", 200, 200);
            load("data/assets/booperdooper_runL.gif", "npc1_LEFT_MOV", 200, 200);
            load("data/assets/Dogepro.png", "npcHouse_UP_IDLE", 200,200);
            load("data/assets/Dogepro1.png", "npcHouse_DOWN_IDLE", 200,200);
            load("data/assets/Hongcha1.png", "npc2_UP_IDLE", 58, 86);
            load("data/assets/Hongcha.png", "npc2_UP_MOV", 58, 86);
            load("data/assets/booperdooper_idleL.png", "npc2_LEFT_IDLE", 58, 86);
            load("data/assets/booperdooper_runL.gif", "npc2_LEFT_MOV", 58, 86);
            load("data/assets/booperdooper_idleD.png", "npc2_DOWN_IDLE", 58, 86);
            load("data/assets/booperdooper_runD.gif", "npc2_DOWN_MOV", 58, 86);
            load("data/assets/booperdooper_idleR.png", "npc2_RIGHT_IDLE", 58, 86);
            load("data/assets/booperdooper_runR.gif", "npc2_RIGHT_MOV", 58, 86);

            load("data/assets/house1.png", "house1", 200, 200);

            loadMapTextures();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finishLoading();
    }

}
