package io.quarkus.workshop.superheroes.fight;

import io.quarkus.workshop.superheroes.fight.client.Hero;
import io.quarkus.workshop.superheroes.fight.client.Villain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "A fight between one hero and one villain")
public record Fighters(@NotNull @Valid Hero hero, @NotNull @Valid Villain villain) {
  public Fighters(Fighters fighters) {
    this(fighters.hero(), fighters.villain());
  }
}
