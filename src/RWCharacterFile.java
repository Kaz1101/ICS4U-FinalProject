import java.io.*;
import java.util.*;

public class RWCharacterFile {

    /**
     * Reading csv of initial character data file and returns as array
     * @param characterID a specific character ID that identifies which character's file to search for
     * @return a String array of data that contains data of a GameCharacter object
     * @throws IOException if file is not found
     */
    public static String[] readInitialFile(String characterID) throws IOException{
        BufferedReader read = new BufferedReader(new FileReader("data/characterData/" + characterID + ".csv"));
        return read.readLine().split(",");
    }

	/*
	What should be called in main class

	GameCharacter booperdooper = new BooperDooper(readInitialFile("booperdooper"));

	String[] enemyType = {"range_enemy", "melee_enemy", "tank_enemy", "support_enemy"};
	Random r = new Random(23479832);
	String type = enemyType(r.nextINt());
	GameCharacter mob1 = new Enemy(readInitialFile(type));
	GameCharacter alsorangemobbutnumber2 = new Enemy(readInitialFile("rangeenemy"));

	 */

    /**
     * Write the current state of the GameCharacter object into a csv file
     * @param character a GameCharacter object that we want to save the character file for
     */
    public void writeData(GameObject character) {
        String name = character.getCharacterID();
        File f = new File("data/saveData/" + name + ".csv");
        try(BufferedWriter buffer = new BufferedWriter(new FileWriter(f))) {
            buffer.write(Arrays.toString(character.saveData()));
        } catch (IOException cant_code) {
            System.out.println("welp ʅ( ･´‸･｀)ʃ");
        }
    }



    public ArrayList<String> readData(GameObject character) {
        String name = character.getCharacterID();
        File f = new File("data/savedData/" + name + ".csv");
        ArrayList<String> saveData = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            saveData.add(Arrays.toString(reader.readLine().split(",")));
        } catch (IOException you_suck_at_coding_not_me){
//            new OopsiePoopsie();
            System.out.println("...man");
        }
        return saveData;
    }
}
