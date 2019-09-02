package dev.greyferret.ferretbot.service;

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
import java.util.HashSet;
import java.util.List;

/**
 * Created by GreyFerret on 27.12.2017.
 */
@Service
public class ViewerService {
	private static final Logger logger = LogManager.getLogger(ViewerService.class);

	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Transactional
	public void setSubscriber(Viewer viewer, Boolean isSub) {
		if (viewer.isSub() != isSub) {
			viewer.setSub(isSub);
			entityManager.merge(viewer);
			entityManager.flush();
		}
	}

	@Transactional
	public Viewer createViewer(String login) {
		Viewer viewer = new Viewer(login.toLowerCase());
		entityManager.persist(viewer);
		return viewer;
	}

	@Transactional
	public HashSet<Viewer> checkViewers(List<String> users) {
		HashSet<Viewer> viewers = new HashSet<>();
		boolean changed = false;
		for (String user : users) {
			Viewer viewer = entityManager.find(Viewer.class, user);
			if (viewer == null) {
				viewer = createViewer(user);
				changed = true;
			}
			viewers.add(viewer);
		}
		if (changed)
			entityManager.flush();
		return viewers;
	}

	@Transactional
	public void updateVisual(Viewer viewer, String visual) {
		if (viewer == null || StringUtils.isBlank(visual))
			return;
		viewer.setLoginVisual(visual);
		entityManager.merge(viewer);
		entityManager.flush();
	}

	@Transactional
	public void updateFollowerStatus(Viewer viewer, String followedAt, boolean isFollower) {
		if (viewer == null)
			return;
		viewer.setFollower(isFollower);
		viewer.setFollowedAt(followedAt);
		entityManager.merge(viewer);
		entityManager.flush();
		logger.info("Updated follower info for: " + viewer.getLoginVisual());
	}

	@Transactional
	public void addPointsForViewers(HashSet<Viewer> users) {
		for (Viewer viewer : users) {
			if (viewer != null) {
				if (viewer.isSub())
					viewer.addTruePoints(2L);
				else
					viewer.addTruePoints(1L);
				entityManager.merge(viewer);
			}
		}
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
					logger.info("Found Viewer by name: " + viewerByName.getLoginVisual());
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
		return entityManager.find(Viewer.class, login.toLowerCase());
	}

	@Transactional
	public void updateApproved(Viewer viewer, boolean approved) {
		logger.info("Viewer " + viewer.getLoginVisual() + " set as approved");
		viewer.setApproved(approved);
		entityManager.merge(viewer);
		entityManager.flush();
	}

	@Transactional
	public void updateViewer(Viewer viewer) {
		logger.info("Viewer " + viewer.getLoginVisual() + " was updated");
		entityManager.merge(viewer);
		entityManager.flush();
	}

	public void setVip(Viewer viewer, boolean isVip) {
		if (viewer.isVip() != isVip) {
			viewer.setVip(isVip);
			entityManager.merge(viewer);
			entityManager.flush();
		}
	}
}
