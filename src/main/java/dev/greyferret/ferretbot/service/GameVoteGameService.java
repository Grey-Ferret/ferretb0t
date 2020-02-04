package dev.greyferret.ferretbot.service;

import dev.greyferret.ferretbot.entity.GameVoteBonusVote;
import dev.greyferret.ferretbot.entity.GameVoteGame;
import dev.greyferret.ferretbot.entity.GameVoteVoting;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Emote;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Log4j2
public class GameVoteGameService {
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	@Autowired
	private GameVoteVotingService gameVoteVotingService;

	@Transactional
	public boolean reset() {
		try {
			List<GameVoteGame> gameVoteGames = getAllGames();
			for (GameVoteGame gameVoteGame : gameVoteGames) {
				entityManager.remove(gameVoteGame);
			}
			if (gameVoteGames.size() > 0) {
				entityManager.flush();
			}
			return true;
		} catch (Exception ex) {
			log.error(ex.toString());
			return false;
		}
	}

	@Transactional
	public GameVoteVoting getOrCreateVotingByChannelAndGame(Long voteChannelId, GameVoteGame game) {
		GameVoteVoting gameVoteVoting = gameVoteVotingService.getVotingByChannelAndGameId(voteChannelId, game);
		if (gameVoteVoting == null) {
			return addVoting(voteChannelId, game);
		} else {
			return gameVoteVoting;
		}
	}

	@Transactional
	private GameVoteVoting addVoting(Long voteChannelId, GameVoteGame game) {
		GameVoteVoting res = new GameVoteVoting(voteChannelId, game);
		entityManager.persist(res);
		entityManager.flush();
		return res;
	}

	public GameVoteVoting getVotingByChannelAndEmote(Long voteChannelId, Long emoteId) {
		GameVoteGame game = getGameByEmoteId(emoteId);
		return getOrCreateVotingByChannelAndGame(voteChannelId, game);
	}

	public List<GameVoteVoting> getVotingsForChannelId(Long voteChannelId) {
		return gameVoteVotingService.getVotingsByChannel(voteChannelId);
	}

