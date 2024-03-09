package io.quarkus.workshop.superheroes.villain;

import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;

import io.quarkus.workshop.superheroes.villain.model.Villain;

import io.restassured.common.mapper.TypeRef;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.*;
import static jakarta.ws.rs.core.HttpHeaders.*;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.hamcrest.CoreMatchers.is;
import static org.jboss.resteasy.reactive.RestResponse.Status.OK;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class VillainResourceTest {

  private static final String DEFAULT_NAME = "Super Chocolatine";
  private static final String DEFAULT_OTHER_NAME = "Super Chocolatine chocolate in";
  private static final String DEFAULT_PICTURE = "super_chocolatine.png";
  private static final String DEFAULT_POWERS = "does not eat pain au chocolat";
  private static final int DEFAULT_LEVEL = 42;

  private static final String UPDATED_NAME = "Super Chocolatine (updated)";
  private static final String UPDATED_OTHER_NAME = "Super Chocolatine chocolate in (updated)";
  private static final String UPDATED_PICTURE = "super_chocolatine_updated.png";
  private static final String UPDATED_POWERS = "does not eat pain au chocolat (updated)";
  private static final int UPDATED_LEVEL = 43;
  private static Long villainId ;



  @Test
  @DisplayName("hello endpoint test")
  void test1() {
    given()
      .when()
      .get("/api/villains/hello")
      .then()
      .statusCode(200)
      .body(is("Hello Villain Resource"));
  }

  @Test
  @DisplayName("should Not Get Unknown Villain")
  void teste2() {
    Long randomId = new Random().nextLong();
    given()
      .pathParam("id", randomId)
      .when()
      .get("/api/villains/{id}")
      .then()
      .statusCode(NO_CONTENT.getStatusCode());
  }

  @Test
  @DisplayName("should Get Random Villain")
  void test3() {
    given()
      .when()
      .get("/api/villains/random")
      .then()
      .statusCode(OK.getStatusCode())
      .contentType(APPLICATION_JSON);
  }

  @Test
  @DisplayName("should Not Add Invalid Item")
  void test4() {
    Villain villain = new Villain(null, DEFAULT_OTHER_NAME, 0, DEFAULT_PICTURE, DEFAULT_POWERS);

    given()
      .body(villain)
      .header(CONTENT_TYPE, APPLICATION_JSON)
      .header(ACCEPT, APPLICATION_JSON)
      .when()
      .post("/api/villains")
      .then()
      .statusCode(BAD_REQUEST.getStatusCode());
  }

  @Test
  @Order(1)
  @DisplayName("should Get Initial Items")
  void test5() {
    var NB_VILLAINS = Villain.count();
    List<Villain> villains = get("/api/villains")
      .then()
      .statusCode(OK.getStatusCode())
      .contentType(APPLICATION_JSON)
      .extract()
      .body()
      .as(getVillainTypeRef());
    assertEquals(NB_VILLAINS, villains.size());
  }

  @Test
  @Order(2)
  @DisplayName("should Add An Item")
  void test6() {
    var NB_VILLAINS = Villain.count();
    Villain villain = new Villain(DEFAULT_NAME, DEFAULT_OTHER_NAME, DEFAULT_LEVEL, DEFAULT_PICTURE, DEFAULT_POWERS);

    String location = given()
      .body(villain)
      .header(CONTENT_TYPE, APPLICATION_JSON)
      .header(ACCEPT, APPLICATION_JSON)
      .when()
      .post("/api/villains")
      .then()
      .statusCode(CREATED.getStatusCode()).extract().header("Location");

    assertTrue(location.contains("/api/villains"));

    // Stores the id
    String[] segments = location.split("/");
    villainId = Long.parseLong(segments[segments.length - 1]);
    assertNotNull(villainId);
    Log.info("id: " + villainId );

    given()
      .pathParam("id", villainId)
      .when()
      .get("/api/villains/{id}")
      .then()
      .statusCode(OK.getStatusCode())
      .contentType(APPLICATION_JSON)
        .body("name", Is.is(DEFAULT_NAME))
        .body("otherName", Is.is(DEFAULT_OTHER_NAME))
        .body("level", Is.is(DEFAULT_LEVEL))
        .body("picture", Is.is(DEFAULT_PICTURE))
        .body("powers", Is.is(DEFAULT_POWERS));

    List<Villain> villains = get("/api/villains")
      .then()
      .statusCode(OK.getStatusCode())
      .contentType(APPLICATION_JSON)
      .extract()
      .body()
      .as(getVillainTypeRef());
    assertEquals(NB_VILLAINS + 1, villains.size());
  }

  @Test
  @Order(3)
  @DisplayName("test Updating An Item")
  void test7() {
    var NB_VILLAINS = Villain.count();
    Villain villain = new Villain(UPDATED_NAME,UPDATED_OTHER_NAME, UPDATED_LEVEL,  UPDATED_PICTURE, UPDATED_POWERS);
    Log.info(villainId);
    villain.id = villainId;

    given()
      .pathParam("id", villainId)
      .body(villain)
      .header(CONTENT_TYPE, APPLICATION_JSON)
      .header(ACCEPT, APPLICATION_JSON)
      .when().put("/api/villains/{id}")
      .then()
      .statusCode(OK.getStatusCode())
      .body("name", Is.is(UPDATED_NAME))
      .body("otherName", Is.is(UPDATED_OTHER_NAME))
      .body("level", Is.is(UPDATED_LEVEL))
      .body("picture", Is.is(UPDATED_PICTURE))
      .body("powers", Is.is(UPDATED_POWERS));

    List<Villain> villains = get("/api/villains")
      .then()
      .statusCode(OK.getStatusCode()).contentType(APPLICATION_JSON)
      .extract()
      .body()
      .as(getVillainTypeRef());
    assertEquals(NB_VILLAINS, villains.size());
  }

  @Test
  @Order(4)
  @DisplayName("should Remove An Item")
  void test8() {
    var NB_VILLAINS = Villain.count();
    Log.info(villainId);
    given().pathParam("id", villainId)
      .when()
      .delete("/api/villains/{id}")
      .then()
      .statusCode(NO_CONTENT.getStatusCode());

    var count_final = Villain.count();
    assertEquals(NB_VILLAINS-1, count_final);
  }

  private TypeRef<List<Villain>> getVillainTypeRef() {
    return new TypeRef<List<Villain>>() {
      // Kept empty on purpose
    };
  }

}
