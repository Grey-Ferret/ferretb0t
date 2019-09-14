package dev.greyferret.ferretbot.service;

import dev.greyferret.ferretbot.entity.Adventure;
import dev.greyferret.ferretbot.entity.AdventureResponse;
import dev.greyferret.ferretbot.entity.Viewer;
import dev.greyferret.ferretbot.entity.ViewerLootsMap;
import org.apache.commons.lang3.StringUtils;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Created by GreyFerret on 27.12.2017.
 */
@Service
public class AdventureService {
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private EntityManagerFactory entityManagerFactory;

	public Adventure getAdventure() {
		return getAdventure(false, false);
	}

	public Adventure getStartAdventure() {
		return getAdventure(true, false);
	}

	public Adventure getFinalAdventure() {
		return getAdventure(false, true);
	}

	@Transactional
	protected Adventure getAdventure(boolean isStart, boolean isFinal) {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Adventure> criteria = builder.createQuery(Adventure.class);
		Root<Adventure> root = criteria.from(Adventure.class);
		criteria.select(root);
		criteria.where(builder.and(builder.equal(root.get("isStart"), isStart), builder.equal(root.get("isFinal"), isFinal)));
		List<Adventure> adventures = entityManager.createQuery(criteria).getResultList();
		if (adventures == null || adventures.size() == 0) {
			return null;
		}
		Random rand = new Random();
		int i = rand.nextInt(adventures.size());
		return adventures.get(i);
	}

	public HashMap<String, AdventureResponse> getAdventureResponses(Long id) {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<AdventureResponse> criteria = builder.createQuery(AdventureResponse.class);
		Root<AdventureResponse> root = criteria.from(AdventureResponse.class);
		criteria.select(root);
		criteria.where(builder.equal(root.get("adventure"), id));
		List<AdventureResponse> responses = entityManager.createQuery(criteria).getResultList();
		HashMap<String, AdventureResponse> res = new HashMap<>();
		for (AdventureResponse response : responses) {
			res.put(response.getKey(), response);
		}
		return res;
	}
}
