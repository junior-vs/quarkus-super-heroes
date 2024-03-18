package io.quarkus.workshop.superheroes.fight.rest;

import io.quarkus.workshop.superheroes.fight.Fight;
import io.quarkus.workshop.superheroes.fight.service.FightService;

import io.smallrye.mutiny.Uni;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;

import java.util.List;

@Path("/api/fights")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Fights")
public class FightResource {

  final FightService service;
  private final Logger logger;

  public FightResource(FightService service, Logger logger) {
    this.service = service;
    this.logger = logger;
  }

  @GET
  @Path("/hello")
  @Produces(MediaType.TEXT_PLAIN)
  public String hello() {
    return "Hello from RESTEasy Reactive";
  }

  /**
   * GET all fights
   *
   * @return Uni<List < Fight>>
   */
  @GET
  @Operation(summary = "Returns all fights")
  @APIResponse(
      responseCode = "200",
      description = "Get all fights, or empty list if none found",
      content = @Content(mediaType = MediaType.APPLICATION_JSON,
                         schema = @Schema(implementation = Fight.class, type = SchemaType.ARRAY),
                         examples = @ExampleObject(name = "fights", value = Examples.VALID_EXAMPLE_FIGHT_LIST)
                         )
      )
  public Uni<List<Fight>> getAllFigths() {
    return service.findAllFights().invoke(fights -> {
      logger.info("Found {} fights", fights.size());
    });
  }

}
