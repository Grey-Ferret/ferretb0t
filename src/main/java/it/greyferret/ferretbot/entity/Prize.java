package it.greyferret.ferretbot.entity;

import java.util.ArrayList;

public class Prize {
	private String name;
	private int amount;
	private double chanceInsidePool;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public double getChanceInsidePool() {
		return chanceInsidePool;
	}

	public void setChanceInsidePool(double chanceInsidePool) {
		this.chanceInsidePool = chanceInsidePool;
	}

	private Prize() {
	}

	public Prize(String name, int amount, double chanceInsidePool) {
		this.name = name;
		this.amount = amount;
		this.chanceInsidePool = chanceInsidePool;
	}

	public static ArrayList<Prize> calcChancesInsideListOfPrizes(ArrayList<Prize> input) {
		double sumChance = 0;
		ArrayList<Prize> res = new ArrayList<>();
		for (Prize p : input) {
			sumChance = sumChance + p.getChanceInsidePool();
		}
		for (Prize p : input) {
			Prize toAdd = p;
			toAdd.setChanceInsidePool(p.getChanceInsidePool() / sumChance);
			res.add(toAdd);
		}
		return res;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Prize)) return false;

		Prize prize = (Prize) o;

		return name != null ? name.equalsIgnoreCase(prize.name) : prize.name == null;
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}
}
