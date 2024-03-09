package io.quarkus.workshop.superheroes.hero;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = ComponentModel.JAKARTA_CDI, nullValuePropertyMappingStrategy = IGNORE)
public interface HeroPartialUpdateMapper {
  /**
   * Maps all <code><strong>non-null</strong></code> fields from {@code input} onto {@code target}.
   *
   * @param input  The input {@link Hero}
   * @param target The target {@link Hero}
   */
  void mapPartialUpdate(Hero input, @MappingTarget Hero target);
}
