package dev.greyferret.ferretbot.service;

import dev.greyferret.ferretbot.entity.GameVoteGame;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Emote;
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
import java.util.HashSet;
import java.util.List;
import java.util.Random;

@Service
@Log4j2
public class GameVoteGameService {
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Transactional
	public boolean reset() {
		try {
			List<GameVoteGame> gameVoteGames = getAll();
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
	public GameVoteGame getByGame(String game) {
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
	public GameVoteGame getGameByName(String name) {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<GameVoteGame> criteria = builder.createQuery(GameVoteGame.class);
		Root<GameVoteGame> root = criteria.from(GameVoteGame.class);
		criteria.select(root);
		criteria.where(builder.equal(root.get("name"), name));
		try {
			return entityManager.createQuery(criteria).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Transactional
	public boolean addOrUpdate(GameVoteGame _gameVoteGame) {
		boolean found = false;
		GameVoteGame gameVoteGame = entityManager.find(GameVoteGame.class, _gameVoteGame.getId());
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
	public List<GameVoteGame> getAll() {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<GameVoteGame> criteria = builder.createQuery(GameVoteGame.class);
		Root<GameVoteGame> root = criteria.from(GameVoteGame.class);
		criteria.select(root);
		return entityManager.createQuery(criteria).getResultList();
	}

	@Transactional
	public Long findNewEmoteId(List<Emote> emotes) {
		HashSet<Long> foundEmotes = getAllEmotes();
		if (foundEmotes.size() >= emotes.size()) {
			return null;
		}
		int idE = -1;
		Random rand = new Random();
		boolean added = false;
		Emote emote = null;
		while (!added) {
			idE = rand.nextInt(emotes.size());
			emote = emotes.get(idE);
			if (emote.getRoles() == null || emote.getRoles().size() == 0) {
				added = foundEmotes.add(emote.getIdLong());
			}
		}
		return emote.getIdLong();
	}

	@Transactional
	public HashSet<Long> getAllEmotes() {
		List<GameVoteGame> allGames = getAll();
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
	public boolean addVoter(long emoteId, long userId) {
		GameVoteGame game = getGameByEmoteId(emoteId);
		HashSet<Long> voters = game.getVoters();
		boolean res = voters.add(userId);
		game.setVoters(voters);
		entityManager.merge(game);
		return res;
	}

	@Transactional
	public boolean clearVoters() {
		List<GameVoteGame> games = getAll();
		for (GameVoteGame gameVoteGame : games) {
			gameVoteGame.setVoters(new HashSet<>());
			entityManager.merge(gameVoteGame);
		}
		if (games.size() > 0) {
			entityManager.flush();
		}
		return true;
	}

	@Transactional
	public void saveGameForVote() {
		List<GameVoteGame> games = getAll();
		for (GameVoteGame game : games) {
			game.setInVote(true);
			game.setGameVote(game.getGame());
			entityManager.merge(game);
		}
		if (games != null && games.size() > 0) {
			entityManager.flush();
		}
	}
}
