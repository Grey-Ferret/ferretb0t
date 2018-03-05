package net.greyferret.ferretb0t.service;

import net.greyferret.ferretb0t.entity.Loots;
import net.greyferret.ferretb0t.entity.Viewer;
import net.greyferret.ferretb0t.exception.TransactionRuntimeFerretB0tException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by GreyFerret on 19.12.2017.
 */
@Service
public class LootsService {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private ViewerService viewerService;

    private static final Logger logger = LogManager.getLogger();

    /***
     * Checking loots for being recorded in DB
     *
     * @param lootsSet loots from site
     * @return set of the found new loots
     */
    @Transactional
    public Set<Loots> checkOutLoots(Set<Loots> lootsSet) {
        Set<Loots> res = new HashSet<>();

        try {
            for (Loots loots : lootsSet) {
                boolean newLootsFound = false;

                CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
                CriteriaQuery<Viewer> criteria = builder.createQuery(Viewer.class);
                Root<Viewer> root = criteria.from(Viewer.class);
                criteria.select(root);

                criteria.where(builder.equal(root.get("lootsName"), loots.getAuthorLootsName().toLowerCase()));

                Viewer viewer;
                List<Viewer> userList = entityManager.createQuery(criteria).getResultList();
                if (userList == null || userList.size() == 0) {
                    viewer = null;
                } else {
                    if (userList.size() > 1)
                        logger.warn("Found more than 1 user with the same very same Loots name: " + loots.getAuthorLootsName());
                    viewer = userList.get(0);
                }
                if (loots.getUser() == null) {
                    loots.setUser(viewer);
                }
                Loots foundLoots = entityManager.find(Loots.class, loots.getId());
                if (foundLoots == null) {
                    entityManager.persist(loots);
                    newLootsFound = true;
                    res.add(loots);
                    logger.info("New Loots found!");
                    logger.info(loots);
                }
                if (newLootsFound) {
                    entityManager.flush();
                }
            }
        } catch (Exception e) {
            logger.error(e);
            throw new TransactionRuntimeFerretB0tException(e);
        }

        return res;
    }

    /***
     * Method that gets set of the unpaid loots and marks them as paid
     *
     * @return unpaid loots
     */
    @Transactional
    public Set<Loots> payForUnpaidLoots() {
        Set<Loots> res = new HashSet<>();

        try {
            CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
            CriteriaQuery<Loots> criteria = builder.createQuery(Loots.class);
            Root<Loots> root = criteria.from(Loots.class);
            criteria.select(root);

            criteria.where(builder.equal(root.get("paid"), false));

            List<Loots> lootsList = entityManager.createQuery(criteria).getResultList();


            for (Loots loots : lootsList) {
                if (loots.getUser() == null) {
//                    logger.warn("No user was found for Loots " + loots.getId() + " from " + loots.getAuthorLootsName());
//                    logger.info("Trying to fix...");
                    Viewer viewer = viewerService.findViewerForLootsName(loots.getAuthorLootsName());
                    if (viewer == null) {
//                        logger.info("No viewer found...");
                    }
                    else {
                        loots.setUser(viewer);
                        logger.info("Viewer found! " + viewer.getLoginWithCase());
                        loots.setPaid(true);
                        entityManager.merge(loots);
                        res.add(loots);
                        logger.info("Payment incoming for Loots " + loots.getId() + " from " + loots.getAuthorTwitchName());
                    }
                } else {
                    loots.setPaid(true);
                    entityManager.merge(loots);
                    res.add(loots);
                    logger.info("Payment incoming for Loots " + loots.getId() + " from " + loots.getAuthorTwitchName());
                }
            }
            if (res.size() > 0)
                entityManager.flush();
        } catch (Exception e) {
            logger.error(e);
            throw new TransactionRuntimeFerretB0tException(e);
        }

        return res;
    }

    public Set<String> findLootsForRepair() {
        Set<String> res = new HashSet<>();

        try {
            CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
            CriteriaQuery<Loots> criteria = builder.createQuery(Loots.class);
            Root<Loots> root = criteria.from(Loots.class);
            criteria.select(root);

            criteria.where(builder.equal(root.get("paid"), false));

            List<Loots> lootsList = entityManager.createQuery(criteria).getResultList();

            for (Loots loots : lootsList) {
                if (loots.getUser() == null) {
                    res.add(loots.getAuthorLootsName());
                }
            }
        } catch (Exception e) {
            logger.error(e);
            throw new TransactionRuntimeFerretB0tException(e);
        }
        return res;
    }
}
