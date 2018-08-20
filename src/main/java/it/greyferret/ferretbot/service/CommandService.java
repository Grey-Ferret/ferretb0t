package it.greyferret.ferretbot.service;

import it.greyferret.ferretbot.entity.Command;
import it.greyferret.ferretbot.util.FerretBotUtils;
import it.greyferret.ferretbot.wrapper.ChannelMessageEventWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.HashSet;
import java.util.List;

@Service
public class CommandService {
	private static final Logger logger = LogManager.getLogger(CommandService.class);

	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Transactional
	public String addOrEditCommand(@Nonnull String code, @Nonnull String text) {
		if (StringUtils.isNotBlank(code) && StringUtils.isNotBlank(text)) {
			if (code.startsWith("!")) {
				code = code.substring(1);
			}
			Command command = this.getCommandByCode(code);
			if (command != null) {
				command.setResponse(text);
				entityManager.merge(command);
				entityManager.flush();
				return "команда " + code + " успешно обновлена!";
			} else {
				command = new Command();
				HashSet<String> codes = new HashSet<>();
				codes.add(code);
				command.setCodes(codes);
				command.setResponse(text);
				entityManager.persist(command);
				entityManager.flush();
				return "команда " + code + " успешно добавлена!";
			}
		}
		return "";
	}

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

		Command command = getCommandByCode(code);
		if (command != null) {
			proceedTextCommand(command, event);
			return true;
		}
		return false;
	}

	@Transactional
	public Command getCommandByCode(String code) {
		HashSet<Command> commands = getLikeAllCommands(code);
		for (Command command : commands) {
			for (String t : command.getCodes()) {
				if (t.equalsIgnoreCase(code))
					return command;
			}
		}
		return null;
	}

	@Transactional
	protected HashSet<Command> getLikeAllCommands(String code) {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Command> criteria = builder.createQuery(Command.class);
		Root<Command> root = criteria.from(Command.class);
		criteria.select(root);

		criteria.where(builder.like(root.get("codes"), code));

		List<Command> commands = entityManager.createQuery(criteria).getResultList();
		HashSet<Command> res = new HashSet<>();
		res.addAll(commands);
		return res;
	}

	@Transactional
	public HashSet<Command> getAllCommands() {
		HashSet<Command> all = new HashSet<>();

		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Command> criteria = builder.createQuery(Command.class);
		Root<Command> root = criteria.from(Command.class);
		criteria.select(root);

		List<Command> commandList = entityManager.createQuery(criteria).getResultList();
		all.addAll(commandList);
		return all;
	}
}

