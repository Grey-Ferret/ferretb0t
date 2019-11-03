package dev.greyferret.ferretbot.service;

import dev.greyferret.ferretbot.entity.Loots;
import dev.greyferret.ferretbot.entity.Viewer;
import dev.greyferret.ferretbot.entity.ViewerLootsMap;
import dev.greyferret.ferretbot.exception.TransactionRuntimeFerretBotException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by GreyFerret on 19.12.2017.
 */
@Service
@Log4j2
public class LootsService {
	@Value("${main.zone-id}")
	private String zoneId;

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
			boolean persisted = false;
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
					persisted = true;
					res.add(loots);
					log.info("New Loots found!");
					log.info(loots.toString());
				}
			}
			if (persisted) {
				entityManager.flush();
			}
		} catch (Exception e) {
			log.error(e.toString());
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

		ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of(zoneId)).minusMonths(3);
		Predicate oldDate = builder.lessThan(root.get("date"), zdt);
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
					log.info("Payment incoming for Loots " + loots.getId() + " from " + loots.getViewerLootsMap().getViewer().getLoginVisual());
				}
			}
			if (res.size() > 0)
				entityManager.flush();
		} catch (Exception e) {
			log.error(e.toString());
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
