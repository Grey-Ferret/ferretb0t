package dev.greyferret.ferretbot.service;

import dev.greyferret.ferretbot.entity.GameVoteGame;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

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
	public boolean containsId(String id) {
		GameVoteGame gameVoteGame = entityManager.find(GameVoteGame.class, id);
		if (gameVoteGame != null) {
			return true;
		}
		return false;
	}

	@Transactional
	public List<GameVoteGame> getByGame(String game) {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<GameVoteGame> criteria = builder.createQuery(GameVoteGame.class);
		Root<GameVoteGame> root = criteria.from(GameVoteGame.class);
		criteria.select(root);
		criteria.where(builder.equal(root.get("game"), game));
		List<GameVoteGame> gameVoteGames = entityManager.createQuery(criteria).getResultList();
		return gameVoteGames;
	}

	@Transactional
	public void addOrUpdate(GameVoteGame gameVoteGame) {
		GameVoteGame _gameVoteGame = entityManager.find(GameVoteGame.class, gameVoteGame.getId());
		if (_gameVoteGame == null) {
			entityManager.persist(gameVoteGame);
		} else {
			entityManager.merge(gameVoteGame);
		}
		entityManager.flush();
	}

	@Transactional
	public List<GameVoteGame> getAll() {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<GameVoteGame> criteria = builder.createQuery(GameVoteGame.class);
		Root<GameVoteGame> root = criteria.from(GameVoteGame.class);
		criteria.select(root);
		return entityManager.createQuery(criteria).getResultList();
	}
}
