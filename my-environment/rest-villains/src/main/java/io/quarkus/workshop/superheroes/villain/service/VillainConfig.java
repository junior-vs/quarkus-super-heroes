package io.quarkus.workshop.superheroes.villain.service;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "villain")
public interface VillainConfig {

  Level level();

  interface Level {
    /**
     * The <code>villain.level.multiplier</code> configuration item. Defaults to <code>1.0</code>.
     */
    @WithDefault("1.0")
    double multiplier();
  }


}
