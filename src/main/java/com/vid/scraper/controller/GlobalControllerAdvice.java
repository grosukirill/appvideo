package com.vid.scraper.controller;

import com.vid.scraper.exception.*;
import com.vid.scraper.model.ErrorCode;
import com.vid.scraper.model.ErrorDto;
import com.vid.scraper.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        ErrorDto errorDto = new ErrorDto(e.getMessage(),
                ErrorCode.AUTHENTICATION_ERROR.number,
                ErrorCode.AUTHENTICATION_ERROR,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        ErrorResponse response = new ErrorResponse(errorDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleAuthenticationException(EmailTakenException e) {
        ErrorDto errorDto = new ErrorDto(e.getMessage(),
                ErrorCode.EMAIL_TAKEN.number,
                ErrorCode.EMAIL_TAKEN,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        ErrorResponse response = new ErrorResponse(errorDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleAuthenticationException(TagNotFoundException e) {
        ErrorDto errorDto = new ErrorDto(e.getMessage(),
                ErrorCode.TAG_NOT_FOUND.number,
                ErrorCode.TAG_NOT_FOUND,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        ErrorResponse response = new ErrorResponse(errorDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleAuthenticationException(VideoNotFoundException e) {
        ErrorDto errorDto = new ErrorDto(e.getMessage(),
                ErrorCode.VIDEO_NOT_FOUND.number,
                ErrorCode.VIDEO_NOT_FOUND,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        ErrorResponse response = new ErrorResponse(errorDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException e) {
        ErrorDto errorDto = new ErrorDto(e.getMessage(),
                ErrorCode.BAD_CREDENTIALS.number,
                ErrorCode.BAD_CREDENTIALS,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        ErrorResponse response = new ErrorResponse(errorDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleMatchingPasswordsException(MatchingPasswordException e) {
        ErrorDto errorDto = new ErrorDto(e.getMessage(),
                ErrorCode.MATCHING_PASSWORDS.number,
                ErrorCode.MATCHING_PASSWORDS,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        ErrorResponse response = new ErrorResponse(errorDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException e) {
        ErrorDto errorDto = new ErrorDto(e.getMessage(),
                ErrorCode.INVALID_TOKEN.number,
                ErrorCode.INVALID_TOKEN,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        ErrorResponse response = new ErrorResponse(errorDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleDuplicateViewException(DuplicateViewsException e) {
        ErrorDto errorDto = new ErrorDto(e.getMessage(),
                ErrorCode.DUPLICATE_VIEW.number,
                ErrorCode.DUPLICATE_VIEW,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        ErrorResponse response = new ErrorResponse(errorDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleIllegalSearchTypeException(IllegalSearchTypeException e) {
        ErrorDto errorDto = new ErrorDto(e.getMessage(),
                ErrorCode.ILLEGAL_SEARCH_TYPE.number,
                ErrorCode.ILLEGAL_SEARCH_TYPE,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        ErrorResponse response = new ErrorResponse(errorDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
