package io.quarkus.workshop.superheroes.villain;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

import jakarta.enterprise.event.Observes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VillainApplicationLifeCycle {


  private static final Logger LOGGER = LoggerFactory.getLogger(VillainApplicationLifeCycle.class);


  void onStart(@Observes StartupEvent ev) {
    LOGGER.info(" __     ___ _ _       _             _    ____ ___ ");
    LOGGER.info(" \\ \\   / (_) | | __ _(_)_ __       / \\  |  _ \\_ _|");
    LOGGER.info("  \\ \\ / /| | | |/ _` | | '_ \\     / _ \\ | |_) | | ");
    LOGGER.info("   \\ V / | | | | (_| | | | | |   / ___ \\|  __/| | ");
    LOGGER.info("    \\_/  |_|_|_|\\__,_|_|_| |_|  /_/   \\_\\_|  |___|");
  }

  void onStop(@Observes ShutdownEvent ev) {
    LOGGER.info("The application VILLAIN is stopping...");
  }
}
