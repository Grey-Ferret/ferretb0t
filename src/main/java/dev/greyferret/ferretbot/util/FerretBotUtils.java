package dev.greyferret.ferretbot.util;

import dev.greyferret.ferretbot.entity.*;
import dev.greyferret.ferretbot.exception.NotEnoughEmotesDiscordException;
import io.magicthegathering.javasdk.resource.Card;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Created by GreyFerret on 18.12.2017.
 */
@Log4j2
public class FerretBotUtils {
	/***
	 * Format message, delete whitespaces
	 *
	 * @param message
	 * @return
	 */
	public static String buildMessage(String message) {
		String temp = message.replaceAll("\\s+", " ");
		return temp;
	}

	/***
	 * Building !bonus message
	 *
	 * @param nick
	 * @param points
	 * @return
	 */
	public static String buildMessageAddPoints(String nick, Long points) {
		if (points == null || StringUtils.isBlank(nick))
			return "";
		return "!bonus " + nick + " " + points;
	}

	/***
	 * Building !bonus message with remove instead
	 *
	 * @param nick
	 * @param points
	 * @return
	 */
	public static String buildRemovePointsMessage(String nick, Long points) {
		if (points == null || StringUtils.isBlank(nick))
			return "";
		return "!bonus " + nick + " -" + points;
	}

	/***
	 * Parsing author name (with special logic for guest)
	 *
	 * @param authorUnparsed
	 * @return parsed name
	 */
	public static String parseLootsAuthor(String authorUnparsed) {
		try {
			return parseLootsAuthor(authorUnparsed, authorUnparsed.toLowerCase().startsWith("guest_".toLowerCase()));
		} catch (Exception e) {
			log.error("Error while parsing author of loots with name: " + authorUnparsed);
			return authorUnparsed;
		}
	}

	/***
	 * Parsing author name (with special logic for guest)
	 *
	 * @param authorUnparsed
	 * @param isGuest
	 * @return parsed name
	 */
	public static String parseLootsAuthor(@Nonnull String authorUnparsed, boolean isGuest) {
		String res = authorUnparsed;
		if (StringUtils.isNotBlank(authorUnparsed)) {
			try {
				if (isGuest) {
					res = authorUnparsed.substring(6, authorUnparsed.length() - 10);
				}
			} catch (Exception e) {
				log.error("Could not parse following name: " + authorUnparsed, e);
			}
			return StringUtils.deleteWhitespace(res);
		}
		log.error("Author of loots was blank");
		return "";
	}

	public static String buildDiscordMessageLog(Message message) {
		try {
			return message.getMember().getUser().getName() + "(" + message.getMember().getNickname() + ") in #" + message.getChannel().getName() + ": " + message.getContentRaw();
		} catch (Exception e) {
			log.error("Could not build Log based on the following message: " + message, e);
			return "";
		}
	}

	public static String buildMergedViewersNicknames(Set<Viewer> viewers) {
		String selectedViewersString = "";
		for (Viewer viewer : viewers) {
			if (!StringUtils.isBlank(selectedViewersString)) {
				selectedViewersString = selectedViewersString + ", ";
			}
			selectedViewersString = selectedViewersString + viewer.getLoginVisual();
		}
		return selectedViewersString;
	}

	public static String buildMergedViewersNicknamesWithMention(Set<Viewer> viewers) {
		String selectedViewersString = "@";
		for (Viewer viewer : viewers) {
			if (selectedViewersString.length() > 1) {
				selectedViewersString = selectedViewersString + ", @";
			}
			selectedViewersString = selectedViewersString + viewer.getLoginVisual();
		}
		return selectedViewersString;
	}

	public static ArrayList<Viewer> combineViewerListWithSubluck(Collection<Viewer> viewerList, int subLuckModifier) {
		ArrayList<Viewer> resList = new ArrayList<>();
		for (Viewer viewer : viewerList) {
			resList.add(viewer);
			if (viewer.isSub()) {
				for (int j = 1; j < subLuckModifier; j++) {
					resList.add(viewer);
				}
			}
		}
		return resList;
	}

