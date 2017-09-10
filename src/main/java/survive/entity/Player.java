package survive.entity;

/**
 * Created by wayne on 2017/9/7.
 */
public class Player extends GameObject {

    public int id;

    public String name;

    public int mass = 5;

    public Target target;

    public Player(String name, int id, double x, double y) {
        this.name = name;
        this.id = id;
        this.x = x;
        this.y = y;
        this.radius =mass/2*Math.PI;
    }

    public Player() {
    }

    public class Target{
        public int x;
        public int y;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", mass=" + mass +
                ", radius=" + radius +
                ", target=" + target.x+"|"+target.y +
                '}';
    }
}
