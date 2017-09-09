package survive;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        Game.getInstance(); //init Game
        SpringApplication.run(Application.class, args);
    }
}
