package com.app.springapp2.db;

import com.app.springapp2.SpringApp2Application;
import com.app.springapp2.model.Product;
import com.app.springapp2.model.User;
import com.app.springapp2.repo.PositionRepo;
import com.app.springapp2.repo.ProductRepo;
import com.app.springapp2.repo.UserRepo;
import com.app.springapp2.service.PositionService;
import com.app.springapp2.service.ProductService;
import com.app.springapp2.service.UserService;
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

import java.util.List;
import java.util.Map;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringApp2Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DBTest {

    StringBuilder errors = null;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private PositionRepo positionRepo;
    @Autowired
    private ProductService productService;
    @Autowired
    private PositionService positionService;

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
    public void createProduct() {
        before();


        Product p1 = productService.create(
                "product03",
                99.99,
                1.2,
                0.7,
                21.1,
                245.1
        );

        List<Product> ps = productService.getAllProducts();
        Product fp = productRepo.findByName(p1.getName());

        after();
        System.out.println();
    }

    @Test
    @Order(2)
    public void editProduct() {
        before();








        after();
        System.out.println();
    }

    @Test
    @Order(3)
    public void deleteProduct() {
        before();








        after();
        System.out.println();
    }
}
