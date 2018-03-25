package com.akamai.processingsystem.dto;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class ScheduleInformationDTO
{
   private long cost;
   private List<Long> jobIds;
   private long nextJobToRun;
   private LocalDateTime timeWhenNextTaskRun;
}
