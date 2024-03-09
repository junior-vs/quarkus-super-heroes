package io.quarkus.workshop.superheroes.villain.rest;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import io.quarkus.workshop.superheroes.villain.model.ExamplesObject;
import io.quarkus.workshop.superheroes.villain.model.Villain;

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
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
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
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;

import io.quarkus.workshop.superheroes.villain.service.VillainService;

import io.smallrye.common.annotation.NonBlocking;

@Path("/api/villains")
public class VillainResource {

  Logger logger;
  VillainService service;

  public VillainResource(Logger logger, VillainService service) {
    this.service = service;
    this.logger = logger;
  }

  @APIResponse(
    responseCode = "200",
    description = "Gets random villain",
    content = @Content(
      mediaType = APPLICATION_JSON,
      schema = @Schema(implementation = Villain.class, required = true),
      examples = @ExampleObject(name = "villain", value = ExamplesObject.VALID_EXAMPLE_VILLAIN)
    )
  )
  @APIResponse(
    responseCode = "404",
    description = "No villain found"
  )
  @Operation(summary = "Returns a random villain")
  @GET
  @Path("/random")
  public RestResponse<Villain> getRandomVillain() {
    return this.service.findRandomVillain().map(v -> {
      this.logger.debugf("Found random villain: %s", v);
      return RestResponse.ok(v);
    }).orElseGet(() -> {
      this.logger.debug("No random villain found");
      return RestResponse.status(Status.NOT_FOUND);
    });
  }

  @Operation(summary = "Returns all the villains from the database")
  @APIResponse(
    responseCode = "200",
    description = "Gets all villains",
    content = @Content(
      mediaType = APPLICATION_JSON,
      schema = @Schema(implementation = Villain.class, type = SchemaType.ARRAY),
      examples = @ExampleObject(name = "villains", value = ExamplesObject.VALID_EXAMPLE_VILLAIN_LIST)
    )
  )
  @GET
  public RestResponse<List<Villain>> getAllVillains(
    @Parameter(name = "name_filter", description = "An optional filter parameter to filter results by name")
    @QueryParam("name_filter") Optional<String> nameFilter) {
    List<Villain> villains = nameFilter.map(this.service::findAllVillainsHavingName).orElseGet(this.service::findAllVillains);
    this.logger.debugf("Total number of villains: %d", villains.size());
    return RestResponse.status(Status.OK, villains);

  }

  @Operation(summary = "Returns a villain for a given identifier")
  @APIResponse(
    responseCode = "200",
    description = "Gets a villain for a given id",
    content = @Content(
      mediaType = APPLICATION_JSON,
      schema = @Schema(implementation = Villain.class),
      examples = @ExampleObject(name = "villain", value = ExamplesObject.VALID_EXAMPLE_VILLAIN)
    )
  )
  @APIResponse(
    responseCode = "204",
    description = "The villain is not found for a given identifier"
  )@GET
  @Path("/{id}")
  public RestResponse<Villain> getVillain(@Parameter(name = "id", required = true) @PathParam("id") Long id) {
    return this.service.findVillainById(id).map(v -> {
      this.logger.debugf("Found villain: %s", v);
      return RestResponse.ok(v);
    }).orElseGet(() -> {
      this.logger.debugf("No villain found with id %d", id);
      return RestResponse.status(Status.NO_CONTENT);
    });
  }

  @Operation(summary = "Creates a valid villain")
  @APIResponse(
    responseCode = "201",
    description = "The URI of the created villain",
    headers = @Header(name = HttpHeaders.LOCATION, schema = @Schema(implementation = URI.class))
  )
  @APIResponse(
    responseCode = "400",
    description = "Invalid villain passed in (or no request body found)"
  )
  @POST
  @Consumes(APPLICATION_JSON)
  public RestResponse<?> createVillain(
    @RequestBody(
      name = "villain",
      required = true,
      content = @Content(
        mediaType = APPLICATION_JSON,
        schema = @Schema(implementation = Villain.class),
        examples = @ExampleObject(name = "valid_villain", value = ExamplesObject.VALID_EXAMPLE_VILLAIN_TO_CREATE)
      )
    )
    @Valid @NotNull Villain villain, @Context UriInfo uriInfo) {
    var entity = this.service.persistVillain(villain);
    UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(entity.id));
    return RestResponse.created(builder.build());
  }

  @Operation(summary = "Updates an exiting  villain")
  @APIResponse(
    responseCode = "200",
    description = "The updated villain",
    content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Villain.class))
  )
  @PUT
  @Path("/{id}")
  public RestResponse<?> fullyUpdateVillain(@PathParam("id") Long id, @Valid @NotNull Villain villain) {

    if (villain.id == null) {
      villain.id = id;
    }

    return service.replaceVillain(villain).map(v -> {
      this.logger.infof("Villain replaced with new values %s", v);
      return RestResponse.ok(v);
    }).orElseGet(() -> {
      this.logger.debugf("No villain found with id %d", villain.id);
      return RestResponse.status(Status.NOT_FOUND);
    });

  }

  @POST
  @Path("/multiple")
  @Consumes(APPLICATION_JSON)
  public RestResponse<?> replaceAllVillains(@NotNull List<Villain> villains, @Context UriInfo uriInfo) {
    this.service.createAllVillains(villains);
    var uri = uriInfo.getAbsolutePathBuilder().build();
    this.logger.debugf("New Villains created with URI %s", uri.toString());
    return RestResponse.created(uri);
  }

  @PATCH
  @Path("/{id}")
  public Response partiallyUpdateVillain(@PathParam("id") Long id, @NotNull Villain villain) {
    if (villain.id == null) {
      villain.id = id;
    }

    return this.service.partialUpdateVillain(villain).map(v -> {
      this.logger.debugf("Villain updated with new values %s", v);
      return Response.ok(v).build();
    }).orElseGet(() -> {
      this.logger.debugf("No villain found with id %d", villain.id);
      return Response.status(Status.NOT_FOUND).build();
    });
  }

  @DELETE
  public void deleteAllVillains() {
    this.service.deleteAllVillains();
    this.logger.debug("Deleted all villains");
  }
  @Operation(summary = "Deletes an exiting villain")
  @APIResponse(responseCode = "204")
  @DELETE
  @Path("/{id}")
  public void deleteVillain(@PathParam("id") Long id) {
    this.service.deleteVillain(id);
    this.logger.debugf("Villain with id %d deleted ", id);
  }

  @GET
  @Path("/hello")
  @Produces(MediaType.TEXT_PLAIN)
  @NonBlocking
  public String hello() {
    this.logger.debug("Hello Villain Resource");
    return "Hello Villain Resource";
  }
}
