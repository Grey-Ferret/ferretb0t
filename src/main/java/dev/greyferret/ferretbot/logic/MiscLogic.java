package dev.greyferret.ferretbot.logic;

import dev.greyferret.ferretbot.entity.Misc;
import dev.greyferret.ferretbot.service.MiscService;
import dev.greyferret.ferretbot.wrapper.ChannelMessageEventWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MiscLogic {
	@Autowired
	private MiscService miscService;

	private static final String DARK_SOULS_DEATH_COUNT_ID = "DARK_SOULS_DEATH_COUNT_ID";

	public void proceed(ChannelMessageEventWrapper event, String message) {
		if (message.toLowerCase().startsWith("!смерти")) {
			Misc misc = miscService.getMiscById(DARK_SOULS_DEATH_COUNT_ID);
			if (misc == null) {
				misc = new Misc(DARK_SOULS_DEATH_COUNT_ID);
				misc.setNumber(0);
			}
			event.sendMessageWithMention("Количество смертей: " + misc.getNumber());
		}
	}

	public void proceedAdmin(ChannelMessageEventWrapper event, String message) {
		if (message.toLowerCase().startsWith("!died")) {
			Misc misc = miscService.getMiscById(DARK_SOULS_DEATH_COUNT_ID);
			if (misc == null) {
				misc = new Misc(DARK_SOULS_DEATH_COUNT_ID);
				misc.setNumber(0);
			} else {
				misc.setNumber(misc.getNumber() + 1);
			}
			miscService.updateMisc(misc);
			event.sendMessageMe("YOU DIED");
		}
	}
}
