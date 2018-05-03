package net.greyferret.ferretb0t.service;

import net.greyferret.ferretb0t.entity.Loots;
import net.greyferret.ferretb0t.entity.Viewer;
import net.greyferret.ferretb0t.entity.ViewerLootsMap;
import net.greyferret.ferretb0t.exception.TransactionRuntimeFerretB0tException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
import java.util.Set;

/**
 * Created by GreyFerret on 19.12.2017.
 */
@Service
public class LootsService {
	private static final Logger logger = LogManager.getLogger();
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	@Autowired
	private ApplicationContext context;
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
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new TransactionRuntimeFerretB0tException(e);
		}

		return res;
	}

	/***
	 * Method that gets set of the unpaid loots and marks them as paid
	 *
	 * @return unpaid loots
	 */
	@Transactional
	public Set<Loots> payForUnpaidLoots() {
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
			throw new TransactionRuntimeFerretB0tException(e);
		}

		return res;
	}
}
