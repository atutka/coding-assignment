package com.akamai.processingsystem.schedule;

import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Value
public class CreateScheduleRequest
{
   private MultipartFile file;
   private LocalDateTime startDateTime;
}
