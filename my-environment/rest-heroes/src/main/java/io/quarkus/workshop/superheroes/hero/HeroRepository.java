package io.quarkus.workshop.superheroes.hero;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;

import io.quarkus.hibernate.reactive.panache.common.WithSession;

import io.smallrye.mutiny.Uni;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Random;

@ApplicationScoped
@WithSession
public class HeroRepository implements PanacheRepository<Hero> {

  Uni<Hero> findRandom() {
    return count()
      .map(count -> (count > 0) ? count: null)
      .onItem().ifNotNull().transform(count -> new Random().nextInt(count.intValue()))
      .onItem().ifNotNull().transformToUni(randomHero -> findAll().page(randomHero, 1).firstResult());
  }

  Uni<List<Hero>> listAllWhereNameLike(String name) {
    return (name != null) ?
           list("LOWER(name) LIKE CONCAT('%', ?1, '%')", name.toLowerCase()) :
           Uni.createFrom().item(List::of);

  }

}
