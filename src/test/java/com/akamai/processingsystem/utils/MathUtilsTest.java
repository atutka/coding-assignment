package com.akamai.processingsystem.utils;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MathUtilsTest
{
   @Test
   public void testCalculateLcm() throws Exception
   {
      int a = 8;
      int b = 10;
      int c = 4;
      int result = MathUtils.lcm(ImmutableList.of(a, b, c));

      assertThat(result).isEqualTo(40);
   }

   @Test
   public void testLcm() throws Exception
   {
      int a = 8;
      int b = 10;
      int result = MathUtils.lcm(a, b);

      assertThat(result).isEqualTo(40);
   }

   @Test
   public void testGcf() throws Exception
   {
      int a = 8;
      int b = 10;
      int result = MathUtils.gcf(a, b);

      assertThat(result).isEqualTo(2);
   }

}