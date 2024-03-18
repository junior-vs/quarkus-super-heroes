package io.quarkus.workshop.superheroes.fight.client;

import jakarta.validation.constraints.NotBlank;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "The villain fighting against the hero")
public record Villain(@NotBlank String name, @NotBlank int level, @NotBlank String picture, String powers) {
  public Villain(Villain villain) {
    this(villain.name(), villain.level(), villain.picture(), villain.powers());
  }
}
