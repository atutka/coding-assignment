package com.akamai.processingsystem.schedule;

import com.akamai.processingsystem.dto.ScheduleDTO;
import org.easymock.EasyMockRunner;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.*;

@RunWith(EasyMockRunner.class)
public class ScheduleDTOMapperTest
{
   @TestSubject
   private ScheduleDTOMapper mapper = new ScheduleDTOMapper();

   @Test
   public void testMap()
   {
      Schedule schedule = createMock("schedule", Schedule.class);
      UUID uuid = UUID.fromString("1c6b5222-a07b-4eb7-bd0d-506fbfecd9a0");
      expect(schedule.getId()).andReturn(uuid);
      replay(schedule);

      ScheduleDTO result = mapper.map(schedule);

      verify(schedule);
      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo(uuid.toString());
   }
}