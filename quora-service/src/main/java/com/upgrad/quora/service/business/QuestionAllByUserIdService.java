package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionAllByUserIdService {


    @Autowired
    private QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> questionByUserId(final String authorizationToken, final String userId) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = questionDao.getUserAuthToken(authorizationToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        else if(!userAuthTokenEntity.getUuid().equals(userId))
        {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        else if(userAuthTokenEntity.getLogoutAt()!=null)
        {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
        }

        List<QuestionEntity> questionEntityList =  questionDao.getAllQuestionsByUserId(userId);

        if(questionEntityList.size()==0)
        {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");

        }
        else

            return questionEntityList;
    }






}
