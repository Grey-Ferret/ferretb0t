package dev.greyferret.ferretbot.service;

import dev.greyferret.ferretbot.entity.Viewer;
import dev.greyferret.ferretbot.entity.ViewerLootsMap;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
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
import java.util.Set;

@Service
@Log4j2
public class ViewerLootsMapService {
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	@Autowired
	private ViewerService viewerService;
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
		String res = "Something went wrong";
		if (StringUtils.isNotBlank(lootsName) && StringUtils.isNotBlank(twitchName)) {
			ViewerLootsMap viewerLootsMap = getViewerLootsMap(lootsName.toLowerCase());
			Viewer viewer = viewerService.getViewerByName(twitchName.toLowerCase());
			if (viewerLootsMap == null) {
				res = "Не было найдено Лутса с такого ника...";
				return res;
			}
			if (viewer == null) {
				res = "Нет такого твич ника в наших зрителях";
				return res;
			}
			viewerLootsMap.setViewer(viewer);
			entityManager.merge(viewerLootsMap);
			entityManager.flush();
			res = "Готово!";
		}
		return res;
	}

	public ViewerLootsMap getViewerLootsMap(String lootsName) {
		if (StringUtils.isNotBlank(lootsName)) {
			ViewerLootsMap viewerLootsMap = entityManager.find(ViewerLootsMap.class, lootsName.toLowerCase());
			return viewerLootsMap;
		} else
			return null;
	}

	@Transactional
	public Set<String> getRepairList() {
		Set<String> res = new HashSet<>();
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ViewerLootsMap> query = builder.createQuery(ViewerLootsMap.class);
		Root<ViewerLootsMap> from = query.from(ViewerLootsMap.class);

		query.select(from);
		query.where(builder.isNull(from.get("viewer")));

		List<ViewerLootsMap> resultList = entityManager.createQuery(query).getResultList();
		for (ViewerLootsMap viewerLootsMap : resultList) {
			res.add(viewerLootsMap.getLootsName());
		}
		return res;
	}

	@Transactional
	public String showAliasMessage(String lootsName) {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ViewerLootsMap> criteria = builder.createQuery(ViewerLootsMap.class);
		Root<ViewerLootsMap> root = criteria.from(ViewerLootsMap.class);
		criteria.select(root);

		criteria.where(builder.equal(root.get("lootsName"), lootsName.toLowerCase()));

		ViewerLootsMap viewerLootsMap = null;
		List<ViewerLootsMap> viewerLootsMapList = entityManager.createQuery(criteria).getResultList();
		if (viewerLootsMapList == null || viewerLootsMapList.size() == 0) {
			return "No alias were found";
		} else {
			if (viewerLootsMapList.size() > 1)
				log.warn("Found more than 1 viewer with the same very same Loots name: " + lootsName);
			viewerLootsMap = viewerLootsMapList.get(0);
			String twitchNick = "";
			Viewer viewer = viewerLootsMap.getViewer();
			if (viewer == null)
				twitchNick = "<не установлен>";
			else
				twitchNick = viewer.getLogin();
			return "L:" + viewerLootsMap.getLootsName() + " T:" + twitchNick;
		}
	}

	@Transactional
	public String deleteViewerLootsMap(String lootsName) {
		ViewerLootsMap viewerLootsMap = getViewerLootsMap(lootsName);
		if (viewerLootsMap == null)
			return lootsName + " не был найден в связях.";
		if (viewerLootsMap.getViewer() != null) {
			return " для ника " + lootsName + " был найден зритель " + viewerLootsMap.getViewer().getLogin() + ". Удаление запрещено, обратитесь к @GreyFerret";
		}
		lootsService.deleteLootsForNickname(lootsName);
		entityManager.remove(viewerLootsMap);
		try {
			entityManager.flush();
			return "Успешно удалено!";
		} catch (ConstraintViolationException ex) {
			log.error(ex.toString());
			return "Что-то пошло не так...";
		}
	}
}
