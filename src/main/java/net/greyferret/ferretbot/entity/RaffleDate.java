package net.greyferret.ferretbot.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;

@Entity
@Table(name = "raffle")
public class RaffleDate implements Serializable {
	@Id
	@Column(name = "date_id")
	private int dateId;
	@org.hibernate.annotations.Type(type = "org.hibernate.type.TextType")
	@Column(name = "list_of_raffles")
	private String mapOfRaffles;

	public RaffleDate() {
	}

	public RaffleDate(int dateId) {
		this.dateId = dateId;
		HashMap<Integer, Boolean> _mapOfRaffles = new HashMap<>();
		for (int i = 0; i < 16; i++) {
			_mapOfRaffles.put(i, false);
		}
		Gson gson = new Gson();
		this.mapOfRaffles = gson.toJson(_mapOfRaffles);
	}

	public int getDateId() {
		return dateId;
	}

	public void setDateId(int dateId) {
		this.dateId = dateId;
	}

	public HashMap<Integer, Boolean> getMapOfRaffles() {
		Gson gson = new Gson();
		Type type = new TypeToken<HashMap<Integer, Boolean>>() {
		}.getType();
		return gson.fromJson(this.mapOfRaffles, type);
	}

	public void setMapOfRaffles(HashMap<Integer, Boolean> mapOfRaffles) {
		Gson gson = new Gson();
		this.mapOfRaffles = gson.toJson(mapOfRaffles);
	}
}
