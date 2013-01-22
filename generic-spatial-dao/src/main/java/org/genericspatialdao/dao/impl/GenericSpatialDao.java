package org.genericspatialdao.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;
import org.genericspatialdao.configuration.CriteriaOptions;
import org.genericspatialdao.configuration.DaoConfiguration;
import org.genericspatialdao.dao.Dao;
import org.genericspatialdao.exception.DaoException;
import org.genericspatialdao.service.EntityManagerService;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;

/**
 * 
 * @author Joao Savio C. Longo - joaosavio@gmail.com
 * 
 */
public class GenericSpatialDao<T> implements Dao<T> {

	private static final String UNCHECKED = "unchecked";
	private static final String FAILED_TO_REMOVE_ALL = "Failed to remove all: ";
	private static final String CAUSE = ". Cause: ";
	private static final String PERSISTING_OBJECT = "Persisting object: ";
	private static final String REMOVING_OBJECT = "Removing object: ";
	private static final String MERGING_OBJECT = "Merging object: ";
	private static final String REFRESHING_OBJECT = "Refreshing object: ";
	private static final String FAILED_TO_MERGE = "Failed to merge: ";
	private static final String FAILED_TO_REMOVE = "Failed to remove: ";
	private static final String FAILED_TO_PERSIST = "Failed to persist: ";
	private static final String FAILED_TO_REFRESH = "Failed to refresh: ";
	private static final String OBJECT_BY_ID = " object by id ";
	private static final String OBJECTS_BY_IDS = " objects by ids ";
	private static final String FINDING = "Finding ";
	private static final String EXECUTING_QUERY = "Executing query: ";
	private static final String FAILED_TO_EXECUTE_QUERY = "Failed to execute query: ";
	private static final String ERROR = "Error: ";
	private static final String RESULT = "Result: ";
	private static final String EMPTY_LIST = "Empty list";
	private static final Logger LOG = Logger.getLogger(GenericSpatialDao.class);

	private final Class<T> entityClass;
	private final DaoConfiguration configuration;

	public GenericSpatialDao(Class<T> entityClass,
			DaoConfiguration configuration) {
		this.entityClass = entityClass;
		this.configuration = configuration;
	}

	@Override
	public T find(Object id) {
		if (LOG.isInfoEnabled()) {
			LOG.info(FINDING + entityClass.getName() + OBJECT_BY_ID + id);
		}
		T t = getEntityManager().find(entityClass, id);
		if (LOG.isDebugEnabled()) {
			LOG.debug(RESULT + t);
		}
		return t;
	}

	@Override
	public List<T> find(Object... id) {
		if (LOG.isInfoEnabled()) {
			LOG.info(FINDING + entityClass.getName() + OBJECTS_BY_IDS
					+ Arrays.toString(id));
		}
		List<T> resultList = new ArrayList<T>();
		for (Object idEntity : id) {
			T result = getEntityManager().find(entityClass, idEntity);
			if (result != null) {
				resultList.add(result);
			}
		}
		if (resultList == null || resultList.isEmpty()) {
			return null;
		}
		return resultList;
	}

	@Override
	public T find(Object id, Map<String, Object> properties) {
		if (LOG.isInfoEnabled()) {
			LOG.info(FINDING + entityClass.getSimpleName() + OBJECT_BY_ID + id
					+ " and properties: " + properties);
		}
		T t = getEntityManager().find(entityClass, id, properties);
		if (LOG.isDebugEnabled()) {
			LOG.debug(RESULT + t);
		}
		return t;
	}

	@Override
	public List<T> findAll() {
		return findAll(null);
	}

	@SuppressWarnings(UNCHECKED)
	@Override
	public List<T> findAll(CriteriaOptions criteriaOptions) {
		List<T> result = (List<T>) findByCriteria(null, null, criteriaOptions);
		if (LOG.isDebugEnabled()) {
			LOG.debug(RESULT + result);
		}
		return result;
	}

	@Override
	public void persist(T... t) {
		persist(Arrays.asList(t));
	}

