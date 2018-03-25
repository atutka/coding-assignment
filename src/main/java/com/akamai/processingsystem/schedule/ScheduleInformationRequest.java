package com.akamai.processingsystem.schedule;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class ScheduleInformationRequest
{
   private UUID scheduleId;
   private LocalDateTime timestamp;
}
