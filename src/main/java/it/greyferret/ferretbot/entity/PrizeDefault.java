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
		prizes.add(new Prize("300 IQ", 3, type));
		prizes.add(new Prize("500 IQ", 2, type));
		prizes.add(new Prize("1000 IQ", 1, type));
		prizePool = new PrizePool(type, prizes, 0.041);
		res.put(type, prizePool);

		type = 1;
		prizes = new ArrayList<>();
		prizes.add(new Prize("Случайное простое желание", 8, type));
		prizes.add(new Prize("Случайное сложное желание", 4, type));
		prizes.add(new Prize("Первый Spec-час", 2, type));
		prizes.add(new Prize("Заказной стрим на 2 часа", 1, type));
		prizePool = new PrizePool(type, prizes, 0.233);
		res.put(type, prizePool);

		type = 2;
		prizes = new ArrayList<>();
		prizes.add(new Prize("Платная подписка на канал", 2, type));
		prizes.add(new Prize("500 руб.", 2, type));
		prizePool = new PrizePool(type, prizes, 0.019);
		res.put(type, prizePool);

		return res;
	}

	public static PrizePool getPrizePoolForType(int type) {
		HashMap<Integer, PrizePool> allPrizes = getAllPrizes();
		return allPrizes.get(type);
	}
}
