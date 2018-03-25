package com.akamai.processingsystem.job;

import com.univocity.parsers.annotations.Parsed;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Job
{
   @Parsed(index = 0)
   private long id;

   @Parsed(index = 1)
   private int period;

   @Parsed(index = 2)
   private int duration;

   @Parsed(index = 3)
   private int cost;
}
