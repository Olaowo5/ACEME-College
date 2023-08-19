/**
 * For Testing the Proffessor
 * Created by:  Group 3
 *   040982007, Olamide, Owolabi (as from ACSIS)
 *   41056895, Jennifer Acevedocarmona (as from ACSIS)
 *   041030048, Delorme, Keelan (as from ACSIS)
 */
package acmecollege;

import static org.junit.jupiter.api.Assertions.*;

import javax.ws.rs.core.MediaType;
import java.lang.invoke.MethodHandles;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static acmecollege.utility.MyConstants.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import acmecollege.entity.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.junit.jupiter.api.Order;


@TestMethodOrder(MethodOrderer.MethodName.class)
class TestStudent {

	 private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
	    private static final Logger logger = LogManager.getLogger(_thisClaz);

	    static final String HTTP_SCHEMA = "http";
	    static final String HOST = "localhost";
	    static final int PORT = 8080;
	    static Student student;

	    // Test fixture(s)
	    static URI uri;
	    static HttpAuthenticationFeature adminAuth;
	    static HttpAuthenticationFeature userAuth;

	    @BeforeAll
	    public static void oneTimeSetUp() throws Exception {
	        logger.debug("oneTimeSetUp");
	        uri = UriBuilder
	            .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
	            .scheme(HTTP_SCHEMA)
	            .host(HOST)
	            .port(PORT)
	            .build();
	        adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
	        userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER, DEFAULT_USER_PASSWORD);
	    
	        student = new Student();
	        student.setFirstName("Freddie");
	        student.setLastName("Mercury");
	    }

	    protected WebTarget webTarget;
	    @BeforeEach
	    public void setUp() {
	        Client client = ClientBuilder.newClient(
	            new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
	        webTarget = client.target(uri);
	    }

	    
	    @Test
	    @Order(1)
	    /**
	     *  To get all Students with admin
	     * @throws JsonMappingException
	     * @throws JsonProcessingException
	     */
	    public void test01_get_all_students_with_adminrole() throws JsonMappingException, JsonProcessingException {
	        Response response = webTarget
	            	            .register(adminAuth)
	            .path(STUDENT_RESOURCE_NAME)
	            .request()
	            .get();
	        List<Student> students = response.readEntity(new GenericType<List<Student>>(){});
	        assertThat(response.getStatus(), is(200));
	        assertThat(students, is(not(empty())));
	    }

		
	    @Test
	    @Order(2)
	    /**
	     * To get all Students with user role
	     * @throws JsonMappingException
	     * @throws JsonProcessingException
	     */
	    public void test02_get_all_students_with_userrole() throws JsonMappingException, JsonProcessingException {
	        Response response = webTarget
	            .register(userAuth)
	           	 .path(STUDENT_RESOURCE_NAME)
	            .request()
	            .get();
	        assertThat(response.getStatus(), is(403));
		}
		
		 
		 @Test
		 @Order(3)
		 /**
		  * To get Student with id using admin 
		  * @throws JsonMappingException
		  * @throws JsonProcessingException
		  */
		 public void test03_get_student_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
			 Response response = webTarget
				//.register(userAuth)
				.register(adminAuth)
				.path(STUDENT_RESOURCE_NAME+"/1")
				.request()
				.get();
			 assertThat(response.getStatus(), is(200));
		     Student student = response.readEntity(new GenericType<Student>() {	    	 
		     });
		     assertThat(student.getId(), is(1));
		    }
		 
		 
		 @Test
		 @Order(4)
		 /**
		  * To get Student with id using user role
		  * @throws JsonMappingException
		  * @throws JsonProcessingException
		  */
		 public void test04_get_student_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
			 Response response = webTarget
				.register(userAuth)
				.path(STUDENT_RESOURCE_NAME+"/1")
				.request()
				.get();
			 assertThat(response.getStatus(), is(200));
		 }
		 
		 @Test
		 @Order(5)	
		 /**
		  * To add to Student with admin role
		  * @throws JsonMappingException
		  * @throws JsonProcessingException
		  */
	     public void test05_add_student_with_adminrole() throws JsonMappingException, JsonProcessingException{
			 Student student = new Student();
			 student.setFirstName("Freddie");
		     student.setLastName("Mercury");
			 Response response = webTarget
				//.register(userAuth)
	            .register(adminAuth)
	            .path(STUDENT_RESOURCE_NAME)
	            .request()
	            .post(Entity.json(student));
			 assertThat(response.getStatus(), is(200));
		 }
		 
		 
		 @Test
		 @Order(6)
		 /**
		  * To add to Student with user role
		  * @throws JsonMappingException
		  * @throws JsonProcessingException
		  */
	     public void test06_add_student_with_userrole() throws JsonMappingException, JsonProcessingException{
			 Student student = new Student();
			 student.setFirstName("Freddie");
		     student.setLastName("Mercury");
			 Response response = webTarget
				.register(userAuth)
	            .path(STUDENT_RESOURCE_NAME)
	            .request()
	            .post(Entity.json(student));
			 assertThat(response.getStatus(), is(403));
	    }
		
		 @Test
		 @Order(7)
		 public void test07_Update_student_with_adminrole() throws JsonMappingException, JsonProcessingException{
			
			 Response response1 = webTarget
     	            .register(adminAuth)
				        .path(STUDENT_RESOURCE_NAME)
				        .request()
				        .get();
		        List<Student> students = response1.readEntity(new GenericType<List<Student>>(){});
		    	
		        Student updatedStud = students.get(students.size() -1);
		        
		        updatedStud.setFirstName("Jordan");
		        updatedStud.setLastName("Mary");
		         String id = Integer.toString(students.get(students.size() -1).getId());
		         
		    	Response response = webTarget
			    	//.register(userAuth)        	
			        .register(adminAuth)
			        .path(STUDENT_RESOURCE_NAME+"/"+ id)
			        .request()
			        .put(Entity.entity(updatedStud, MediaType.APPLICATION_JSON));
		    	
		    	
		        assertThat(response.getStatus(), is(200));
		        assertThat(updatedStud.getFirstName(), is("Jordan"));
	    }
		    @Test
		    @Order(8)
		    /**
		     * To delete from Student with admin role
		     * @throws JsonMappingException
		     * @throws JsonProcessingException
		     */
		    public void test08_delete_student_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
		    	
		        Response response1 = webTarget
        	            .register(adminAuth)
				        .path(STUDENT_RESOURCE_NAME)
				        .request()
				        .get();
		        List<Student> students = response1.readEntity(new GenericType<List<Student>>(){});
		    	
		         String id = Integer.toString(students.get(students.size() -1).getId());
		         
		    	Response response = webTarget
			    	//.register(userAuth)        	
			        .register(adminAuth)
			        .path(STUDENT_RESOURCE_NAME+"/"+ id)
			        .request()
			        .delete();
		        assertThat(response.getStatus(), is(200));
		    }
	   
	    
	    
		 
	    @Test
	    @Order(8)
	    /**
	     * To delete from Student table with user role
	     * @throws JsonMappingException
	     * @throws JsonProcessingException
	     */
	    public void test08_delete_student_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
	    	Response response = webTarget
		    	.register(userAuth)        	
		        //.register(adminAuth)
		        .path(STUDENT_RESOURCE_NAME+"/"+student.getId())
		        .request()
		        .delete();
	        assertThat(response.getStatus(), is(403));
	    }

}