	@Override
	public void persist(List<T> list) {
		if (isEmpty(list)) {
			return;
		}
		try {
			autoBeginTransaction();
			for (T entity : list) {
				persist(entity);
			}
			autoCommit();
		} catch (Exception e) {
			autoRollback();
			throw new DaoException(e);
		}
	}

	protected void persist(T t) {
		if (LOG.isInfoEnabled()) {
			LOG.info(PERSISTING_OBJECT + t);
		}
		try {
			getEntityManager().persist(t);
		} catch (Exception e) {
			String message = FAILED_TO_PERSIST + e.getMessage() + CAUSE
					+ e.getCause();
			LOG.error(message);
			throw new DaoException(message, e);
		}
	}

	@Override
	public void remove(T... t) {
		remove(Arrays.asList(t));
	}

	@Override
	public void remove(List<T> list) {
		if (isEmpty(list)) {
			return;
		}
		try {
			autoBeginTransaction();
			for (T entity : list) {
				remove(entity);
			}
			autoCommit();
		} catch (Exception e) {
			autoRollback();
			throw new DaoException(e);
		}
	}

	protected void remove(T t) {
		if (LOG.isInfoEnabled()) {
			LOG.info(REMOVING_OBJECT + t);
		}
		try {
			getEntityManager().remove(t);
		} catch (Exception e) {
			String message = FAILED_TO_REMOVE + e.getMessage() + CAUSE
					+ e.getCause();
			LOG.error(message);
			throw new DaoException(message, e);
		}
	}

	@Override
	public void merge(T... t) {
		merge(Arrays.asList(t));
	}

	@Override
	public void merge(List<T> list) {
		if (isEmpty(list)) {
			return;
		}
		try {
			autoBeginTransaction();
			for (T entity : list) {
				merge(entity);
			}
			autoCommit();
		} catch (Exception e) {
			autoRollback();
			throw new DaoException(e);
		}
	}

	protected void merge(T t) {
		if (LOG.isInfoEnabled()) {
			LOG.info(MERGING_OBJECT + t);
		}
		try {
			getEntityManager().merge(t);
		} catch (Exception e) {
			String message = FAILED_TO_MERGE + e.getMessage() + CAUSE
					+ e.getCause();
			LOG.error(message);
			throw new DaoException(message, e);
		}
	}

	@Override
	public void refresh(T... t) {
		refresh(Arrays.asList(t));
	}

	@Override
	public void refresh(List<T> list) {
		if (isEmpty(list)) {
			return;
		}
		try {
			autoBeginTransaction();
			for (T entity : list) {
				refresh(entity);
			}
			autoCommit();
		} catch (Exception e) {
			autoRollback();
			throw new DaoException(e);
		}
	}

	protected void refresh(T t) {
		if (LOG.isInfoEnabled()) {
			LOG.info(REFRESHING_OBJECT + t);
		}
		try {
			getEntityManager().refresh(t);
		} catch (Exception e) {
			String message = FAILED_TO_REFRESH + e.getMessage() + CAUSE
					+ e.getCause();
			LOG.error(message);
			throw new DaoException(message, e);
		}
	}

	@Override
	public void clear() {
		LOG.info("Clearing entity manager");
		getEntityManager().clear();
	}

	@Override
	public void flush() {
		LOG.info("Flushing");
		autoBeginTransaction();
		getEntityManager().flush();
	}

	@SuppressWarnings(UNCHECKED)
	@Override
	public List<T> findByCriteria(List<Criterion> list) {
		return (List<T>) findByCriteria(list, null, null);
	}

	@SuppressWarnings(UNCHECKED)
	@Override
	public List<T> findByCriteria(List<Criterion> list,
			CriteriaOptions criteriaOptions) {
		return (List<T>) findByCriteria(list, null, criteriaOptions);
	}

	@Override
	public List<?> findByCriteria(List<Criterion> list, Projection projection) {
		return findByCriteria(list, projection, null);
	}

