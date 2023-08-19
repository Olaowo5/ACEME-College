/** Created by:  Group 3
*   040982007, Olamide, Owolabi (as from ACSIS)
*   41056895, Jennifer Acevedo carmona (as from ACSIS)
*   041030048, Delorme, Keelan (as from ACSIS) */
package acmecollege.rest.resource;

import acmecollege.ejb.ACMECollegeService;
import javax.ws.rs.core.Response.Status;
import acmecollege.entity.Course;
import acmecollege.entity.Professor;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.soteria.WrappingCallerPrincipal;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static acmecollege.utility.MyConstants.*;


@Path(COURSE_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;

    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getCourses() {
        LOG.debug("retrieving all courses ...");
        List<Course> course = service.getAllCourses();
        return Response.ok(course).build();
    }

 

    
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteCourse(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("Deleting course with id = {}", id);
        Course course = service.getCourseById(id);
        if (course == null) {
			HttpErrorResponse err = new HttpErrorResponse(Status.CONFLICT.getStatusCode(), 
					"Entity not found.");
			return Response.status(Status.CONFLICT).entity(err).build();
		}
		try {
			service.deleteCourseById(id);
			return Response.ok(course).build(); // return 200 OK if deletion successful
		} catch (Exception e) {
			HttpErrorResponse err = new HttpErrorResponse(Status.CONFLICT.getStatusCode(),
					"Entity associated to a course registration cannot be deleted.");
			return Response.status(Status.CONFLICT).entity(err).build();
		}
    }


    
 

    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addcourse2(Course newCourse) {
    	Response response = null;
    	Course newCourseWithIdTimestamps = service.persistCourse(newCourse);
    	response = Response.ok(newCourseWithIdTimestamps).build();
        return response;
    }
    
 
    @GET
    @Path(RESOURCE_PATH_ID_PATH)
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getCourseById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("retrieving course with id = {}", id);
        Course course = service.getById(Course.class, Course.COURSE_BY_ID, id);
        if (course == null) {
            throw new NotFoundException("Course not found");
        }
        if (sc.isCallerInRole(USER_ROLE)) {
            WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal) sc.getCallerPrincipal();
            SecurityUser sUser = (SecurityUser) wCallerPrincipal.getWrapped();
            Student student = sUser.getStudent();
            if (student == null || !student.getCourseRegistrations().contains(course)) {
                throw new ForbiddenException("User trying to access resource it does not own (wrong userid)");
            }
        }
        return Response.ok(course).build();
    }


}
