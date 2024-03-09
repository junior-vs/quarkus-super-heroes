package io.quarkus.workshop.superheroes.villain.service;


import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import io.quarkus.workshop.superheroes.villain.model.Villain;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;


/**
 * Mapper to map <code><strong>non-null</strong></code> fields on an input {@link Villain} onto a target {@link Villain}.
 */
@Mapper(componentModel = ComponentModel.JAKARTA_CDI, nullValuePropertyMappingStrategy = IGNORE)
public interface VillainPartialUpdateMapper {
  /**
   * Maps all <code><strong>non-null</strong></code> fields from {@code input} onto {@code target}.
   * @param input The input {@link Villain}
   * @param target The target {@link Villain}
   */
  void mapPartialUpdate(Villain input, @MappingTarget Villain target);
}
