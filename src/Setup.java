
import java.io.*;
import java.util.ArrayList;

public class Setup extends LoadedSprites {
    public Setup(File mapDir, String textureFolder){
        File mapData = new File(mapDir.getAbsolutePath() + "DATA.csv");
        File mapLoose = new File(mapDir.getAbsolutePath() + "loose.csv");
        data = mapData;
        loose = mapLoose;
        textureDir = textureFolder;
    }

    public String textureDir;
    private final File data;
    private final File loose;
    public static String[][] textureData;
    public static boolean[][] collisionData;


    //temp
    private final int scaleX = 100;
    private final int scaleY = 100;

    public void loadMapTextures() throws IOException {
        ArrayList<String[]> textData = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(data))) {
            String line;
            while ((line = br.readLine()) != null) {
                textData.add(line.split(","));
            }
        }
        textureData = textData.toArray(new String[textData.size()][]);
        for (int i = 0; i < textureData.length; i++) {
            for (int j = 0; j < textureData[0].length; j++) {
                if(!tempText.contains(textureData[i][j])) {
                    load(textureDir + textureData[i][j] + ".png", textureData[i][j], scaleX, scaleY);
                }
            }
        }
        colMax = textureData[0].length;
        rowMax = textureData.length;
        collisionTiles();
    }

    public void collisionTiles() throws IOException{
        BufferedReader read = new BufferedReader(new FileReader("data/map/collisionTiles.csv"));
         mapCollision(read.readLine().split(","));
    }

    private void mapCollision(String[] collision){
        collisionData = new boolean[textureData.length][textureData[0].length];
        for (int i = 0; i < textureData.length; i++){
            for (int j = 0; j < textureData[0].length; j++){
                for (String s : collision) {
                    if (textureData[i][j].equals(s)) {
                        collisionData[i][j] = true;
                        break;
                    }
                }
            }
        }
    }

    public static int colMax;
    public static int rowMax;
    public void load(){
        try {
            load("data/TestMovementPics/movinggif.gif", "TestDummy", 500, 500);
            load("data/assets/Booperdooper.png", "booperdooper_UP_IDLE", 200, 200);
            load("data/assets/Booperdooper1.png", "booperdooper_UP_MOV", 200, 200);
            load("data/assets/Dogepro.png", "booperdooper_LEFT_IDLE", 200, 200);
            load("data/assets/Dogepro1.png", "booperdooper_LEFT_MOV", 200, 200);
            load("data/assets/Hongcha.png", "booperdooper_DOWN_IDLE", 200, 200);
            load("data/assets/Hongcha1.png", "booperdooper_DOWN_MOV", 200, 200);
            load("data/TestMovementPics/m1.png", "booperdooper_RIGHT_IDLE", 200, 200);
            load("data/TestMovementPics/movinggif.gif", "booperdooper_RIGHT_MOV", 200, 200);
            load("data/assets/Booperdooper.png", "enemy_UP_IDLE", 200, 200);
            loadMapTextures();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finishLoading();
    }

}
