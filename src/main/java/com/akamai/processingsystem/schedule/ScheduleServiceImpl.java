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
      List<Job> performedJobs = schedule.findWorkingJobs(request.getTimestamp());
      long cost = performedJobs.stream().mapToInt(Job::getCost).sum();
      List<Long> jobsIds = performedJobs.stream().map(Job::getId).collect(Collectors.toList());
      long nextTask = schedule.findNextWorkingJob(request.getTimestamp()).getId();
      LocalDateTime nextTaskDateTime = schedule.calculateNextWorkingJobTime(request.getTimestamp());
      return new ScheduleInformation(cost, jobsIds, nextTask, nextTaskDateTime);
//      int maxPeriod = jobs.stream().max(Comparator.comparingInt(Job::getPeriod)).get().getPeriod();
//      sx.setMaxPeriod(maxPeriod);
//      NavigableMap<Integer, List<Job>> jobsByPeriod = jobs.stream()
//              .collect(Collectors.groupingBy(Job::getPeriod, TreeMap::new, Collectors.toList()));
//      for(Map.Entry<Integer, List<Job>> entry : jobsByPeriod.entrySet())
//      {
//         Integer period = entry.getKey();
//         List<Job> jobList = entry.getValue().stream().sorted((j1, j2)-> Integer.compare(j2.getCost(), j1.getCost())).collect(Collectors.toList());
//         jobList.forEach(sx::addSlot);
//
//      }
//      List<Job> minimalPerionJobs = jobs.stream().sorted((j1, j2)-> Integer.compare(j2.getCost(), j1.getCost())).collect(Collectors.toList());
//      return null;
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
