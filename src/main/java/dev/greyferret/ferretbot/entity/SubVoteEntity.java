package dev.greyferret.ferretbot.entity;

import net.dv8tion.jda.api.entities.Emote;

import java.util.ArrayList;

public class SubVoteEntity {
	private String message;
	private ArrayList<Emote> emotes;

	public SubVoteEntity(String res, ArrayList<Emote> selectedEmotes) {
		this.message = res;
		this.emotes = selectedEmotes;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ArrayList<Emote> getEmotes() {
		return emotes;
	}

	public void setEmotes(ArrayList<Emote> emotes) {
		this.emotes = emotes;
	}

	@Override
	public String toString() {
		return "SubVoteEntity{" +
				"message='" + message + '\'' +
				", emotes=" + emotes +
				'}';
	}
}
