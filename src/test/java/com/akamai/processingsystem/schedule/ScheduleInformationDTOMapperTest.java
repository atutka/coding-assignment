package com.akamai.processingsystem.schedule;

import com.akamai.processingsystem.dto.ScheduleInformationDTO;
import com.google.common.collect.ImmutableList;
import org.easymock.EasyMockRunner;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.*;

@RunWith(EasyMockRunner.class)
public class ScheduleInformationDTOMapperTest
{
   @TestSubject
   private ScheduleInformationDTOMapper mapper = new ScheduleInformationDTOMapper();

   @Test
   public void testMap()
   {
      long cost = 100L;
      List<Long> jobIds = ImmutableList.of(10L);
      long nextJob = 101L;
      LocalDateTime localDateTime = LocalDateTime.of(2018, 3, 25, 10, 0, 0);
      ScheduleInformation scheduleInformation = createMock("scheduleInformation", ScheduleInformation.class);
      expect(scheduleInformation.getCost()).andReturn(cost);
      expect(scheduleInformation.getJobIds()).andReturn(jobIds);
      expect(scheduleInformation.getNextJobToRun()).andReturn(nextJob);
      expect(scheduleInformation.getTimeWhenNextTaskRun()).andReturn(localDateTime);
      replay(scheduleInformation);

      ScheduleInformationDTO result = mapper.map(scheduleInformation);

      verify(scheduleInformation);
      assertThat(result).isNotNull();
      assertThat(result.getCost()).isSameAs(cost);
      assertThat(result.getJobIds()).containsAll(jobIds);
      assertThat(result.getNextJobToRun()).isSameAs(nextJob);
      assertThat(result.getTimeWhenNextTaskRun()).isEqualTo(localDateTime);
   }
}