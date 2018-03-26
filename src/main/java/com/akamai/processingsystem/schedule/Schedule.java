package com.akamai.processingsystem.schedule;

import com.akamai.processingsystem.job.Job;
import com.google.common.collect.Range;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.akamai.processingsystem.utils.MathUtils.lcm;
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
              .filter(s -> s.getRange().contains((int) offset))
              .map(Slot::getJob)
              .collect(Collectors.toList());
   }

   public Job findNextWorkingJob(LocalDateTime localDateTime)
   {
      long offset = SECONDS.between(startDateTime, localDateTime) % maxPeriod;
      return slots.stream()
              .filter(s -> s.getRange().upperEndpoint() > offset && !s.getRange().contains((int) offset))
              .findFirst()
              .get()
              .getJob();
   }

   public LocalDateTime calculateNextWorkingJobTime(LocalDateTime localDateTime)
   {
      long offset = SECONDS.between(startDateTime, localDateTime) % maxPeriod;
      Slot slot = slots.stream()
              .filter(s -> s.getRange().upperEndpoint() > offset && !s.getRange().contains((int) offset))
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
      List<Integer> periods = jobs.stream()
              .map(Job::getPeriod)
              .collect(Collectors.toList());
      maxPeriod = lcm(periods);
      int[] costTable = new int[maxPeriod];
      List<Job> sortedJobs = jobs.stream()
              .sorted(Comparator.comparing(Job::getDuration)
                      .thenComparing(Comparator.comparing(Job::getPeriod))
                      .thenComparing(Collections.reverseOrder(Comparator.comparing(Job::getCost))))
              .collect(Collectors.toList());
      sortedJobs.forEach(j -> addSlot(j, costTable));
      slots.sort(slotComparator);
   }

   public void addSlot(Job job, int[] costTable)
   {
      int duration = job.getDuration();
      int period = job.getPeriod();
      int cost = job.getCost();
      if (slots.isEmpty())
      {
         int occurrences = maxPeriod / period;
         for (int i = 0; i < occurrences; i++)
         {
            int lowerBoundary = i * period;
            int upperBoundary = i * period + duration;
            Range<Integer> range = Range.closed(lowerBoundary, upperBoundary);
            Slot slot = new Slot(job, range);
            slots.add(slot);
            for (int j = lowerBoundary; j < upperBoundary; j++)
            {
               costTable[j] = cost;
            }
         }
         return;
      }
      List<Range<Integer>> ranges = findRanges(costTable, duration, period);
      List<Slot> preparedSlots = ranges.stream()
              .map(r -> new Slot(job, r))
              .collect(Collectors.toList());
      slots.addAll(preparedSlots);
      for (Range<Integer> range : ranges)
      {
         for (int i = range.lowerEndpoint(); i < range.upperEndpoint(); i++)
         {
            costTable[i] = costTable[i] + cost;
         }
      }
   }

   private List<Range<Integer>> findRanges(int[] costTable, int duration, int period)
   {
      Range<Integer> range = null;
      int minCost = 0x7fffffff;
      for (int i = 0; i < costTable.length; i = i + period)
      {
         int lowerBoundary = 0;
         for (int j = i; j < i + period; j++)
         {
            int upperBoundary = j + duration;
            if (upperBoundary > period)
            {
               continue;
            }
            int segmentCost = 0;
            for (int k = j; k < upperBoundary; k++)
            {
               segmentCost = segmentCost + costTable[k];
            }
            segmentCost = segmentCost / duration;
            if (segmentCost < minCost)
            {
               minCost = segmentCost;
               range = Range.closed(lowerBoundary, lowerBoundary + duration);
               if (minCost == 0)
               {
                  return prepareRangeResults(range, period);
               }
            }
            lowerBoundary++;
         }
      }
      return prepareRangeResults(range, period);
   }

   private List<Range<Integer>> prepareRangeResults(Range<Integer> range, int period)
   {
      List<Range<Integer>> results = new ArrayList<>();
      int lowerEndpoint = range.lowerEndpoint();
      int upperEndpoint = range.upperEndpoint();
      for (int i = 0; i < maxPeriod / period; i++)
      {
         Range<Integer> r = Range.closed(i * period + lowerEndpoint, i * period + upperEndpoint);
         results.add(r);
      }
      return results;
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
            if (o1.getRange().upperEndpoint() < o2.getRange().upperEndpoint())
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
      private Range<Integer> range;

      Slot(Job job, Range<Integer> range)
      {
         this.job = job;
         this.range = range;
      }
   }
}
