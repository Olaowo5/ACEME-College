/**
 * File:  TestACMECollegeSystem.java
 * Course materials (23W) CST 8277
 * Teddy Yap
 * (Original Author) Mike Norman
 *
 * @date 2020 10
 *
 * (Modified)  * 
 * Updated by:  Group 3
 *   040982007, Olamide, Owolabi (as from ACSIS)
 *   41056895, Jennifer Acevedocarmona (as from ACSIS)
 *   041030048, Delorme, Keelan (as from ACSIS)
 */
package acmecollege;

import static acmecollege.utility.MyConstants.APPLICATION_API_VERSION;
import static acmecollege.utility.MyConstants.APPLICATION_CONTEXT_ROOT;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static acmecollege.utility.MyConstants.DEFAULT_USER;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static acmecollege.utility.MyConstants.STUDENT_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.MEMBERSHIP_CARD_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.CLUB_MEMBERSHIP_RESOURCE_NAME;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import acmecollege.entity.Student;
import acmecollege.entity.*;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.client.Entity;
import org.junit.jupiter.api.Order;


@SuppressWarnings("unused")

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestACMECollegeSystem {
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    private static final Logger logger = LogManager.getLogger(_thisClaz);

    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 8080;

    // Test fixture(s)
    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;

    @BeforeAll
    public static void oneTimeSetUp() throws Exception {
        logger.debug("oneTimeSetUp\n");
        uri = UriBuilder
            .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
            .scheme(HTTP_SCHEMA)
            .host(HOST)
            .port(PORT)
            .build();
        adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
        userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER, DEFAULT_USER_PASSWORD);
    }

    protected WebTarget webTarget;
    @BeforeEach
    public void setUp() {
    	//logger.debug("Olacall");
        Client client = ClientBuilder.newClient(
            new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
        webTarget = client.target(uri);
       // logger.debug("\nOlacall " + uri+"\n");
        //logger.debug("bingo " + webTarget.toString()+"\n");
    }

    @Test
    @Order(1)
    public void test01_all_students_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	
    	 logger.debug("Olacallii " + uri +"\n");
//    	/System.out.println("Response Status: " + webTarget.toString());
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .get();
        logger.debug("Olacalliii " + webTarget.toString());
        assertThat(response.getStatus(), is(200));
        List<Student> students = response.readEntity(new GenericType<List<Student>>(){});
        assertThat(students, is(not(empty())));
       // assertThat(students, hasSize(1));
       // assertThat(students, hassize(greaterThanOrEqualTo(1)));
    }
    
    /**
     * Test get all membership cards with admin privilege
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(2)
    public void test01_all_membership_cards_with_adminrole() throws JsonMappingException, JsonProcessingException{
            Response response = webTarget
                    //.register(userAuth)
                    .register(adminAuth)
                    .path(MEMBERSHIP_CARD_RESOURCE_NAME)
                    .request()
                    .get();
            assertThat(response.getStatus(), is(200));
            logger.debug("WHats the stitch " + response.getStatus());
            List<MembershipCard> cards = response.readEntity(new GenericType<List<MembershipCard>>(){});
          
            assertThat(cards, is(not(empty())));
    }

    /**
     * Test get all membership cards with user privilege, which will get HTTP 403 Forbidden response,
     * insufficient rights to a resource
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(3)
    public void test02_all_membership_cards_with_userrole() throws JsonMappingException, JsonProcessingException{
        Response response = webTarget
                .register(userAuth)
                //.register(adminAuth)
                .path(MEMBERSHIP_CARD_RESOURCE_NAME)
                .request()
                .get();
        assertThat(response.getStatus(), is(403)); //insufficient rights to a resource
    }

    @Test
    @Order(4)
    public void test02_membership_card_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException{
        Response response = webTarget
                //.register(userAuth)
                .register(adminAuth)
                .path(MEMBERSHIP_CARD_RESOURCE_NAME + "/1")
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
        MembershipCard card = response.readEntity(MembershipCard.class);
        assertThat(card.getId(), is(1));
    }

    @Test
    @Order(5)
    public void test03_add_membership_card() throws JsonMappingException, JsonProcessingException{
        Student owner = new Student();
        owner.setId(1);

        ClubMembership membership = new ClubMembership();
        membership.setId(1);
        MembershipCard card = new MembershipCard();
        card.setClubMembership(membership);
        card.setOwner(owner);
        card.setSigned(true);

        Response response = webTarget
                //.register(userAuth)
                .register(adminAuth)
                .path(MEMBERSHIP_CARD_RESOURCE_NAME)//
                .request()
                .post(Entity.entity(card, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(200));
        assertThat(card.getOwner().getId(), is(1));
    }

    /**
     * Test to add a membership cards with user privilege, which will get HTTP 403 Forbidden response,
     * insufficient rights to a resource
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(6)
    public void test04_add_membership_card_with_userrole() throws JsonMappingException, JsonProcessingException{
        Student owner = new Student();
        owner.setId(1);

        ClubMembership membership = new ClubMembership();
        membership.setId(1);
        MembershipCard card = new MembershipCard();
        card.setClubMembership(membership);
        card.setOwner(owner);
        card.setSigned(true);
        Response response = webTarget
                .register(userAuth)
               //.register(adminAuth)
                .path(MEMBERSHIP_CARD_RESOURCE_NAME)//
                .request()
                .post(Entity.entity(card, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(403)); //insufficient rights to a resource
    }


    /**
     * Membership Card 9 does not belong to user cst8277, expect get 403 no privilege
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(7)
    public void test04_membership_card_by_id_with_userrole() throws JsonMappingException, JsonProcessingException{
        Response response = webTarget
                .register(userAuth)
                //.register(adminAuth)
                .path(MEMBERSHIP_CARD_RESOURCE_NAME + "/9")
                .request()
                .get();
        assertThat(response.getStatus(), is(403));

    }

    /**
     * Admin delete a membership card
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(8)
    public void test04_delete_membership_card_by_id_with_Adminrole() throws JsonMappingException, JsonProcessingException{
        Response response1 = webTarget
                //.register(userAuth)
                .register(adminAuth)
                .path(MEMBERSHIP_CARD_RESOURCE_NAME)
                .request()
                .get();
        List<MembershipCard> cards = response1.readEntity(new GenericType<List<MembershipCard>>(){});
        String id = Integer.toString(cards.get(cards.size() -1).getId());
        
    	Response response2 = webTarget
                //.register(userAuth)
                .register(adminAuth)
                .path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + id)
                .request()
                .delete();
        assertThat(response2.getStatus(), is(200));
    }

    @Test
    @Order(9)
    public void test04_update_membership_card_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException{
        Response response1 = webTarget
                //.register(userAuth)
                .register(adminAuth)
                .path(MEMBERSHIP_CARD_RESOURCE_NAME)
                .request()
                .get();
        List<MembershipCard> cards = response1.readEntity(new GenericType<List<MembershipCard>>(){});
        Student updatingStudent = new Student();
        updatingStudent.setLastName("Adejoke");
        updatingStudent.setFirstName("Kunbi");
        MembershipCard card = cards.get(cards.size() - 1);
        card.setOwner(updatingStudent);


        Response response2 = webTarget
                //.register(userAuth)
                .register(adminAuth)
                .path(MEMBERSHIP_CARD_RESOURCE_NAME + "/1")
                .request()
                .put(Entity.entity(card, MediaType.APPLICATION_JSON));
        assertThat(response2.getStatus(), is(200));
        assertThat(card.getOwner().getFirstName(), is("Kunbi"));
    }
}