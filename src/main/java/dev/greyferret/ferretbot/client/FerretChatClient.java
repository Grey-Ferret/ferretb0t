package dev.greyferret.ferretbot.client;

import dev.greyferret.ferretbot.config.ApplicationConfig;
import dev.greyferret.ferretbot.config.ChatConfig;
import dev.greyferret.ferretbot.listener.FerretBotChatListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.defaults.DefaultBuilder;
import org.kitteh.irc.client.library.defaults.DefaultClient;
import org.kitteh.irc.client.library.element.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component("FerretChatClient")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@EnableConfigurationProperties({ChatConfig.class, ApplicationConfig.class})
public class FerretChatClient extends DefaultClient {
	private static final Logger logger = LogManager.getLogger(FerretChatClient.class);

	@Autowired
	private ChatConfig chatConfig;
	@Autowired
	private ApplicationContext context;
	@Autowired
	private ApplicationConfig applicationConfig;

	private DefaultClient client;

	private FerretChatClient() {
	}

	@PostConstruct
	private void postConstruct() {
		DefaultBuilder defaultBuilder = (DefaultBuilder) Client.builder();
		client = (DefaultClient) defaultBuilder.nick(chatConfig.getLogin())
				.serverPassword(chatConfig.getPassword())
				.serverHost("irc.twitch.tv")
				.name("FerretBotClient")
				.build();
		FerretBotChatListener ferretBotChatListener = context.getBean(FerretBotChatListener.class, client);
		client.addChannel(chatConfig.getChannelWithHashTag());
		client.getEventManager().registerEventListener(ferretBotChatListener);
	}

	public void connect() {
		client.connect();
//		client.sendMessage(chatConfig.getChannelWithHashTag(), Messages.HELLO_MESSAGE);
	}

	public void sendMessage(@Nonnull String text) {
		if (StringUtils.isNotBlank(text)) {
			logger.info(text);
			if (!applicationConfig.isDebug())
				sendMessage(chatConfig.getChannelWithHashTag(), text);
		}
	}

	public void sendMessageMe(@Nonnull String text) {
		if (StringUtils.isNotBlank(text)) {
			text = "/me " + text;
			logger.info(text);
			if (!applicationConfig.isDebug())
				sendMessage(chatConfig.getChannelWithHashTag(), text);
		}
	}

	@Override
	public void sendMessage(@Nonnull String target, @Nonnull String message) {
		if (StringUtils.isNotBlank(target) && StringUtils.isNotBlank(message)) {
			if (!target.startsWith("#")) //Fix for Twitch channel
				target = "#" + target;
			client.sendMessage(target, message);
		}
	}

	@Nonnull
	@Override
	public Optional<Channel> getChannel(@Nonnull String name) {
		if (StringUtils.isNotBlank(name)) {
			return this.client.getActorTracker().getTrackedChannel(name);
		}
		return Optional.empty();
	}

	@Bean("getViewers")
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public List<String> getViewers() {
		String channelName = chatConfig.getChannelWithHashTag();

		Optional<Channel> channel = client.getChannel(channelName);
		if (channel.isPresent()) {
			return channel.get().getNicknames();
		} else {
			logger.warn("No channel was found!");
			return new ArrayList<>();
		}
	}
}
