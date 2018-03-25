package com.akamai.processingsystem.schedule;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class ScheduleInformation
{
   private long cost;
   private List<Long> jobIds;
   private long nextJobToRun;
   private LocalDateTime timeWhenNextTaskRun;
}
