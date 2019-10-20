package com.upgrad.quora.service.dao;

import java.util.List;

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

	/* Saves answer in database */
	public AnswerEntity saveAnswer(AnswerEntity answerEntity) {
		entityManager.persist(answerEntity);
		return answerEntity;
	}

	/* Return single answer corresponding to ID. Returns NULL if n result */
	public AnswerEntity getAnswerByID(String answerUUID) {

		try {
			return entityManager.createNamedQuery("answerByID", AnswerEntity.class).setParameter("uuid", answerUUID)
					.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
	
	/* Updates existing answer */
	public AnswerEntity updateAnswer(AnswerEntity answerEntity) {
		 return entityManager.merge(answerEntity);
	}
	
	/* Delete existing answer */
	public void deleteAnswer(AnswerEntity answerEntity) {
		entityManager.remove(answerEntity);
	}
	
	/* Get all answer by question */
	public List<AnswerEntity> getAllAnswersByQuestion(QuestionEntity question){
		try {
			return entityManager.createNamedQuery("answersByQuestionID", AnswerEntity.class).setParameter("questionEntity", question)
					.getResultList();
		} catch (NoResultException nre) {
			return null;
		}
	}
}
