package it.greyferret.ferretbot.entity;

public class Prize {
	private String name;
	private int amount;

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

	private Prize() {
	}

	public Prize(String name, int amount) {
		this.name = name;
		this.amount = amount;
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
