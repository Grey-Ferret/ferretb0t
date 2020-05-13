package dev.greyferret.ferretbot.service;

import dev.greyferret.ferretbot.entity.Interactive;
import dev.greyferret.ferretbot.entity.RedeemedInteractive;
import dev.greyferret.ferretbot.entity.Viewer;
import dev.greyferret.ferretbot.processor.StreamElementsAPIProcessor;
import dev.greyferret.ferretbot.repository.RedeemedInteractiveRepository;
import dev.greyferret.ferretbot.wrapper.ChannelMessageEventWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Service
public class InteractiveService {
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private StreamElementsAPIProcessor streamElementsAPIProcessor;
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	@Autowired
	private ViewerService viewerService;
	@Autowired
	private RedeemedInteractiveRepository redeemedInteractiveRepository;

	@Transactional
	public boolean proceedInteractiveCommand(Interactive interactive, ChannelMessageEventWrapper event) {
		if (interactive != null) {
			if (interactive.isDisabled()) {
				return false;
			}
			boolean enoughPoints = streamElementsAPIProcessor.updatePoints(event.getLogin(), interactive.getPrice());
			if (enoughPoints) {
				Viewer viewer = viewerService.getViewerByName(event.getLogin().toLowerCase());
				redeemedInteractiveRepository.save(new RedeemedInteractive(interactive, viewer));
				event.sendMessageWithMention(" успешно заказал \"" + interactive.getResponse() + "\"");
			} else {
				event.sendMessageWithMention("Недостаточно IQ для заказа");
			}
			return true;
		}
		return false;
	}

	@Transactional
	public Interactive getInteractiveCommandByCode(String code) {
		if (code.startsWith("!")) {
			code = code.substring(1);
		}

		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Interactive> criteria = builder.createQuery(Interactive.class);
		Root<Interactive> root = criteria.from(Interactive.class);
		criteria.select(root);
		criteria.where(builder.equal(root.get("code"), code.toLowerCase()));
		try {
			return entityManager.createQuery(criteria).getSingleResult();
		} catch (PersistenceException ex) {
			return null;
		}
	}
}

