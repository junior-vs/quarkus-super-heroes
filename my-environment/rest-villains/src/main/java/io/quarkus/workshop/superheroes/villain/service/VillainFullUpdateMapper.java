package io.quarkus.workshop.superheroes.villain.service;


import io.quarkus.workshop.superheroes.villain.Villain;

import jakarta.enterprise.context.ApplicationScoped;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;


/**
 * Mapper to map all fields on an input {@link Villain} onto a target {@link Villain}.
 */
@Mapper
  (componentModel = ComponentModel.JAKARTA_CDI)
public interface VillainFullUpdateMapper {
  /**
   * Maps all fields except <code>id</code> from {@code input} onto {@code target}.
   * @param source The input {@link Villain}
   * @param target The target {@link Villain}
   */
  @Mapping(target = "id", ignore = true)
  void mapFullUpdate(Villain source, @MappingTarget Villain target);
}
