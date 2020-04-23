package dev.greyferret.ferretbot.processor;

import dev.greyferret.ferretbot.config.ApplicationConfig;
import dev.greyferret.ferretbot.config.ChatConfig;
import dev.greyferret.ferretbot.config.Messages;
import dev.greyferret.ferretbot.listener.FerretBotChatListener;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.defaults.DefaultBuilder;
import org.kitteh.irc.client.library.defaults.DefaultClient;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.feature.twitch.TwitchSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component("FerretChatClient")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@EnableConfigurationProperties(ChatConfig.class)
@Log4j2
public class FerretChatProcessor implements Runnable, ApplicationListener<ContextStartedEvent> {
	@Autowired
	private ChatConfig chatConfig;
	@Autowired
	private ApplicationContext context;
	@Autowired
	private ApplicationConfig applicationConfig;

	private DefaultClient client;

	private FerretChatProcessor() {
	}

	@PostConstruct
	private void postConstruct() {
		DefaultBuilder defaultBuilder = (DefaultBuilder) Client.builder();
		client = (DefaultClient) defaultBuilder
				.server()
				.host("irc.twitch.tv")
				.port(443)
				.password(chatConfig.getPassword())
				.then()
				.nick(chatConfig.getLogin())
				.build();
		TwitchSupport.addSupport(client);
		FerretBotChatListener ferretBotChatListener = context.getBean(FerretBotChatListener.class, client);
		client.addChannel(chatConfig.getChannelWithHashTag());
		client.getEventManager().registerEventListener(ferretBotChatListener);
	}

	public void sendMessage(String text) {
		if (StringUtils.isNotBlank(text)) {
			log.info(text);
			if (!applicationConfig.isDebug())
				sendMessage(chatConfig.getChannelWithHashTag(), text);
		}
	}

	public void sendMessageMe(@Nonnull String text) {
		if (StringUtils.isNotBlank(text)) {
			text = "/me " + text;
			log.info(text);
			if (!applicationConfig.isDebug())
				sendMessage(chatConfig.getChannelWithHashTag(), text);
		}
	}

	public void sendMessage(@Nonnull String target, @Nonnull String message) {
		if (StringUtils.isNotBlank(target) && StringUtils.isNotBlank(message)) {
			if (!target.startsWith("#")) //Fix for Twitch channel
				target = "#" + target;
			client.sendMessage(target, message);
		}
	}

	@Nonnull
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
			log.warn("No channel was found!");
			return new ArrayList<>();
		}
	}

	@Override
	public void run() {
		client.connect();
		if (!applicationConfig.isDebug()) {
			client.sendMessage(chatConfig.getChannelWithHashTag(), Messages.HELLO_MESSAGE);
		}
	}

	@Override
	public void onApplicationEvent(ContextStartedEvent contextStartedEvent) {
		Thread thread = new Thread(this);
		thread.setName("Ferret Chat Bot Thread");
		thread.start();
		log.info(thread.getName() + " started");
	}
}
