package survive.entity;

import java.util.ArrayList;

/**
 * Created by wayne on 2017/9/7.
 */
public class GameData {

    public ArrayList<Food> foods;

    public ArrayList<Player> others;

    public Player player;

    public GameData(Player player,ArrayList<Food> foods, ArrayList<Player> others) {
        this.player = player;
        this.foods = foods;
        this.others = others;
    }
}
