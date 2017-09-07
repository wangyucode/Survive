package survive.entity;

/**
 * Created by wayne on 2017/9/7.
 */
public class Player {

    public String name;

    public int x;
    public int y;
    public int radius = 5;

    public int direction = 0; //1 top, 2 right, 3 down, 4 left

    public Player(String name) {
        this.name = name;
    }
}
