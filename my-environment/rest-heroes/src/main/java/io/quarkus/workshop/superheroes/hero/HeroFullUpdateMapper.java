package io.quarkus.workshop.superheroes.hero;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = ComponentModel.JAKARTA_CDI)
public interface HeroFullUpdateMapper {
  /**
   * Maps all fields except <code>id</code> from {@code input} onto {@code target}.
   * @param input The input {@link Hero}
   * @param target The target {@link Hero}
   */
  @Mapping(target = "id", ignore = true)
  void mapFullUpdate(Hero input, @MappingTarget Hero target);
}
