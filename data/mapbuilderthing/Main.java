import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import javax.imageio.ImageIO;
import java.util.Scanner;
import java.util.ArrayList;
public class Main {
	/**Writen by Graham
	*takes @param directory then loads the png, gets the per pixel colour data and adds it : sperated to a 2d String[][] and @returns rgbArr
	*/
	public static String[][] imgToRGBArray(String directory) throws IOException {
		BufferedImage temp = ImageIO.read(new File(directory));
		String[][] rgbArr = new String[temp.getHeight()][temp.getWidth()];
		for (int i = 0; i < temp.getHeight(); i++) {
			for (int j = 0; j < temp.getWidth(); j++) {
				int p = temp.getRGB(j, i);
				Color color = new Color(p, false);
				rgbArr[i][j] = color.getRed() + ":" + color.getGreen() + ":" + color.getBlue();
			}
		}
		return rgbArr;
	}
	/**Writen by Graham
	*takes @param directory then loads the csv in to @return out String[][] with data
	*/
	public static String[][] importColorCompair(String directory) {
		File temp = new File(directory);
		ArrayList<String[]> list = new ArrayList<String[]>();
		try(Scanner in = new Scanner(temp)) {
			while(in.hasNext()) {
				String tempIn = in.nextLine();
				list.add(tempIn.split(","));
			}
		} catch(IOException e) {
			System.out.println(e);
		}
		String[][] out = list.toArray(new String[0][0]);
		return out;
	}
	/**Writen by Graham
	*takes @param rgbArr and compairs it to @param colorChart and replaces matches with specified names and @returns amp with updated data
	*/
	public static String[][] rgbArrayToMap(String[][] colorChart, String[][] rgbArr) {
		String[][] output = rgbArr;
		for (int i = 0; i < rgbArr.length; i++) {
			for (int j = 0; j < rgbArr[i].length; j++) {
				for (int k = 0; k < colorChart.length; k++) {
					System.out.println(rgbArr[i][j]);
					if(rgbArr[i][j].equals(colorChart[k][0])) {
						output[i][j] = colorChart[k][1];
						System.out.println(rgbArr[i][j] + " match found");
					} else {
						System.out.println(rgbArr[i][j] + " no match found");
					}
				}
			}
		}
		return output;
	}
	/**Writen by Graham
	*outputs @param map as a .CSV fiile
	*/
	public static void outputToCSV(String[][] map) throws IOException {
		FileWriter output = new FileWriter(new File("data/map/output.csv"));
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length - 1; j++) {
				output.append(map[i][j] + ",");
			}
			output.append(map[i][map[i].length - 1]);
			output.append(System.lineSeparator());
			output.flush();
		}
		System.out.println();
		output.close();
	}
	/**Writen by Graham
	*Main control method that bring everything together
	*/
	public static void main(String[] args) throws IOException {
		Scanner input = new Scanner(System.in);
		System.out.println("paste in image directory");
		String[][] rgbArr = imgToRGBArray(input.nextLine());
		System.out.println("paste in CSV with color stuff directory");
		String[][] colorInfo = importColorCompair(input.nextLine());
		System.out.println("starting convertion");
		outputToCSV(rgbArrayToMap(colorInfo, rgbArr));
		System.out.println("convertion done");
		input.close();
	}
}


