package survive.entity;

/**
 * Created by wayne on 2017/9/7.
 */
public class Player extends GameObject {

    public static final double  INIT_MASS= 30;

    public int id;

    public String name;

    private double mass = INIT_MASS;

    public Target target;

    public Player(String name, int id, double x, double y) {
        this.name = name;
        this.id = id;
        this.x = x;
        this.y = y;
        updateRadius();
    }

    public Player() {
    }

    public class Target {
        public int x;
        public int y;
    }

    public void addMass(double m) {
        mass += m;
        updateRadius();
    }

    public double getMass(){
        return mass;
    }

    public void setMass(double m){
        mass = m;
        updateRadius();
    }

    private void updateRadius(){
        radius = mass / (2 * Math.PI);
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
                ", target=" + target.x + "|" + target.y +
                '}';
    }
}
