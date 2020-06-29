package dev.greyferret.ferretbot.service;

import dev.greyferret.ferretbot.entity.DynamicProperty;
import dev.greyferret.ferretbot.entity.json.twitch.token.Token;
import dev.greyferret.ferretbot.repository.DynamicPropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DynamicPropertyService {
	@Autowired
	private DynamicPropertyRepository dynamicPropertyRepository;

	private final static String ACCESS_TOKEN_NAME = "accessToken";
	private final static String REFRESH_TOKEN_NAME = "refreshToken";

	public String getAccessToken() {
		return getProperty(ACCESS_TOKEN_NAME);
	}

	public String getRefreshToken() {
		return getProperty(REFRESH_TOKEN_NAME);
	}

	public void setAccessToken(String accessToken) {
		setProperty(ACCESS_TOKEN_NAME, accessToken);
	}

	public void setRefreshToken(String refreshToken) {
		setProperty(REFRESH_TOKEN_NAME, refreshToken);
	}

	public String getProperty(String name) {
		Optional<DynamicProperty> accessToken = dynamicPropertyRepository.findById(name);
		return accessToken.isPresent() ? accessToken.get().getValue() : "";
	}

	public void setProperty(String name, String value) {
		DynamicProperty dynamicProperty = new DynamicProperty(name, value);
		dynamicPropertyRepository.save(dynamicProperty);
	}

	public Token getToken() {
		Token token = new Token();
		token.setAccessToken(getAccessToken());
		token.setRefreshToken(getRefreshToken());
		return token;
	}

	public void setToken(Token token) {
		setAccessToken(token.getAccessToken());
		setRefreshToken(token.getRefreshToken());
	}
}
