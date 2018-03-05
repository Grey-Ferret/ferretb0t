package net.greyferret.ferretb0t.service;

import net.greyferret.ferretb0t.entity.Viewer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitteh.irc.client.library.element.User;
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

	/***
	 * Method that updates Twitch Nick for Loots Nick
	 *
	 * @param lootsName
	 * @param twitchName
	 * @return
	 */
	@Transactional
	public String updateAlias(String lootsName, String twitchName) {
		boolean isUpdated = false;
		String res = "Something went wrong";
		if (StringUtils.isNotBlank(lootsName) && StringUtils.isNotBlank(twitchName)) {
			Viewer viewer = entityManager.find(Viewer.class, twitchName.toLowerCase());
			if (viewer != null) {
				viewer.setLootsNameWithCase(lootsName);
				entityManager.merge(viewer);
				isUpdated = true;
			} else {
				viewer = entityManager.find(Viewer.class, lootsName.toLowerCase());
				if (viewer != null) {
					viewer.setLootsNameWithCase(twitchName);
					entityManager.merge(viewer);
					isUpdated = true;
				} else {
					res = "Could not find viewer with name " + twitchName + " or " + lootsName;
					logger.info("Could not find viewer for " + twitchName + " or " + lootsName);
				}
			}
		}
		if (isUpdated) {
			entityManager.flush();
			res = "Готово!";
		}
		return res;
	}

	@Transactional
	public String showAliasMessage(String lootsName) {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Viewer> criteria = builder.createQuery(Viewer.class);
		Root<Viewer> root = criteria.from(Viewer.class);
		criteria.select(root);

		criteria.where(builder.equal(root.get("lootsName"), lootsName.toLowerCase()));

		Viewer viewer = null;
		List<Viewer> viewerList = entityManager.createQuery(criteria).getResultList();
		if (viewer == null) {
			return "No alias were found";
		} else {
			if (viewerList.size() > 1)
				logger.warn("Found more than 1 viewer with the same very same Loots name: " + lootsName);
			viewer = viewerList.get(0);
			return "L:" + lootsName + " T:" + viewer.getLoginWithCase();
		}
	}

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
		CriteriaQuery<Viewer> criteria = builder.createQuery(Viewer.class);
		Root<Viewer> root = criteria.from(Viewer.class);
		criteria.select(root);

		criteria.where(builder.equal(root.get("lootsName"), lootsName.toLowerCase()));

		List<Viewer> userList = entityManager.createQuery(criteria).getResultList();
		if (userList == null || userList.size() == 0) {
			return null;
		}
		return userList.get(0);
	}
}
