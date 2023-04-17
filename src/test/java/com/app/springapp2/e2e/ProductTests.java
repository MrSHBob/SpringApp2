package com.app.springapp2.e2e;

import com.app.springapp2.SpringApp2Application;
import com.app.springapp2.model.Product;
import com.app.springapp2.model.User;
import com.app.springapp2.repo.ProductRepo;
import com.app.springapp2.repo.UserRepo;
import com.app.springapp2.requests.ProductRequests;
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

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringApp2Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductTests {

    Long userId = null;
    Long productId = null;
    String token = null;
    StringBuilder errors = null;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    ApplicationContext context;

    @BeforeAll
    void beforeAll() {
        UserRequests userRequests = new UserRequests("http://localhost:8080/");
        userId = userRequests.createUser("usr13");
        token = userRequests.logon("usr13");
    }

    @AfterAll
    void afterAll() {
        userRepo.delete(userRepo.findById(userId).get());
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
    public void createProduct() {
        before();

        String product = "product02";
        Double price = 99.99;
        Double b = 1.2;
        Double j = 0.7;
        Double u = 21.1;
        Double kkl = 245.1;

        ProductRequests productRequests = new ProductRequests("http://localhost:8080/", token);
        productId = productRequests.createProduct(product, price, b, j, u, kkl);

        Response response = productRequests.getResponse();
        Integer statusCode = response.getStatusCode();
        Map map = productRequests.getRespondedParameters();
        // validate response
        if (!statusCode.equals(200)) {
            errors.append("Response status code invalid: exp - 200, got - " + statusCode + "\n");
        }
        String respProduct = map.get("name").toString();
        Double respPrice = Double.valueOf(map.get("price").toString());
        Double respB = Double.valueOf(map.get("b").toString());
        Double respJ = Double.valueOf(map.get("j").toString());
        Double respU = Double.valueOf(map.get("u").toString());
        Double respKkl = Double.valueOf(map.get("kkl").toString());

        if (!respProduct.equals(product)) {
            errors.append("Invalid responded product name: exp - " + product + ", got - " + respProduct + "\n");
        }
        if (!respPrice.equals(price)) {
            errors.append("Invalid responded price: exp - " + price + ", got - " + respPrice + "\n");
        }
        if (!respB.equals(b)) {
            errors.append("Invalid responded B: exp - " + b + ", got - " + respB + "\n");
        }
        if (!respJ.equals(j)) {
            errors.append("Invalid responded J: exp - " + j + ", got - " + respJ + "\n");
        }
        if (!respU.equals(u)) {
            errors.append("Invalid responded U: exp - " + u + ", got - " + respU + "\n");
        }
        if (!respKkl.equals(kkl)) {
            errors.append("Invalid responded KKL: exp - " + kkl + ", got - " + respKkl + "\n");
        }

        // check database
        Product dbProduct = productRepo.findById(productId).get();
        if (!dbProduct.getName().equals(product)) {
            errors.append("Invalid product name in DB: exp - " + product + ", got - " + dbProduct.getName() + "\n");
        }
        if (!dbProduct.getPrice().equals(price)) {
            errors.append("Invalid price in DB: exp - " + price + ", got - " + dbProduct.getPrice() + "\n");
        }
        if (!dbProduct.getB().equals(b)) {
            errors.append("Invalid B in DB: exp - " + b + ", got - " + dbProduct.getB() + "\n");
        }
        if (!dbProduct.getJ().equals(j)) {
            errors.append("Invalid J in DB: exp - " + j + ", got - " + dbProduct.getJ() + "\n");
        }
        if (!dbProduct.getU().equals(u)) {
            errors.append("Invalid U in DB: exp - " + u + ", got - " + dbProduct.getU() + "\n");
        }
        if (!dbProduct.getKkl().equals(kkl)) {
            errors.append("Invalid KKL in DB: exp - " + kkl + ", got - " + dbProduct.getKkl() + "\n");
        }

        productRepo.delete(productRepo.findById(productId).get());

        boolean isNsee = false;
        Product dbProduct2 = null;
        try {
            dbProduct2 = productRepo.findById(productId).get();
        } catch (NoSuchElementException nsee) {
            isNsee = true;
        }
        if (!isNsee) throw new RuntimeException("Product not deleted - " + dbProduct2);

        after();
        System.out.println();
    }

    @Test
    @Order(2)
    public void updateProduct() {
        before();

        String product1 = "product03";
        Double price1 = 99.99;
        Double b1 = 1.2;
        Double j1 = 0.7;
        Double u1 = 21.1;
        Double kkl1 = 245.1;

        ProductRequests productRequests = new ProductRequests("http://localhost:8080/", token);
        productId = productRequests.createProduct(product1, price1, b1, j1, u1, kkl1);

        String product = "product04";
        Double price = 99.98;
        Double b = 2.2;
        Double j = 3.7;
        Double u = 5.1;
        Double kkl = 115.9;

        Long updId = productRequests.updateProduct(productId, product, price, b, j, u, kkl);
        Integer statusCode = productRequests.getResponse().getStatusCode();
        Map map = productRequests.getRespondedParameters();
        // validate response
        if (!statusCode.equals(200)) {
            errors.append("Response status code invalid: exp - 200, got - " + statusCode + "\n");
        }
        if (!productId.equals(updId)) {
            errors.append("Invalid ID: id to update - " + productId + ", id after update - " + updId + "\n");
        }

        String respProduct = map.get("name").toString();
        Double respPrice = Double.valueOf(map.get("price").toString());
        Double respB = Double.valueOf(map.get("b").toString());
        Double respJ = Double.valueOf(map.get("j").toString());
        Double respU = Double.valueOf(map.get("u").toString());
        Double respKkl = Double.valueOf(map.get("kkl").toString());

        if (!respProduct.equals(product)) {
            errors.append("Invalid responded product name: exp - " + product + ", got - " + respProduct + "\n");
        }
        if (!respPrice.equals(price)) {
            errors.append("Invalid responded price: exp - " + price + ", got - " + respPrice + "\n");
        }
        if (!respB.equals(b)) {
            errors.append("Invalid responded B: exp - " + b + ", got - " + respB + "\n");
        }
        if (!respJ.equals(j)) {
            errors.append("Invalid responded J: exp - " + j + ", got - " + respJ + "\n");
        }
        if (!respU.equals(u)) {
            errors.append("Invalid responded U: exp - " + u + ", got - " + respU + "\n");
        }
        if (!respKkl.equals(kkl)) {
            errors.append("Invalid responded KKL: exp - " + kkl + ", got - " + respKkl + "\n");
        }

        // check database
        Product dbProduct = productRepo.findById(updId).get();
        if (!dbProduct.getName().equals(product)) {
            errors.append("Invalid product name in DB: exp - " + product + ", got - " + dbProduct.getName() + "\n");
        }
        if (!dbProduct.getPrice().equals(price)) {
            errors.append("Invalid price in DB: exp - " + price + ", got - " + dbProduct.getPrice() + "\n");
        }
        if (!dbProduct.getB().equals(b)) {
            errors.append("Invalid B in DB: exp - " + b + ", got - " + dbProduct.getB() + "\n");
        }
        if (!dbProduct.getJ().equals(j)) {
            errors.append("Invalid J in DB: exp - " + j + ", got - " + dbProduct.getJ() + "\n");
        }
        if (!dbProduct.getU().equals(u)) {
            errors.append("Invalid U in DB: exp - " + u + ", got - " + dbProduct.getU() + "\n");
        }
        if (!dbProduct.getKkl().equals(kkl)) {
            errors.append("Invalid KKL in DB: exp - " + kkl + ", got - " + dbProduct.getKkl() + "\n");
        }

        productRepo.delete(productRepo.findById(productId).get());

        after();
        System.out.println();
    }

    @Test
    @Order(3)
    public void createProductWithBusyName() {
        before();

        String product = "product05";
        Double price = 99.98;
        Double b = 2.2;
        Double j = 3.7;
        Double u = 5.1;
        Double kkl = 115.9;

        ProductRequests productRequests = new ProductRequests("http://localhost:8080/", token);
        productId = productRequests.createProduct(product, price, b, j, u, kkl);
        Long doubledId = null;
        try {
            doubledId = productRequests.createProduct(product, price, b, j, u, kkl);
        } catch (Exception e) {}

        Integer statusCode = productRequests.getResponse().getStatusCode();
        String bodyStr = productRequests.getResponse().body().asString();
        // validate response
        if (!statusCode.equals(400)) {
            errors.append("Response status code invalid: exp - 400, got - " + statusCode + "\n");
        }
        if (
                !(bodyStr.contains("DataIntegrityViolationException"))
                || !(bodyStr.contains("PRODUCT_TBL.unique_name"))
        ) {
            errors.append("Response invalid - " + bodyStr + "\n");
        }

        productRepo.delete(productRepo.findById(productId).get());

        after();
        System.out.println();
    }

    @Test
    @Order(4)
    public void updateProductWithBusyName() {
        before();

        String product = "product06";
        Double price = 99.98;
        Double b = 2.2;
        Double j = 3.7;
        Double u = 5.1;
        Double kkl = 115.9;

        String product1 = "product07";
        Double price1 = 92.98;
        Double b1 = 2.5;
        Double j1 = 1.7;
        Double u1 = 11.1;
        Double kkl1 = 234.9;

        ProductRequests productRequests = new ProductRequests("http://localhost:8080/", token);
        productId = productRequests.createProduct(product, price, b, j, u, kkl);
        Long productId1 = productRequests.createProduct(product1, price1, b1, j1, u1, kkl1);
        Long doubledId = null;
        try {
            doubledId = productRequests.updateProduct(productId, product1, price1, b1, j1, u1, kkl1);
        } catch (Exception e) {}

        Integer statusCode = productRequests.getResponse().getStatusCode();
        String bodyStr = productRequests.getResponse().body().asString();
        // validate response
        if (!statusCode.equals(400)) {
            errors.append("Response status code invalid: exp - 400, got - " + statusCode + "\n");
        }
        if (
                !(bodyStr.contains("DataIntegrityViolationException"))
                        || !(bodyStr.contains("PRODUCT_TBL.unique_name"))
        ) {
            errors.append("Response invalid - " + bodyStr + "\n");
        }

        // check DB data
        Product dbProduct = productRepo.findById(productId).get();
        if (!dbProduct.getName().equals(product)) {
            errors.append("Invalid product name in DB: exp - " + product + ", got - " + dbProduct.getName() + "\n");
        }
        if (!dbProduct.getPrice().equals(price)) {
            errors.append("Invalid price in DB: exp - " + price + ", got - " + dbProduct.getPrice() + "\n");
        }
        if (!dbProduct.getB().equals(b)) {
            errors.append("Invalid B in DB: exp - " + b + ", got - " + dbProduct.getB() + "\n");
        }
        if (!dbProduct.getJ().equals(j)) {
            errors.append("Invalid J in DB: exp - " + j + ", got - " + dbProduct.getJ() + "\n");
        }
        if (!dbProduct.getU().equals(u)) {
            errors.append("Invalid U in DB: exp - " + u + ", got - " + dbProduct.getU() + "\n");
        }
        if (!dbProduct.getKkl().equals(kkl)) {
            errors.append("Invalid KKL in DB: exp - " + kkl + ", got - " + dbProduct.getKkl() + "\n");
        }

        Product dbProduct1 = productRepo.findById(productId1).get();
        if (!dbProduct1.getName().equals(product1)) {
            errors.append("Invalid product name in DB: exp - " + product1 + ", got - " + dbProduct1.getName() + "\n");
        }
        if (!dbProduct1.getPrice().equals(price1)) {
            errors.append("Invalid price in DB: exp - " + price1 + ", got - " + dbProduct1.getPrice() + "\n");
        }
        if (!dbProduct1.getB().equals(b1)) {
            errors.append("Invalid B in DB: exp - " + b1 + ", got - " + dbProduct1.getB() + "\n");
        }
        if (!dbProduct1.getJ().equals(j1)) {
            errors.append("Invalid J in DB: exp - " + j1 + ", got - " + dbProduct1.getJ() + "\n");
        }
        if (!dbProduct1.getU().equals(u1)) {
            errors.append("Invalid U in DB: exp - " + u1 + ", got - " + dbProduct1.getU() + "\n");
        }
        if (!dbProduct1.getKkl().equals(kkl1)) {
            errors.append("Invalid KKL in DB: exp - " + kkl1 + ", got - " + dbProduct1.getKkl() + "\n");
        }

        productRepo.delete(productRepo.findById(productId).get());
        productRepo.delete(productRepo.findById(productId1).get());

        after();
        System.out.println();
    }

    @Test
    @Order(5)
    public void getListOfProducts() {
        before();

        String product = "product08";
        Double price = 211.05;
        Double b = 5.0;
        Double j = 3.3;
        Double u = 5.1;
        Double kkl = 115.9;

        String product1 = "product09";
        Double price1 = 145.43;
        Double b1 = 3.3;
        Double j1 = 4.4;
        Double u1 = 5.5;
        Double kkl1 = 234.5;

        String product2 = "product10";
        Double price2 = 550.75;
        Double b2 = 11.11;
        Double j2 = 1.1;
        Double u2 = 3.8;
        Double kkl2 = 190.19;

        ProductRequests productRequests = new ProductRequests("http://localhost:8080/", token);
        productId = productRequests.createProduct(product, price, b, j, u, kkl);
        if (!(productRequests.getResponse().getStatusCode() == 200)) {
            errors.append("Response status code invalid: exp - 200, got - " + productRequests.getResponse().getStatusCode() + "\n");
        }
        Long productId1 = productRequests.createProduct(product1, price1, b1, j1, u1, kkl1);
        if (!(productRequests.getResponse().getStatusCode() == 200)) {
            errors.append("Response1 status code invalid: exp - 200, got - " + productRequests.getResponse().getStatusCode() + "\n");
        }
        Long productId2 = productRequests.createProduct(product2, price2, b2, j2, u2, kkl2);
        if (!(productRequests.getResponse().getStatusCode() == 200)) {
            errors.append("Response2 status code invalid: exp - 200, got - " + productRequests.getResponse().getStatusCode() + "\n");
        }

        List<Map<String,Object>> list =  productRequests.getAllProducts();


        boolean found = false;
        for (Map<String, Object> m : list) {
            String rProd = m.get("name").toString();
            if (rProd.equals(product)) {
                found = true;
                Double rPrice = Double.valueOf(m.get("price").toString());
                Double rB = Double.valueOf(m.get("b").toString());
                Double rJ = Double.valueOf(m.get("j").toString());
                Double rU = Double.valueOf(m.get("u").toString());
                Double rKkl = Double.valueOf(m.get("kkl").toString());
                if (!(
                        (rPrice.equals(price))
                        && (rB.equals(b))
                        && (rJ.equals(j))
                        && (rU.equals(u))
                        && (rKkl.equals(kkl))
                )) {
                    errors.append("Unmatched products: " + m + " vs " + product);
                }
                break;
            }
        }
        if (!found) {
            errors.append("Not found product: exp - " + product + " in list " + list);
        }

        found = false;
        for (Map<String, Object> m : list) {
            String rProd = m.get("name").toString();
            if (rProd.equals(product1)) {
                found = true;
                Double rPrice = Double.valueOf(m.get("price").toString());
                Double rB = Double.valueOf(m.get("b").toString());
                Double rJ = Double.valueOf(m.get("j").toString());
                Double rU = Double.valueOf(m.get("u").toString());
                Double rKkl = Double.valueOf(m.get("kkl").toString());
                if (!(
                        (rPrice.equals(price1))
                                && (rB.equals(b1))
                                && (rJ.equals(j1))
                                && (rU.equals(u1))
                                && (rKkl.equals(kkl1))
                )) {
                    errors.append("Unmatched products: " + m + " vs " + product1);
                }
                break;
            }
        }
        if (!found) {
            errors.append("Not found product: exp - " + product1 + " in list " + list);
        }

        found = false;
        for (Map<String, Object> m : list) {
            String rProd = m.get("name").toString();
            if (rProd.equals(product2)) {
                found = true;
                Double rPrice = Double.valueOf(m.get("price").toString());
                Double rB = Double.valueOf(m.get("b").toString());
                Double rJ = Double.valueOf(m.get("j").toString());
                Double rU = Double.valueOf(m.get("u").toString());
                Double rKkl = Double.valueOf(m.get("kkl").toString());
                if (!(
                        (rPrice.equals(price2))
                                && (rB.equals(b2))
                                && (rJ.equals(j2))
                                && (rU.equals(u2))
                                && (rKkl.equals(kkl2))
                )) {
                    errors.append("Unmatched products: " + m + " vs " + product2);
                }
                break;
            }
        }
        if (!found) {
            errors.append("Not found product: exp - " + product2 + " in list " + list);
        }


        // check DB data
        // TODO maybe some when

        productRepo.delete(productRepo.findById(productId).get());
        productRepo.delete(productRepo.findById(productId1).get());
        productRepo.delete(productRepo.findById(productId2).get());

        after();
        System.out.println();
    }
}
