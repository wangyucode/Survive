package survive.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wayne on 2017/9/10.
 */
public class QuadTree {

    private static final int MAX_OBJECTS = 10;
    private static final int MAX_LEVELS = 5;

    private Rect rect;

    private ArrayList<GameObject> objects;

    private QuadTree[] nodes;

    private int level;

    public QuadTree(double x, double y, double w, double h) {
        this(0, x, y, w, h);
    }

    private QuadTree(int level, double x, double y, double w, double h) {
        this.level = level;
        rect = new Rect(x, y, w, h);
        objects = new ArrayList<>(10);
        nodes = new QuadTree[4];
    }

    /**
     * Clears the quadtree
     */
    public void clear() {
        objects.clear();

        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    /**
     * Splits the node into 4 subnodes
     */
    private void split() {
        double subWidth = rect.w / 2;
        double subHeight = rect.h / 2;
        double x = rect.x;
        double y = rect.y;

        nodes[0] = new QuadTree(level + 1, x + subWidth, y, subWidth, subHeight);
        nodes[1] = new QuadTree(level + 1, x, y, subWidth, subHeight);
        nodes[2] = new QuadTree(level + 1, x, y + subHeight, subWidth, subHeight);
        nodes[3] = new QuadTree(level + 1, x + subWidth, y + subHeight, subWidth, subHeight);
    }

    /**
     * Determine which node the object belongs to. -1 means
     * object cannot completely fit within a child node and is part
     * of the parent node
     */
    private int getIndex(GameObject object) {
        int index = -1;
        double verticalMidpoint = rect.x + (rect.w / 2);
        double horizontalMidpoint = rect.y + (rect.h / 2);

        // Object can completely fit within the top quadrants
        boolean topQuadrant = (object.y + object.radius < horizontalMidpoint);
        // Object can completely fit within the bottom quadrants
        boolean bottomQuadrant = (object.y - object.radius > horizontalMidpoint);

        // Object can completely fit within the left quadrants
        if (object.x + object.radius < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 2;
            }
        }
        // Object can completely fit within the right quadrants
        else if (object.x - object.radius > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }

        return index;
    }

    /**
     * Insert the object into the quadtree. If the node
     * exceeds the capacity, it will split and add all
     * objects to their corresponding nodes.
     */
    public void insert(GameObject object) {
        if (nodes[0] != null) {
            int index = getIndex(object);

            if (index != -1) {
                nodes[index].insert(object);

                return;
            }
        }

        objects.add(object);

        if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
            split();
            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(objects.get(i));
                if (index != -1) {
                    nodes[index].insert(objects.remove(i));
                } else {
                    i++;
                }
            }
        }
    }

    public void remove(GameObject object){
        if (nodes[0] != null) {
            int index = getIndex(object);

            if (index != -1) {
                nodes[index].remove(object);

                return;
            }
        }

        objects.remove(object);
    }

    /**
     * Return all objects that could collide with the given object
     */
    public List<GameObject> retrieve(List<GameObject> returnObjects, GameObject object) {
        int index = getIndex(object);
        if (index != -1 && nodes[0] != null) {
            nodes[index].retrieve(returnObjects, object);
        }

        returnObjects.addAll(objects);

        return returnObjects;
    }


    public static class Rect {
        public double x;
        public double y;
        public double w;
        public double h;

        public Rect(double x, double y, double w, double h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }

}
