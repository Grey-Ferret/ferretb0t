package net.greyferret.ferretb0t.logic;

import net.greyferret.ferretb0t.service.LootsService;
import net.greyferret.ferretb0t.service.ViewerService;
import net.greyferret.ferretb0t.util.FerretB0tUtils;
import net.greyferret.ferretb0t.wrapper.ChannelMessageEventWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ChatLogic {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private ViewerService viewerService;
    @Autowired
    private LootsService lootsService;

    public void alias(ChannelMessageEventWrapper event, String message) {
        String[] split = StringUtils.split(message, ' ');
        if (split.length == 3) {
            String answer = viewerService.updateAlias(split[1], split[2]);
            logger.info("User change ended with following message: " + answer);
            event.sendMessageWithMention(answer);
        } else if (split.length == 2) {
            String answer = viewerService.showAliasMessage(split[1]);
            event.sendMessageWithMention(answer);
        }
    }

    public void repair(ChannelMessageEventWrapper event) {
        Set<String> lootsForRepair = lootsService.findLootsForRepair();
        if (lootsForRepair == null || lootsForRepair.size() == 0) {
            event.sendMessage("Nothing to repair! :)");
            return;
        }
        String repairNames = StringUtils.join(lootsForRepair, ", ");
        event.sendMessage("To fix: " + repairNames);
    }
}
