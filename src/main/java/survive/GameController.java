package survive;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import survive.entity.ClientMessage;
import survive.entity.GameData;
import survive.entity.ServerMessage;

@Controller
public class GameController {


    @MessageMapping("/login")
    @SendTo("/topic/login")
    public ServerMessage<GameData> greeting(ClientMessage message) throws Exception {
        Game game = Game.getInstance();
        if (!game.checkNameExist(message.getName())) {
            return new ServerMessage<>(0, "login", "name exist", null);
        } else {
            game.addPlayer(message.getName());
            return new ServerMessage<>(1, "login", "welcome", game.gameData);
        }


    }


}