	@Override
	public List<?> findByCriteria(List<Criterion> list, Projection projection,
			CriteriaOptions criteriaOptions) {
		if (LOG.isInfoEnabled()) {
			LOG.info("Finding by criteria. Conditions: " + list
					+ ". Projection: " + projection + ". Options: "
					+ criteriaOptions);
		}
		try {
			Criteria criteria = getSession().createCriteria(entityClass);
			fillCriteria(criteria, list, projection, criteriaOptions);

			List<?> result = criteria.list();

			if (LOG.isDebugEnabled()) {
				LOG.debug(RESULT + result);
			}
			return result;
		} catch (Exception e) {
			String message = ERROR + e.getMessage() + CAUSE + e.getCause();
			LOG.error(message);
			throw new DaoException(message, e);
		}
	}

	private void fillCriteria(Criteria criteria, List<Criterion> list,
			Projection projection, CriteriaOptions criteriaOptions) {
		if (list != null) {
			for (Criterion criterion : list) {
				criteria.add(criterion);
			}
		}
		if (projection != null) {
			criteria.setProjection(projection);
		}
		if (criteriaOptions != null) {
			if (criteriaOptions.getResultTransformer() != null) {
				criteria.setResultTransformer(criteriaOptions
						.getResultTransformer());
			}
			if (criteriaOptions.getOrders() != null) {
				for (Order order : criteriaOptions.getOrders()) {
					criteria.addOrder(order);
				}
			}
			if (criteriaOptions.getFirstResult() != null) {
				criteria.setFirstResult(criteriaOptions.getFirstResult());
			}
			if (criteriaOptions.getMaxResults() != null) {
				criteria.setMaxResults(criteriaOptions.getMaxResults());
			}
		}
	}

