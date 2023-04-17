package com.app.springapp2.e2e;

import com.app.springapp2.SpringApp2Application;
import com.app.springapp2.model.User;
import com.app.springapp2.repo.UserRepo;
import com.app.springapp2.requests.UserRequests;
import com.app.springapp2.utilities.JsonSerializer;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.Map;
import java.util.NoSuchElementException;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringApp2Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRegistrationTest {

    StringBuilder errors = null;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    ApplicationContext context;

    void before() {
        errors = new StringBuilder();
    }
    void after() {
        if ((errors != null) && (!errors.isEmpty())) {
            Assert.assertEquals("", errors.toString());
        }
    }

    @Test
    @Order(1)
    public void createUser() {
        before();

        UserRequests userRequests = new UserRequests("http://localhost:8080/");
        long id = userRequests.createUser("usr11");

        Response response = userRequests.getResponse();

        ResponseBody body = response.getBody();
        Integer statusCode = response.getStatusCode();
        Map map = userRequests.getRespondedParameters();
        // validate response
        if (!statusCode.equals(200)) {
            errors.append("Response status code invalid: exp - 200, got - " + statusCode + "\n");
        }
        if (!map.get("username").equals("usr11")) {
            errors.append("Invalid responded username: exp - usr11, got - " + map.get("username") + "\n");
        }

        // check database
        User dbUser = userRepo.findById(id).get();
        if (!dbUser.getUsername().equals("usr11")) {
            errors.append("Invalid username in DB: exp - usr11, got - " + dbUser.getUsername() + "\n");
        }

        // delete user
        userRepo.delete(dbUser);
        boolean isNsee = false;
        User dbUser2 = null;
        try {
            dbUser2 = userRepo.findById(id).get();
        } catch (NoSuchElementException nsee) {
            isNsee = true;
        }
        if (!isNsee) throw new RuntimeException("User not deleted - " + dbUser2);

        after();
        System.out.println();
    }

    @Test
    @Order(2)
    public void authenticateUser() {
        before();

        UserRequests userRequests = new UserRequests("http://localhost:8080/");
        Long id = userRequests.createUser("usr01");
        String token = userRequests.logon("usr01");

        Response response = userRequests.getResponse();
        Integer statusCode = response.getStatusCode();

        if (!statusCode.equals(200)) {
            errors.append("Response status code invalid: exp - 200, got - " + statusCode + "\n");
        }
        if (token.isEmpty()) {
            errors.append("Empty token - " + token + "\n");
        }

        User dbUser = userRepo.findById(id).get();
        userRepo.delete(dbUser);

        after();
        System.out.println();
    }

    @Test
    @Order(3)
    public void creationWithUnavailableName() {
        before();

        UserRequests userRequests = new UserRequests("http://localhost:8080/");
        long id = userRequests.createUser("usr12");
        try {
            userRequests.createUser("usr12");
        } catch (Exception e) {}

        Response response = userRequests.getResponse();

        String bodyStr = response.getBody().asString();
        Integer statusCode = response.getStatusCode();
        // validate response
        if (!statusCode.equals(400)) {
            errors.append("Response status code invalid: exp - 400, got - " + statusCode + "\n");
        }
        if (!bodyStr.contains("constraint [USER_TBL.unique_user_name]")) {
            errors.append("Invalid response: should contain \"constraint [USER_TBL.unique_user_name]\", got - "
                    + bodyStr + "\n");
        }

        // check database
        User dbUser = userRepo.findById(id).get();

        // delete user
        userRepo.delete(dbUser);
        after();
        System.out.println();
    }
}
