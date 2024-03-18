package io.quarkus.workshop.superheroes.fight;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;
import java.util.Objects;

@MongoEntity(collection = "Fights")
@Schema(description = "Each fight has a winner, a loser, and a location")
public class Fight extends ReactivePanacheMongoEntity {

  @NotNull Instant fightDate;
  @NotEmpty String winnerName;
  @NotNull Integer winnerLevel;
  @NotEmpty String winnerPowers;
  @NotEmpty String winnerPicture;
  @NotEmpty String loserName;
  @NotNull Integer loserLevel;
  @NotEmpty String loserPowers;
  @NotEmpty String loserPicture;

  @NotEmpty String winnerTeam;

  @NotEmpty String loserTeam;

  public FightLocation location = new FightLocation();

  public Fight(Instant fightDate, String winnerName, Integer winnerLevel, String winnerPowers, String winnerPicture, String loserName, Integer loserLevel, String loserPowers,
    String loserPicture, String winnerTeam, String loserTeam, FightLocation location) {
    this.fightDate = fightDate;
    this.winnerName = winnerName;
    this.winnerLevel = winnerLevel;
    this.winnerPowers = winnerPowers;
    this.winnerPicture = winnerPicture;
    this.loserName = loserName;
    this.loserLevel = loserLevel;
    this.loserPowers = loserPowers;
    this.loserPicture = loserPicture;
    this.winnerTeam = winnerTeam;
    this.loserTeam = loserTeam;
    this.location = location;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Fight fight = (Fight) o;
    return this.id == fight.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Fight{");
    sb.append("fightDate=").append(fightDate);
    sb.append(", winnerName='").append(winnerName).append('\'');
    sb.append(", winnerLevel=").append(winnerLevel);
    sb.append(", winnerPowers='").append(winnerPowers).append('\'');
    sb.append(", winnerPicture='").append(winnerPicture).append('\'');
    sb.append(", loserName='").append(loserName).append('\'');
    sb.append(", loserLevel=").append(loserLevel);
    sb.append(", loserPowers='").append(loserPowers).append('\'');
    sb.append(", loserPicture='").append(loserPicture).append('\'');
    sb.append(", winnerTeam='").append(winnerTeam).append('\'');
    sb.append(", loserTeam='").append(loserTeam).append('\'');
    sb.append(", location=").append(location);
    sb.append('}');
    return sb.toString();
  }
}
