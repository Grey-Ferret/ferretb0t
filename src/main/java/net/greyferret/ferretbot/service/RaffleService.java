package net.greyferret.ferretbot.service;

import net.greyferret.ferretbot.entity.RaffleDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

@Service
public class RaffleService {
	private static final Logger logger = LogManager.getLogger(RaffleService.class);

	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Transactional
	public RaffleDate get(int dateId) {
		RaffleDate raffleDate = entityManager.find(RaffleDate.class, dateId);
		if (raffleDate == null) {
			raffleDate = new RaffleDate(dateId);
			entityManager.persist(raffleDate);
			entityManager.flush();
		}
		return raffleDate;
	}

	@Transactional
	public void put(RaffleDate raffleDate) {
		entityManager.merge(raffleDate);
		entityManager.flush();
	}
}

