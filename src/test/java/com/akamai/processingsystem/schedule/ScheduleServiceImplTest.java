package com.akamai.processingsystem.schedule;

import com.akamai.processingsystem.job.Job;
import com.google.common.collect.ImmutableList;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.*;

@RunWith(EasyMockRunner.class)
public class ScheduleServiceImplTest
{
   private static final String UUID_ID= "1c6b5222-a07b-4eb7-bd0d-506fbfecd9a0";
   private CsvParser csvParser;

   private BeanListProcessor<Job> jobBeanListProcessor;

   private ScheduleServiceImpl scheduleService;

   @Mock
   private Schedule schedule;

   @Before
   public void setUp()
   {
      Map<UUID, Schedule> scheduleMap = new HashMap<>();
      scheduleMap.put(UUID.fromString(UUID_ID), schedule);
      jobBeanListProcessor = new BeanListProcessor<>(Job.class);
      CsvParserSettings settings = new CsvParserSettings();
      settings.getFormat().setLineSeparator("\n");
      settings.setProcessor(jobBeanListProcessor);
      settings.setHeaderExtractionEnabled(true);
      csvParser = new CsvParser(settings);
      scheduleService = new ScheduleServiceImpl(csvParser, jobBeanListProcessor);
      ReflectionTestUtils.setField(scheduleService, "scheduleMap", scheduleMap);
   }

   @Test
   public void testCreate() throws Exception
   {
      InputStream inputStream = new FileInputStream("src/test/resources/dataset.csv");
      MultipartFile file = createMock("file", MultipartFile.class);
      expect(file.getInputStream()).andReturn(inputStream);
      LocalDateTime localDateTime = LocalDateTime.of(2018, 3, 25, 10, 0 ,0);
      CreateScheduleRequest request = createMock("request", CreateScheduleRequest.class);
      expect(request.getFile()).andReturn(file);
      expect(request.getStartDateTime()).andReturn(localDateTime);
      replay(file, request);

      Schedule result = scheduleService.create(request);

      verify(file, request);
      assertThat(result).isNotNull();
      assertThat(result.getId()).isNotNull();
      assertThat(result.getStartDateTime()).isEqualTo(localDateTime);
   }

   @Test
   public void testGetScheduleInformation() throws Exception
   {
      LocalDateTime nextWorkingJobTime = LocalDateTime.of(2018, 3, 25, 10, 0, 0);
      long jobId = -1200L;
      Job job = createMock(Job.class);
      expect(job.getId()).andReturn(jobId);
      ScheduleInformationRequest request = createMock("request", ScheduleInformationRequest.class);
      UUID uuid = UUID.fromString(UUID_ID);
      LocalDateTime localDateTime = LocalDateTime.of(2018, 3, 25, 10, 0,0);
      expect(request.getScheduleId()).andReturn(uuid);
      expect(request.getTimestamp()).andReturn(localDateTime).times(3);
      expect(schedule.calculateNextWorkingJobTime(eq(localDateTime))).andReturn(nextWorkingJobTime);
      expect(schedule.findNextWorkingJob(eq(localDateTime))).andReturn(job);
      expect(schedule.findWorkingJobs(eq(localDateTime))).andReturn(ImmutableList.of());
      replay(schedule, request, job);

      ScheduleInformation result = scheduleService.getScheduleInformation(request);

      verify(schedule, request, job);
      assertThat(result).isNotNull();
      assertThat(result.getCost()).isEqualTo(0);
      assertThat(result.getJobIds()).isEqualTo(ImmutableList.of());
      assertThat(result.getNextJobToRun()).isEqualTo(jobId);
      assertThat(result.getTimeWhenNextTaskRun()).isEqualTo(nextWorkingJobTime);
   }

   @Test
   public void testGetMaximumInstantCost() throws Exception
   {
      String maxCostInstant = "5";
      UUID uuid = UUID.fromString(UUID_ID);
      expect(schedule.calculateMaxInstantCost()).andReturn(maxCostInstant);
      replay(schedule);

      String result = scheduleService.getMaximumInstantCost(uuid);

      verify(schedule);

      assertThat(result).isEqualTo(maxCostInstant);
   }

}