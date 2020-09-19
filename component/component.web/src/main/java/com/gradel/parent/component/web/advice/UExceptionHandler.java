package com.gradel.parent.component.web.advice;

import com.gradel.parent.component.web.entity.UException;
import com.gradel.parent.component.web.entity.UMessage;
import com.gradel.parent.component.web.entity.UResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * UExceptionHandler
 *
 * @Date 2020/2/14 下午12:27
 * @Author  sdeven.chen.dongwei@gmail.com
 */
@Slf4j
@RestControllerAdvice
public class UExceptionHandler {
    @ExceptionHandler(UException.class)
    public ResponseEntity<UResponse> handlerMException(UException me){
        log.error(me.getMessage(), me);
        UResponse response = new UResponse<>(null, new UMessage(me.getCode(), me.getMessage()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<UResponse> handlerBindException(MethodArgumentNotValidException me){
        log.error(me.getMessage(), me);
        UResponse response = new UResponse<>(null, UMessage.VALID_BIND_EXCEPTION, me.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UResponse> handlerException(Exception me){
        log.error(me.getMessage(), me);
        UResponse response = new UResponse<>(null, UMessage.UNKNOWN_EXCEPTION, me.getMessage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
