package net.greyferret.ferretbot.service;

import net.greyferret.ferretbot.entity.Prize;
import net.greyferret.ferretbot.entity.PrizeDefault;
import net.greyferret.ferretbot.entity.PrizePool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Service
public class PrizePoolService {
	private static final Logger logger = LogManager.getLogger(PrizePoolService.class);

	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Transactional
	public Prize rollPrize() {
		Random rand = new Random();
		HashMap<Integer, PrizePool> prizePoolMap = getEntireCurrentPrizePool();
		Prize prize = null;
		logger.info("Rolling raffle!");

		for (Integer i : prizePoolMap.keySet()) {
			PrizePool prizePool = prizePoolMap.get(i);
			if (prize == null) {
				double randDouble = rand.nextDouble();
				boolean rollResult = randDouble < (prizePool.getCurrentChance() / 100);
				logger.info("Rolled (" + rollResult + ") PrizePool #" + i + ": " + randDouble + " against " + prizePool.getCurrentChance() / 100);
				if (rollResult) {
					logger.info("Win! Current chance was: " + prizePool.getCurrentChance() / 100);
					prize = selectPrize(prizePool);
					resetChance(prizePool);
				} else {
					increaseChance(prizePool);
				}
			} else {
				increaseChance(prizePool);
			}
		}
		return prize;
	}

	@Transactional
	protected Prize selectPrize(PrizePool prizePool) {
		logger.info("Selecting prize...");
		Prize res = null;
		Random rand = new Random();
		double randDouble = rand.nextDouble();
		logger.info("Rolled: " + randDouble);
		for (Prize prize : prizePool.getPrizes()) {
			res = prize;
			if (prize.getChanceInsidePool() < randDouble) {
				logger.info("Selected " + prize.getName());
				break;
			} else {
				randDouble = randDouble - prize.getChanceInsidePool();
			}
		}
		removePrizeFromPool(prizePool, res);
		return res;
	}

	@Transactional
	protected void resetChance(PrizePool prizePool) {
		logger.info("Resetting chance for PrizePool " + prizePool.getType());
		setChance(prizePool, prizePool.getChance());
	}

	@Transactional
	protected void removePrizeFromPool(PrizePool pool, Prize prize) {
		logger.info("Removing prize from pool");

		ArrayList<Prize> prizes = pool.getPrizes();
		ArrayList<Prize> newPrizes = new ArrayList<>();
		for (Prize p : prizes) {
			if (p.getName().equalsIgnoreCase(prize.getName())) {
				if (p.getAmount() > 1) {
					p.setAmount(p.getAmount() - 1);
					newPrizes.add(p);
				}
			} else {
				newPrizes.add(p);
			}
		}

		pool.setPrizes(Prize.calcChancesInsideListOfPrizes(newPrizes));
		entityManager.merge(pool);
		entityManager.flush();
	}

	@Transactional
	protected void increaseChance(PrizePool prizePool) {
		Double chance = prizePool.getCurrentChance() + prizePool.getChance();
		setChance(prizePool, chance);

	}

	@Transactional
	protected void setChance(PrizePool prizePool, double chance) {
		prizePool.setCurrentChance(chance);
		entityManager.merge(prizePool);
		entityManager.flush();
	}

	@Transactional
	protected HashMap<Integer, PrizePool> getEntireCurrentPrizePool() {
		HashMap<Integer, PrizePool> entireCurrentPrizePool = new HashMap<>();

		for (int i = 0; i < PrizeDefault.amountOfTypes; i++) {
			CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
			CriteriaQuery<PrizePool> criteria = builder.createQuery(PrizePool.class);
			Root<PrizePool> root = criteria.from(PrizePool.class);
			criteria.select(root);
			criteria.where(builder.equal(root.get("type"), i));

			List<PrizePool> resultList = entityManager.createQuery(criteria).getResultList();
			PrizePool res;
			if (resultList == null || resultList.size() == 0) {
				res = restorePrizePoolForType(i);
			} else {
				res = resultList.get(0);
			}
			entireCurrentPrizePool.put(i, res);
		}
		return entireCurrentPrizePool;
	}

	@Transactional
	protected PrizePool restorePrizePoolForType(int type) {
		logger.info("Restoring presents for type " + type);
		PrizePool prizePool = PrizeDefault.getPrizePoolForType(type);
		if (prizePool != null) {
			entityManager.persist(prizePool);
			entityManager.flush();
		}
		return prizePool;
	}
}

