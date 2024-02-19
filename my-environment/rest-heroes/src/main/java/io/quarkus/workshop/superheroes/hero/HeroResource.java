package io.quarkus.workshop.superheroes.hero;

import io.quarkus.hibernate.validator.runtime.jaxrs.ResteasyReactiveViolationException;

import io.smallrye.mutiny.Uni;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

import jakarta.ws.rs.core.UriInfo;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;
import org.slf4j.Logger;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/api/heroes")
public class HeroResource {

  private Logger logger;

  private HeroService service;

  public HeroResource(Logger logger, HeroService service) {
    this.logger = logger;
    this.service = service;
  }

  @GET
  @Path("/hello")
  @Produces(MediaType.TEXT_PLAIN)
  public String hello() {
    return "Hello from RESTEasy Reactive";
  }

  @GET
  @Path("/random")
  @Operation(summary = "Returns a random hero")
  @APIResponse(responseCode = "200", description = "Gets a random hero", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Hero.class, required = true), examples = @ExampleObject(name = "hero", value = Examples.VALID_EXAMPLE_HERO)))
  @APIResponse(responseCode = "404", description = "No hero found")
  public Uni<RestResponse<Hero>> randomHero() {
    return this.service.findRandom().onItem().ifNotNull().transform(hero -> {
      this.logger.debug("Found random hero: %s", hero.toString());
      return RestResponse.ok(hero);
    }).onItem().ifNull().continueWith(() -> {
      logger.debug("No Random hero found");
      return RestResponse.notFound();
    });
  }

  /**
   * @param nameFilter
   * @return
   */
  @GET
  @Operation(summary = "Returns all the heroes from the database")
  @APIResponse(responseCode = "200", description = "Gets all heroes", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Hero.class, type = SchemaType.ARRAY), examples = @ExampleObject(name = "heroes", value = Examples.VALID_EXAMPLE_HERO_LIST)))
  public Uni<List<Hero>> getAllHeroes(@Parameter(name = "name_filter", description = "An optional filter parameter to filter results by name") @QueryParam("name_filter") Optional<String> nameFilter) {

    return nameFilter.map(this.service::findAllHeroesHavingName).orElseGet(this.service::findAllHeroes).invoke(heroes -> this.logger.debug("Total number of heroes: %d", heroes.size()));
  }

  /**
   * Returns a hero for a given identifier
   *
   * @param id description of parameter
   * @return description of return value
   */

  @GET
  @Path("/{id}")
  @Operation(summary = "Returns a hero for a given identifier")
  @APIResponse(responseCode = "200", description = "Gets a hero for a given id", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Hero.class), examples = @ExampleObject(name = "hero", value = Examples.VALID_EXAMPLE_HERO)))
  @APIResponse(responseCode = "404", description = "The hero is not found for a given identifier")
  public Uni<RestResponse<Hero>> getHero(@Parameter(name = "id", required = true) @PathParam("id") Long id) {
    return this.service.findHero(id).onItem().ifNotNull().transform(hero -> {
      this.logger.debug("Found hero: %s", hero.toString());
      return RestResponse.ok(hero);
    }).onItem().ifNull().continueWith(() -> {
      logger.debug("No hero found");
      return RestResponse.notFound();
    });
  }

  @POST
  @Consumes(APPLICATION_JSON)
  @Operation(summary = "Creates a valid hero")
  @APIResponse(responseCode = "201", description = "The URI of the created hero", headers = @Header(name = HttpHeaders.LOCATION, schema = @Schema(implementation = URI.class)))
  @APIResponse(responseCode = "400", description = "Invalid hero passed in (or no request body found)")
  public Uni<RestResponse> createHero(@RequestBody(name = "hero", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Hero.class), examples = @ExampleObject(name = "valid_hero", value = Examples.VALID_EXAMPLE_HERO_TO_CREATE))) @Valid @NotNull Hero hero, @Context UriInfo uriInfo) {

    return this.service.create(hero).map(heroCreated -> RestResponse.created(uriInfo.getBaseUriBuilder().path(String.valueOf(heroCreated.getId())).build()));
  }

  @PUT
  @Path("/{id}")
  @Consumes(APPLICATION_JSON)
  @Operation(summary = "Completely updates/replaces an exiting hero by replacing it with the passed-in hero")
  @APIResponse(responseCode = "204", description = "Replaced the hero")
  @APIResponse(responseCode = "400", description = "Invalid hero passed in (or no request body found)")
  @APIResponse(responseCode = "404", description = "No hero found")
  public Uni<RestResponse<Hero>> fullyUpdateHero(@Parameter(name = "id", required = true) @PathParam("id") Long id, @RequestBody(name = "hero", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Hero.class), examples = @ExampleObject(name = "valid_hero", value = Examples.VALID_EXAMPLE_HERO))) @Valid @NotNull Hero hero) {

    if (hero.getId() == null) {
      hero.setId(id);
    }

    return this.service.replaceHero(hero).onItem().ifNotNull().transform(h -> {
      this.logger.debug("Hero replaced with new values %s", h);
      return RestResponse.ok(h);
    }).onItem().ifNull().continueWith(() -> {
      this.logger.debug("No hero found with id %d", hero.getId());
      return RestResponse.status(Status.NOT_FOUND);
    });
  }

  @PUT
  @Consumes(APPLICATION_JSON)
  @Operation(summary = "Completely replace all heroes with the passed-in heroes")
  @APIResponse(responseCode = "201", description = "The URI to retrieve all the created heroes", headers = @Header(name = HttpHeaders.LOCATION, schema = @Schema(implementation = URI.class)))
  @APIResponse(responseCode = "400", description = "Invalid heroes passed in (or no request body found)")
  public Uni<RestResponse> replaceAllHeroes(@RequestBody(name = "valid_villains", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Hero.class, type = SchemaType.ARRAY), examples = @ExampleObject(name = "heroes", value = Examples.VALID_EXAMPLE_HERO_LIST))) @NotNull List<Hero> heroes, @Context UriInfo uriInfo) {
    return this.service.replaceAllHeroes(heroes).map(h -> {
      var uri = uriInfo.getAbsolutePathBuilder().build();
      this.logger.debug("New Heroes created with URI %s", uri.toString());
      return RestResponse.created(uri);
    });
  }

  @PATCH
  @Path("/{id}")
  @Consumes(APPLICATION_JSON)
  @Operation(summary = "Partially updates an exiting hero")
  @APIResponse(responseCode = "200", description = "Updated the hero", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Hero.class), examples = @ExampleObject(name = "hero", value = Examples.VALID_EXAMPLE_HERO)))
  @APIResponse(responseCode = "400", description = "Null hero passed in (or no request body found)")
  @APIResponse(responseCode = "404", description = "No hero found")
  public Uni<RestResponse<Hero>> partiallyUpdateHero(@Parameter(name = "id", required = true) @PathParam("id") Long id,
    @RequestBody(name = "valid_hero", required = true, content = @Content(schema = @Schema(implementation = Hero.class),
                                                                          examples = @ExampleObject(name = "valid_hero", value = Examples.VALID_EXAMPLE_HERO)))
    @NotNull @Valid Hero hero) {
    if (hero.getId() == null) {
      hero.setId(id);
    }

    return this.service.partialUpdateHero(hero)
      .onItem().ifNotNull().transform(h -> {
      this.logger.debug("Hero updated with new values %s", h);
      return RestResponse.ok(h);
    }).onItem().ifNull().continueWith(() -> {
      this.logger.debug("No hero found with id %d", hero.getId());
      return RestResponse.status(Status.NOT_FOUND);
    }).onFailure(ConstraintViolationException.class).transform(cve -> new ResteasyReactiveViolationException
      (((ConstraintViolationException) cve).getConstraintViolations()));
  }

  @DELETE
  @Operation(summary = "Delete all heroes")
  @APIResponse(
    responseCode = "204",
    description = "Deletes all heroes"
  )
  public Uni<Void> deleteAllHeros() {
    return this.service.deleteAllHeroes()
      .invoke(() -> this.logger.debug("Deleted all heroes"));
  }

  @DELETE
  @Path("/{id}")
  @Operation(summary = "Deletes an exiting hero")
  @APIResponse(
    responseCode = "204",
    description = "Deletes a hero"
  )
  public Uni<Void> deleteHero(@Parameter(name = "id", required = true) @PathParam("id") Long id) {
    return this.service.deleteHero(id)
      .invoke(() -> this.logger.debug("Hero deleted with %d", id));
  }


}
