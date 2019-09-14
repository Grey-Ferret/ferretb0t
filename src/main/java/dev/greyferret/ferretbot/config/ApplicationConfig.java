package dev.greyferret.ferretbot.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.ZoneId;

/**
 * Created by GreyFerret on 15/12/2017.
 */
@ConfigurationProperties(prefix = "main")
public class ApplicationConfig {
	private boolean _debug;
	private String _zoneId;

	public void setDebug(String debug) {
		if (StringUtils.isNotBlank(debug) &&
				(debug.equalsIgnoreCase("true") || debug.equalsIgnoreCase("1"))) {
			this._debug = true;
		} else {
			this._debug = false;
		}
	}

	public void setZoneId(String zoneId) {
		this._zoneId = zoneId;
	}

	public ZoneId getZoneId() {
		return ZoneId.of(_zoneId);
	}

	public boolean isDebug() {
		return this._debug;
	}
}
