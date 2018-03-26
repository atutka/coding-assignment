package com.akamai.processingsystem.schedule;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ScheduleInformationRequest
{
   private UUID scheduleId;
   private LocalDateTime timestamp;

   public ScheduleInformationRequest(UUID scheduleId, LocalDateTime timestamp)
   {
      this.scheduleId = scheduleId;
      this.timestamp = timestamp;
   }
}
