package com.example.filedemo.dao;

import com.example.filedemo.model.FileModel;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class FileModelDaoHibernateImpl implements FileModelDao {

	// define field for entity manager
	private EntityManager entityManager;
	private static final Logger logger = LoggerFactory.getLogger(FileModelDaoHibernateImpl.class);

	// set up constructor injection
	@Autowired
	public FileModelDaoHibernateImpl(EntityManager theEntityManager) {
		entityManager = theEntityManager;
	}


	@Override
	public List<String> findAllFileNames() {

		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);

		// create a query
		Query<String> theQuery =
				currentSession.createQuery("select e.fileName from FileModel e", String.class);

		logger.info(String.valueOf(theQuery));

		// execute query and return the results
		return theQuery.getResultList();
	}


	@Override
	public List<FileModel> findAllFileModels() {

		Session currentSession = entityManager.unwrap(Session.class);

		Query<FileModel> modelQuery = currentSession.createQuery("from FileModel", FileModel.class);


		return modelQuery.getResultList();
	}

	@Override
	public List<FileModel> findAllFileModelsFilterByName(String name) {

		Session currentSession = entityManager.unwrap(Session.class);

		Query<FileModel> modelQuery =
				currentSession.createQuery("from FileModel e where lower(e.fileName) " +
						"like lower('%" + name + "%')", FileModel.class);

		return modelQuery.getResultList();
	}

	@Override
	public List<FileModel> findAllFileModelsFilterByType(String type) {

		Session currentSession = entityManager.unwrap(Session.class);

		Query<FileModel> modelQuery =
				currentSession.createQuery("from FileModel e where e.fileName " +
						"like '%" + type + "'", FileModel.class);

		return modelQuery.getResultList();
	}


	@Override
	public FileModel findById(int id) {

		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);

		// get the employee

		// return the employee
		return currentSession.get(FileModel.class, id);
	}


	@Override
	public void save(FileModel fileModel) {

		Session currentSession = entityManager.unwrap(Session.class);

		currentSession.saveOrUpdate(fileModel);

	}


	@Override
	public void deleteById(int id) {

		// get the current hibernate session
		Session currentSession = entityManager.unwrap(Session.class);

		// delete object with primary key
		Query theQuery =
				currentSession.createQuery(
						"delete from FileModel where id=:fileId");
		theQuery.setParameter("fileId", id);

		theQuery.executeUpdate();
	}
}
