package com.akamai.processingsystem.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class ErrorResponse
{
   private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

   private HttpStatus status;
   private String errorMessage;
   private String dateTime;

   private ErrorResponse(HttpStatus status, String errorMessage)
   {
      this.errorMessage = errorMessage;
      this.status = status;
      this.dateTime = LocalDateTime.now().format(DATE_TIME_FORMAT);
   }

   public static ErrorResponse create(HttpStatus status, String errorMessage)
   {
      return new ErrorResponse(status, errorMessage);
   }
}
