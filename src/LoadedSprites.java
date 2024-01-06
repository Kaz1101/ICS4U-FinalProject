
import java.awt.Image;
import java.io.IOException;
import javax.swing.*;
import java.util.ArrayList;

public class LoadedSprites {
    //temporary public for now
    protected ArrayList<Image> tempLoad = new ArrayList<>();
    protected ArrayList<String> tempText = new ArrayList<>();
    protected static Image[] textures;
    protected static String[] NameID;

    /**
     * Takes assets and adds it to a temporary arraylist
     * @param textureDir Directory of asset
     * @param textureName Name of asset
     * @param scaleX Scale of asset in the x-axis
     * @param scaleY Scale of asset in the y-axis
     * @throws IOException if asset file is not found
     */
    public void load(String textureDir, String textureName, int scaleX, int scaleY) throws IOException {
        Image temp = new ImageIcon(textureDir).getImage().getScaledInstance(scaleX, scaleY, Image.SCALE_DEFAULT);
//		Image tempL = ImageIO.read(new File(textureDir.getAbsoluteFile() + textureName + ".png"));
//		Image tempS = tempL.getScaledInstance(scaleX, scaleY, Image.SCALE_SMOOTH);
        tempLoad.add(temp);
        tempText.add(textureName);
        temp.flush();
//		tempL.flush();
//		tempS.flush();
    }

    /**
     * Converts temporary array list into two definitive arrays to be called by other classes; one for the assets, and the other for asset name
     */
    public void finishLoading() {
        textures = tempLoad.toArray(new Image[tempLoad.size()]);
        NameID = tempText.toArray(new String[tempText.size()]);
        tempLoad.clear();
        tempText.clear();
    }


    /**
     * Clears asset arrays
     */
    public static void clearLoaded() {
        textures = new Image[0];
        NameID = new String[0];
    }

    /**
     *
     * @param texture name of texture to be pulled
     * @return the pulled texture
     */
    public static Image pullTexture(String texture) {
        int id = -1;
        for (int i = 0; i < NameID.length; i++) {
            if (NameID[i].equals(texture)) {
                return textures[i];
            }
        }
        return textures[0];
    }
}
