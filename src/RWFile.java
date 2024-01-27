import java.io.*;
import java.util.*;

public class RWFile {

    /**Writen by Graham edited by Luka
     * Reading csv of initial character data file and returns as array
     * @param characterID a specific character ID that identifies which character's file to search for
     * @return a String array of data that contains data of a GameCharacter object
     * @throws IOException if file is not found
     */
    public static String[] readInitialFile(String characterID) throws IOException{
        BufferedReader read = new BufferedReader(new FileReader("data/objectData/" + characterID + ".csv"));
        return read.readLine().split(",");
    }

    public static String[] readGeneral(String fileDir) throws IOException{
        BufferedReader read = new BufferedReader(new FileReader(fileDir + ".csv"));
        return read.readLine().split(",");
    }



    /**Writen by Graham edited by Luka
     * Write the current state of the GameCharacter object into a csv file
     * @param character a GameCharacter object that we want to save the character file for
     */
    public static void writeData(GameObject character) {
        String name = character.getObjectID();
        File f = new File("data/saveData/" + name + ".csv");
        try(BufferedWriter buffer = new BufferedWriter(new FileWriter(f))) {
            buffer.write(Arrays.toString(character.saveData()).replace("[", "").replace(" ", "").replace("]", ""));
        } catch (IOException cant_code) {
            System.out.println("welp ʅ( ･´‸･｀)ʃ");
        }
    }

    /**
     * Writes object data of every object within a given inventory to a csv file
     * @param inv the inventory object to be saved
     */
    public static void writeInventory(Inventory inv){
        File f = new File("data/saveData/inventory.csv");
        String[] temp = new String[inv.inventorySpace.size()];
        for (int i = 0; i < inv.inventorySpace.size(); i++){
            temp[i] = inv.inventorySpace.get(i).getObjectID();
        }
        try(BufferedWriter buffer = new BufferedWriter(new FileWriter(f))) {
            buffer.write(Arrays.toString(temp).replace("[", "").replace(" ", "").replace("]", ""));
        } catch (IOException cant_code) {
            System.out.println("welp ʅ( ･´‸･｀)ʃ");
        }
    }

    /**
     * Reads data from a saved csv data file
     * @param name the name of the file to be read
     * @return a String array containing all information of the object that the file represents
     */
    public static String[] readData(String name) {
        File f = new File("data/saveData/" + name + ".csv");
//        ArrayList<String> saveData = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String temp = reader.readLine();
            if (temp != null) {
                return temp.split(",");
            }
        } catch (IOException you_suck_at_coding_not_me){
//            new OopsiePoopsie();
            System.out.println("...man");
        }
        return null;
    }

    /**
     * Updates objects in-game to the objectList csv to be saved
     */
    public static void updateList(){
        File f = new File("data/saveData/objectList.csv");
        ArrayList<String> temp = new ArrayList<>();
        try(BufferedWriter buffer = new BufferedWriter(new FileWriter(f))) {
            for (GameObject obj : GameFrame.game_objects){
                temp.add(obj.getObjectID());
                temp.add("0");
            }
            for (GameObject obj : GameFrame.sub_game_objects){
                temp.add(obj.getObjectID());
                temp.add("1");
            }
            buffer.write(Arrays.toString(temp.toArray()).replace("[", "").replace(" ", "").replace("]", ""));
        } catch (IOException cant_code) {
            System.out.println("welp ʅ( ･´‸･｀)ʃ");
        }
    }
}
