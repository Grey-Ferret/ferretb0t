package it.greyferret.ferretbot.entity;

public class Prize {
	private String name;
	private int amount;
	private int type;

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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	private Prize() {
	}

	public Prize(String name, int amount, int type) {
		this.name = name;
		this.amount = amount;
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Prize)) return false;

		Prize prize = (Prize) o;

		if (type != prize.type) return false;
		return name.equals(prize.name);
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + type;
		return result;
	}
}
