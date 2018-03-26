package com.akamai.processingsystem.controller;

import com.akamai.processingsystem.dto.ScheduleDTO;
import com.akamai.processingsystem.dto.ScheduleInformationDTO;
import com.akamai.processingsystem.mapper.Mapper;
import com.akamai.processingsystem.schedule.*;
import org.easymock.Capture;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.*;

@RunWith(EasyMockRunner.class)
public class ProcessingSystemControllerTest
{
   @Mock
   private ScheduleService scheduleService;

   @Mock(fieldName = "scheduleDTOMapper")
   private Mapper<Schedule, ScheduleDTO> scheduleDTOMapper;

   @Mock(fieldName = "scheduleInformationDTOMapper")
   private Mapper<ScheduleInformation, ScheduleInformationDTO> scheduleInformationDTOMapper;

   @TestSubject
   private ProcessingSystemController controller = new ProcessingSystemController(scheduleService, scheduleDTOMapper, scheduleInformationDTOMapper);

   @Test
   public void testCreateSchedule() throws Exception
   {
      MultipartFile multipartFile = createMock("multipartFile", MultipartFile.class);
      String dateTime = "2018-03-25 09:00:00";
      Schedule schedule = createMock("schedule", Schedule.class);
      ScheduleDTO scheduleDTO = createMock("scheduleDTO", ScheduleDTO.class);
      Capture<CreateScheduleRequest> createScheduleRequestCapture = newCapture();
      expect(scheduleService.create(capture(createScheduleRequestCapture))).andReturn(schedule);
      expect(scheduleDTOMapper.map(same(schedule))).andReturn(scheduleDTO);

      replay(schedule, scheduleDTO, multipartFile, scheduleService, scheduleDTOMapper);

      ScheduleDTO result = controller.createSchedule(multipartFile, dateTime);

      verify(schedule, scheduleDTO, multipartFile, scheduleService, scheduleDTOMapper);

      assertThat(result).isSameAs(scheduleDTO);
      CreateScheduleRequest createScheduleRequest = createScheduleRequestCapture.getValue();
      assertThat(createScheduleRequest.getFile()).isSameAs(multipartFile);
      assertThat(createScheduleRequest.getStartDateTime())
              .isEqualTo(LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
   }

   @Test
   public void testGetScheduleInformation() throws Exception
   {
      String scheduleId = "1c6b5222-a07b-4eb7-bd0d-506fbfecd9a0";
      String timestamp = "2018-03-25 09:00:00";
      ScheduleInformation scheduleInformation = createMock("scheduleInformation", ScheduleInformation.class);
      ScheduleInformationDTO scheduleInformationDTO = createMock("scheduleInformationDTO", ScheduleInformationDTO.class);

      Capture<ScheduleInformationRequest> scheduleInformationRequestCapture = newCapture();
      expect(scheduleService.getScheduleInformation(capture(scheduleInformationRequestCapture))).andReturn(scheduleInformation);
      expect(scheduleInformationDTOMapper.map(same(scheduleInformation))).andReturn(scheduleInformationDTO);

      replay(scheduleService, scheduleInformationDTOMapper, scheduleInformation, scheduleInformationDTO);

      ScheduleInformationDTO result = controller.getScheduleInformation(scheduleId, timestamp);

      verify(scheduleService, scheduleInformationDTOMapper, scheduleInformation, scheduleInformationDTO);

      assertThat(result).isSameAs(scheduleInformationDTO);
      ScheduleInformationRequest scheduleInformationRequest = scheduleInformationRequestCapture.getValue();
      assertThat(scheduleInformationRequest.getScheduleId().toString()).isEqualTo(scheduleId);
      assertThat(scheduleInformationRequest.getTimestamp())
              .isEqualTo(LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
   }

   @Test
   public void testGetMaxInstantCost() throws Exception
   {
      String scheduleId = "1c6b5222-a07b-4eb7-bd0d-506fbfecd9a0";
      String maxInstantCost = "5";

      Capture<UUID> uuidCapture = newCapture();
      expect(scheduleService.getMaximumInstantCost(capture(uuidCapture))).andReturn(maxInstantCost);
      replay(scheduleService);

      String result = controller.getMaxInstantCost(scheduleId);

      verify(scheduleService);

      assertThat(result).isEqualTo(maxInstantCost);
   }

}