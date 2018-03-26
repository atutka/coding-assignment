package com.akamai.processingsystem.controller;

import com.akamai.processingsystem.dto.ScheduleDTO;
import com.akamai.processingsystem.dto.ScheduleInformationDTO;
import com.akamai.processingsystem.mapper.Mapper;
import com.akamai.processingsystem.schedule.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
public class ProcessingSystemController
{
   private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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
   public ScheduleDTO createSchedule(final @RequestPart(value = "file") MultipartFile file,
                                     final @RequestPart(value = "startDateTime") String startDateTime) throws Exception
   {
      LocalDateTime dateTime = LocalDateTime.parse(startDateTime, DATE_TIME_FORMATTER);
      CreateScheduleRequest request = new CreateScheduleRequest(file, dateTime);
      Schedule schedule = scheduleService.create(request);
      return scheduleDTOMapper.map(schedule);
   }

   @GetMapping(value = "/schedules/{id}")
   public ScheduleInformationDTO getScheduleInformation(@PathVariable("id") String scheduleId, @NotNull String timestamp)
   {
      LocalDateTime localDateTime = LocalDateTime.parse(timestamp, DATE_TIME_FORMATTER);
      UUID uuid = UUID.fromString(scheduleId);
      ScheduleInformationRequest request = new ScheduleInformationRequest(uuid, localDateTime);
      ScheduleInformation scheduleInformation = scheduleService.getScheduleInformation(request);
      return scheduleInformationDTOMapper.map(scheduleInformation);
   }

   @GetMapping(value = "/schedules/{id}/costs/max")
   public String getMaxInstantCost(@PathVariable("id") String scheduleId)
   {
      UUID uuid = UUID.fromString(scheduleId);
      return scheduleService.getMaximumInstantCost(uuid);
   }
}
