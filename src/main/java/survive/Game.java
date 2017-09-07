package survive;

import survive.entity.Food;
import survive.entity.GameData;
import survive.entity.Player;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by wayne on 2017/9/7.
 */
public class Game {

    public static final int width = 600;
    public static final int height = 600;

    public static final int foodCount = 100;

    public Food[] foods;

    public ArrayList<Player> players;

    public GameData gameData;

    private static Game mInstance;

    private Game() {
        foods = new Food[foodCount];

        initFood();

        players = new ArrayList<>();

        gameData = new GameData(foods, players);
    }

    private void initFood() {
        Random random = new Random();
        for (int i = 0; i < foodCount; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            foods[i] = new Food(x, y);
        }
    }


    public static Game getInstance() {
        if (mInstance == null) {
            mInstance = new Game();
        }

        return mInstance;
    }

    public boolean checkNameExist(String name){
        for (Player player : players) {
            if (player.name.equals(name)) {
                return false;
            }
        }

        return true;
    }

    public void addPlayer(String name){
        players.add(new Player(name));
    }
}
