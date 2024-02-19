package io.quarkus.workshop.superheroes.shared.config;

import jakarta.enterprise.inject.Produces;

import jakarta.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerProducer {

  @Produces
  public Logger produceLog(InjectionPoint injectionPoint) {
    return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
  }
}
