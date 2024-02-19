package io.quarkus.workshop.superheroes.hero;

import io.netty.handler.codec.DefaultHeaders.ValueValidator;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import jakarta.enterprise.context.ApplicationScoped;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;

import org.slf4j.Logger;

import java.util.List;

@ApplicationScoped
public class HeroService {

  private final HeroRepository repo;
  private final Logger logger;
  private final HeroPartialUpdateMapper heroPartialUpdateMapper;
  private final HeroFullUpdateMapper heroFullUpdateMapper;
  private final Validator validator;

  public HeroService(HeroRepository repo, Logger logger, HeroPartialUpdateMapper heroPartialUpdateMapper, HeroFullUpdateMapper heroFullUpdateMapper, Validator validator) {
    this.repo = repo;
    this.logger = logger;
    this.heroPartialUpdateMapper = heroPartialUpdateMapper;
    this.heroFullUpdateMapper = heroFullUpdateMapper;
      this.validator = validator;
  }

  Uni<Hero> findRandom() {
    logger.debug("Finding random hero");
    return this.repo.findRandom();
  }

  public Uni<List<Hero>> findAllHeroesHavingName(String name) {
    logger.debug("Finding all heroes having name = %s", name);
    return this.repo.listAllWhereNameLike(name);
  }

  public Uni<List<Hero>> findAllHeroes() {
    logger.debug("Getting all heroes");
    return this.repo.listAll();
  }

  public Uni<Hero> findHero(Long id) {
    logger.debug("Finding hero with id = %s", id);
    return this.repo.findById(id);
  }

  @WithTransaction
  public Uni<Hero> create(Hero hero) {
    logger.debug("Persisting hero: %s", hero);
    return this.repo.persist(hero);
  }

  @WithTransaction
  public Uni<Hero> replaceHero(@NotNull @Valid Hero hero) {
    logger.debug("Replacing hero: %s", hero);
    return this.repo.findById(hero.getId()).onItem().ifNotNull().transform(h -> {
      this.heroFullUpdateMapper.mapFullUpdate(hero, h);
      return h;
    });
  }

  @WithTransaction
  public Uni<Hero> partialUpdateHero(@NotNull Hero hero) {
    logger.debug("Partially updating hero: %s", hero);
    return this.repo.findById(hero.getId()).onItem().ifNotNull().transform(h -> {
      this.heroPartialUpdateMapper.mapPartialUpdate(hero, h);
      return h;
    }).onItem().ifNotNull().transform(this::validatePartialUpdate);
  }

  private Hero validatePartialUpdate(Hero hero) {
    var violations = this.validator.validate(hero);

    if ((violations != null) && !violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }

    return hero;
  }

  @WithTransaction
  public Uni<Void> replaceAllHeroes(List<Hero> heroes) {
    logger.debug("Replacing all heroes");
    return deleteAllHeroes()
      .replaceWith(this.repo.persist(heroes));
  }

  @WithTransaction
  public Uni<Void> deleteHero(Long id) {
    logger.debug("Deleting hero by id = %d", id);
    return this.repo.deleteById(id).replaceWithVoid();
  }
  @WithTransaction
  public Uni<Void> deleteAllHeroes() {
    logger.debug("Deleting all heroes");
    return this.repo.listAll()
      .onItem().transformToMulti(list -> Multi.createFrom().iterable(list))
      .map(Hero::getId)
      .onItem().transformToUniAndMerge(this::deleteHero)
      .collect().asList()
      .replaceWithVoid();
  }



}
