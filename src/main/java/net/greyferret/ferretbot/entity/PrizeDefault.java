package net.greyferret.ferretbot.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class PrizeDefault {
	public static final int amountOfTypes = 4;

	private static HashMap<Integer, PrizePool> getAllPrizes() {
		HashMap<Integer, PrizePool> res = new HashMap<>();
		ArrayList<Prize> prizes;
		PrizePool prizePool;
		int type;

		type = 0;
		prizes = new ArrayList<>();
		prizes.add(new Prize("300 поинтов", 3, 4));
		prizes.add(new Prize("500 поинтов", 2, 2.5));
		prizes.add(new Prize("1000 поинтов", 1, 1.5));
		prizePool = new PrizePool(type, Prize.calcChancesInsideListOfPrizes(prizes), 0.06);
		res.put(type, prizePool);

		type = 1;
		prizes = new ArrayList<>();
		prizes.add(new Prize("Случайное простое желание", 8, 4));
		prizes.add(new Prize("Случайное элитное желание", 4, 3));
		prizes.add(new Prize("Сыграть дуэтом", 2, 2.25));
		prizes.add(new Prize("Заказной стрим на 2 часа", 1, 1));
		prizePool = new PrizePool(type, Prize.calcChancesInsideListOfPrizes(prizes), 0.354);
		res.put(type, prizePool);

		type = 2;
		prizes = new ArrayList<>();
		prizes.add(new Prize("Платная подписка на канал", 2, 0.75));
		prizes.add(new Prize("Monster Hunter World PC", 1, 0.25));
		prizePool = new PrizePool(type, Prize.calcChancesInsideListOfPrizes(prizes), 0.017);
		res.put(type, prizePool);

		type = 3;
		prizes = new ArrayList<>();
		prizes.add(new Prize("25 руб.", 10, 8.50));
		prizes.add(new Prize("50 руб.", 5, 5));
		prizes.add(new Prize("100 руб.", 4, 3));
		prizes.add(new Prize("500 руб.", 2, 0.3));
		prizePool = new PrizePool(type, Prize.calcChancesInsideListOfPrizes(prizes), 0.677);
		res.put(type, prizePool);

		return res;
	}

	public static PrizePool getPrizePoolForType(int type) {
		HashMap<Integer, PrizePool> allPrizes = getAllPrizes();
		return allPrizes.get(type);
	}
}
