package io.quarkus.workshop.superheroes.hero;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;

import io.smallrye.mutiny.Uni;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Random;

public class Hero{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "hero_seq_id")
  @SequenceGenerator(name = "hero_seq_id", sequenceName = "hero_seq_id", allocationSize = 1)
  @Column(name = "id")
  private Long id;

  @NotNull
  @Size(min = 3, max = 50)
  private String name;

  private String otherName;

  @NotNull
  @Min(1)
  private Integer Level;

  private String picture;

  @Column(columnDefinition = "TEXT")
  private String powers;

  @Deprecated
  public Hero() {
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Hero{");
    sb.append("name='").append(name).append('\'');
    sb.append(", otherName='").append(otherName).append('\'');
    sb.append(", Level=").append(Level);
    sb.append(", picture='").append(picture).append('\'');
    sb.append(", powers='").append(powers).append('\'');
    sb.append('}');
    return sb.toString();
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getOtherName() {
    return otherName;
  }

  public Integer getLevel() {
    return Level;
  }

  public String getPicture() {
    return picture;
  }

  public String getPowers() {
    return powers;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
