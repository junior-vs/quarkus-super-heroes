package io.quarkus.workshop.superheroes.fight.service;

import io.quarkus.workshop.superheroes.fight.Fight;
import io.quarkus.workshop.superheroes.fight.FightBuilder;
import io.quarkus.workshop.superheroes.fight.Fighters;

import io.smallrye.mutiny.Uni;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import jakarta.validation.Valid;

import org.bson.types.ObjectId;
import org.slf4j.Logger;

import java.time.Instant;
import java.util.List;
import java.util.Random;

import static jakarta.transaction.Transactional.TxType.SUPPORTS;

@ApplicationScoped
@Transactional(SUPPORTS)
public class FightService {

  private final Logger logger;
  private final Random random = new Random();
  private FightConfig fightConfig;

  public FightService(Logger logger) {
      this.logger = logger;
  }

  public Uni<List<Fight>> findAllFights() {
    logger.debug("Getting all fights");
    return Fight.listAll();
  }

  public Uni<Fight> findFightById(String id) {
    logger.info("Getting fight with id: " + id);
    return Fight.findById(new ObjectId(id));
  }

  public Uni<Fighters> findRandomFighters() {
    logger.info("Getting random fighters");
    // TODO will be implemented
    return null;
  }

  public Uni<Fight> persitFight(Fight fight) {
    logger.info("Persisting fight");
    // TODO will be improved
    return Fight.persist(fight).replaceWith(fight)
      //.map(this.fitghtMapper::toShecma)
      .invoke(f -> this.logger.info("Persisted fight: " + f)).replaceWith(fight);
  }

  Uni<Fight> determineWinner(@Valid FightRequest fightRequest) {
    logger.debug("Determining winner between fighters: %s", fightRequest);

    // Amazingly fancy logic to determine the winner...
    return Uni.createFrom().item(() -> {
      Fight fight;

      if (shouldHeroWin(fightRequest)) {
        fight = heroWonFight(fightRequest);
      }
      else if (shouldVillainWin(fightRequest)) {
        fight = villainWonFight(fightRequest);
      }
      else {
        fight = getRandomWinner(fightRequest);
      }
      return fight;
    }).invoke(this::persitFight);
  }

  private Fight getRandomWinner(FightRequest fightRequest) {
    return this.random.nextBoolean() ? heroWonFight(fightRequest) : villainWonFight(fightRequest);
  }

  private Fight villainWonFight(FightRequest fightRequest) {
    return new FightBuilder()
      .setWinnerName(fightRequest.villain().name())
      .setWinnerPicture(fightRequest.villain().picture())
      .setWinnerLevel(fightRequest.villain().level())
      .setWinnerPowers(fightRequest.villain().powers())
      .setLoserName(fightRequest.hero().name())
      .setLoserPicture(fightRequest.hero().picture())
      .setLoserLevel(fightRequest.hero().level())
      .setLoserPowers(fightRequest.hero().powers())
      .setWinnerTeam(this.fightConfig.villain().teamName())
      .setLoserTeam(this.fightConfig.hero().teamName())
      .setLocation(fightRequest.location())
      .setFightDate(Instant.now())
      .createFight();

  }

  private boolean shouldVillainWin(FightRequest fightRequest) {
    return fightRequest.hero().level() < fightRequest.villain().level();
  }

  private Fight heroWonFight(FightRequest fightRequest) {
    logger.info("Yes, Hero %s won over %s :o)", fightRequest.hero().name(), fightRequest.villain().name());

    return new FightBuilder()
      .setWinnerName(fightRequest.hero().name())
      .setWinnerPicture(fightRequest.hero().picture())
      .setWinnerLevel(fightRequest.hero().level())
      .setWinnerPowers(fightRequest.hero().powers())
      .setLoserName(fightRequest.villain().name())
      .setLoserPicture(fightRequest.villain().picture())
      .setLoserLevel(fightRequest.villain().level())
      .setLoserPowers(fightRequest.villain().powers())
      .setWinnerTeam(this.fightConfig.hero().teamName())
      .setLoserTeam(this.fightConfig.villain().teamName())
      .setLocation(fightRequest.location())
      .setFightDate(Instant.now())
      .createFight();

  }

  private boolean shouldHeroWin(FightRequest fightRequest) {
    int heroAdjust = this.random.nextInt(this.fightConfig.hero().adjustBound());
    int villainAdjust = this.random.nextInt(this.fightConfig.villain().adjustBound());

    return (fightRequest.hero().level() + heroAdjust) > (fightRequest.villain().level() + villainAdjust);
  }
}
