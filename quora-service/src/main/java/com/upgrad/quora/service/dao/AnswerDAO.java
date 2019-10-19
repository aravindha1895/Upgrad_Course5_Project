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
	
	public void deleteAnswer(AnswerEntity answerEntity) {
		entityManager.remove(answerEntity);
	}
	
	public List<AnswerEntity> getAllAnswersByQuestion(QuestionEntity question){
		try {
			return entityManager.createNamedQuery("answersByQuestionID", AnswerEntity.class).setParameter("questionEntity", question)
					.getResultList();
		} catch (NoResultException nre) {
			return null;
		}
	}
}
