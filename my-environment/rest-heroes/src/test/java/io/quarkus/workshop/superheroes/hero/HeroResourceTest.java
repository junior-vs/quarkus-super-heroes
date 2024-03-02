package io.quarkus.workshop.superheroes.hero;

import io.quarkus.test.junit.QuarkusTest;

import io.restassured.RestAssured;

import io.restassured.http.ContentType;

import org.jboss.resteasy.reactive.RestResponse.StatusCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class HeroResourceTest {

  private static final int DEFAULT_ORDER = 0;
  static final String INVALID_EXAMPLE_HERO = """
    {		
    	"name": " ",
    	"level": -10			
    }
    """;

  @BeforeAll
  static void beforeAll() {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @Test
  @Order(DEFAULT_ORDER)
  void testHelloEndpoint() {
    given().when().get("/api/heroes/hello").then().statusCode(200).body(is("Hello from RESTEasy Reactive"));
  }

  @Test
  @Order(DEFAULT_ORDER)
  @DisplayName("Should not get unknown hero")
  void testGetUnknownHero() {
    given().when().get("/api/heroes/{id}", 999).then().statusCode(StatusCode.NOT_FOUND);
  }

  @Test
  @Order(DEFAULT_ORDER)
  @DisplayName("Should get random hero")
  void testGetRandomHero() {
    given().when().get("/api/heroes/random").then().statusCode(StatusCode.OK);
  }

  @Test
  @Order(DEFAULT_ORDER)
  @DisplayName("Should not add invalid Hero")
  void testAddInvalidHero() {
    given()
      .when()
        .body(INVALID_EXAMPLE_HERO)
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .post("/api/heroes")
      .then()
        .statusCode(StatusCode.BAD_REQUEST);
  }
  @Test
  @Order(DEFAULT_ORDER)
  @DisplayName("Should not Fully update invalid Hero")
  void testUpdateInvalidHero() {
    given()
      .when()
        .body(INVALID_EXAMPLE_HERO)
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .put("/api/heroes/{id}", 999)
      .then()
        .statusCode(StatusCode.BAD_REQUEST);
  }
  @Test
  @Order(DEFAULT_ORDER)
  @DisplayName("Should not partially update invalid Hero")
  void testPatchInvalidHero() {
    given()
      .when()
        .body(INVALID_EXAMPLE_HERO)
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .patch("/api/heroes/{id}", 999)
      .then()
        .statusCode(StatusCode.BAD_REQUEST);
  }
  @Test
  @Order(DEFAULT_ORDER)
  @DisplayName("Should not create null Hero")
  void testNullHero() {
    given()
      .when()
        .body("")
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .post("/api/heroes")
      .then()
        .statusCode(StatusCode.BAD_REQUEST);
  }
  @Test
  @Order(DEFAULT_ORDER)
  @DisplayName("Should not fully update null Hero")
  void testUpdateNullHero() {
    given()
      .when()
        .body("")
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .put("/api/heroes/{id}", 999)
      .then()
        .statusCode(StatusCode.BAD_REQUEST);
  }
  @Test
  @Order(DEFAULT_ORDER)
  @DisplayName("Should not partially update null Hero")
  void testPatchNullHero() {
    given().when().body("").contentType(ContentType.JSON).accept(ContentType.JSON).patch("/api/heroes/{id}", 999).then().statusCode(StatusCode.BAD_REQUEST);
  }
  @Test
  @Order(DEFAULT_ORDER)
  @DisplayName("Should not fully update nof found item")
  void testUpdateNotFoundHero() {
    given().when()
      .body(Examples.VALID_EXAMPLE_HERO)
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON).
        put("/api/heroes/{id}", 999)
      .then().statusCode(StatusCode.NOT_FOUND);
  }

  @Test
  @Order(DEFAULT_ORDER)
  @DisplayName("Should not partially update nof found item")
  void testPatchNotFoundHero() {
    given().when()
      .body(Examples.VALID_EXAMPLE_HERO)
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON).
        patch("/api/heroes/{id}", 999)
      .then().statusCode(StatusCode.NOT_FOUND);
  }

}
