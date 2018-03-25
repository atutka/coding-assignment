package com.akamai.processingsystem.schedule;

import com.akamai.processingsystem.job.Job;
import com.google.common.collect.Range;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.akamai.processingsystem.utils.MathUtils.calculateLcm;
import static java.time.temporal.ChronoUnit.SECONDS;

public class Schedule
{
   private static final Comparator<Slot> slotComparator = new SlotComparator();
   private UUID id;
   private LocalDateTime startDateTime;
   private List<Slot> slots = new ArrayList<>();
   private int maxPeriod;

   public Schedule(UUID id, LocalDateTime startDateTime, List<Job> jobs)
   {
      this.id = id;
      this.startDateTime = startDateTime;
      calculateJobsToSlots(jobs);
   }

   public List<Job> findWorkingJobs(LocalDateTime localDateTime)
   {
      long offset = SECONDS.between(startDateTime, localDateTime) % maxPeriod;
      return slots.stream()
              .filter(s -> s.getRange().contains(offset))
              .map(Slot::getJob)
              .collect(Collectors.toList());
   }

   public Job findNextWorkingJob(LocalDateTime localDateTime)
   {
      long offset = SECONDS.between(startDateTime, localDateTime) % maxPeriod;
      return slots.stream()
              .filter(s -> s.getRange().upperEndpoint() > offset && !s.getRange().contains(offset))
              .findFirst()
              .get()
              .getJob();
   }

   public LocalDateTime calculateNextWorkingJobTime(LocalDateTime localDateTime)
   {
      long offset = SECONDS.between(startDateTime, localDateTime) % maxPeriod;
      Slot slot = slots.stream()
              .filter(s -> s.getRange().upperEndpoint() > offset && !s.getRange().contains(offset))
              .findFirst()
              .get();
      localDateTime = localDateTime.minusSeconds(offset);
      return localDateTime.plusSeconds(slot.getRange().lowerEndpoint());
   }

   public String calculateMaxInstantCost()
   {
      return slots.stream().collect(Collectors.groupingBy(s -> s.getRange().lowerEndpoint(), Collectors
              .summingInt(s -> s.getJob().getCost()))).values().stream().max(Comparator.comparingInt(Integer::valueOf)).get().toString();
   }

//   public void addSlot(Job job)
//   {
//      int duration = job.getDuration();
//      int period = job.getPeriod();
//      int cost = job.getCost();
//      if(slots.isEmpty())
//      {
//         int occurences = maxPeriod/period;
//         for (int i=0; i<occurences; i++)
//         {
//            slots.add(new Slot(job.getId(), i*period, i*period+duration, cost));
//         }
//         return;
//      }
////      for(Map.Entry<Integer, List<Slot>> entry : slots.entrySet())
////      {
////         List<Slot> slots = entry.getValue();
////         int localMaxPeriod = slots.stream().filter(s -> s.range.getMaximum() < period).map(Slot::getRange).map(ValueRange::getMaximum).mapToInt
////                 (Long::intValue).max()
////                 .getAsInt();
////         if(period - localMaxPeriod >= duration)
////         {
////            int occurences = maxPeriod/period;
////            for (int i=0; i<occurences; i++)
////            {
////               slots.add(new Slot(job.getId(), i*period + localMaxPeriod, i*period + localMaxPeriod+duration, cost));
////            }
////            return;
////         }
////      }
//      level++;
//      int occurences = maxPeriod/period;
//      for (int i=0; i<occurences; i++)
//      {
//         slots.add(new Slot(job.getId(), maxPeriod-i*period - duration, maxPeriod-i*period, job.getCost()));
//      }
//   }

   public UUID getId()
   {
      return id;
   }

   public void setId(UUID id)
   {
      this.id = id;
   }

   public LocalDateTime getStartDateTime()
   {
      return startDateTime;
   }

   public void setStartDateTime(LocalDateTime startDateTime)
   {
      this.startDateTime = startDateTime;
   }

   private void calculateJobsToSlots(List<Job> jobs)
   {
      jobs.forEach(this::addSlot);
      List<Integer> periods = jobs.stream()
              .map(Job::getPeriod)
              .collect(Collectors.toList());
      slots.sort(slotComparator);
      maxPeriod = calculateLcm(periods);
   }

   private void addSlot(Job job)
   {
      int duration = job.getDuration();
      int period = job.getPeriod();
      int occurrences = maxPeriod / period;
      for (int i = 0; i < occurrences; i++)
      {
         Slot slot = new Slot(job, i * period, i * period + duration);
         slots.add(slot);
      }
   }

   private static class SlotComparator implements Comparator<Slot>
   {

      @Override
      public int compare(Slot o1, Slot o2)
      {
         if (o1.getRange().lowerEndpoint() > o2.getRange().lowerEndpoint())
         {
            return 1;
         }
         if (o1.getRange().lowerEndpoint().equals(o2.getRange().lowerEndpoint()))
         {
            if (o1.getRange().upperEndpoint() > o2.getRange().upperEndpoint())
            {
               return -1;
            }
            if(o1.getRange().upperEndpoint() < o2.getRange().upperEndpoint())
            {
               return 1;
            }
            return 0;
         }
         return -1;
      }
   }

   @Getter
   @Setter
   private static class Slot
   {
      private Job job;
      private Range<Long> range;

      Slot(Job job, long x1, long x2)
      {
         this.job = job;
         this.range = Range.closed(x1, x2);
      }
   }
}
