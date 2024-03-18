package io.quarkus.workshop.superheroes.fight.service;

import io.quarkus.workshop.superheroes.fight.FightLocation;
import io.quarkus.workshop.superheroes.fight.client.Hero;

import io.quarkus.workshop.superheroes.fight.client.Villain;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(description = "A request to perform a fight between one hero and one villain in a location")
public record FightRequest(@NotNull @Valid Hero hero, @NotNull @Valid Villain villain, FightLocation location) {
  public FightRequest(FightRequest fightRequest) {
    this(fightRequest.hero(), fightRequest.villain(), fightRequest.location());
  }
}
