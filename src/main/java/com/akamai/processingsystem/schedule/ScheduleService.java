package com.akamai.processingsystem.schedule;

import java.io.IOException;
import java.util.UUID;

public interface ScheduleService
{
   /**
    * Creates schedule based on request
    * @param request
    *    information for create request
    * @return
    *    created {@link Schedule}
    * @throws IOException
    *    occurs when there is a problem with parsing file
    */
   Schedule create(CreateScheduleRequest request) throws IOException;

   /**
    * Gives information about schedule requested
    * @param request
    *    basic schedule information to lookup
    * @return
    *    information about schedul {@link ScheduleInformation}
    */
   ScheduleInformation getScheduleInformation(ScheduleInformationRequest request);

   /**
    * Gives information about maximum instant cost
    * @param scheduleId
    *    schedule identification
    * @return
    *    maximum instant cost
    */
   String getMaximumInstantCost(UUID scheduleId);
}
