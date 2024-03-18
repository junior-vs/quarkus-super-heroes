package io.quarkus.workshop.superheroes.fight;

import java.time.Instant;

public class FightBuilder {
    private Instant fightDate;
    private String winnerName;
    private Integer winnerLevel;
    private String winnerPowers;
    private String winnerPicture;
    private String loserName;
    private Integer loserLevel;
    private String loserPowers;
    private String loserPicture;
    private String winnerTeam;
    private String loserTeam;
    private FightLocation location;

    public FightBuilder setFightDate(Instant fightDate) {
        this.fightDate = fightDate;
        return this;
    }

    public FightBuilder setWinnerName(String winnerName) {
        this.winnerName = winnerName;
        return this;
    }

    public FightBuilder setWinnerLevel(Integer winnerLevel) {
        this.winnerLevel = winnerLevel;
        return this;
    }

    public FightBuilder setWinnerPowers(String winnerPowers) {
        this.winnerPowers = winnerPowers;
        return this;
    }

    public FightBuilder setWinnerPicture(String winnerPicture) {
        this.winnerPicture = winnerPicture;
        return this;
    }

    public FightBuilder setLoserName(String loserName) {
        this.loserName = loserName;
        return this;
    }

    public FightBuilder setLoserLevel(Integer loserLevel) {
        this.loserLevel = loserLevel;
        return this;
    }

    public FightBuilder setLoserPowers(String loserPowers) {
        this.loserPowers = loserPowers;
        return this;
    }

    public FightBuilder setLoserPicture(String loserPicture) {
        this.loserPicture = loserPicture;
        return this;
    }

    public FightBuilder setWinnerTeam(String winnerTeam) {
        this.winnerTeam = winnerTeam;
        return this;
    }

    public FightBuilder setLoserTeam(String loserTeam) {
        this.loserTeam = loserTeam;
        return this;
    }

    public FightBuilder setLocation(FightLocation location) {
        this.location = location;
        return this;
    }

    public Fight createFight() {
        return new Fight(fightDate, winnerName, winnerLevel, winnerPowers, winnerPicture, loserName, loserLevel, loserPowers, loserPicture, winnerTeam, loserTeam, location);
    }
}
