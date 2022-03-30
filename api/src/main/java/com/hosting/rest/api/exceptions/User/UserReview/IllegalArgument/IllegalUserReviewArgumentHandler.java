package com.hosting.rest.api.exceptions.User.UserReview.IllegalArgument;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Francisco Coya · https://github.com/FranciscoCoya
 * @version v1.0.0
 * @description Manejador de la exception IllegalUserReviewException.
 **/
@ControllerAdvice
public class IllegalUserReviewArgumentHandler {


	@ResponseBody
	@ExceptionHandler(IllegalUserReviewArgumentException.class)
	public ResponseEntity<Map<String, String>> illegalUserReviewArgumentHandler(HttpServletRequest req,
			IllegalUserReviewArgumentException exception) {
		
		Map<String, String> errorResponse = new HashMap<>();

		errorResponse.put("path", req.getServletPath());
		errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
		errorResponse.put("message", exception.getMessage());
		
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
}