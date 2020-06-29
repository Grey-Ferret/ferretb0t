package dev.greyferret.ferretbot.pubsub;

import dev.greyferret.ferretbot.pubsub.domain.PubSubRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PubSubSubscription {
    @Getter(AccessLevel.PACKAGE)
    private final PubSubRequest request;
}
