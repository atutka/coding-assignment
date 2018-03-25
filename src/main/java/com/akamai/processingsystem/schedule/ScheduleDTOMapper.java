package com.akamai.processingsystem.schedule;

import com.akamai.processingsystem.dto.ScheduleDTO;
import com.akamai.processingsystem.mapper.Mapper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ScheduleDTOMapper implements Mapper<Schedule, ScheduleDTO>
{
   @Override
   public ScheduleDTO mapper(Schedule schedule)
   {
      ScheduleDTO scheduleDTO = new ScheduleDTO();
      UUID id = schedule.getId();
      scheduleDTO.setId(id.toString());
      return scheduleDTO;
   }
}
