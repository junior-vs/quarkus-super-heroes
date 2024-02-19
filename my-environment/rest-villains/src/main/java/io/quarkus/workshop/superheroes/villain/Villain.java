package io.quarkus.workshop.superheroes.villain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import io.quarkus.workshop.superheroes.villain.service.VillainConfig;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Entity
public class Villain extends PanacheEntity {

  @NotBlank
  @Size(min = 3, max = 50)
  @Column(length = 50, nullable = false)
  private String name;

  @Column(length = 50, nullable = true)
  private String otherName;

  @NotNull
  @Positive
  @Column(nullable = false)
  private Integer level;
  private String picture;
  @Column(columnDefinition = "TEXT")
  private String powers;

  public Villain() {
  }

  public Villain(String name, String otherName, Integer level, String picture, String powers) {
    this.name = name;
    this.otherName = otherName;
    this.level = level;
    this.picture = picture;
    this.powers = powers;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Villain{");
    sb.append("name='").append(name).append('\'');
    sb.append(", otherName='").append(otherName).append('\'');
    sb.append(", level=").append(level);
    sb.append(", picture='").append(picture).append('\'');
    sb.append(", powers='").append(powers).append('\'');
    sb.append(", id=").append(id);
    sb.append('}');
    return sb.toString();
  }

  public String getName() {
    return name;
  }

  public String getOtherName() {
    return otherName;
  }

  public Integer getLevel() {
    return level;
  }

  public String getPicture() {
    return picture;
  }

  public String getPowers() {
    return powers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Villain villain = (Villain) o;
    return Objects.equals(id, villain.id) && Objects.equals(name, villain.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }

  /* persiste methods */

  public static Optional<Villain> findRandom() {
    var countVillains = count();

    if (countVillains > 0) {
      var randomVillain = new Random().nextInt((int) countVillains);
      return findAll().page(randomVillain, 1).firstResultOptional();
    }
    return Optional.empty();

  }

  public static List<Villain> listAllWhereNameLike(@Valid @NotBlank String name) {
    return (name != null) ? list("LOWER(name) LIKE CONCAT('%', ?1, '%')", name.toLowerCase()) : List.of();
  }

  public void update(Villain villain) {
    this.name = villain.getName();
    this.level = villain.getLevel();
    this.picture = villain.getPicture() != null ? villain.getPicture() : this.getPicture();
    this.otherName = villain.getOtherName() != null ? villain.getOtherName() : this.getOtherName();
    this.powers = villain.getPowers() != null ? villain.getPowers() : this.getPowers();
  }

  public void updateLevel(VillainConfig config) {
     this.level = (int) Math.round(level * config.level().multiplier());
  }
}
