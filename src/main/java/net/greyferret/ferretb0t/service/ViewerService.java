package net.greyferret.ferretb0t.service;

import net.greyferret.ferretb0t.entity.Viewer;
import net.greyferret.ferretb0t.entity.ViewerLootsMap;
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
import java.util.List;

/**
 * Created by GreyFerret on 27.12.2017.
 */
@Service
public class ViewerService {
	private static final Logger logger = LogManager.getLogger();
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	@Autowired
	private ApplicationContext context;
	@Autowired
	private LootsService lootsService;

	@Transactional
	public void checkViewersAndAddPoints(List<String> users, boolean isChannelOnline) {
		boolean changed = false;
		for (String user : users) {
			Viewer viewer = entityManager.find(Viewer.class, user);
			if (viewer == null) {
				viewer = new Viewer(user);
				entityManager.persist(viewer);
				changed = true;
			} else {
				if (isChannelOnline) {
					viewer.addTruePoints(1L);
					entityManager.merge(viewer);
					changed = true;
				}
			}
		}
		if (changed)
			entityManager.flush();
	}

	@Transactional
	public boolean addPoints(String login, Long points) {
		Viewer viewer = entityManager.find(Viewer.class, login);
		if (viewer == null)
			return false;
		viewer.addPoints(points);
		entityManager.merge(viewer);
		entityManager.flush();
		return true;
	}

	@Transactional
	public boolean removePoints(String login, Long points) {
		Viewer viewer = entityManager.find(Viewer.class, login);
		if (viewer == null)
			return false;
		boolean b = viewer.removePoints(points);
		if (!b)
			return false;
		entityManager.merge(viewer);
		entityManager.flush();
		return true;
	}

	@Transactional
	public Viewer findViewerForLootsName(String lootsName) {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ViewerLootsMap> criteria = builder.createQuery(ViewerLootsMap.class);
		Root<ViewerLootsMap> root = criteria.from(ViewerLootsMap.class);
		criteria.select(root);

		criteria.where(builder.equal(root.get("lootsName"), lootsName.toLowerCase()));

		List<ViewerLootsMap> viewerLootsMapList = entityManager.createQuery(criteria).getResultList();
		if (viewerLootsMapList == null || viewerLootsMapList.size() == 0) {
			return null;
		} else {
			ViewerLootsMap viewerLootsMap = viewerLootsMapList.get(0);
			Viewer viewer = viewerLootsMap.getViewer();
			if (viewer == null) {
				Viewer viewerByName = getViewerByName(lootsName.toLowerCase());
				if (viewerByName != null) {
					logger.info("Found Viewer by name: " + viewerByName);
					viewerLootsMap.setViewer(viewerByName);
					entityManager.merge(viewerLootsMap);
					entityManager.flush();
					return viewerByName;
				}
			} else {
				return viewer;
			}
		}
		return null;
	}

	@Transactional
	public Viewer getViewerByName(String login) {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Viewer> criteria = builder.createQuery(Viewer.class);
		Root<Viewer> root = criteria.from(Viewer.class);
		criteria.select(root);

		criteria.where(builder.equal(root.get("login"), login));

		List<Viewer> resultList = entityManager.createQuery(criteria).getResultList();
		if (resultList == null || resultList.size() == 0) {
			return null;
		} else
			return resultList.get(0);
	}
}
