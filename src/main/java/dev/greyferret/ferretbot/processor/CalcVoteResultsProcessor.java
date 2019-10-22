package dev.greyferret.ferretbot.processor;

import dev.greyferret.ferretbot.entity.GameVoteGame;
import dev.greyferret.ferretbot.service.GameVoteGameService;
import dev.greyferret.ferretbot.util.FerretBotUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Log4j2
public class CalcVoteResultsProcessor implements Runnable {
	@Autowired
	private GameVoteGameService gameVoteGameService;
	@Autowired
	private DiscordProcessor discordProcessor;

	@Override
	public void run() {
		ArrayList<GameVoteGame> topGames = getTopGames();

		if (topGames.size() == 0) {
			discordProcessor.writeVoteChannel.sendMessage("Голосуем?").queue();
		} else if (topGames.size() == 1) {
			discordProcessor.writeVoteChannel.sendMessage("До окончания голосования осталась **одна минута**!\n\n**Текущий фаворит**: " + topGames.get(0).getGameVote()).queue();
		} else {
			discordProcessor.writeVoteChannel.sendMessage("До окончания голосования осталась **одна минута**!\n\n**Текущие фавориты**: " + FerretBotUtils.joinGamesBySeparator(topGames, ", ")).queue();
		}

		try {
			Thread.sleep(60 * 1000);
		} catch (InterruptedException e) {
			log.error(e);
		}

		topGames = getTopGames();
		if (topGames.size() == 0) {
			discordProcessor.writeVoteChannel.sendMessage("Голосуем?").queue();
		} else if (topGames.size() == 1) {
			discordProcessor.writeVoteChannel.sendMessage("**ПОБЕДИТЕЛЬ**: " + topGames.get(0).getGameVote()).queue();
		} else {
			int i = ThreadLocalRandom.current().nextInt(topGames.size());
			log.info("Rolling Game Vote Winner out of " + topGames.size() + ". Result is: " + i);
			GameVoteGame game = topGames.get(i);
			discordProcessor.writeVoteChannel.sendMessage("**ПОБЕДИТЕЛЬ**: " + game.getGameVote() + "!\n\n**Рандом решил между**: " + FerretBotUtils.joinGamesBySeparator(topGames, ", ")).queue();
		}
	}

	private ArrayList<GameVoteGame> getTopGames() {
		ArrayList<GameVoteGame> topGames = new ArrayList<>();
		int topVoteGame = 0;
		List<GameVoteGame> games = gameVoteGameService.getAll();
		for (GameVoteGame game : games) {
			int gameVotersForCurrentGame = game.getVoters().size();
			if (gameVotersForCurrentGame > topVoteGame) {
				topVoteGame = gameVotersForCurrentGame;
				topGames = new ArrayList<>();
			}
			if (gameVotersForCurrentGame == topVoteGame) {
				topGames.add(game);
			}
		}
		return topGames;
	}
}
