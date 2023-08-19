/**
 * For Testing the Proffessor
 * Created by:  Group 3
 *   040982007, Olamide, Owolabi (as from ACSIS)
 *   41056895, Jennifer Acevedocarmona (as from ACSIS)
 *   041030048, Delorme, Keelan (as from ACSIS)
 */
package acmecollege;

import static org.junit.jupiter.api.Assertions.*;

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
class TestProfessor {

	 private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
	    private static final Logger logger = LogManager.getLogger(_thisClaz);

	    static final String HTTP_SCHEMA = "http";
	    static final String HOST = "localhost";
	    static final int PORT = 8080;
	    static Professor professor;

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
	    
	        professor = new Professor();
	        professor.setProfessor("Jennifer", "Ggmoew", "SoftwareProg");
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
	     *  To get all Professor with admin
	     * @throws JsonMappingException
	     * @throws JsonProcessingException
	     */
	    public void test01_get_all_professor_with_adminrole() throws JsonMappingException, JsonProcessingException {
	        Response response = webTarget
	            	            .register(adminAuth)
	            .path(PROFESSOR_SUBRESOURCE_NAME)
	            .request()
	            .get();
	        List<Professor> professors = response.readEntity(new GenericType<List<Professor>>(){});
	        assertThat(response.getStatus(), is(200));
	        assertThat(professors, is(not(empty())));
	    }

		
	    @Test
	    @Order(2)
	    /**
	     * To get all Professor with user role
	     * @throws JsonMappingException
	     * @throws JsonProcessingException
	     */
	    public void test02_get_all_professor_with_userrole() throws JsonMappingException, JsonProcessingException {
	        Response response = webTarget
	            .register(userAuth)
	           	 .path(PROFESSOR_SUBRESOURCE_NAME)
	            .request()
	            .get();
	        assertThat(response.getStatus(), is(403));
		}
		
		 
		 @Test
		 @Order(3)
		 /**
		  * To get professor with id using admin 
		  * @throws JsonMappingException
		  * @throws JsonProcessingException
		  */
		 public void test03_get_professor_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
			 Response response = webTarget
				//.register(userAuth)
				.register(adminAuth)
				.path(PROFESSOR_SUBRESOURCE_NAME+"/1")
				.request()
				.get();
			 assertThat(response.getStatus(), is(200));
		     Professor professor = response.readEntity(new GenericType<Professor>() {	    	 
		     });
		     assertThat(professor.getId(), is(1));
		    }
		 
		 
		 @Test
		 @Order(4)
		 /**
		  * To get professor with id using user role
		  * @throws JsonMappingException
		  * @throws JsonProcessingException
		  */
		 public void test04_get_professor_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
			 Response response = webTarget
				.register(userAuth)
				.path(PROFESSOR_SUBRESOURCE_NAME+"/1")
				.request()
				.get();
			 assertThat(response.getStatus(), is(403));
		 }
		 
		 @Test
		 @Order(5)	
		 /**
		  * To add to professor with admin role
		  * @throws JsonMappingException
		  * @throws JsonProcessingException
		  */
	     public void test05_add_professor_with_adminrole() throws JsonMappingException, JsonProcessingException{
			 Professor professor = new Professor("Issae", "Jule", "Mathematics", null);
			 Response response = webTarget
				//.register(userAuth)
	            .register(adminAuth)
	            .path(PROFESSOR_SUBRESOURCE_NAME)
	            .request()
	            .post(Entity.json(professor));
			 assertThat(response.getStatus(), is(200));
		 }
		 
		 
		 @Test
		 @Order(6)
		 /**
		  * To add to professor with user role
		  * @throws JsonMappingException
		  * @throws JsonProcessingException
		  */
	     public void test06_add_professor_with_userrole() throws JsonMappingException, JsonProcessingException{
			 Professor professor = new Professor("Issae", "July", "Movie", null);
			 Response response = webTarget
				.register(userAuth)
	            .path(PROFESSOR_SUBRESOURCE_NAME)
	            .request()
	            .post(Entity.json(professor));
			 assertThat(response.getStatus(), is(403));
	    }
		
	   
	    @Test
	    @Order(7)
	    /**
	     * To delete from professor with admin role
	     * @throws JsonMappingException
	     * @throws JsonProcessingException
	     */
	    public void test07_delete_professor_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
	    	Response response = webTarget
		    	//.register(userAuth)        	
		        .register(adminAuth)
		        .path(PROFESSOR_SUBRESOURCE_NAME+"/"+ 1)
		        .request()
		        .delete();
	        assertThat(response.getStatus(), is(200));
	    }
	    
	    
	    @Test
	    @Order(8)
	    /**
	     * To delete from Professor table with user role
	     * @throws JsonMappingException
	     * @throws JsonProcessingException
	     */
	    public void test08_delete_professor_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
	    	Response response = webTarget
		    	.register(userAuth)        	
		        //.register(adminAuth)
		        .path(PROFESSOR_SUBRESOURCE_NAME+"/"+professor.getId())
		        .request()
		        .delete();
	        assertThat(response.getStatus(), is(403));
	    }

}
