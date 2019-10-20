package com.upgrad.quora.api.controller;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
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

import com.upgrad.quora.api.model.AnswerDeleteResponse;
import com.upgrad.quora.api.model.AnswerDetailsResponse;
import com.upgrad.quora.api.model.AnswerEditRequest;
import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/")
public class AnswerController {

	@Autowired
	private AnswerService answerService;

	@RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)

	
	@ApiOperation(value = "Create a answer for question by giving question ID", response =AnswerResponse.class )
	@ApiResponses(value = {
			@ApiResponse( message = "ANSWER CREATED", response = AnswerResponse.class, code = 201),
			@ApiResponse( message = "Authorization failed", response = AuthorizationFailedException.class, code = 403),
			@ApiResponse( message = "Question entity not found", response = InvalidQuestionException.class, code = 404)
			})
	public ResponseEntity<AnswerResponse> createAnswer(@PathVariable("questionId") String questionId,
			@RequestHeader("authorization") final String authorizationToken, @RequestBody AnswerRequest answerRequest)
			throws AuthorizationFailedException, InvalidQuestionException {
		// Setting some attributes of answer entity from request
		AnswerEntity answerEntity = new AnswerEntity();
		answerEntity.setAnswer(answerRequest.getAnswer());
		answerEntity.setDate(ZonedDateTime.now());
		answerEntity.setUuid(UUID.randomUUID().toString());
		final AnswerEntity postedAnswer = answerService.postAnswerForQuestion(authorizationToken, questionId,
				answerEntity);
		AnswerResponse answerResponse = new AnswerResponse().id(postedAnswer.getUuid()).status("ANSWER CREATED");
		return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
	}
	
	@ApiOperation(value = "Edit already existing answer by giving answer ID", response =AnswerResponse.class )
	@ApiResponses(value = {
			@ApiResponse( message = "ANSWER EDITED", response = AnswerResponse.class, code = 201),
			@ApiResponse( message = "Authorization failed", response = AuthorizationFailedException.class, code = 403),
			@ApiResponse( message = "Question / Answer entity not found", response = InvalidQuestionException.class, code = 404)
			})
	@RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<AnswerResponse> editAnswerContent(@PathVariable("answerId") String answerId,
			@RequestHeader("authorization") final String authorizationToken,
			@RequestBody AnswerEditRequest answerEditRequest)
			throws AuthorizationFailedException, InvalidQuestionException, AnswerNotFoundException {
		// Setting some attributes of answer entity from request
		AnswerEntity answerEntity = new AnswerEntity();
		answerEntity.setAnswer(answerEditRequest.getContent());
		answerEntity.setDate(ZonedDateTime.now());
		answerEntity.setUuid(answerId);
		final AnswerEntity postedAnswer = answerService.editAnswer(authorizationToken, answerId, answerEntity);
		AnswerResponse answerResponse = new AnswerResponse().id(postedAnswer.getUuid()).status("ANSWER EDITED");
		return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
	}

	@ApiOperation(value = "Delete existing answer by giving answer ID", response =AnswerDeleteResponse.class )
	@ApiResponses(value = {
			@ApiResponse( message = "ANSWER DELETED", response = AnswerDeleteResponse.class, code = 200),
			@ApiResponse( message = "Authorization failed", response = AuthorizationFailedException.class, code = 403),
			@ApiResponse( message = "Question / Answer entity not found", response = InvalidQuestionException.class, code = 404),
		 })
	@RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") String answerId,
			@RequestHeader("authorization") final String authorizationToken)
			throws AuthorizationFailedException, AnswerNotFoundException, InvalidQuestionException {
		answerService.deleteAnswer(authorizationToken, answerId);
		/* If we reach this point, then no exception happened and delete operation is success */
		AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(answerId).status("ANSWER DELETED");
		return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
	}

	@ApiOperation(value = "Fetch all answers for a question ID", response =AnswerDetailsResponse.class )
	@ApiResponses(value = {
			@ApiResponse( message = "Returns the list of answers", response = AnswerDetailsResponse.class, code = 200),
			@ApiResponse( message = "Authorization failed", response = AuthorizationFailedException.class, code = 403),
			@ApiResponse( message = "Question / Answer entity not found", response = InvalidQuestionException.class, code = 404),
			 })
	@RequestMapping(method = RequestMethod.GET, path = "answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(
			@PathVariable("questionId") String questionId,
			@RequestHeader("authorization") final String authorizationToken)
			throws AuthorizationFailedException, InvalidQuestionException, AnswerNotFoundException {
		List<AnswerEntity> answerList = answerService.getAllAnswersToQuestion(authorizationToken, questionId);
		List<AnswerDetailsResponse> answerDetailsResponseList = new ArrayList<AnswerDetailsResponse>();
		// Forming response from entity
		for (AnswerEntity answer : answerList) {
			answerDetailsResponseList.add(new AnswerDetailsResponse().id(answer.getUuid())
					.answerContent(answer.getAnswer()).questionContent(answer.getQuestionEntity().getContent()));
		}
		return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponseList, HttpStatus.OK);
	}
}