	@SuppressWarnings(UNCHECKED)
	@Override
	public T findUniqueByCriteria(List<Criterion> list) {
		if (LOG.isInfoEnabled()) {
			LOG.info("Finding unique by criteria. Conditions: " + list);
		}
		try {
			Criteria criteria = getSession().createCriteria(entityClass);
			for (int i = 0; i < list.size(); i++) {
				criteria.add(list.get(i));
			}
			T result = (T) criteria.uniqueResult();
			if (LOG.isDebugEnabled()) {
				LOG.debug(RESULT + result);
			}
			return result;
		} catch (Exception e) {
			String message = ERROR + e.getMessage();
			LOG.error(message);
			throw new DaoException(message, e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List executeSQL(String sql) {
		if (LOG.isInfoEnabled()) {
			LOG.info(EXECUTING_QUERY + sql);
		}
		try {
			Query q = getSession().createSQLQuery(sql);
			List result = q.list();
			if (LOG.isDebugEnabled()) {
				LOG.debug(RESULT + result);
			}
			return result;
		} catch (Exception e) {
			String message = FAILED_TO_EXECUTE_QUERY + e.getMessage() + CAUSE
					+ e.getCause();
			LOG.error(message);
			throw new DaoException(message, e);
		}
	}

	@Override
	public int executeSQLUpdate(String sql) {
		if (LOG.isInfoEnabled()) {
			LOG.info(EXECUTING_QUERY + sql);
		}
		try {
			Query q = getSession().createSQLQuery(sql);
			int result = q.executeUpdate();
			if (LOG.isDebugEnabled()) {
				LOG.debug(RESULT + result);
			}
			return result;
		} catch (Exception e) {
			autoRollback();
			String message = FAILED_TO_EXECUTE_QUERY + e.getMessage() + CAUSE
					+ e.getCause();
			LOG.error(message);
			throw new DaoException(message, e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List executeHQL(String hql) {
		if (LOG.isInfoEnabled()) {
			LOG.info(EXECUTING_QUERY + hql);
		}
		try {
			Query q = getSession().createQuery(hql);
			List result = q.list();
			if (LOG.isDebugEnabled()) {
				LOG.debug(RESULT + result);
			}
			return result;
		} catch (Exception e) {
			String message = FAILED_TO_EXECUTE_QUERY + e.getMessage() + CAUSE
					+ e.getCause();
			LOG.error(message);
			throw new DaoException(message, e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List executeHQL(String hqlTemplate, Object... params) {
		try {
			Query q = getSession().createQuery(hqlTemplate);
			int index = 0;
			for (Object param : params) {
				q.setParameter(index++, param);
			}
			if (LOG.isInfoEnabled()) {
				LOG.info(EXECUTING_QUERY + q.getQueryString());
			}
			List result = q.list();
			if (LOG.isDebugEnabled()) {
				LOG.debug(RESULT + result);
			}
			return result;
		} catch (Exception e) {
			String message = FAILED_TO_EXECUTE_QUERY + e.getMessage() + CAUSE
					+ e.getCause();
			LOG.error(message);
			throw new DaoException(message, e);
		}
	}

	@Override
	public int executeHQLUpdate(String hql) {
		if (LOG.isInfoEnabled()) {
			LOG.info(EXECUTING_QUERY + hql);
		}
		try {
			Query q = getSession().createQuery(hql);
			int result = q.executeUpdate();
			if (LOG.isDebugEnabled()) {
				LOG.debug(RESULT + result);
			}
			return result;
		} catch (Exception e) {
			autoRollback();
			String message = FAILED_TO_EXECUTE_QUERY + e.getMessage() + CAUSE
					+ e.getCause();
			LOG.error(message);
			throw new DaoException(message, e);
		}
	}

	@Override
	public void removeAll() {
		if (LOG.isInfoEnabled()) {
			LOG.info("Removing all of type " + entityClass.getSimpleName());
		}
		try {
			String hql = "DELETE FROM " + entityClass.getSimpleName();
			executeHQLUpdate(hql);
		} catch (Exception e) {
			autoRollback();
			String message = FAILED_TO_REMOVE_ALL + e.getMessage() + CAUSE
					+ e.getCause();
			LOG.error(message);
			throw new DaoException(message, e);
		}
	}

	@Override
	public long count() {
		if (LOG.isInfoEnabled()) {
			LOG.info("Counting rows of " + entityClass.getSimpleName());
		}
		try {
			Criteria criteria = getSession().createCriteria(entityClass);
			criteria.setProjection(Projections.rowCount());
			long result = (Long) criteria.uniqueResult();
			if (LOG.isDebugEnabled()) {
				LOG.debug(RESULT + result);
			}
			return result;
		} catch (Exception e) {
			String message = ERROR + e.getMessage() + CAUSE + e.getCause();
			LOG.error(message);
			throw new DaoException(message, e);
		}
	}

	@Override
	public Session getSession() {
		return ((Session) getEntityManager().getDelegate());
	}

	@Override
	public EntityManager getEntityManager() {
		return EntityManagerService.getEntityManager(configuration);
	}

	@Override
	public void beginTransaction() {
		EntityManagerService.beginTransaction(configuration);
	}

	@Override
	public void commit() {
		EntityManagerService.commit(configuration);
	}

	@Override
	public void rollback() {
		EntityManagerService.rollback(configuration);
	}

	@Override
	public void close() {
		EntityManagerService.close(configuration);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("entityClass", entityClass)
				.append("DAOConfiguration", configuration).toString();
	}

	protected void autoBeginTransaction() {
		if (configuration.isAutoTransaction()) {
			beginTransaction();
		}
	}

	protected void autoRollback() {
		if (configuration.isAutoTransaction()) {
			rollback();
		}
	}

	protected void autoCommit() {
		if (configuration.isAutoTransaction()) {
			commit();
		}
	}

	protected Class<T> getEntityClass() {
		return entityClass;
	}

	protected DaoConfiguration getConfiguration() {
		return configuration;
	}

	protected boolean isEmpty(List<T> list) {
		if (list == null || list.size() == 0) {
			LOG.warn(EMPTY_LIST);
			return true;
		}
		return false;
	}
}