package com.app.springapp2.e2e;

import com.app.springapp2.SpringApp2Application;
import com.app.springapp2.model.Product;
import com.app.springapp2.model.User;
import com.app.springapp2.repo.PositionRepo;
import com.app.springapp2.repo.ProductRepo;
import com.app.springapp2.repo.UserRepo;
import com.app.springapp2.requests.PositionRequests;
import com.app.springapp2.requests.ProductRequests;
import com.app.springapp2.requests.UserRequests;
import com.app.springapp2.service.PositionService;
import com.app.springapp2.utilities.JsonSerializer;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringApp2Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PositionTests {

    Long userId1 = null;
    String token1 = null;
    String username1 = "usr21";
    Long userId2 = null;
    String token2 = null;
    String username2 = "usr22";

    Long productId1 = null;
    Long productId2 = null;
    StringBuilder errors = null;
    @Autowired
    private PositionService positionService;
    @Autowired
    private PositionRepo positionRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    ApplicationContext context;

    @BeforeAll
    void beforeAll() {
        UserRequests userRequests = new UserRequests("http://localhost:8080/");
        userId1 = userRequests.createUser(username1);
        token1 = userRequests.logon(username1);
        userId2 = userRequests.createUser(username2);
        token2 = userRequests.logon(username2);

        ProductRequests productRequests = new ProductRequests("http://localhost:8080/", token1);
        productId1 = productRequests.createProduct(
                "product21", 77.77, 7.7, 7.8, 7.9, 177.71
        );
        productId2 = productRequests.createProduct(
                "product22", 88.88, 8.7, 8.8, 8.9, 188.81
        );
    }
    @AfterAll
    void afterAll() {
        userRepo.delete(userRepo.findById(userId1).get());
        userRepo.delete(userRepo.findById(userId2).get());
        productRepo.delete(productRepo.findById(productId1).get());
        productRepo.delete(productRepo.findById(productId2).get());
    }
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
    public void createPosition() {
        before();

        String date = "2023-03-23";
        Double mass = 500.50;

        PositionRequests positionRequests = new PositionRequests("http://localhost:8080/", token1);
        Long posId1 = positionRequests.createPosition(productId1, mass, date);

        Integer statusCode = positionRequests.getResponse().getStatusCode();
        Map map = positionRequests.getRespondedParameters();

        String rDate = map.get("date").toString();
        Double rMass = Double.valueOf(map.get("mass").toString());
        Double rSum = Double.valueOf(map.get("sum").toString());
        Double rCalcB = Double.valueOf(map.get("calcB").toString());
        Double rCalcJ = Double.valueOf(map.get("calcJ").toString());
        Double rCalcU = Double.valueOf(map.get("calcU").toString());
        Double rTotalEnergy = Double.valueOf(map.get("totalenergy").toString());

        String ownerIdStr = ((Map) map.get("owner")).get("id").toString();
        Long ownerId = Long.parseLong(ownerIdStr.substring(0, ownerIdStr.length()-2));
        String ownerName = ((Map) map.get("owner")).get("username").toString();
        try {
            ((Map) map.get("owner")).get("password").toString();
            errors.append("Response contains users password: " + map + "\n");
        } catch (NullPointerException npe) {}

        String prodIdStr = ((Map) map.get("product")).get("id").toString();
        Long prodId = Long.parseLong(prodIdStr.substring(0, prodIdStr.length()-2));
        String prodName = ((Map) map.get("product")).get("name").toString();
        Double prodPrice = Double.valueOf(((Map) map.get("product")).get("price").toString());
        Double prodB = Double.valueOf(((Map) map.get("product")).get("b").toString());
        Double prodJ = Double.valueOf(((Map) map.get("product")).get("j").toString());
        Double prodU = Double.valueOf(((Map) map.get("product")).get("u").toString());
        Double prodKkl = Double.valueOf(((Map) map.get("product")).get("kkl").toString());

        if (!statusCode.equals(200)) {
            errors.append("Response status code invalid: exp - 200, got - " + statusCode + "\n");
        }

        // check mass and date
        if (!rMass.equals(mass)) {
            errors.append("Responded owner mismatch: exp - " + mass + ", got - " + rMass + "\n");
        }
        if (!rDate.equals(date)) {
            errors.append("Responded owner mismatch: exp - " + date + ", got - " + rDate + "\n");
        }

        // check calc fields
        Double recalcSum = rMass * prodPrice;
        if (!rSum.equals(recalcSum)) {
            errors.append("Responded sum mismatch: exp - " + recalcSum + ", got - " + rSum + "\n");
        }
        Double recalcB = rMass * prodB;
        if (!rCalcB.equals(recalcB)) {
            errors.append("Responded B mismatch: exp - " + recalcB + ", got - " + rCalcB + "\n");
        }
        Double recalcJ = rMass * prodJ;
        if (!rCalcJ.equals(recalcJ)) {
            errors.append("Responded J mismatch: exp - " + recalcJ + ", got - " + rCalcJ + "\n");
        }
        Double recalcU = rMass * prodU;
        if (!rCalcU.equals(recalcU)) {
            errors.append("Responded U mismatch: exp - " + recalcU + ", got - " + rCalcU + "\n");
        }
        Double recalcEnergy = rMass * prodKkl;
        if (!rTotalEnergy.equals(recalcEnergy)) {
            errors.append("Responded TotalKkl mismatch: exp - " + recalcEnergy + ", got - " + rTotalEnergy + "\n");
        }

        // check owner
        if (!ownerId.equals(userId1)) {
            errors.append("Responded owner mismatch: exp - " + userId1 + ", got - " + ownerId + "\n");
        }
        if (!ownerName.equals(username1)) {
            errors.append("Responded owner mismatch: exp - " + username1 + ", got - " + ownerName + "\n");
        }

        // check product
        if (!prodId.equals(productId1)) {
            errors.append("Responded Product mismatch: exp - " + productId1 + ", got - " + prodId + "\n");
        }
        if (!prodName.equals("product21")) {
            errors.append("Responded Product mismatch: exp - product21, got - " + prodName + "\n");
        }

        positionRepo.delete(positionRepo.findById(posId1).get());


        after();
        System.out.println();
    }

    @Test
    @Order(2)
    public void createPositionWithWrongProduct() {
        before();

        String date = "2023-03-23";
        Double mass = 500.50;
        Long wrongPrId = productId1 + 1000;

        PositionRequests positionRequests = new PositionRequests("http://localhost:8080/", token1);
        try {
            positionRequests.createPosition(wrongPrId, mass, date);
        } catch (Exception e) {}

        Integer statusCode = positionRequests.getResponse().getStatusCode();
        String bodyStr = positionRequests.getResponse().getBody().asString();
        // validate response
        if (!statusCode.equals(400)) {
            errors.append("Response status code invalid: exp - 400, got - " + statusCode + "\n");
        }
        if (
                !(bodyStr.contains("Product with ID = " + wrongPrId + " not found."))
        ) {
            errors.append("Response invalid - " + bodyStr + "\n");
        }

        after();
        System.out.println();
    }


    @Test
    @Order(3)
    public void updatePosition() {
        before();

        String date = "2023-03-23";
        Double mass = 500.50;
        String updDate = "2023-04-05";
        Double updMass = 666.85;

        PositionRequests positionRequests = new PositionRequests("http://localhost:8080/", token1);
        Long posId1 = positionRequests.createPosition(productId1, mass, date);
        Long updPosId1 = positionRequests.updatePosition(posId1, productId2, updMass, updDate);

        Integer statusCode = positionRequests.getResponse().getStatusCode();
        Map map = positionRequests.getRespondedParameters();

        String rDate = map.get("date").toString();
        Double rMass = Double.valueOf(map.get("mass").toString());
        Double rSum = Double.valueOf(map.get("sum").toString());
        Double rCalcB = Double.valueOf(map.get("calcB").toString());
        Double rCalcJ = Double.valueOf(map.get("calcJ").toString());
        Double rCalcU = Double.valueOf(map.get("calcU").toString());
        Double rTotalEnergy = Double.valueOf(map.get("totalenergy").toString());

        String ownerIdStr = ((Map) map.get("owner")).get("id").toString();
        Long ownerId = Long.parseLong(ownerIdStr.substring(0, ownerIdStr.length()-2));
        String ownerName = ((Map) map.get("owner")).get("username").toString();
        try {
            ((Map) map.get("owner")).get("password").toString();
            errors.append("Response contains users password: " + map + "\n");
        } catch (NullPointerException npe) {}

        String prodIdStr = ((Map) map.get("product")).get("id").toString();
        Long prodId = Long.parseLong(prodIdStr.substring(0, prodIdStr.length()-2));
        String prodName = ((Map) map.get("product")).get("name").toString();
        Double prodPrice = Double.valueOf(((Map) map.get("product")).get("price").toString());
        Double prodB = Double.valueOf(((Map) map.get("product")).get("b").toString());
        Double prodJ = Double.valueOf(((Map) map.get("product")).get("j").toString());
        Double prodU = Double.valueOf(((Map) map.get("product")).get("u").toString());
        Double prodKkl = Double.valueOf(((Map) map.get("product")).get("kkl").toString());

        if (!statusCode.equals(200)) {
            errors.append("Response status code invalid: exp - 200, got - " + statusCode + "\n");
        }

        // check mass and date
        if (!rMass.equals(updMass)) {
            errors.append("Responded mass mismatch: exp - " + updMass + ", got - " + rMass + "\n");
        }
        if (!rDate.equals(updDate)) {
            errors.append("Responded date mismatch: exp - " + updDate + ", got - " + rDate + "\n");
        }

        // check calc fields
        Double recalcSum = rMass * prodPrice;
        if (!rSum.equals(recalcSum)) {
            errors.append("Responded sum mismatch: exp - " + recalcSum + ", got - " + rSum + "\n");
        }
        Double recalcB = rMass * prodB;
        if (!rCalcB.equals(recalcB)) {
            errors.append("Responded B mismatch: exp - " + recalcB + ", got - " + rCalcB + "\n");
        }
        Double recalcJ = rMass * prodJ;
        if (!rCalcJ.equals(recalcJ)) {
            errors.append("Responded J mismatch: exp - " + recalcJ + ", got - " + rCalcJ + "\n");
        }
        Double recalcU = rMass * prodU;
        if (!rCalcU.equals(recalcU)) {
            errors.append("Responded U mismatch: exp - " + recalcU + ", got - " + rCalcU + "\n");
        }
        Double recalcEnergy = rMass * prodKkl;
        if (!rTotalEnergy.equals(recalcEnergy)) {
            errors.append("Responded TotalKkl mismatch: exp - " + recalcEnergy + ", got - " + rTotalEnergy + "\n");
        }

        // check owner
        if (!ownerId.equals(userId1)) {
            errors.append("Responded owner mismatch: exp - " + userId1 + ", got - " + ownerId + "\n");
        }
        if (!ownerName.equals(username1)) {
            errors.append("Responded owner mismatch: exp - " + username1 + ", got - " + ownerName + "\n");
        }

        // check product
        if (!prodId.equals(productId2)) {
            errors.append("Responded Product mismatch: exp - " + productId2 + ", got - " + prodId + "\n");
        }
        if (!prodName.equals("product22")) {
            errors.append("Responded Product mismatch: exp - product22, got - " + prodName + "\n");
        }

        positionRepo.delete(positionRepo.findById(posId1).get());

        after();
        System.out.println();
    }

    @Test
    @Order(4)
    public void updatePositionWithWrongProduct() {
        before();

        String date = "2023-03-23";
        Double mass = 500.50;
        Long wrongPrId = productId1 + 1000;

        PositionRequests positionRequests = new PositionRequests("http://localhost:8080/", token1);
        Long posId1 = positionRequests.createPosition(productId1, mass, date);
        try {
            positionRequests.updatePosition(posId1, wrongPrId, mass, date);
        } catch (Exception e) {}

        Integer statusCode = positionRequests.getResponse().getStatusCode();
        String bodyStr = positionRequests.getResponse().getBody().asString();
        // validate response
        if (!statusCode.equals(400)) {
            errors.append("Response status code invalid: exp - 400, got - " + statusCode + "\n");
        }
        if (
                !(bodyStr.contains("Product with ID = " + wrongPrId + " not found."))
        ) {
            errors.append("Response invalid - " + bodyStr + "\n");
        }

        positionRepo.delete(positionRepo.findById(posId1).get());

        after();
        System.out.println();
    }

    @Test
    @Order(5)
    public void updateEnemiesPosition() {
        before();

        String date = "2023-03-23";
        Double mass = 500.50;

        PositionRequests positionRequests1 = new PositionRequests("http://localhost:8080/", token1);
        Long posId1 = positionRequests1.createPosition(productId1, mass, date);

        PositionRequests positionRequests2 = new PositionRequests("http://localhost:8080/", token2);
        try {
            positionRequests2.updatePosition(posId1, productId2, mass, date);
        } catch (Exception e) {}

        Integer statusCode = positionRequests2.getResponse().getStatusCode();
        String bodyStr = positionRequests2.getResponse().getBody().asString();

        // validate response
        if (!statusCode.equals(400)) {
            errors.append("Response status code invalid: exp - 400, got - " + statusCode + "\n");
        }
        if (
                !(bodyStr.contains("Only owned position update allowed."))
        ) {
            errors.append("Banned update executed - " + bodyStr + "\n");
        }

        positionRepo.delete(positionRepo.findById(posId1).get());

        after();
        System.out.println();
    }

    @Test
    @Order(6)
    public void deletePosition() {

        before();

        String date = "2023-02-23";
        Double mass = 500.50;

        PositionRequests positionRequests = new PositionRequests("http://localhost:8080/", token1);
        Long posId1 = positionRequests.createPosition(productId1, mass, date);

        positionRequests.deletePosition(posId1);

        Integer statusCode = positionRequests.getResponse().getStatusCode();
        String bodyStr = positionRequests.getResponse().getBody().asString();

        // validate response
        if (!statusCode.equals(200)) {
            errors.append("Response status code invalid: exp - 200, got - " + statusCode + "\n");
        }
        if (!bodyStr.equals("Position deleted")) {
            errors.append("Wrong response body: exp - \"Position deleted\", got - " + bodyStr + "\n");
        }
        boolean found = false;
        try {
            positionRepo.findById(posId1).get();
            found = true;
        } catch (NoSuchElementException nsee) {}

        if (found) {
            errors.append("Position not deleted from DataBase.");
        }

        after();
        System.out.println();
    }

    @Test
    @Order(7)
    public void deleteEnemiesPosition() {
        before();

        String date = "2023-03-23";
        Double mass = 500.50;

        PositionRequests positionRequests = new PositionRequests("http://localhost:8080/", token1);
        Long posId1 = positionRequests.createPosition(productId1, mass, date);

        PositionRequests positionRequests2 = new PositionRequests("http://localhost:8080/", token2);
        positionRequests2.deletePosition(posId1);

        Integer statusCode = positionRequests2.getResponse().getStatusCode();
        String bodyStr = positionRequests2.getResponse().getBody().asString();

        // validate response
        if (!statusCode.equals(400)) {
            errors.append("Response status code invalid: exp - 400, got - " + statusCode + "\n");
        }
        if (!bodyStr.contains("Wrong owner, not enough rights")) {
            errors.append("Wrong response body: exp - \"Wrong owner, not enough rights\", got - " + bodyStr + "\n");
        }
        boolean found = false;
        try {
            positionRepo.findById(posId1).get();
            found = true;
        } catch (NoSuchElementException nsee) {}

        if (!found) {
            errors.append("Position deleted from DataBase without required rights.");
        }

        positionRepo.delete(positionRepo.findById(posId1).get());

        after();
        System.out.println();
    }

    @Test
    @Order(8)
    public void receivePositionsForOwnerAndDate() {
        before();

        String date1 = "2023-03-23";
        String date2 = "2023-04-03";
        Double mass = 500.50;

        PositionRequests pR1 = new PositionRequests("http://localhost:8080/", token1);
        Long posId11 = pR1.createPosition(productId1, mass, date1);
        Long posId12 = pR1.createPosition(productId2, mass, date2);
        Long posId13 = pR1.createPosition(productId2, mass, date1);

        PositionRequests pR2 = new PositionRequests("http://localhost:8080/", token2);
        Long posId21 = pR2.createPosition(productId1, mass, date1);
        Long posId22 = pR2.createPosition(productId2, mass, date2);

        pR1.requestDailyPositions(date1);
        Integer statusCode11 = pR1.getResponse().getStatusCode();
        if (!statusCode11.equals(200)) {
            errors.append("Response StatusCode11 invalid: exp - 200, got - " + statusCode11 + "\n");
        }
        List<Map<String,Object>> list11 = pR1.getRespondedListOfParameters();
        for (Map<String, Object> m : list11) {
            String rDate = m.get("date").toString();
            String ownerIdStr11 = ((Map) m.get("owner")).get("id").toString();
            Long ownerId = Long.parseLong(ownerIdStr11.substring(0, ownerIdStr11.length()-2));
            if (
                    (!rDate.equals(date1)) || (!ownerId.equals(userId1))
            ) {
                    errors.append("Wrong positions returned: " + list11 + " for " + ownerId + " and " + date1);
            }
        }
        if (list11.size() != 2) {errors.append("Wrong list11 size: exp - 2, got - " + list11);}


        pR1.requestDailyPositions(date2);
        Integer statusCode12 = pR1.getResponse().getStatusCode();
        if (!statusCode12.equals(200)) {
            errors.append("Response StatusCode11 invalid: exp - 200, got - " + statusCode12 + "\n");
        }
        List<Map<String,Object>> list12 = pR1.getRespondedListOfParameters();
        for (Map<String, Object> m : list12) {
            String rDate = m.get("date").toString();
            String ownerIdStr12 = ((Map) m.get("owner")).get("id").toString();
            Long ownerId = Long.parseLong(ownerIdStr12.substring(0, ownerIdStr12.length()-2));
            if (
                    (!rDate.equals(date2)) || (!ownerId.equals(userId1))
            ) {
                errors.append("Wrong positions returned: " + list12 + " for " + ownerId + " and " + date2);
            }
        }
        if (list12.size() != 1) {errors.append("Wrong list12 size: exp - 1, got - " + list12);}


        pR2.requestDailyPositions(date1);
        Integer statusCode21 = pR2.getResponse().getStatusCode();
        if (!statusCode21.equals(200)) {
            errors.append("Response StatusCode21 invalid: exp - 200, got - " + statusCode21 + "\n");
        }
        List<Map<String,Object>> list21 = pR2.getRespondedListOfParameters();
        for (Map<String, Object> m : list21) {
            String rDate = m.get("date").toString();
            String ownerIdStr21 = ((Map) m.get("owner")).get("id").toString();
            Long ownerId = Long.parseLong(ownerIdStr21.substring(0, ownerIdStr21.length()-2));
            if (
                    (!rDate.equals(date1)) || (!ownerId.equals(userId2))
            ) {
                errors.append("Wrong positions returned: " + list21 + " for " + ownerId + " and " + date1);
            }
        }
        if (list21.size() != 1) {errors.append("Wrong list21 size: exp - 1, got - " + list21);}


        pR2.requestDailyPositions(date2);
        Integer statusCode22 = pR2.getResponse().getStatusCode();
        if (!statusCode22.equals(200)) {
            errors.append("Response StatusCode22 invalid: exp - 200, got - " + statusCode22 + "\n");
        }
        List<Map<String,Object>> list22 = pR2.getRespondedListOfParameters();
        for (Map<String, Object> m : list22) {
            String rDate = m.get("date").toString();
            String ownerIdStr22 = ((Map) m.get("owner")).get("id").toString();
            Long ownerId = Long.parseLong(ownerIdStr22.substring(0, ownerIdStr22.length()-2));
            if (
                    (!rDate.equals(date2)) || (!ownerId.equals(userId2))
            ) {
                errors.append("Wrong positions returned: " + list22 + " for " + ownerId + " and " + date2);
            }
        }
        if (list22.size() != 1) {errors.append("Wrong list22 size: exp - 1, got - " + list22);}

        positionRepo.delete(positionRepo.findById(posId11).get());
        positionRepo.delete(positionRepo.findById(posId12).get());
        positionRepo.delete(positionRepo.findById(posId13).get());
        positionRepo.delete(positionRepo.findById(posId21).get());
        positionRepo.delete(positionRepo.findById(posId22).get());

        after();
        System.out.println();
    }
}
