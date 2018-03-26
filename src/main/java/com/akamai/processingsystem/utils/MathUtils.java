package com.akamai.processingsystem.utils;

import java.util.List;

public class MathUtils
{
   /**
    * Calculate Lowest Common Multipier for multiple values
    */
   public static int lcm(List<Integer> values)
   {
      int lcm = values.get(0);
      for (int i = 1; i < values.size(); i++)
      {
         lcm = lcm(lcm, values.get(i));
      }
      return lcm;
   }

   /**
    * Calculate Lowest Common Multiplier
    */
   public static int lcm(int a, int b)
   {
      return (a * b) / gcf(a, b);
   }

   /**
    * Calculate Greatest Common Factor
    */
   public static int gcf(int a, int b)
   {
      if (b == 0)
      {
         return a;
      } else
      {
         return (gcf(b, a % b));
      }
   }
}
