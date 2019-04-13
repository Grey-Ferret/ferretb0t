package dev.greyferret.ferretbot.logic;

import dev.greyferret.ferretbot.wrapper.ChannelMessageEventWrapper;
import io.magicthegathering.javasdk.api.CardAPI;
import io.magicthegathering.javasdk.resource.Card;
import io.magicthegathering.javasdk.resource.ForeignData;
import dev.greyferret.ferretbot.util.FerretBotUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MTGACardFinder {
	public static void findCard(String keyword, ChannelMessageEventWrapper event) {
		if (keyword.length() < 4) {
			event.sendMessageWithMention("Введите текст для поиска длиннее 3 символов.");
			return;
		}
		ArrayList<String> f = new ArrayList<>();
		Pattern p = Pattern.compile("/*[а-яА-Я]/*");
		Matcher m = p.matcher(keyword);
		boolean isRussianCard = false;
		if (m.find()) {
			f.add("language=Russian");
			f.add("name=" + keyword);
			isRussianCard = true;
		} else {
			f.add("name=" + keyword);
		}
		List<Card> allCards = CardAPI.getAllCards(f);
		if (allCards != null && allCards.size() > 0) {
			if (allCards.size() > 1) {
				event.sendMessageWithMention("Было найдено несколько результатов (" + allCards.size() + "). Выбираем первый...");
			}
			Card card = allCards.get(0);
			String res = "";
			if (isRussianCard) {
				for (ForeignData foreignData : card.getForeignNames()) {
					if (foreignData.getLanguage().equalsIgnoreCase("Russian")) {
						res = FerretBotUtils.formCardText(card, foreignData.getName(), foreignData.getText());
						break;
					}
				}
			} else {
				res = FerretBotUtils.formCardText(card, card.getName(), card.getText());
			}
			res = StringUtils.replaceAll(res, "\n", "; ");
			event.sendMessageWithMention(res);
		} else {
			event.sendMessageWithMention("Ничего не найдено.");
		}
	}
}
