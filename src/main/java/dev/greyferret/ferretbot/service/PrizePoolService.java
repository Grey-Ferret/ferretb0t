package dev.greyferret.ferretbot.service;

import dev.greyferret.ferretbot.entity.Prize;
import dev.greyferret.ferretbot.entity.PrizeDefault;
import dev.greyferret.ferretbot.entity.PrizePool;
import lombok.extern.log4j.Log4j2;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Log4j2
public class PrizePoolService {
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Transactional
	public Prize rollPrize() {
		HashMap<Integer, PrizePool> prizePoolMap = getEntireCurrentPrizePool();
		Prize prize = null;
		log.info("Rolling raffle!");

		for (Integer i : prizePoolMap.keySet()) {
			PrizePool prizePool = prizePoolMap.get(i);
			if (prize == null) {
				double randDouble = ThreadLocalRandom.current().nextDouble();
				boolean rollResult = randDouble < (prizePool.getCurrentChance() / 100);
				log.info("Rolled (" + rollResult + ") PrizePool #" + i + ": " + randDouble + " against " + prizePool.getCurrentChance() / 100);
				if (rollResult) {
					log.info("Win! Current chance was: " + prizePool.getCurrentChance() / 100);
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
		log.info("Selecting prize...");
		ArrayList<Prize> allPrizes = new ArrayList<>();
		for (Prize t : prizePool.getPrizes()) {
			for (int i = 0; i < t.getAmount(); i++) {
				allPrizes.add(t);
			}
		}
		Collections.shuffle(allPrizes);
		Prize res = allPrizes.get(0);
		removePrizeFromPool(prizePool, res);
		return res;
	}

	@Transactional
	protected void resetChance(PrizePool prizePool) {
		log.info("Resetting chance for PrizePool " + prizePool.getType());
		setChance(prizePool, prizePool.getChance());
	}

	@Transactional
	protected void removePrizeFromPool(PrizePool pool, Prize prize) {
		log.info("Removing prize from pool");

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

		pool.setPrizes(newPrizes);
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
				if (res.getPrizes() == null || res.getPrizes().size() == 0) {
					res = restorePrizePoolForType(i);
				}
			}
			entireCurrentPrizePool.put(i, res);
		}
		return entireCurrentPrizePool;
	}

	@Transactional
	protected PrizePool restorePrizePoolForType(int type) {
		log.info("Restoring presents for type " + type);
		PrizePool oldPrizePool = entityManager.find(PrizePool.class, type);
		PrizePool prizePool = PrizeDefault.getPrizePoolForType(type);
		if (prizePool != null) {
			if (oldPrizePool == null) {
				entityManager.persist(prizePool);
			} else {
				oldPrizePool.setPrizes(prizePool.getPrizes());
				entityManager.merge(oldPrizePool);
			}
			entityManager.flush();
		}
		return prizePool;
	}
}

