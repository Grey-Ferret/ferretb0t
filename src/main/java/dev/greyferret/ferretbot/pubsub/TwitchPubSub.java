package dev.greyferret.ferretbot.pubsub;

import com.google.gson.Gson;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import dev.greyferret.ferretbot.entity.json.twitch.token.Token;
import dev.greyferret.ferretbot.processor.ApiProcessor;
import dev.greyferret.ferretbot.processor.FerretChatProcessor;
import dev.greyferret.ferretbot.pubsub.domain.PubSubRequest;
import dev.greyferret.ferretbot.pubsub.domain.PubSubResponse;
import dev.greyferret.ferretbot.pubsub.domain.redeem.PointsRedeemData;
import dev.greyferret.ferretbot.pubsub.domain.redeem.Redemption;
import dev.greyferret.ferretbot.pubsub.enums.PubSubType;
import dev.greyferret.ferretbot.pubsub.enums.TMIConnectionState;
import dev.greyferret.ferretbot.service.DynamicPropertyService;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
public class TwitchPubSub implements AutoCloseable {
	@Autowired
	private ApiProcessor apiProcessor;
	@Autowired
	private DynamicPropertyService dynamicPropertyService;
	@Autowired
	private FerretChatProcessor ferretChatProcessor;

	private final static String webSocketServer = "wss://pubsub-edge.twitch.tv:443";

	@Setter(AccessLevel.NONE)
	private WebSocket webSocket;

	private TMIConnectionState connectionState = TMIConnectionState.DISCONNECTED;

	protected final Thread queueThread;

	protected final Thread heartbeatThread;

	protected boolean isClosed = false;

	protected final ArrayBlockingQueue<String> commandQueue = new ArrayBlockingQueue<>(200);

	protected final List<PubSubRequest> subscribedTopics = new ArrayList<>();

	protected long lastPing = ZonedDateTime.now().toInstant().toEpochMilli() - 15 * 1000;

	protected long lastPong = ZonedDateTime.now().toInstant().toEpochMilli();

	protected final ScheduledThreadPoolExecutor taskExecutor = new ScheduledThreadPoolExecutor(99);

	protected PubSubSubscription pointsPubSubSubscription;

	public TwitchPubSub() {
		// connect
		this.connect();

		// Run heartbeat every 4 minutes
		this.heartbeatThread = new Thread(() -> {
			if (isClosed)
				return;

			PubSubRequest request = new PubSubRequest();
			request.setType(PubSubType.PING);
			sendCommand(new Gson().toJson(request));

			lastPing = ZonedDateTime.now().toInstant().toEpochMilli();
		});

		taskExecutor.scheduleAtFixedRate(this.heartbeatThread, 0, 15L, TimeUnit.SECONDS);
		// queue command worker
		this.queueThread = new Thread(() -> {
			while (!isClosed) {
				try {
					// check for missing pong response
					if (ZonedDateTime.now().toInstant().toEpochMilli() >= lastPing + 10000 && lastPong < lastPing) {
						log.warn("PubSub: Didn't receive a PONG response in time, reconnecting to obtain a connection to a different server.");
						reconnect();
					}

					// If connected, send one message from the queue
					String command = commandQueue.poll(1000L, TimeUnit.MILLISECONDS);
					if (command != null) {
						if (connectionState.equals(TMIConnectionState.CONNECTED)) {
							sendCommand(command);
							// Logging
							log.debug("Processed command from queue: [{}].", command);
						}
					}
				} catch (Exception ex) {
					log.error("PubSub: Unexpected error in worker thread", ex);
				}
			}
		});

		taskExecutor.schedule(this.queueThread, 1L, TimeUnit.MILLISECONDS);
		log.debug("PubSub: Started Queue Worker Thread");
	}

	@Synchronized
	public void connect() {
		if (connectionState.equals(TMIConnectionState.DISCONNECTED) || connectionState.equals(TMIConnectionState.RECONNECTING)) {
			try {
				// Change Connection State
				connectionState = TMIConnectionState.CONNECTING;

				// Recreate Socket if state does not equal CREATED
				createWebSocket();

				// Connect to IRC WebSocket
				this.webSocket.connect();
			} catch (Exception ex) {
				log.error("PubSub: Connection to Twitch PubSub failed: {} - Retrying ...", ex.getMessage());

				// Sleep 1 second before trying to reconnect
				try {
					TimeUnit.SECONDS.sleep(1L);
				} catch (Exception ignored) {

				} finally {
					// reconnect
					reconnect();
				}
			}
		}
	}

	@Synchronized
	public void disconnect() {
		if (connectionState.equals(TMIConnectionState.CONNECTED)) {
			connectionState = TMIConnectionState.DISCONNECTING;
		}

		connectionState = TMIConnectionState.DISCONNECTED;

		// CleanUp
		this.webSocket.clearListeners();
		this.webSocket.disconnect();
		this.webSocket = null;
	}

	@Synchronized
	public void reconnect() {
		connectionState = TMIConnectionState.RECONNECTING;
		disconnect();
		connect();
	}

