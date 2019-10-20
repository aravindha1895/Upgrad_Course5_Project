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

	/* Business logic for posting question in database */
	@Transactional(propagation = Propagation.REQUIRED)
	public AnswerEntity postAnswerForQuestion(final String authorizationToken, String questionId,
			AnswerEntity answerEntity) throws AuthorizationFailedException, InvalidQuestionException {
		UserAuthTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorizationToken);
		validateUserAuthToken(userAuthTokenEntity, "post an answer");
		answerEntity.setUser(userAuthTokenEntity.getUser());
		QuestionEntity questionEntity = questionDao.getQuestion(questionId);
		if (questionEntity == null) { // Question not found
			throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
		} else {
			answerEntity.setQuestionEntity(questionEntity);
			answerDao.saveAnswer(answerEntity);
		}

		return answerEntity;
	}

	/* Business logic for editing question in database */
	@Transactional(propagation = Propagation.REQUIRED)
	public AnswerEntity editAnswer(final String authorizationToken, String answerId, AnswerEntity answerEntity)
			throws AuthorizationFailedException, InvalidQuestionException, AnswerNotFoundException {
		UserAuthTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorizationToken);
		validateUserAuthToken(userAuthTokenEntity, "edit an answer");
		AnswerEntity existingAnswerEntity = answerDao.getAnswerByID(answerId);
		if (existingAnswerEntity == null) { // Answer not found
			throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
		} else if (existingAnswerEntity.getUser().getId() != userAuthTokenEntity.getUser().getId()) { // Comparing by
																										// primary key
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

	/* Business logic for deleting question in database */
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteAnswer(final String authorizationToken, String answerId)
			throws AuthorizationFailedException, InvalidQuestionException, AnswerNotFoundException {

		UserAuthTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorizationToken);
		validateUserAuthToken(userAuthTokenEntity, "delete an answer");
		AnswerEntity existingAnswerEntity = answerDao.getAnswerByID(answerId);

		if (existingAnswerEntity == null) { // Answer not found
			throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
		} else {
			UserEntity answerOwner = existingAnswerEntity.getUser();
			if (answerOwner.getId() != userAuthTokenEntity.getUser().getId()) {
				if (!answerOwner.getRole().equals(ADMIN_ROLE)) // Checking if user role is Admin
					throw new AuthorizationFailedException("ATHR-003",
							"Only the answer owner or admin can delete the answer");
			} else {
				answerDao.deleteAnswer(existingAnswerEntity);
			}
		}
	}

	/* Business logic for getting all answers for a question */
	public List<AnswerEntity> getAllAnswersToQuestion(final String authorizationToken, String questionId)
			throws AuthorizationFailedException, InvalidQuestionException {
		UserAuthTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorizationToken);
		validateUserAuthToken(userAuthTokenEntity, "get the answers");
		QuestionEntity questionEntity = questionDao.getQuestion(questionId);
		if (questionEntity == null) { // Question not found
			throw new InvalidQuestionException("QUES-001",
					"The question with entered uuid whose details are to be seen does not exist");
		} else {
			return answerDao.getAllAnswersByQuestion(questionEntity);
		}
	}

	/* This function validates auth token and throws exception if invalid */
	private void validateUserAuthToken(UserAuthTokenEntity userAuthTokenEntity, String suffixMessage)
			throws AuthorizationFailedException {
		if (userAuthTokenEntity == null) {
			throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
		} else if (userAuthTokenEntity.getLogoutAt() != null) {
			throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to " + suffixMessage);
		}
	}

}
