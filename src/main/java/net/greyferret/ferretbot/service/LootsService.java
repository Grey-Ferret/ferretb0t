package net.greyferret.ferretbot.service;

import net.greyferret.ferretbot.entity.Loots;
import net.greyferret.ferretbot.entity.Viewer;
import net.greyferret.ferretbot.entity.ViewerLootsMap;
import net.greyferret.ferretbot.exception.TransactionRuntimeFerretBotException;
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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * Created by GreyFerret on 19.12.2017.
 */
@Service
public class LootsService {
	private static final Logger logger = LogManager.getLogger(LootsService.class);

	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	@Autowired
	private ViewerService viewerService;
	@Autowired
	private ViewerLootsMapService viewerLootsMapService;

	/***
	 * Checking loots for being recorded in DB
	 *
	 * @param lootsSet loots from site
	 * @return set of the found new loots
	 */
	@Transactional
	public Set<Loots> checkOutLoots(Set<Loots> lootsSet) {
		Set<Loots> res = new HashSet<>();
		HashMap<String, ViewerLootsMap> mapOfMaps = new HashMap<>();

		try {
			for (Loots loots : lootsSet) {
				if (loots.getViewerLootsMap() == null) {
					String lootsName = loots.getLootsName();
					ViewerLootsMap viewerLootsMap = mapOfMaps.get(lootsName);
					if (viewerLootsMap == null) {
						viewerLootsMap = viewerLootsMapService.getViewerLootsMap(lootsName);
						if (viewerLootsMap == null) {
							viewerLootsMap = new ViewerLootsMap(lootsName);
						}
						mapOfMaps.put(lootsName, viewerLootsMap);
					}
					loots.setViewerLootsMap(viewerLootsMap);
				}
				Loots foundLoots = entityManager.find(Loots.class, loots.getId());
				if (foundLoots == null) {
					entityManager.persist(loots);
					entityManager.flush();
					res.add(loots);
					logger.info("New Loots found!");
					logger.info(loots);

					removeOldLoots();
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new TransactionRuntimeFerretBotException(e);
		}

		return res;
	}

	@Transactional
	public void removeOldLoots() {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Loots> criteria = builder.createQuery(Loots.class);
		Root<Loots> root = criteria.from(Loots.class);
		criteria.select(root);

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -3);
		Predicate oldDate = builder.lessThan(root.get("date"), calendar.getTime());
		criteria.where(oldDate);

		List<Loots> oldList = entityManager.createQuery(criteria).getResultList();
		for (Loots loots : oldList) {
			entityManager.remove(loots);
		}
		if (oldList != null && oldList.size() > 0) {
			entityManager.flush();
		}
	}

	/***
	 * Method that gets set of the unpaid loots and marks them as paid
	 *
	 * @return unpaid loots
	 */
	@Transactional
	public Set<Loots> getUnpaidLoots() {
		Set<Loots> res = new HashSet<>();
		HashMap<String, ViewerLootsMap> mapOfMaps = new HashMap<>();

		try {
			CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
			CriteriaQuery<Loots> criteria = builder.createQuery(Loots.class);
			Root<Loots> root = criteria.from(Loots.class);
			criteria.select(root);

			criteria.where(builder.equal(root.get("paid"), false));

			List<Loots> lootsList = entityManager.createQuery(criteria).getResultList();

			for (Loots loots : lootsList) {
				ViewerLootsMap viewerLootsMap = loots.getViewerLootsMap();
				if (viewerLootsMap == null) {
					String lootsName = loots.getLootsName();
					viewerLootsMap = mapOfMaps.get(lootsName);
					if (viewerLootsMap == null) {
						viewerLootsMap = viewerLootsMapService.getViewerLootsMap(lootsName);
						if (viewerLootsMap == null) {
							viewerLootsMap = new ViewerLootsMap(lootsName);
							mapOfMaps.put(lootsName, viewerLootsMap);
						}
					}
					loots.setViewerLootsMap(viewerLootsMap);
				}
				if (viewerLootsMap.getViewer() == null) {
					Viewer viewer = viewerService.findViewerForLootsName(viewerLootsMap.getLootsName());
					if (viewer != null) {
						viewerLootsMap.setViewer(viewer);
						loots.setViewerLootsMap(viewerLootsMap);
					}
				}
				if (viewerLootsMap.getViewer() == null) {

				} else {
					loots.setPaid(true);
					entityManager.merge(loots);
					res.add(loots);
					logger.info("Payment incoming for Loots " + loots.getId() + " from " + loots.getViewerLootsMap().getViewer().getLogin());
				}
			}
			if (res.size() > 0)
				entityManager.flush();
		} catch (Exception e) {
			logger.error(e);
			throw new TransactionRuntimeFerretBotException(e);
		}

		return res;
	}

	@Transactional
	public void deleteLootsForNickname(String lootsName) {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Loots> query = builder.createQuery(Loots.class);
		Root<Loots> root = query.from(Loots.class);

		query.select(root);
		ViewerLootsMap viewerLootsMap = new ViewerLootsMap(lootsName.toLowerCase());
		query.where(builder.equal(root.get("viewerLootsMap"), viewerLootsMap));

		List<Loots> resultList = entityManager.createQuery(query).getResultList();
		for (Loots loots : resultList) {
			entityManager.remove(loots);
		}
		entityManager.flush();
	}
}
