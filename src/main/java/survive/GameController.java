package survive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import survive.entity.ClientMessage;
import survive.entity.Player;
import survive.entity.ServerMessage;

@Controller
public class GameController {

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/login")
    @SendTo("/topic/login")
    public ServerMessage<Player> login(ClientMessage message) throws Exception {
        Game game = Game.getInstance();
        game.messagingTemplate = messagingTemplate;
        if (!game.checkNameExist(message.getMessage())) {
            return new ServerMessage<>(0, "login", "name exist", null);
        } else {
            Player player = game.addPlayer(message.getMessage());
            return new ServerMessage<>(1, "login", "welcome", player);
        }
    }

    @MessageMapping("/setTarget")
    public void setTarget(Player player){
        Game.getInstance().updatePlayerTarget(player);
    }



}
