package it.greyferret.ferretbot.service;

import it.greyferret.ferretbot.entity.Raffle;
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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class RaffleService {
	private static final Logger logger = LogManager.getLogger(RaffleService.class);

	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Transactional
	public Raffle get(int id) {
		return entityManager.find(Raffle.class, id);
	}

	@Transactional
	public Raffle getLastToday() {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Raffle> criteria = builder.createQuery(Raffle.class);
		Root<Raffle> root = criteria.from(Raffle.class);
		criteria.select(root);

		Calendar instance = Calendar.getInstance();
		instance.set(Calendar.HOUR_OF_DAY, 0);
		instance.set(Calendar.MINUTE, 0);
		Date date1 = instance.getTime();
		instance.set(Calendar.HOUR_OF_DAY, 23);
		instance.set(Calendar.MINUTE, 59);
		Date date2 = instance.getTime();

		Predicate d1 = builder.lessThanOrEqualTo(root.get("date"), date2);
		Predicate d2 = builder.greaterThanOrEqualTo(root.get("date"), date1);
		Predicate and = builder.and(d1, d2);

		criteria.where(and);

		List<Raffle> raffles = entityManager.createQuery(criteria).getResultList();
		Raffle res = null;
		if (raffles != null && raffles.size() > 0) {
			for (Raffle raffle : raffles) {
				if (res == null || raffle.getDate().after(res.getDate())) {
					res = raffle;
				}
			}
		}
		return res;
	}

	@Transactional
	public void put(Raffle raffle) {
		entityManager.persist(raffle);
		entityManager.flush();
	}
}

