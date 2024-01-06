
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
    private File data;
    private File loose;
    public static String[][] textureData;
    public static boolean[][] collisionData;


    //temp
    private int scaleX = 100;
    private int scaleY = 100;

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
        mapCollision();
    }

    private void mapCollision(){
        collisionData = new boolean[textureData.length][textureData[0].length];
        for (int i = 0; i < textureData.length; i++){
            for (int j = 0; j < textureData[0].length; j++){
                if (Integer.parseInt(textureData[i][j]) == 1){
                    collisionData[i][j] = true;
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
