package com.akamai.processingsystem.schedule;

import com.akamai.processingsystem.job.Job;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService
{
   private CsvParser csvParser;
   private BeanListProcessor<Job> jobBeanListProcessor;
   private Map<UUID, Schedule> scheduleMap = new HashMap<>();

   public ScheduleServiceImpl(CsvParser csvParser, BeanListProcessor<Job> jobBeanListProcessor)
   {
      this.csvParser = csvParser;
      this.jobBeanListProcessor = jobBeanListProcessor;
   }

   @Override
   public Schedule create(CreateScheduleRequest request) throws IOException
   {
      MultipartFile file = request.getFile();
      InputStreamReader inputStreamReader = new InputStreamReader(file.getInputStream());
      csvParser.parse(inputStreamReader);
      List<Job> jobs = jobBeanListProcessor.getBeans();

      UUID id = UUID.randomUUID();
      LocalDateTime startDateTime = request.getStartDateTime();
      Schedule schedule = new Schedule(id, startDateTime, jobs);
      scheduleMap.put(id, schedule);
      return schedule;
   }

   @Override
   public ScheduleInformation getScheduleInformation(ScheduleInformationRequest request)
   {
      Schedule schedule = scheduleMap.get(request.getScheduleId());
      if(schedule == null)
      {
         throw new IllegalArgumentException();
      }
      List<Job> workingJobs = schedule.findWorkingJobs(request.getTimestamp());
      long cost = workingJobs.stream()
              .mapToInt(Job::getCost)
              .sum();
      List<Long> jobsIds = workingJobs.stream()
              .map(Job::getId)
              .collect(Collectors.toList());
      long nextTask = schedule.findNextWorkingJob(request.getTimestamp()).getId();
      LocalDateTime nextTaskDateTime = schedule.calculateNextWorkingJobTime(request.getTimestamp());
      return new ScheduleInformation(cost, jobsIds, nextTask, nextTaskDateTime);
   }

   @Override
   public String getMaximumInstantCost(UUID scheduleId)
   {
      Schedule schedule = scheduleMap.get(scheduleId);
      if(schedule == null)
      {
         throw new IllegalArgumentException();
      }
      return schedule.calculateMaxInstantCost();
   }

}
