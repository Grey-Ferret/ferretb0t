package dev.greyferret.ferretbot.processor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.greyferret.ferretbot.config.BotConfig;
import dev.greyferret.ferretbot.config.WebsocketConfig;
import dev.greyferret.ferretbot.entity.json.twitch.token.Token;
import dev.greyferret.ferretbot.pubsub.TwitchPubSub;
import dev.greyferret.ferretbot.service.DynamicPropertyService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledThreadPoolExecutor;

@Component
@EnableConfigurationProperties({WebsocketConfig.class})
@Log4j2
public class WebSocketProcessor implements Runnable, ApplicationListener<ContextStartedEvent> {
	@Autowired
	private ApiProcessor apiProcessor;
	@Autowired
	private BotConfig botConfig;
	@Autowired
	private BeanFactory beanFactory;
	@Autowired
	private DynamicPropertyService dynamicPropertyService;

	@Override
	public void run() {
		TwitchPubSub twitchPubSub = beanFactory.getBean(TwitchPubSub.class, new ScheduledThreadPoolExecutor(99));
		twitchPubSub.connect();
		Token token = apiProcessor.refreshAccessToken(dynamicPropertyService.getRefreshToken());
		twitchPubSub.listenForPointsEvents(token.getAccessToken(), token.getRefreshToken(), apiProcessor.getStreamerId());
	}

	private MessageConverter createGsonHttpMessageConverter() {
		Gson gson = new GsonBuilder()
				.excludeFieldsWithoutExposeAnnotation()
				.setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
				.create();
		return new MessageConverter() {
			@Override
			public Object fromMessage(Message<?> message, Class<?> targetClass) {
				return gson.fromJson(message.getPayload().toString(), targetClass);
			}

			@Override
			public Message<?> toMessage(Object payload, MessageHeaders headers) {
				GenericMessage<String> result = new GenericMessage<>(gson.toJson(payload));
				return result;
			}
		};
	}

	@Override
	public void onApplicationEvent(ContextStartedEvent contextStartedEvent) {
		if (botConfig.isTwitchPointsOn()) {
			Thread thread = new Thread(this);
			thread.setName("WebSocket Thread");
			thread.start();
			log.info(thread.getName() + " started");
		} else {
			log.info("WebSocket off");
		}
		this.run();
	}
}
