package com.upgrad.quora.service.business;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.upgrad.quora.service.dao.AnswerDAO;
import com.upgrad.quora.service.entity.AnswerEntity;

@Service
public class AnswerService {

	@Transactional(propagation = Propagation.REQUIRED)
	public AnswerEntity postAnswerForQuestion(final String authorizationToken, String questionId, AnswerEntity answerEntity) {
		int userId=1024;
		answerEntity.setUserId(userId);
		AnswerDAO answerDAO = new AnswerDAO();
		answerDAO.saveAnswer(answerEntity);
		return answerEntity;
	}
}
