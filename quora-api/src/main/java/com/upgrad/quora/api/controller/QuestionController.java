package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionEditRequest;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.*;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {


    @Autowired
    private QuestionUploadService questionUploadService;

    @Autowired
    private QuestionAllService questionAllService;

    @Autowired
    private QuestionEditService questionEditService;

    @Autowired
    private QuestionDeleteService questionDeleteService;

    @Autowired
    QuestionAllByUserIdService questionAllByUserIdService;


    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> questionUpload(final QuestionRequest questionRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        final QuestionEntity questionEntity = new QuestionEntity();

        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setCreated_at(ZonedDateTime.now());

        final QuestionEntity createdQuestionEntity = questionUploadService.upload(questionEntity, authorization);
        QuestionResponse questionUploadResponse = new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionUploadResponse, HttpStatus.CREATED);
    }


    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<QuestionDetailsResponse> questionAll(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        final QuestionEntity questionEntity = new QuestionEntity();

        final List<QuestionEntity> createdQuestionEntity = questionAllService.upload(authorization);

        List<QuestionDetailsResponse> questionDetailsResponses = new ArrayList<>();

        for (QuestionEntity q : createdQuestionEntity) {
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse();
            questionDetailsResponse.setId(q.getUuid());
            questionDetailsResponse.setContent(q.getContent());
            questionDetailsResponses.add(questionDetailsResponse);

        }

        return questionDetailsResponses;

    }


    @Transactional(propagation = Propagation.REQUIRED)
    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> questionUpload(@PathVariable("questionId") final String questionId, final QuestionEditRequest questionEditRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        final QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(questionId);
        questionEntity.setContent(questionEditRequest.getContent());
        questionEntity.setCreated_at(ZonedDateTime.now());

        final QuestionEntity createdQuestionEntity = questionEditService.editQuestion(questionEntity, authorization);
        QuestionResponse questionUploadResponse = new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionResponse>(questionUploadResponse, HttpStatus.CREATED);
    }


    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> questionDelete(@RequestHeader("authorization") final String authorization, @PathVariable("questionId") final String questionId) throws AuthorizationFailedException, InvalidQuestionException {
          final QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(questionId);

        final QuestionEntity createdQuestionEntity = questionDeleteService.deleteQuestion(questionEntity, authorization);

        QuestionResponse questionUploadResponse = new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION DELETED");
        return new ResponseEntity<QuestionResponse>(questionUploadResponse, HttpStatus.CREATED);


    }


    @RequestMapping(method = RequestMethod.GET, path = "question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<QuestionDetailsResponse> questionAllUserId(@RequestHeader("authorization") final String authorization , @PathVariable("userId") final String userId) throws AuthorizationFailedException, UserNotFoundException {
        final QuestionEntity questionEntity = new QuestionEntity();

        final List<QuestionEntity> createdQuestionEntity = questionAllByUserIdService.questionByUserId(authorization, userId);

        List<QuestionDetailsResponse> questionDetailsResponses = new ArrayList<>();

        for (QuestionEntity q : createdQuestionEntity) {
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse();
            questionDetailsResponse.setId(q.getUuid());
            questionDetailsResponse.setContent(q.getContent());
            questionDetailsResponses.add(questionDetailsResponse);

        }

        return questionDetailsResponses;

    }




}
