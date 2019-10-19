package com.upgrad.quora.service.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.upgrad.quora.service.dao.AnswerDAO;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;

@Service
public class AnswerService {
	@Autowired
	AnswerDAO answerDao;

	@Autowired
	QuestionDao questionDao;

	@Transactional(propagation = Propagation.REQUIRED)
	public AnswerEntity postAnswerForQuestion(final String authorizationToken, String questionId,
			AnswerEntity answerEntity) throws AuthorizationFailedException, InvalidQuestionException {
		UserAuthTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorizationToken);
		validateUserAuthToken(userAuthTokenEntity, "post a question");
		answerEntity.setUser(userAuthTokenEntity.getUser());
		QuestionEntity questionEntity = questionDao.getQuestion(questionId);
		if (questionEntity == null) {
			throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
		} else {
			answerEntity.setQuestionEntity(questionEntity);
			answerDao.saveAnswer(answerEntity);
		}

		return answerEntity;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public AnswerEntity editAnswer(final String authorizationToken, String answerId, AnswerEntity answerEntity)
			throws AuthorizationFailedException, InvalidQuestionException, AnswerNotFoundException {
		UserAuthTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorizationToken);
		validateUserAuthToken(userAuthTokenEntity,"edit an answer");
		AnswerEntity existingAnswerEntity = answerDao.getAnswerByID(answerId);
		if (existingAnswerEntity == null) {
			throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
		} else if(!existingAnswerEntity.getUser().equals(userAuthTokenEntity.getUser())) {
			throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
		} else {
			answerEntity.setId(existingAnswerEntity.getId());
			answerEntity.setUuid(existingAnswerEntity.getUuid());
			answerEntity.setUser(userAuthTokenEntity.getUser());
			answerEntity.setQuestionEntity(existingAnswerEntity.getQuestionEntity());
			answerDao.updateAnswer(answerEntity);
		}

		return answerEntity;
	}

	private void validateUserAuthToken(UserAuthTokenEntity userAuthTokenEntity, String suffixMessage) throws AuthorizationFailedException {
		if (userAuthTokenEntity == null) {
			throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
		} else if (userAuthTokenEntity.getLogoutAt() != null) {
			throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to "+suffixMessage);
		}
	}

}
