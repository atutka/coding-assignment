package com.akamai.processingsystem.schedule;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateScheduleRequest
{
   private MultipartFile file;
   private LocalDateTime startDateTime;

   public CreateScheduleRequest(MultipartFile file, LocalDateTime startDateTime)
   {
      this.file = file;
      this.startDateTime = startDateTime;
   }
}
