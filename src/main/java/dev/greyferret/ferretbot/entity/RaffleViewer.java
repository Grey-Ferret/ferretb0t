package dev.greyferret.ferretbot.entity;

import dev.greyferret.ferretbot.config.SpringConfig;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class RaffleViewer implements Serializable {
	private String login;
	private Queue<ZonedDateTime> messageTimes;
	private final int amountOfTimes = 2;

	public RaffleViewer(String login) {
		this.login = login;
		messageTimes = new LinkedList<>();
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public ArrayList<ZonedDateTime> getMessageTimes() {
		return new ArrayList<>(messageTimes);
	}

	public void addMessageTime(ZonedDateTime zdt) {
		this.messageTimes.add(zdt);
		if (this.messageTimes.size() > amountOfTimes) {
			this.messageTimes.poll();
		}
	}

	public boolean ifSuitable() {
		if (this.messageTimes.size() < amountOfTimes) {
			return false;
		}
		ZonedDateTime suitablePeriod = ZonedDateTime.now(SpringConfig.getZoneId());
		suitablePeriod.minusMinutes(30);
		boolean res = true;
		for (ZonedDateTime zdt : messageTimes) {
			if (zdt.isBefore(suitablePeriod)) {
				res = false;
			}
		}
		return res;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof RaffleViewer)) return false;

		RaffleViewer that = (RaffleViewer) o;

		return login != null ? login.equals(that.login) : that.login == null;
	}

	@Override
	public int hashCode() {
		return login != null ? login.hashCode() : 0;
	}
}
