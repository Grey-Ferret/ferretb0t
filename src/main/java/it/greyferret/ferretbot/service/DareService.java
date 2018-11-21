package it.greyferret.ferretbot.service;

import it.greyferret.ferretbot.entity.Dare;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

@Service
public class DareService {
	private static final Logger logger = LogManager.getLogger(DareService.class);

	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Transactional
	public String addOrEditDare(@Nonnull Integer category, @Nonnull String text) {
		if (category != null && StringUtils.isNotBlank(text)) {
			Dare dare = new Dare();
			dare.setCategory(category);
			dare.setText(text);
			entityManager.persist(dare);
			entityManager.flush();
			return "желание успешно добавлено!";
		}
		return "";
	}

	@Transactional
	public String rollDare(@Nonnull Integer category) {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Dare> criteria = builder.createQuery(Dare.class);
		Root<Dare> root = criteria.from(Dare.class);
		criteria.select(root);
		criteria.where(builder.equal(root.get("category"), category));
		List<Dare> resultList = entityManager.createQuery(criteria).getResultList();
		if (resultList != null && resultList.size() > 0) {
			Collections.shuffle(resultList);
			return "Было выбрано желание: " + resultList.get(0).getText();
		}
		return "";
	}
}