package com.akamai.processingsystem.mapper;

@FunctionalInterface
public interface Mapper<SOURCE, TARGET>
{
   /**
    * Maps one object to another
    * @param source
    *    source object to map from
    * @return
    *    new object mapped to
    */
   TARGET mapper(SOURCE source);

}
