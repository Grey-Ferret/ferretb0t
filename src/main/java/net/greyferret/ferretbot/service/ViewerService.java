package net.greyferret.ferretbot.service;

import net.greyferret.ferretbot.entity.Viewer;
import net.greyferret.ferretbot.entity.ViewerLootsMap;
import net.greyferret.ferretbot.util.FerretBotUtils;
import net.greyferret.ferretbot.wrapper.ChannelMessageEventWrapper;
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
import java.util.ArrayList;
import java.util.Collections;
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
	public HashSet<Viewer> checkViewers(List<String> users) {
		HashSet<Viewer> viewers = new HashSet<>();
		boolean changed = false;
		for (String user : users) {
			Viewer viewer = entityManager.find(Viewer.class, user);
			if (viewer == null) {
				viewer = new Viewer(user);
				entityManager.persist(viewer);
				changed = true;
			}
			viewers.add(viewer);
		}
		if (changed)
			entityManager.flush();
		return viewers;
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
	public void addPointsForViewers(List<String> users) {
		boolean changed = false;
		HashSet<Viewer> viewers = new HashSet<>();
		for (String user : users) {
			Viewer viewer = entityManager.find(Viewer.class, user);
			if (viewer == null) {
				viewer = new Viewer(user);
				entityManager.persist(viewer);
				changed = true;
			} else {
				if (viewer.isSub())
					viewer.addTruePoints(2L);
				else
					viewer.addTruePoints(1L);
				entityManager.merge(viewer);
				changed = true;
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
		return entityManager.find(Viewer.class, login.toLowerCase());
	}

	@Transactional
	public void addToGoList(Viewer viewer, ChannelMessageEventWrapper event) {
		if (viewer.getGoStatus() == 0) {
			viewer.setGoStatus(1);
			entityManager.merge(viewer);
			entityManager.flush();
		} else if (viewer.getGoStatus() != 1) {
			event.sendMessageWithMention(" невозможно войти в очередь - вы уже играли! Попросите стримера вернуть в очередь или обновить ее!");
		}
	}

	@Transactional
	public void removeToGoList(Viewer viewer, ChannelMessageEventWrapper event) {
		if (viewer.getGoStatus() == 1) {
			viewer.setGoStatus(0);
			entityManager.merge(viewer);
			entityManager.flush();
			event.sendMessageWithMention(" успешно удален из очереди!");
		} else if (viewer.getGoStatus() == 0) {
			event.sendMessageWithMention(" успешно удален из очереди!");
		}
	}

	@Transactional
	public HashSet<Viewer> selectGoList(int numberOfPeople) {
		final int subLuckModifier = 2;
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Viewer> criteria = builder.createQuery(Viewer.class);
		Root<Viewer> root = criteria.from(Viewer.class);
		criteria.select(root);

		criteria.where(builder.equal(root.get("goStatus"), 1));

		List<Viewer> foundList = entityManager.createQuery(criteria).getResultList();
		HashSet<Viewer> selectedList = new HashSet<>();
		ArrayList<Viewer> randomList = FerretBotUtils.combineViewerListWithSubluck(foundList, subLuckModifier);

		if (foundList.size() <= numberOfPeople) {
			selectedList = new HashSet<>(foundList);
		} else {
			while (selectedList.size() < numberOfPeople && randomList.size() > 0) {
				Collections.shuffle(randomList);
				Viewer selectedViewer = randomList.get(0);
				selectedList.add(selectedViewer);
				randomList.removeIf(selectedViewer::equals);
			}
		}
		return selectedList;
	}

	@Transactional
	public void resetGoList(ChannelMessageEventWrapper event) {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Viewer> criteria = builder.createQuery(Viewer.class);
		Root<Viewer> root = criteria.from(Viewer.class);
		criteria.select(root);

		criteria.where(builder.notEqual(root.get("goStatus"), 0));

		List<Viewer> resultList = entityManager.createQuery(criteria).getResultList();
		for (Viewer viewer : resultList) {
			viewer.setGoStatus(0);
			entityManager.merge(viewer);
		}
		entityManager.flush();
		event.sendMessageWithMention(" очередь была успешно очищена!");
	}

	@Transactional
	public void returnToGoList(String login, ChannelMessageEventWrapper event) {
		Viewer viewer = entityManager.find(Viewer.class, login);
		if (viewer == null) {
			event.sendMessageWithMention(" Пользователь " + login + " не был найден");
		} else {
			viewer.setGoStatus(0);
			entityManager.merge(viewer);
			entityManager.flush();
			event.sendMessageWithMention(" Успешно!");
		}
	}

	public int goListSize(ChannelMessageEventWrapper event) {
		return goListSizeByStatus(1);
	}


	public int goListBlockedSize(ChannelMessageEventWrapper event) {
		return goListSizeByStatus(2);
	}

	@Transactional
	protected int goListSizeByStatus(int statusId) {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Viewer> criteria = builder.createQuery(Viewer.class);
		Root<Viewer> root = criteria.from(Viewer.class);
		criteria.select(root);

		criteria.where(builder.equal(root.get("goStatus"), statusId));

		List<Viewer> resultList = entityManager.createQuery(criteria).getResultList();
		return resultList.size();
	}

	@Transactional
	public void returnToDefaultStatus(HashSet<String> timeoutList) {
		boolean isChanged = false;
		for (String s : timeoutList) {
			Viewer viewer = getViewerByName(s);
			if (viewer != null) {
				viewer.setGoStatus(0);
				entityManager.merge(viewer);
				isChanged = true;
			}
		}
		if (isChanged)
			entityManager.flush();
	}
}
