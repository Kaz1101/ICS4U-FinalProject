import java.awt.*;
import java.util.ArrayList;

public class Inventory {
    private int boundsX = Main.x / 6;
    private int boundsY = Main.y / 6;
    private int distX = Main.x - Main.x / 3;
    private int distY = Main.y - Main.y / 3;
    private int slotStartX = boundsX + 50;
    private int slotStartY = boundsY + 25;
    public static int slotCol = 0;
    public static int slotRow = 0;
    private int selectLength = 100;

    protected ArrayList<GameObject> inventorySpace;
    private int curItemIDX;

    public Inventory(){
        inventorySpace = new ArrayList<>(40);
    }
    public void draw(Graphics2D gr){
        int selectX = slotStartX + slotCol * 100;
        int selectY = slotStartY + slotRow * 100;
        int slotX = slotStartX;
        int slotY = slotStartY;

        gr.setColor(Color.white);
        gr.fillRect(boundsX, boundsY, distX, distY);
        gr.setColor(Color.black);
        gr.fillRect(boundsX + 5, boundsY + 5, distX - 10, distY - 10);

        gr.setColor(Color.white);
        gr.fillRect(selectX, selectY, selectLength, selectLength);

        for (int i = 0; i < inventorySpace.size(); i++){
            gr.drawImage(LoadedSprites.pullTexture(inventorySpace.get(i).getObjectID().replaceAll("\\d" ,"")), slotX, slotY, null);
            slotX += 100;

            if (slotX == 9 || slotX == 18 || slotX == 27){
                slotX = slotStartX;
                slotY += 100;
            }
        }

        curItemIDX = slotCol + slotRow * 4;
        gr.setFont(new Font("Calibri Bold", Font.PLAIN, 32));
        if (curItemIDX < inventorySpace.size()) {
            gr.drawString(description(inventorySpace.get(curItemIDX).getObjectID()), boundsX + 100, distY);
        }
    }

    private String description(String item){
        item = item.replaceAll("\\d", "");
        switch (item){
            case "potion" -> {
                return "A small bottle of red liquid that heals 20 HP. Tastes a little sweet.";
            }
            case "supahpotion" -> {
                return "A suspiciously orange looking bottle. Heals 100 HP.";
            }
            case "thatthing" -> {
                return "An extremely rare potion that feels strangely familiar. What does it do?";
            }
            case "spdBoost" -> {
                return "Tastes very bitter. But you do move fast for a while :D";
            }
            case "key" -> {
                return "A key emitting a magical power...what could it possibly open?";
            }
        }
        return "yea i dont think you are supposed to see this";
    }

    protected GameObject getCurObj(){
        curItemIDX = slotCol + slotRow * 4;
        return inventorySpace.get(curItemIDX);
    }

    protected int getCurItemIDX(){
        curItemIDX = slotCol + slotRow * 4;
        return curItemIDX;
    }

    protected void clear(){
        inventorySpace.removeAll(inventorySpace);
    }


}
