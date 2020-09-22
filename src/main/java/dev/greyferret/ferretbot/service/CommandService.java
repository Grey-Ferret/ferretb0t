package dev.greyferret.ferretbot.service;

import dev.greyferret.ferretbot.entity.Command;
import dev.greyferret.ferretbot.entity.CommandAlias;
import dev.greyferret.ferretbot.util.FerretBotUtils;
import dev.greyferret.ferretbot.wrapper.ChannelMessageEventWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class CommandService {
	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public String addOrEditCommand(@Nonnull String code, @Nonnull String text) {
		if (StringUtils.isNotBlank(code) && StringUtils.isNotBlank(text)) {
			if (code.startsWith("!")) {
				code = code.substring(1);
			}
			CommandAlias commandAlias = entityManager.find(CommandAlias.class, code.toLowerCase());
			if (commandAlias != null) {
				Command command = commandAlias.getCommand();
				command.setResponse(text);
				entityManager.merge(command);
				entityManager.flush();
				return "команда " + code + " успешно обновлена!";
			} else {
				commandAlias = new CommandAlias();
				Command command = new Command();
				command.setResponse(text);
				entityManager.persist(command);
				commandAlias.setId(code.toLowerCase());
				commandAlias.setCommand(command);
				entityManager.persist(commandAlias);
				entityManager.flush();
				return "команда " + code + " успешно добавлена!";
			}
		}
		return "";
	}

	private static void sendCommand(Command command, ChannelMessageEventWrapper event) {
		String[] split = StringUtils.split(FerretBotUtils.buildMessage(event.getMessage()), ' ');
		if (command.getResponseType() == 1)
			event.sendMessage(command.getResponse());
		else if (split.length == 1)
			event.sendMessageWithMention(command.getResponse());
		else
			event.sendMessageWithMention(command.getResponse(), split[split.length - 1]);
	}

	public static void proceedTextCommand(Command command, ChannelMessageEventWrapper event) {
		if (!command.isDisabled()) {
			sendCommand(command, event);
		}
	}

	@Transactional
	public boolean proceedTextCommand(String code, ChannelMessageEventWrapper event) {
		if (code.startsWith("!"))
			code = code.substring(1);

		CommandAlias commandAlias = entityManager.find(CommandAlias.class, code.toLowerCase());
		if (commandAlias != null) {
			proceedTextCommand(commandAlias.getCommand(), event);
			return true;
		}
		return false;
	}

	@Transactional
	public String addCommandAlias(String commandToAddName, String commandAliasName) {
		commandAliasName = commandAliasName.toLowerCase();
		commandToAddName = commandToAddName.toLowerCase();
		String res;
		CommandAlias commandAlias = entityManager.find(CommandAlias.class, commandAliasName);
		if (commandAlias == null) {
			return "Команда " + commandAliasName + " не найдена";
		}
		CommandAlias commandToAdd = entityManager.find(CommandAlias.class, commandToAddName);
		if (commandToAdd == null) {
			commandToAdd = new CommandAlias();
			commandToAdd.setId(commandToAddName.toLowerCase());
			commandToAdd.setCommand(commandAlias.getCommand());
			entityManager.persist(commandToAdd);
			entityManager.flush();
			res = "Успешно добавлено!";
		} else {
			commandToAdd.setCommand(commandAlias.getCommand());
			entityManager.merge(commandToAdd);
			entityManager.flush();
			res = "Успешно обновлено!";
		}

		return res;
	}

	@Transactional
	public String enableCommand(String code) {
		return changeDisableFieldForCommand(code, false);
	}

	@Transactional
	public String disableCommand(String code) {
		return changeDisableFieldForCommand(code, true);
	}

	@Transactional
	protected String changeDisableFieldForCommand(String code, boolean b) {
		if (StringUtils.isNotBlank(code) && StringUtils.isNotBlank(code)) {
			if (code.startsWith("!")) {
				code = code.substring(1);
			}
			CommandAlias commandAlias = entityManager.find(CommandAlias.class, code.toLowerCase());
			if (commandAlias != null) {
				Command command = entityManager.find(Command.class, commandAlias.getCommand().getId());
				command.setDisabled(b);
				entityManager.merge(command);
				entityManager.flush();
				return "команда " + code + " успешно обновлена!";
			} else {
				return "команда " + code + " была не найдена.";
			}
		}
		return "";
	}
}

