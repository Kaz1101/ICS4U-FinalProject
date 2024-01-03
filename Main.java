import javax.swing.*;

public class Main extends GameFrame{
    public static enum GameState {MENU, INGAME};
    public static GameState game_state;




    public static void main(String[] args) {
        Main m = new Main();
        m.setVisible(true);
    }

    @Override
    public void setup() {
        main_menu();
    }

    public void main_menu(){
        game_state = GameState.MENU;
        ImageIcon title = new ImageIcon("data/game_visual/menu/title.png");
        Object[] menu_options = {"New Game", "Continue", "Instructions", "Quit"};
        int chosen_option = JOptionPane.showOptionDialog(null, null, "MENU", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, title, menu_options, menu_options[1]);
    }

    @Override
    public void do_tick() {
        if(game_state == GameState.INGAME){
            //check for all game conditions here, if beat level, if paused, etc...
            //call for method to move all players by checking with if statement for key pressed and then calling the move method of the gamecharacter
        }
    }
}