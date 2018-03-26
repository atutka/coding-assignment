package com.akamai.processingsystem.schedule;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ScheduleInformation
{
   private long cost;
   private List<Long> jobIds;
   private long nextJobToRun;
   private LocalDateTime timeWhenNextTaskRun;

   public ScheduleInformation(long cost, List<Long> jobIds, long nextJobToRun, LocalDateTime timeWhenNextTaskRun)
   {
      this.cost = cost;
      this.jobIds = jobIds;
      this.nextJobToRun = nextJobToRun;
      this.timeWhenNextTaskRun = timeWhenNextTaskRun;
   }
}