	private void createWebSocket() {
		try {
			// WebSocket
			this.webSocket = new WebSocketFactory().createSocket(webSocketServer);

			// WebSocket Listeners
			this.webSocket.clearListeners();
			this.webSocket.addListener(new WebSocketAdapter() {

				@Override
				public void onConnected(WebSocket ws, Map<String, List<String>> headers) {
					log.info("Connecting to Twitch PubSub {}", webSocketServer);

					// Connection Success
					connectionState = TMIConnectionState.CONNECTED;

					log.info("Connected to Twitch PubSub {}", webSocketServer);

					// resubscribe to all topics after disconnect
					subscribedTopics.forEach(topic -> listenOnTopic(topic));
				}

				@Override
				public void onTextMessage(WebSocket ws, String text) {
					try {
						// parse message
						PubSubResponse message = new Gson().fromJson(text, PubSubResponse.class);
						if (message.getType().equals(PubSubType.MESSAGE)) {
							log.info("Received WebSocketMessage: " + text);

							String topic = message.getData().getTopic();

							// Handle Messages
//							if (topic.startsWith("channel-bits-events-v1")) {
//							} else if (topic.startsWith("channel-subscribe-events-v1")) {
//							} else if (topic.startsWith("channel-commerce-events-v1")) {
//							} else
							if (topic.startsWith("channel-points-channel-v1")) {
								String pointsMessage = message.getData().getMessage();
								PointsRedeemData pointsRedeemData = new Gson().fromJson(pointsMessage, PointsRedeemData.class);
								Redemption redemption = pointsRedeemData.getData().getRedemption();
								if (redemption.getReward().getTitle().equals("Бан на 4 часа")) {
									new Thread(() -> {
										try {
											Thread.sleep(10 * 1000);
										} catch (InterruptedException e) {
											log.error(e);
										}
										ferretChatProcessor.sendMessage("/timeout " + redemption.getUser().getLogin() + " 14400");
									}).start();
								}
							}
						} else if (message.getType().equals(PubSubType.RESPONSE)) {
							// topic subscription success or failed, response to listen command
							// System.out.println(message.toString());
							if (message.getError().length() > 0) {
								if (message.getError().equalsIgnoreCase("ERR_BADAUTH")) {
									log.error("PubSub: You used a invalid oauth token to subscribe to the topic. Please use a token that is authorized for the specified channel.");
									resubscribeToTopic(pointsPubSubSubscription);
								} else {
									log.error("PubSub: Failed to subscribe to topic - [" + message.getError() + "]");
								}
							}

						} else if (message.getType().equals(PubSubType.PONG)) {
							lastPong = ZonedDateTime.now().toInstant().toEpochMilli();
						} else if (message.getType().equals(PubSubType.RECONNECT)) {
							log.warn("PubSub: Server instance we're connected to will go down for maintenance soon, reconnecting to obtain a new connection!");
							reconnect();
						} else {
							// unknown message
							log.debug("PubSub: Unknown Message Type: " + message.toString());
						}
					} catch (Exception ex) {
						log.warn("PubSub: Unparsable Message: " + text + " - [" + ex.getMessage() + "]");
						log.error(ex);
					}
				}

				@Override
				public void onDisconnected(WebSocket websocket,
				                           WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame,
				                           boolean closedByServer) {
					if (!connectionState.equals(TMIConnectionState.DISCONNECTING)) {
						log.info("Connection to Twitch PubSub lost (WebSocket)! Retrying ...");

						// connection lost - reconnecting
						reconnect();
					} else {
						connectionState = TMIConnectionState.DISCONNECTED;
						log.info("Disconnected from Twitch PubSub (WebSocket)!");
					}
				}

			});


		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}

	private void updateAccessToken() {
	}

	private void sendCommand(String command) {
		// will send command if connection has been established
		if (connectionState.equals(TMIConnectionState.CONNECTED) || connectionState.equals(TMIConnectionState.CONNECTING)) {
			// command will be uppercase.
			this.webSocket.sendText(command);
		} else {
			log.warn("Can't send IRC-WS Command [{}]", command);
		}
	}

	private void queueRequest(PubSubRequest request) {
		commandQueue.add(new Gson().toJson(request));
	}

	public PubSubSubscription listenOnTopic(PubSubRequest request) {
		queueRequest(request);
		subscribedTopics.add(request);
		return new PubSubSubscription(request);
	}

	public void resubscribeToTopic(PubSubSubscription subscription) {
		log.info("Refreshing Access Token for WebSocket");
		Token token = apiProcessor.refreshAccessToken(dynamicPropertyService.getRefreshToken());

		PubSubRequest request = subscription.getRequest();
		if (request.getType() != PubSubType.LISTEN) {
			log.warn("Cannot unsubscribe using request with unexpected type: {}", request.getType());
			return;
		}
		int topicIndex = subscribedTopics.indexOf(request);
		if (topicIndex == -1) {
			log.warn("Not subscribed to topic: {}", request);
			return;
		}
		subscribedTopics.remove(topicIndex);

		// use data from original request and send UNLISTEN
		PubSubRequest unlistenRequest = new PubSubRequest();
		unlistenRequest.setType(PubSubType.UNLISTEN);
		unlistenRequest.setNonce(request.getNonce());
		Map<String, Object> data = request.getData();
		data.put("auth_token", token.getAccessToken());
		unlistenRequest.setData(data);

		queueRequest(unlistenRequest);

		listenForPointsEvents(unlistenRequest);
	}

	public PubSubSubscription listenForPointsEvents(String accessToken, String refreshToken, String userId) {
		PubSubRequest request = new PubSubRequest();
		request.setNonce(UUID.randomUUID().toString());
		request.getData().put("auth_token", dynamicPropertyService.getAccessToken());
		request.getData().put("topics", Collections.singletonList("channel-points-channel-v1." + userId));
		pointsPubSubSubscription = listenForPointsEvents(request);
		return pointsPubSubSubscription;
	}

	public PubSubSubscription listenForPointsEvents(PubSubRequest request) {
		request.setType(PubSubType.LISTEN);
		pointsPubSubSubscription = listenOnTopic(request);
		return pointsPubSubSubscription;
	}

	public void close() {
		if (!isClosed) {
			isClosed = true;
			taskExecutor.remove(this.heartbeatThread);
			taskExecutor.remove(this.queueThread);
			disconnect();
		}
	}

}
