package net.greyferret.ferretb0t;

import net.greyferret.ferretb0t.client.FerretChatClient;
import net.greyferret.ferretb0t.config.Messages;
import net.greyferret.ferretb0t.engine.ChatEngine;
import net.greyferret.ferretb0t.engine.DiscordEngine;
import net.greyferret.ferretb0t.engine.LootsEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Main class, contains bot chat and loots bots
 * <p>
 * Created by GreyFerret on 07.12.2017.
 */
@Component
public class FerretB0t implements Runnable {
    @Autowired
    private ApplicationContext context;

    private static final Logger logger = LogManager.getLogger();

    private boolean isOn;
    private LootsEngine lootsEngine;
    private ChatEngine chatEngine;
    private DiscordEngine discordEngine;
    private Thread lootsThread;
    private Thread chatThread;
    private Thread discordThread;

    public FerretB0t() {
    }

    @PostConstruct
    private void postConstruct() {
        this.lootsEngine = context.getBean(LootsEngine.class);
        this.chatEngine = context.getBean(ChatEngine.class);
        this.discordEngine = context.getBean(DiscordEngine.class);
        this.lootsThread = new Thread(this.lootsEngine);
        this.lootsThread.setName("Loots Bot");
        this.chatThread = new Thread(this.chatEngine);
        this.chatThread.setName("Chat Bot");
        this.discordThread = new Thread(this.discordEngine);
        this.discordThread.setName("Discord Bot");
    }

    @Override
    public void run() {
        this.isOn = true;
        this.discordThread.start();
        this.lootsThread.start();
        this.chatThread.start();

        while (isOn) {

        }
    }
}
