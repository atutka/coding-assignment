package com.akamai.processingsystem.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

@RestControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler
{
   private static final ResourceBundle RESOURCE_BUNDLE = PropertyResourceBundle.getBundle("validationMessages_en");
   private static final String WRONG_DATA_MESSAGE = RESOURCE_BUNDLE.getString("error.illegaldata.msg");
   private static final String INTERNAL_ERROR_MESSAGE = RESOURCE_BUNDLE.getString("error.internal.msg");

   @ExceptionHandler(value = {IOException.class, IllegalArgumentException.class})
   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public ErrorResponse handleWrongDataException(HttpServletRequest req, Exception ex)
   {
      log.error("Error with wrong data", ex);
      return ErrorResponse.create(HttpStatus.BAD_REQUEST, WRONG_DATA_MESSAGE);
   }

   @ExceptionHandler(value = { Exception.class })
   @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
   public ErrorResponse handleUnknownException(Exception ex) {
      log.error("Internal application error", ex);
      return ErrorResponse.create(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR_MESSAGE);
   }
}
