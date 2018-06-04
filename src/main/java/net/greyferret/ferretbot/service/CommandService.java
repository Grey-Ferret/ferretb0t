package net.greyferret.ferretbot.service;

import net.greyferret.ferretbot.entity.Command;
import net.greyferret.ferretbot.util.FerretBotUtils;
import net.greyferret.ferretbot.wrapper.ChannelMessageEventWrapper;
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
import java.util.Set;

@Service
public class CommandService {
	private static final Logger logger = LogManager.getLogger(CommandService.class);

	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private EntityManagerFactory entityManagerFactory;

	private static void findAndSendMessageWithMention(Command command, ChannelMessageEventWrapper event) {
		String[] split = StringUtils.split(FerretBotUtils.buildMessage(event.getMessage()), ' ');
		if (split.length == 1)
			event.sendMessageWithMention(command.getResponse());
		else
			event.sendMessageWithMention(command.getResponse(), split[split.length - 1]);
	}

	public static void proceedTextCommand(Command command, ChannelMessageEventWrapper event) {
		findAndSendMessageWithMention(command, event);
	}

	@Transactional
	public boolean proceedTextCommand(String code, ChannelMessageEventWrapper event) {
		if (code.startsWith("!"))
			code = code.substring(1);

		for (Command command : getAllCommands()) {
			if (command.getAllCodes().contains(code)) {
				proceedTextCommand(command, event);
				return true;
			}
		}
		return false;
	}

	@Transactional
	public Set<Command> getAllCommands() {
		Set<Command> all = new HashSet<>();

		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Command> criteria = builder.createQuery(Command.class);
		Root<Command> root = criteria.from(Command.class);
		criteria.select(root);

		List<Command> commandList = entityManager.createQuery(criteria).getResultList();
		all.addAll(commandList);
		return all;
	}
}

