package dev.greyferret.ferretbot.processor;

import dev.greyferret.ferretbot.entity.GameVoteGame;
import dev.greyferret.ferretbot.entity.GamevoteChannelCombination;
import dev.greyferret.ferretbot.service.GameVoteGameService;
import dev.greyferret.ferretbot.util.FerretBotUtils;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Log4j2
public class CalcVoteResultsProcessor implements Runnable {
	private GamevoteChannelCombination channelCombination;
	private GameVoteGameService gameVoteGameService;
	private int minsBeforeCountDown;

	public CalcVoteResultsProcessor(GamevoteChannelCombination channelCombination, GameVoteGameService gameVoteGameService, int minsBeforeCountDown) {
		this.channelCombination = channelCombination;
		this.gameVoteGameService = gameVoteGameService;
		this.minsBeforeCountDown = minsBeforeCountDown;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(minsBeforeCountDown * 60 * 1000);
		} catch (InterruptedException e) {
			log.error(e);
		}
		ArrayList<GameVoteGame> topGames = getTopGames(channelCombination.getAddChannelId());

		if (topGames.size() == 0) {
			channelCombination.getVoteChannel().sendMessage("Голосуем?").queue();
		} else if (topGames.size() == 1) {
			channelCombination.getVoteChannel().sendMessage("До окончания голосования осталась **одна минута**!\n\n**Текущий фаворит**: " + topGames.get(0).getGameVote()).queue();
		} else {
			channelCombination.getVoteChannel().sendMessage("До окончания голосования осталась **одна минута**!\n\n**Текущие фавориты**: " + FerretBotUtils.joinGamesBySeparator(topGames, ", ")).queue();
		}

		try {
			Thread.sleep(60 * 1000);
		} catch (InterruptedException e) {
			log.error(e);
		}

		topGames = getTopGames(channelCombination.getAddChannelId());
		if (topGames.size() == 0) {
			channelCombination.getVoteChannel().sendMessage("Голосуем?").queue();
		} else if (topGames.size() == 1) {
			channelCombination.getVoteChannel().sendMessage("**ПОБЕДИТЕЛЬ**: " + topGames.get(0).getGameVote()).queue();
		} else {
			int i = ThreadLocalRandom.current().nextInt(topGames.size());
			log.info("Rolling Game Vote Winner out of " + topGames.size() + ". Result is: " + i);
			GameVoteGame game = topGames.get(i);
			channelCombination.getVoteChannel().sendMessage("**ПОБЕДИТЕЛЬ**: " + game.getGameVote() + "!\n\n**Рандом решил между**: " + FerretBotUtils.joinGamesBySeparator(topGames, ", ")).queue();
		}
	}

	private ArrayList<GameVoteGame> getTopGames(Long textChannelId) {
		ArrayList<GameVoteGame> topGames = new ArrayList<>();
		int topVoteGame = 0;
		List<GameVoteGame> games = this.gameVoteGameService.getAllWithTextChannelId(textChannelId);
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
