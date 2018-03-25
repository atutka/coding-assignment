package com.akamai.processingsystem.controller;

import com.akamai.processingsystem.dto.ScheduleDTO;
import com.akamai.processingsystem.dto.ScheduleInformationDTO;
import com.akamai.processingsystem.mapper.Mapper;
import com.akamai.processingsystem.schedule.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
public class ProcessingSystemController
{
   private ScheduleService scheduleService;
   private Mapper<Schedule, ScheduleDTO> scheduleDTOMapper;
   private Mapper<ScheduleInformation, ScheduleInformationDTO> scheduleInformationDTOMapper;

   public ProcessingSystemController(ScheduleService scheduleService, Mapper<Schedule, ScheduleDTO> scheduleDTOMapper,
                                     Mapper<ScheduleInformation, ScheduleInformationDTO> scheduleInformationDTOMapper)
   {
      this.scheduleService = scheduleService;
      this.scheduleDTOMapper = scheduleDTOMapper;
      this.scheduleInformationDTOMapper = scheduleInformationDTOMapper;
   }

   @PutMapping(value = "/schedules", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   public ScheduleDTO createSchedule(final @RequestPart(value = "file", required = false) MultipartFile file,
                                     final @RequestPart(value = "startDateTime", required = false) String startDateTime) throws Exception
   {
      CreateScheduleRequest request = new CreateScheduleRequest(file, LocalDateTime.parse(startDateTime));
      Schedule schedule = scheduleService.create(request);
      return scheduleDTOMapper.mapper(schedule);
   }

   @GetMapping(value = "/schedules/{id}")
   public ScheduleInformationDTO getScheduleInformation(@PathVariable("id") String scheduleId, String timestamp)
   {
      ScheduleInformationRequest request = new ScheduleInformationRequest(UUID.fromString(scheduleId), LocalDateTime.parse(timestamp));
      ScheduleInformation scheduleInformation = scheduleService.getScheduleInformation(request);
      return scheduleInformationDTOMapper.mapper(scheduleInformation);
   }

   @GetMapping(value = "/schedules/{id}/costs")
   public String getMaxInstantCost(@PathVariable("id") String scheduleId)
   {
      return scheduleService.getMaximumInstantCost(UUID.fromString(scheduleId));
   }
}