	public static String escapeNicknameForDiscord(String login) {
		login = StringUtils.replace(login, "_", "\\_");
		login = StringUtils.replace(login, "*", "\\*");
		return login;
	}

	public static String formCardText(Card card, String name, String text) {
		String res;
		res = name;
		if (card.getToughness() != null && card.getPower() != null) {
			res = res + " " + card.getPower() + "/" + card.getToughness();
		}
		res = res + " " + card.getManaCost();
		if (text != null && text.length() > 0) {
			res = res + " " + text;
		}
		return res;
	}

	public static SubVoteEntity formSubVoteEntity(List<SubVoteGame> games, List<Emote> emotes, boolean withEmotes) throws NotEnoughEmotesDiscordException {
		String res = "";
		List<Emote> selectedEmotes = new ArrayList<>();
		HashSet<Integer> selectedEmotesId = new HashSet<>();
		if (games.size() < emotes.size()) {
			for (SubVoteGame subVoteGame : games) {
				String game = subVoteGame.getGame();
				String name = subVoteGame.getName();
				Random rand = new Random();
				boolean added = false;
				int idE = -1;
				while (!added) {
					idE = rand.nextInt(emotes.size());
					Emote emote = emotes.get(idE);
					if (emote.getRoles() == null || emote.getRoles().size() == 0) {
						added = selectedEmotesId.add(idE);
					}
				}
				if (idE >= 0) {
					Emote emote = emotes.get(idE);
					selectedEmotes.add(emote);
					if (StringUtils.isNoneBlank(res)) {
						res = res + "\n";
					}
					if (withEmotes) {
						res = res + emote.getAsMention() + " - ";
					}
					res = res + game + " (" + name + ")";
				}
			}
		} else {
			throw new NotEnoughEmotesDiscordException("Games amount:" + games.size() + "; Emotes amount: " + emotes.size());
		}
		return new SubVoteEntity(res, (ArrayList<Emote>) selectedEmotes);
	}

	public static GameVoteEntity formGameVoteEntity(List<GameVoteGame> games, List<Emote> emotes, boolean withEmotes) throws NotEnoughEmotesDiscordException {
		String res = "";
		List<Emote> selectedEmotes = new ArrayList<>();
		HashSet<Integer> selectedEmotesId = new HashSet<>();
		if (games.size() < emotes.size()) {
			for (GameVoteGame subVoteGame : games) {
				String game = subVoteGame.getGame();
				String name = subVoteGame.getName();
				Random rand = new Random();
				boolean added = false;
				int idE = -1;
				while (!added) {
					idE = rand.nextInt(emotes.size());
					Emote emote = emotes.get(idE);
					if (emote.getRoles() == null || emote.getRoles().size() == 0) {
						added = selectedEmotesId.add(idE);
					}
				}
				if (idE >= 0) {
					Emote emote = emotes.get(idE);
					selectedEmotes.add(emote);
					if (StringUtils.isNoneBlank(res)) {
						res = res + "\n";
					}
					if (withEmotes) {
						res = res + emote.getAsMention() + " - ";
					}
					res = res + game + " (" + name + ")";
				}
			}
		} else {
			throw new NotEnoughEmotesDiscordException("Games amount:" + games.size() + "; Emotes amount: " + emotes.size());
		}
		return new GameVoteEntity(res, (ArrayList<Emote>) selectedEmotes);
	}


	public static String formAdventureResponses(HashMap<String, AdventureResponse> responses) {
		String res = "Варианты ответов: ";
		boolean isFirst = true;
		for (String key : responses.keySet()) {
			AdventureResponse response = responses.get(key);
			if (!isFirst) {
				res = res + " / ";
			} else {
				isFirst = false;
			}
			res = res + response.getText();
		}
		return res;
	}
}