package survive;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import survive.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by wayne on 2017/9/7.
 */
public class Game implements Runnable {

    public static final int width = 800;
    public static final int height = 800;
    public static final int visibleWidth = 400;
    public static final int visibleHeight = 400;

    public static final int foodCount = 100;

    public static final int speed = 30;

    public ArrayList<Food> foods;

    public ArrayList<Player> players;

    public GameData gameData;

    private static Game mInstance;

    private long lastUpdateTime;
    private long lastMoveTime;
    private static final int updateInterval = 70;

    private Random random;

    SimpMessagingTemplate messagingTemplate;

    private QuadTree quadTree;

    private Game() {
        random = new Random();
        foods = new ArrayList<>(100);
        players = new ArrayList<>();

        initFood();

        gameData = new GameData(null, foods, players);

        initQuadTree();

        new Thread(this).start();
    }

    private void initQuadTree() {
        quadTree = new QuadTree(0, 0, width, height);
        for (Food f : foods) {
            quadTree.insert(f);
        }
    }

    private void initFood() {

        for (int i = 0; i < foodCount; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            foods.add(new Food(x, y));
        }
    }


    public static Game getInstance() {
        if (mInstance == null) {
            mInstance = new Game();
        }

        return mInstance;
    }

    public boolean checkNameExist(String name) {
        for (Player player : players) {
            if (player.name.equals(name)) {
                return false;
            }
        }

        return true;
    }

    public Player addPlayer(String name) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        Player player = new Player(name, players.size() + 1, x, y);
        players.add(player);
        quadTree.insert(player);
        return player;
    }

    @Override
    public void run() {
        while (true) {
            movePlayer();
            collisionCheck();
            long timeElapse = System.currentTimeMillis() - lastUpdateTime;
            if (timeElapse > updateInterval) {
                sendUpdate();
            } else {
                try {
                    Thread.sleep(updateInterval - timeElapse);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void collisionCheck() {
        List<GameObject> objectsMayCollision = new ArrayList<>();
        for (Player player : players) {
            objectsMayCollision.clear();
            quadTree.retrieve(objectsMayCollision, player);

            for (GameObject o : objectsMayCollision) {
                double distance = Math.sqrt(Math.pow(player.x - o.x, 2) + Math.pow(player.y - o.y, 2));
                if (distance < Math.abs(player.radius - o.radius)) { //内切
                    if (o instanceof Food) {
                        player.addMass(1);
                        quadTree.remove(o);
                        o.x = random.nextInt(width);
                        o.y = random.nextInt(height);
                        quadTree.insert(o);
                    } else if (o instanceof Player) {
                        if (player.getMass() > ((Player) o).getMass()) {
                            player.addMass(((Player) o).getMass() * 0.8);
                            quadTree.remove(o);
                            ((Player) o).setMass(Player.INIT_MASS);
                            o.x = random.nextInt((int) (width - o.radius + 0.5));
                            o.y = random.nextInt((int) (height - o.radius + 0.5));
                            quadTree.insert(o);
                        }
                    }
                }
            }
        }
    }


    private void sendUpdate() {
        for (Player player : players) {
            double left = player.x - visibleWidth / 2;
            double top = player.y - visibleHeight / 2;
            double right = left + visibleWidth;
            double bottom = top + visibleHeight;

            ArrayList<Food> visibleFoods = new ArrayList<>();
            ArrayList<Player> visiblePlayer = new ArrayList<>();

            for (Food food : foods) {
                if (food.x > left && food.y > top && food.x < right && food.y < bottom) {
                    visibleFoods.add(food);
                }
            }

            for (Player p : players) {
                if (player.id != p.id && p.x > left && p.y > top && p.x < right && p.y < bottom) {
                    visiblePlayer.add(p);
                }
            }

            GameData visibleData = new GameData(player, visibleFoods, visiblePlayer);
            ServerMessage<GameData> message = new ServerMessage<>(1, "gameData", "sendUpdate", visibleData);
            messagingTemplate.convertAndSendToUser(String.valueOf(player.id), "/update", message);
        }

        lastUpdateTime = System.currentTimeMillis();

    }

    private void movePlayer() {
        if (lastMoveTime == 0) {
            lastMoveTime = System.currentTimeMillis();
        }
        long timeElapse = System.currentTimeMillis() - lastMoveTime;

        for (Player player : players) {
            if (player == null || player.target == null) {
                return;
            }
            quadTree.remove(player);

            double deg = Math.atan2(player.target.y, player.target.x);
            double deltaX = Math.cos(deg) * speed * timeElapse / 1000;
            player.x += deltaX;
            double deltaY = Math.sin(deg) * speed * timeElapse / 1000;
            player.y += deltaY;

            if (player.x + player.radius > width) {
                player.x = width - player.radius;
            }

            if (player.x - player.radius < 0) {
                player.x = player.radius;
            }

            if (player.y + player.radius > height) {
                player.y = height - player.radius;
            }

            if (player.y - player.radius < 0) {
                player.y = player.radius;
            }

            quadTree.insert(player);
        }
        lastMoveTime = System.currentTimeMillis();
    }

    public void updatePlayerTarget(Player player) {
        for (Player p : players) {
            if (p.id == player.id) {
                p.target = player.target;
                break;
            }
        }
    }
}
