package com.upgrad.quora.service.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;

@Repository
public class AnswerDAO {

	@PersistenceContext
	private EntityManager entityManager;

	public AnswerEntity saveAnswer(AnswerEntity answerEntity) {
		entityManager.persist(answerEntity);
		return answerEntity;
	}

	public AnswerEntity getAnswerByID(String answerUUID) {

		try {
			return entityManager.createNamedQuery("answerByID", AnswerEntity.class).setParameter("uuid", answerUUID)
					.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
	
	public AnswerEntity updateAnswer(AnswerEntity answerEntity) {
		 return entityManager.merge(answerEntity);
	}
}
