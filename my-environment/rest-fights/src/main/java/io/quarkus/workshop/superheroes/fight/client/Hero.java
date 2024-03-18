package io.quarkus.workshop.superheroes.fight.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

@Schema(description = "The hero fighting against the villain")
public record Hero(@NotBlank String name, @NotNull int level, @NotBlank String picture, String powers) {
  public Hero(Hero hero) {
    this(hero.name(), hero.level(), hero.picture(), hero.powers());
  }
}