	@Transactional
	public GameVoteGame getByUserId(String userId) {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<GameVoteGame> criteria = builder.createQuery(GameVoteGame.class);
		Root<GameVoteGame> root = criteria.from(GameVoteGame.class);
		criteria.select(root);
		criteria.where(builder.equal(root.get("userId"), userId));
		try {
			return entityManager.createQuery(criteria).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Transactional
	public GameVoteGame getByName(String game) {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<GameVoteGame> criteria = builder.createQuery(GameVoteGame.class);
		Root<GameVoteGame> root = criteria.from(GameVoteGame.class);
		criteria.select(root);
		criteria.where(builder.equal(root.get("game"), game));
		try {
			return entityManager.createQuery(criteria).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Transactional
	public boolean addOrUpdate(GameVoteGame _gameVoteGame) {
		boolean found = false;
		GameVoteGame gameVoteGame = getByUserId(_gameVoteGame.getUserId());
		if (gameVoteGame == null) {
			entityManager.persist(_gameVoteGame);
		} else {
			found = true;
			gameVoteGame.setGame(_gameVoteGame.getGame());
			entityManager.merge(gameVoteGame);
		}
		entityManager.flush();
		return found;
	}

	@Transactional
	public Long findNewEmoteId(Long textChannelId, List<Emote> emotes) {
		HashSet<Long> foundEmotes = getAllEmotes(textChannelId);
		if (foundEmotes.size() >= emotes.size()) {
			return null;
		}
		int idE = -1;
		boolean added = false;
		Emote emote = null;
		while (!added) {
			idE = ThreadLocalRandom.current().nextInt(emotes.size());
			emote = emotes.get(idE);
			if (emote.getRoles() == null || emote.getRoles().size() == 0) {
				added = foundEmotes.add(emote.getIdLong());
			}
		}
		return emote.getIdLong();
	}

	@Transactional
	public HashSet<Long> getAllEmotes(Long textChannelId) {
		List<GameVoteGame> allGames = getAllGames();
		HashSet<Long> emotesCodes = new HashSet<>();
		for (GameVoteGame game : allGames) {
			emotesCodes.add(game.getEmoteId());
		}
		return emotesCodes;
	}

	@Transactional
	public GameVoteGame getGameByEmoteId(long emoteId) {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<GameVoteGame> criteria = builder.createQuery(GameVoteGame.class);
		Root<GameVoteGame> root = criteria.from(GameVoteGame.class);
		criteria.select(root);
		criteria.where(builder.equal(root.get("emoteId"), emoteId));
		GameVoteGame res = entityManager.createQuery(criteria).getSingleResult();
		return res;
	}

	@Transactional
	public GameVoteGame getGameByUserId(String userId) {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<GameVoteGame> criteria = builder.createQuery(GameVoteGame.class);
		Root<GameVoteGame> root = criteria.from(GameVoteGame.class);
		criteria.select(root);
		criteria.where(builder.equal(root.get("userId"), userId));
		GameVoteGame res = entityManager.createQuery(criteria).getSingleResult();
		return res;
	}

	@Transactional
	public void addVoter(Long textChannelId, Integer votes, long emoteId, long userId) {
		GameVoteVoting voting = getVotingByChannelAndEmote(textChannelId, emoteId);
		HashMap<Long, Integer> voters = voting.getVoters();
		if (voters == null) {
			voters = new HashMap<>();
		}
		voters.put(userId, votes);
		voting.setVoters(voters);
		entityManager.merge(voting);
	}

	@Transactional
	public void removeVoter(Long textChannelId, long emoteId, long userId) {
		GameVoteVoting voting = getVotingByChannelAndEmote(textChannelId, emoteId);
		HashMap<Long, Integer> voters = voting.getVoters();
		HashMap<Long, Integer> newVoters = new HashMap<>();
		boolean deleted = false;
		for (Long _userId : voters.keySet()) {
			if (userId != _userId) {
				newVoters.put(_userId, voters.get(_userId));
			} else {
				deleted = true;
			}
		}
		if (deleted) {
			voting.setVoters(newVoters);
			entityManager.merge(voting);
			entityManager.flush();
		}
	}

	@Transactional
	public boolean clearVoters(Long textChannelId) {
		List<GameVoteVoting> votings = getVotingsForChannelId(textChannelId);
		for (GameVoteVoting voting : votings) {
			voting.setVoters(new HashMap<>());
			entityManager.merge(voting);
		}
		if (votings.size() > 0) {
			entityManager.flush();
		}
		return true;
	}

	@Transactional
	public void saveGameForVote(Long textChannelId) {
		List<GameVoteGame> allGames = getAllGames();
		ArrayList<GameVoteVoting> votings = new ArrayList<>();
		for (GameVoteGame game : allGames) {
			votings.add(getOrCreateVotingByChannelAndGame(textChannelId, game));
		}

		for (GameVoteVoting voting : votings) {
			voting.setInVote(true);
			voting.setGameVote(voting.getGame().getGame());
			entityManager.merge(voting);
		}
		if (votings != null && votings.size() > 0) {
			entityManager.flush();
		}
	}

	@Transactional
	public List<GameVoteGame> getAllGames() {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<GameVoteGame> criteria = builder.createQuery(GameVoteGame.class);
		Root<GameVoteGame> root = criteria.from(GameVoteGame.class);
		criteria.select(root);
		return entityManager.createQuery(criteria).getResultList();
	}

	@Transactional
	public String removeGame(ArrayList<String> userIds) {
		String res = "Успешно удалены варианты: ";
		ArrayList<String> games = new ArrayList<>();
		for (String userId : userIds) {
			String _game = "";
			try {
				GameVoteGame game = getGameByUserId(userId);
				_game = game.getGame();
				entityManager.remove(game);
				entityManager.flush();
			} catch (Exception ex) {
				log.error(ex);
				continue;
			}
			games.add(_game);
		}
		if (games.size() == 0) {
			return "Игр от указанных зрителей найдено не было...";
		}
		return res + StringUtils.join(games, ", ");
	}

	@Transactional
	public String removeGame(String userId) {
		String res = "Успешно удалён вариант: ";
		String _game = "";
		GameVoteGame game;
		try {
			game = getGameByUserId(userId);
			_game = game.getGame();
		} catch (Exception ex) {
			log.error(ex);
			return "А был ли вариантик?..";
		}
		entityManager.remove(game);
		entityManager.flush();
		return res + _game;
	}

	@Transactional
	public List<GameVoteBonusVote> getAllBonusVotes() {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<GameVoteBonusVote> criteria = builder.createQuery(GameVoteBonusVote.class);
		Root<GameVoteBonusVote> root = criteria.from(GameVoteBonusVote.class);
		criteria.select(root);
		try {
			return entityManager.createQuery(criteria).getResultList();
		} catch (NoResultException ex) {
			return new ArrayList<>();
		}
	}
}
