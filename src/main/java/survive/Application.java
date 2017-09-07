package survive;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {


    public static void main(String[] args) {
        Game.getInstance(); //init Game
        SpringApplication.run(Application.class, args);
    }
}
