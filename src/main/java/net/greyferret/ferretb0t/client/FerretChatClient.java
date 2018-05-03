package net.greyferret.ferretb0t.client;

import net.greyferret.ferretb0t.config.ApplicationConfig;
import net.greyferret.ferretb0t.config.ChatConfig;
import net.greyferret.ferretb0t.config.Messages;
import net.greyferret.ferretb0t.listener.CustomizedDefaultEventListener;
import net.greyferret.ferretb0t.listener.FerretB0tChatListener;
import net.greyferret.ferretb0t.util.FerretB0tUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.defaults.DefaultBuilder;
import org.kitteh.irc.client.library.defaults.DefaultClient;
import org.kitteh.irc.client.library.defaults.DefaultEventListener;
import org.kitteh.irc.client.library.defaults.feature.DefaultActorTracker;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.util.Sanity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component("FerretChatClient")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class FerretChatClient extends DefaultClient {
	private static final Logger logger = LogManager.getLogger();
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
				.name("FerretB0t")
				.build();
		FerretB0tChatListener ferretB0tChatListener = context.getBean(FerretB0tChatListener.class, client);
		client.addChannel(chatConfig.getChannelWithHashTag());
		client.getActorTracker().trackChannel(chatConfig.getChannelWithHashTag());
		for (Object listener : client.getEventManager().getRegisteredEventListeners()) {
			if (listener instanceof DefaultEventListener) {
				client.getEventManager().unregisterEventListener(listener);
				CustomizedDefaultEventListener customizedDefaultEventListener = context.getBean(CustomizedDefaultEventListener.class, client);
				client.getEventManager().registerEventListener(customizedDefaultEventListener);
			}
		}
		client.getEventManager().registerEventListener(ferretB0tChatListener);
	}

	public void connect() {
		client.connect();
		client.sendMessage(chatConfig.getChannelWithHashTag(), Messages.HELLO_MESSAGE);
	}

	public void sendMessage(String text) {
		logger.info(text);
		if (!applicationConfig.isDebug())
			client.sendMessage(chatConfig.getChannelWithHashTag(), text);
	}

	@Nonnull
	@Override
	public Optional<Channel> getChannel(@Nonnull String name) {
		return this.client.getActorTracker().getTrackedChannel(Sanity.nullCheck(name, "Channel name cannot be null"));
	}

	@Bean("getViewers")
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public List<String> getViewers() {
		String channelName = chatConfig.getChannelWithHashTag();

		Optional<Channel> _channel = client.getChannel(channelName);
		if (_channel.isPresent()) {
			Channel channel = _channel.get();
			return channel.getNicknames();
		} else {
			logger.warn("No channel was found");
			client.removeChannel(channelName);
			client.addChannel(channelName);
			if (client.getChannel(channelName).isPresent()) {
				logger.info("remove>add fixed it");
			} else {
				logger.info("remove>add DIDNT FIX IT");
				FerretB0tUtils.fixClient(client, channelName);
			}
			return new ArrayList<>();
		}
	}
}
