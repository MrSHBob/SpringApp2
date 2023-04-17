package com.app.springapp2.controller;

import com.app.springapp2.model.Product;
import com.app.springapp2.model.ProductRequest;
import com.app.springapp2.model.User;
import com.app.springapp2.service.ProductService;
import com.app.springapp2.service.UserService;
import com.app.springapp2.utilities.JsonSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // TODO add product POST
    @PostMapping("/add")
    public ResponseEntity<String> addProduct(
            @RequestBody ProductRequest req
    ) {
        Product product = null;
        try {
            product = productService.create(
                    req.getName(), req.getPrice(), req.getB(), req.getJ(), req.getU(), req.getKkl());
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.toString());
        }

        if ((product != null) && (product.getId() > 0)) {
            String jsonResponse = JsonSerializer.gson().toJson(product);
            return ResponseEntity.ok(jsonResponse);
        }
        return ResponseEntity.status(400).body("Product creation failed");
    }

    // TODO update product POST
    @PostMapping("/update")
    public ResponseEntity<String> updateProduct(
            @RequestBody ProductRequest req
    ) {
        Product product = null;
        try {
            product = productService.update(
                    req.getId(), req.getName(), req.getPrice(), req.getB(), req.getJ(), req.getU(), req.getKkl());
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.toString());
        }

        if ((product != null) && (product.getId() > 0)) {
            String jsonResponse = JsonSerializer.gson().toJson(product);
            return ResponseEntity.ok(jsonResponse);
        }
        return ResponseEntity.status(400).body("Product creation failed");
    }
    // TODO get all products GET (maybe with limit)
    @GetMapping("/list")
    public ResponseEntity<String> getListOfProducts() {
        List<Product> products = productService.getAllProducts();
        String jsonResponse = JsonSerializer.gson().toJson(products);
        return ResponseEntity.ok(jsonResponse);
    }

    // TODO delete product (change status to archive) POST
}
