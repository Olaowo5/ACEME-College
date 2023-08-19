package acmecollege.rest.resource;

/** Created by:  Group 3
*   040982007, Olamide, Owolabi (as from ACSIS)
*   41056895, Jennifer Acevedocarmona (as from ACSIS)
*   041030048, Delorme, Keelan (as from ACSIS) */

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.PROFESSOR_SUBRESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.Professor;

@Path(PROFESSOR_SUBRESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProfessorResource {
	
	private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;

    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getProfessors() {
        LOG.debug("retrieving all professors ...");
        List<Professor> professors = service.getAll(Professor.class, "Professor.findAll");
        Response response = Response.ok(professors).build();
        return response;
    }

    @GET
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getProfessorById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
    	LOG.debug("try to retrieve specific professor " + id);
        Response response = null;
        Professor professor = null;
        professor = service.getProfessorById(id);
        response = Response.status(professor == null ? Status.NOT_FOUND : Status.OK).entity(professor).build();
        return response;
    }

    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addProfessor(Professor newProfessor) {
    	Response response = null;
    	Professor newProfessorWithIdTimestamps = service.persistProfessor(newProfessor);
    	response = Response.ok(newProfessorWithIdTimestamps).build();
        return response;
    }
    
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteProfessorById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
    	Response response = null;
    	service.deleteProfessorById(id);
    	response = Response.ok("Professor ID: " + id + " Deleted").build();
      	return response;
    }

}
