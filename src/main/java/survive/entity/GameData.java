package survive.entity;

import java.util.ArrayList;

/**
 * Created by wayne on 2017/9/7.
 */
public class GameData {

    public Food[] foods;

    public ArrayList<Player> players;

    public GameData(Food[] foods, ArrayList<Player> players) {
        this.foods = foods;
        this.players = players;
    }
}
