package com.upgrad.quora.api.controller;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.entity.AnswerEntity;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@RestController
@RequestMapping("/")
public class AnswerController {
	
    @Autowired
    private AnswerService answerService;
	@RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)

	@ApiResponses(value = {
    @ApiResponse(code = 2003, message = "Successful operation", response = AnswerResponse.class)
	})
	public ResponseEntity<AnswerResponse> createAnswer(@PathVariable("questionId") String questionId,
			@RequestHeader("authorization") final String authorizationToken, @RequestBody AnswerRequest answerRequest) {
	//	System.out.println(authorizationToken);
		// System.out.println(questionId);
	   // System.out.println(answerRequest.toString());
	   AnswerEntity answerEntity = new AnswerEntity();
	   answerEntity.setAnswer(answerRequest.getAnswer());
	   answerEntity.setQuestionId(Integer.parseInt(questionId));
	   answerEntity.setDate(ZonedDateTime.now());
	   answerEntity.setUuid(UUID.randomUUID().toString());
	   final AnswerEntity postedAnswer = answerService.postAnswerForQuestion(authorizationToken, questionId, answerEntity);
	   AnswerResponse answerResponse = new AnswerResponse().id(postedAnswer.getUuid()).status("ANSWER CREATED");
	   return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
	}
}
