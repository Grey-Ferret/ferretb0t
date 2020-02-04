package dev.greyferret.ferretbot.service;

import dev.greyferret.ferretbot.entity.GameVoteGame;
import dev.greyferret.ferretbot.entity.GameVoteVoting;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class GameVoteVotingService {
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private EntityManagerFactory entityManagerFactory;


	public List<GameVoteVoting> getVotingsByChannel(Long voteChannelId) {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<GameVoteVoting> criteria = builder.createQuery(GameVoteVoting.class);
		Root<GameVoteVoting> root = criteria.from(GameVoteVoting.class);
		criteria.select(root);
		criteria.where(builder.equal(root.get("voteChannelId"), voteChannelId));
		try {
			return entityManager.createQuery(criteria).getResultList();
		} catch (NoResultException ex) {
			return new ArrayList<>();
		}
	}

	public GameVoteVoting getVotingByChannelAndGameId(Long voteChannelId, GameVoteGame game) {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<GameVoteVoting> criteria = builder.createQuery(GameVoteVoting.class);
		Root<GameVoteVoting> root = criteria.from(GameVoteVoting.class);
		criteria.select(root);
		criteria.where(builder.and(builder.equal(root.get("voteChannelId"), voteChannelId), builder.equal(root.get("game"), game.getId())));
		try {
			return entityManager.createQuery(criteria).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}
}
