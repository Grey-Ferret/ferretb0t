package dev.greyferret.ferretbot.pubsub.domain;

import dev.greyferret.ferretbot.pubsub.enums.PubSubType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PubSubRequest {

    private PubSubType type;

    private String nonce;

    private Map<String, Object> data = new HashMap<>();

}
