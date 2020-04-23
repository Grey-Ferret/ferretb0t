package dev.greyferret.ferretbot.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by GreyFerret on 23.04.2020.
 */
@Getter
@ConfigurationProperties(prefix = "viewers")
public class ViewersConfig {
	private Set<String> raffleIgnore;

	public void setRaffleIgnore(List<String> raffleIgnore) {
		this.raffleIgnore = new LinkedHashSet<>();
		for(String s : raffleIgnore) {
			this.raffleIgnore.add(s.toLowerCase());
		}
	}
}
