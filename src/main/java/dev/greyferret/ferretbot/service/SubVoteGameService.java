package dev.greyferret.ferretbot.service;

import dev.greyferret.ferretbot.entity.SubVoteGame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.EntityMode;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Set;

@Service
public class SubVoteGameService {
	private static final Logger logger = LogManager.getLogger(SubVoteGameService.class);

	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Transactional
	public boolean reset() {
		try {
			List<SubVoteGame> subVoteGames = getAll();
			for (SubVoteGame subVoteGame : subVoteGames) {
				entityManager.remove(subVoteGame);
			}
			if (subVoteGames.size() > 0) {
				entityManager.flush();
				return true;
			}
			return false;
		} catch (Exception ex) {
			logger.error(ex);
			return false;
		}
	}

	@Transactional
	public boolean containsId(String id) {
		SubVoteGame subVoteGame = entityManager.find(SubVoteGame.class, id);
		if (subVoteGame != null) {
			return true;
		}
		return false;
	}

	@Transactional
	public List<SubVoteGame> getByGame(String game) {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<SubVoteGame> criteria = builder.createQuery(SubVoteGame.class);
		Root<SubVoteGame> root = criteria.from(SubVoteGame.class);
		criteria.select(root);
		criteria.where(builder.equal(root.get("game"), game));
		List<SubVoteGame> subVoteGames = entityManager.createQuery(criteria).getResultList();
		return subVoteGames;
	}

	@Transactional
	public void addOrUpdate(SubVoteGame subVoteGame) {
		SubVoteGame _subVoteGame = entityManager.find(SubVoteGame.class, subVoteGame.getId());
		if (_subVoteGame == null) {
			entityManager.persist(subVoteGame);
		} else {
			entityManager.merge(subVoteGame);
		}
		entityManager.flush();
	}

	@Transactional
	public List<SubVoteGame> getAll() {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<SubVoteGame> criteria = builder.createQuery(SubVoteGame.class);
		Root<SubVoteGame> root = criteria.from(SubVoteGame.class);
		criteria.select(root);
		return entityManager.createQuery(criteria).getResultList();
	}
}
