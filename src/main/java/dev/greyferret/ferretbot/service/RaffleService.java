package dev.greyferret.ferretbot.service;

import dev.greyferret.ferretbot.entity.Raffle;
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
import java.util.List;

@Service
public class RaffleService {
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Transactional
	public Raffle get(int id) {
		return entityManager.find(Raffle.class, id);
	}

	@Transactional
	public Raffle getLast() {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Raffle> criteria = builder.createQuery(Raffle.class);
		Root<Raffle> root = criteria.from(Raffle.class);
		criteria.orderBy(builder.desc(root.get("date")));
		List<Raffle> raffles = entityManager.createQuery(criteria).setMaxResults(10).getResultList();

		if (raffles == null || raffles.size() == 0)
			return null;
		return raffles.get(0);
	}

	@Transactional
	public void put(Raffle raffle) {
		entityManager.persist(raffle);
		entityManager.flush();
	}
}

