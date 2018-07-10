package net.greyferret.ferretbot.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

public class RaffleViewer implements Serializable {
	private String login;
	private Queue<Calendar> messageTimes;
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

	public ArrayList<Calendar> getMessageTimes() {
		return new ArrayList<>(messageTimes);
	}

	public void addMessageTime(Calendar calendar) {
		this.messageTimes.add(calendar);
		if (this.messageTimes.size() > amountOfTimes) {
			this.messageTimes.poll();
		}
	}

	public boolean ifSuitable() {
		Calendar suitablePeriod = Calendar.getInstance();
		suitablePeriod.add(Calendar.MINUTE, -30);
		boolean res = true;
		for (Calendar c : messageTimes) {
			if (c.before(suitablePeriod)) {
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
