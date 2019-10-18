package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionDeleteService {

    @Autowired
    private QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(QuestionEntity questionEntity, final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorizationToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        else if(userAuthTokenEntity.getLogoutAt()!=null)
        {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
        }

        questionEntity.setUser(userAuthTokenEntity.getUser());

        QuestionEntity questionEditEntity  = questionDao.getQuestion(questionEntity.getUuid());

        if (questionEditEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        else if(questionEntity.getUser().getEmail()!=questionEditEntity.getUser().getEmail())
        {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can delete the question");
        }

        else
        {
            questionDao.deleteQuestion(questionEditEntity);
        }

        return questionEditEntity;
    }

}
