package survive.entity;

/**
 * Created by wayne on 2017/9/7.
 */
public class Player {

    public int id;

    public String name;

    public double x;
    public double y;

    public int mass = 1;
    public int radius =(int) (2*Math.PI*mass);

    public Target target;

    public Player(String name, int id, double x, double y) {
        this.name = name;
        this.id = id;
        this.x = x;
        this.y = y;
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
