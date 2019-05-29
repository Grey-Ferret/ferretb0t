package dev.greyferret.ferretbot.service;

import dev.greyferret.ferretbot.entity.Misc;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

/**
 * Created by GreyFerret on 27.12.2017.
 */
@Service
public class MiscService {
	private static final Logger logger = LogManager.getLogger(MiscService.class);

	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Transactional
	public Misc getMiscById(String id) {
		if (StringUtils.isNotBlank(id)) {
			return entityManager.find(Misc.class, id);
		} else {
			return null;
		}
	}

	@Transactional
	public void updateMisc(Misc misc) {
		if (StringUtils.isNotBlank(misc.getId())) {
			Misc _misc = entityManager.find(Misc.class, misc.getId());
			if (_misc == null) {
				entityManager.persist(misc);
			} else {
				entityManager.merge(misc);
			}
			entityManager.flush();
		} else {

		}
	}
}
