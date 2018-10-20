package it.greyferret.ferretbot.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class PrizeDefault {
	public static final int amountOfTypes = 3;

	private static HashMap<Integer, PrizePool> getAllPrizes() {
		HashMap<Integer, PrizePool> res = new HashMap<>();
		ArrayList<Prize> prizes;
		PrizePool prizePool;
		int type;

		type = 0;
		prizes = new ArrayList<>();
		prizes.add(new Prize("300 поинтов", 3));
		prizes.add(new Prize("500 поинтов", 2));
		prizes.add(new Prize("1000 поинтов", 1));
		prizePool = new PrizePool(type, prizes, 0.055);
		res.put(type, prizePool);

		type = 1;
		prizes = new ArrayList<>();
		prizes.add(new Prize("Случайное простое желание", 8));
		prizes.add(new Prize("Случайное элитное желание", 4));
		prizes.add(new Prize("Сыграть дуэтом", 2));
		prizes.add(new Prize("Заказной стрим на 2 часа", 1));
		prizePool = new PrizePool(type, prizes, 0.318);
		res.put(type, prizePool);

		type = 2;
		prizes = new ArrayList<>();
		prizes.add(new Prize("Платная подписка на канал", 2));
		prizes.add(new Prize("Маунт для BRR", 4));
		prizes.add(new Prize("500 руб.", 2));
		prizePool = new PrizePool(type, prizes, 0.096);
		res.put(type, prizePool);

		return res;
	}

	public static PrizePool getPrizePoolForType(int type) {
		HashMap<Integer, PrizePool> allPrizes = getAllPrizes();
		return allPrizes.get(type);
	}
}
