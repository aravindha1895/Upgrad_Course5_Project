package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;


@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public UserAuthTokenEntity getUserAuthToken(final String accesstoken) {
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class).setParameter("accessToken", accesstoken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }


    public List<QuestionEntity> getAllQuestions() {
        try {
            return entityManager.createNamedQuery("allQuestions", QuestionEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }


    public QuestionEntity getQuestion(final String uuid) {

        try {
            return entityManager.createNamedQuery("oneQuestion", QuestionEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }


    }

    public QuestionEntity editQuestion(QuestionEntity questionEntity) {

        return entityManager.merge(questionEntity);
        //entityManager.createNamedQuery("editQuestion").setParameter("uuid", questionEntity.getUuid()).setParameter("content", questionEntity.getContent());

    }

    public void deleteQuestion(QuestionEntity questionEntity) {
        //entityManager.createNamedQuery("deleteQuestion").setParameter("uuid", questionEntity.getUuid())
        entityManager.merge(questionEntity);
        entityManager.remove(questionEntity);

    }

    public List<QuestionEntity> getAllQuestionsByUserId(String userId) {

        try {
            return entityManager.createNamedQuery("allQuestionsByUserId", QuestionEntity.class).setParameter("uuid",userId).getResultList();
        } catch (NoResultException nre) {
            return null;
        }

    }
}
