package com.akamai.processingsystem.schedule;

import com.akamai.processingsystem.dto.ScheduleInformationDTO;
import com.akamai.processingsystem.mapper.Mapper;
import org.springframework.stereotype.Component;

@Component
public class ScheduleInformationDTOMapper implements Mapper<ScheduleInformation, ScheduleInformationDTO>
{
   @Override
   public ScheduleInformationDTO mapper(ScheduleInformation scheduleInformation)
   {
      return new ScheduleInformationDTO(scheduleInformation.getCost(),
              scheduleInformation.getJobIds(),
              scheduleInformation.getNextJobToRun(),
              scheduleInformation.getTimeWhenNextTaskRun());
   }
}
