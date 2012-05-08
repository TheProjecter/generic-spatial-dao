package org.genericspatialdao.services;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.apache.log4j.Logger;
import org.genericspatialdao.exception.DAOException;
import org.hibernate.Session;

/**
 * 
 * @author joaosavio
 * 
 */
public class EntityManagerService {

	private static final Logger LOG = Logger
			.getLogger(EntityManagerService.class);

	private static ThreadLocal<EntityManager> session = new ThreadLocal<EntityManager>();

	/**
	 * @return an entity manager of a target persistence unit
	 */
	public static synchronized EntityManager getEntityManager(
			String persistenceUnitName) {
		EntityManager em = session.get();
		if (em == null) {
			EntityManagerFactory emf = EntityManagerFactoryService
					.getEntityManagerFactory(persistenceUnitName);
			LOG.debug("Creating entity manager");
			em = emf.createEntityManager();
			session.set(em);
		}
		return em;
	}

	/**
	 * Close entity manager remove it from session
	 */
	public static synchronized void close() {
		EntityManager em = session.get();
		if (em != null) {
			if (em.isOpen()) {
				LOG.info("Closing entity manager");
				em.close();
			}
			LOG.debug("Removing entity manager from session");
			session.set(null);
		}
	}

	/**
	 * Close quietly entity manager and session
	 */
	public static synchronized void closeQuietly() {
		try {
			close();
		} catch (DAOException e) {

		}
	}

	/**
	 * Begins a transaction if it is not active
	 */
	public static void beginTransaction(String persistenceUnitName) {
		EntityManager em = getEntityManager(persistenceUnitName);
		EntityTransaction transaction = em.getTransaction();
		if (!transaction.isActive()) {
			LOG.info("Beginning transaction");
			transaction.begin();
		}
	}

	/**
	 * Commits if transaction is active
	 */
	public static void commit(String persistenceUnitName) {
		EntityManager em = getEntityManager(persistenceUnitName);
		EntityTransaction transaction = em.getTransaction();
		if (transaction.isActive()) {
			LOG.info("Commiting");
			transaction.commit();
		}
	}

	/**
	 * Rollback if transaction is active
	 */
	public static void rollback(String persistenceUnitName) {
		EntityManager em = getEntityManager(persistenceUnitName);
		EntityTransaction transaction = em.getTransaction();
		if (transaction.isActive()) {
			LOG.info("Rollbacking");
			transaction.rollback();
		}
	}

	/**
	 * @return session for Hibernate
	 */
	public static Session getSession(String persistenceUnitName) {
		EntityManager em = getEntityManager(persistenceUnitName);
		return ((Session) em.getDelegate());
	}
}
