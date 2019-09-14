package dev.greyferret.ferretbot.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;

@Entity
@Table(name = "prize_pool")
public class PrizePool {
	@Column(name = "type", updatable = false, nullable = false)
	@Id
	private int type;
	@Type(type = "org.hibernate.type.TextType")
	@Column(name = "prizeJson")
	private String prizeJson;
	@Column(name="chance", columnDefinition="DOUBLE PRECISION", updatable = false, nullable = false)
	private BigDecimal chance;
	@Column(name="current_chance", columnDefinition="DOUBLE PRECISION")
	private BigDecimal currentChance;

	private PrizePool() {
	}

	public PrizePool(int type, ArrayList<Prize> prize, double chance) {
		this.type = type;
		this.setPrizes(prize);
		setChance(chance);
		setCurrentChance(chance);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public ArrayList<Prize> getPrizes() {
		Gson gson = new Gson();
		java.lang.reflect.Type type = new TypeToken<ArrayList<Prize>>() {
		}.getType();
		return gson.fromJson(this.prizeJson, type);
	}

	public void setPrizes(ArrayList<Prize> prize) {
		Gson gson = new Gson();
		this.prizeJson = gson.toJson(prize);
	}

	public double getCurrentChance() {
		return currentChance.doubleValue();
	}

	public void setCurrentChance(double currentChance) {
		this.currentChance = new BigDecimal(currentChance);
	}

	public double getChance() {
		return chance.doubleValue();
	}

	public void setChance(double chance) {
		this.chance = new BigDecimal(chance);
	}
}
