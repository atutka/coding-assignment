package com.akamai.processingsystem.schedule;

import com.akamai.processingsystem.job.Job;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.easymock.EasyMockRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(EasyMockRunner.class)
public class ScheduleTest
{
   private Schedule schedule;

   @Before
   public  void setUp() throws FileNotFoundException
   {
      InputStream inputStream = new FileInputStream("src/test/resources/dataset.csv");
      BeanListProcessor<Job> jobBeanListProcessor = new BeanListProcessor<>(Job.class);
      CsvParserSettings settings = new CsvParserSettings();
      settings.getFormat().setLineSeparator("\n");
      settings.setProcessor(jobBeanListProcessor);
      settings.setHeaderExtractionEnabled(true);
      CsvParser csvParser = new CsvParser(settings);
      csvParser.parse(inputStream);
      List<Job> jobs = jobBeanListProcessor.getBeans();
      UUID uuid = UUID.randomUUID();
      LocalDateTime localDateTime = LocalDateTime.of(2018, 3, 25, 10, 0 ,0);
      schedule = new Schedule(uuid, localDateTime, jobs);
   }

   @Test
   public void testFindWorkingJobs()
   {
      LocalDateTime localDateTime = LocalDateTime.of(2018, 3, 25, 10, 0, 2);
      List<Job> jobs = schedule.findWorkingJobs(localDateTime);

      List<Long> jobIds = jobs.stream()
              .map(Job::getId)
              .collect(Collectors.toList());
      assertThat(jobIds).containsExactly(1L);
   }

   @Test
   public void testFindNextWorkingJob()
   {
      LocalDateTime localDateTime = LocalDateTime.of(2018, 3, 25, 10, 0, 2);
      Job job = schedule.findNextWorkingJob(localDateTime);
      assertThat(job.getId()).isSameAs(2L);
   }

   @Test
   public void testCalculateNextWorkingJobTime()
   {
      LocalDateTime localDateTime = LocalDateTime.of(2018, 3, 25, 10, 0, 2);
      LocalDateTime result = schedule.calculateNextWorkingJobTime(localDateTime);
      assertThat(result).isEqualTo(LocalDateTime.of(2018, 3, 25, 10, 0 , 3));
   }

   @Test
   public void testCalculateMaxInstantCost()
   {
      String result = schedule.calculateMaxInstantCost();
      assertThat(result).isEqualTo("5");
   }

}