package com.upgrad.quora.service.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Repository;

import com.upgrad.quora.service.entity.AnswerEntity;

@Repository
public class AnswerDAO {

    @PersistenceContext
    private EntityManager entityManager;
    
    public AnswerEntity saveAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }
}
