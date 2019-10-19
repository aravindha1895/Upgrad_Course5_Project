package com.upgrad.quora.service.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.upgrad.quora.service.dao.AnswerDAO;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;

@Service
public class AnswerService {
	@Autowired
	AnswerDAO answerDao;

	@Autowired
	QuestionDao questionDao;

	private static final String ADMIN_ROLE = "admin";
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
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteAnswer(final String authorizationToken, String answerId)
			throws AuthorizationFailedException, InvalidQuestionException, AnswerNotFoundException {

		UserAuthTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorizationToken);
		validateUserAuthToken(userAuthTokenEntity,"delete an answer");
		AnswerEntity existingAnswerEntity = answerDao.getAnswerByID(answerId);
		
		if (existingAnswerEntity == null) {
			throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
		} else {
			UserEntity answerOwner = existingAnswerEntity.getUser();
			if(answerOwner.getId()!=userAuthTokenEntity.getUser().getId()) {
				if(!answerOwner.getRole().equals(ADMIN_ROLE))
					throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
			} else {
				answerDao.deleteAnswer(existingAnswerEntity);
			}
		}
	}
	
	
	public List<AnswerEntity> getAllAnswersToQuestion(final String authorizationToken, String questionId) throws AuthorizationFailedException, InvalidQuestionException {
		UserAuthTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorizationToken);
		validateUserAuthToken(userAuthTokenEntity,"get the answers");
		QuestionEntity questionEntity = questionDao.getQuestion(questionId);
		if (questionEntity == null) {
			throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
		} else {
			return answerDao.getAllAnswersByQuestion(questionEntity);
		}
	}
	private void validateUserAuthToken(UserAuthTokenEntity userAuthTokenEntity, String suffixMessage) throws AuthorizationFailedException {
		if (userAuthTokenEntity == null) {
			throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
		} else if (userAuthTokenEntity.getLogoutAt() != null) {
			throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to "+suffixMessage);
		}
	}

}
