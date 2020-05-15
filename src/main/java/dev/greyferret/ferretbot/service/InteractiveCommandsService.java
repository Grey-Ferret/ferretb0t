package dev.greyferret.ferretbot.service;

import dev.greyferret.ferretbot.entity.InteractiveCommand;
import dev.greyferret.ferretbot.processor.StreamElementsAPIProcessor;
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
public class InteractiveCommandsService {
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private StreamElementsAPIProcessor streamElementsAPIProcessor;
	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Transactional
	public boolean proceedInteractiveCommand(InteractiveCommand interactiveCommand, ChannelMessageEventWrapper event) {
		if (interactiveCommand != null) {
			if (interactiveCommand.isDisabled()) {
				return false;
			}
			boolean enoughPoints = streamElementsAPIProcessor.updatePoints(event.getLogin(), -1 * interactiveCommand.getPrice());
			if (enoughPoints) {
				event.sendMessageWithMention(" успешно заказал \"" + interactiveCommand.getResponse() + "\"");
			} else {
				event.sendMessageWithMention("Недостаточно IQ для заказа");
			}
			return true;
		}
		return false;
	}

	@Transactional
	public InteractiveCommand getInteractiveCommandByCode(String code) {
		if (code.startsWith("!")) {
			code = code.substring(1);
		}

		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<InteractiveCommand> criteria = builder.createQuery(InteractiveCommand.class);
		Root<InteractiveCommand> root = criteria.from(InteractiveCommand.class);
		criteria.select(root);
		criteria.where(builder.equal(root.get("code"), code.toLowerCase()));
		try {
			return entityManager.createQuery(criteria).getSingleResult();
		} catch (PersistenceException ex) {
			return null;
		}
	}
}

