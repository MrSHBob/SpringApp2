package com.app.springapp2.service;

import com.app.springapp2.model.Product;
import com.app.springapp2.model.User;
import com.app.springapp2.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepo repo;

    public Product create(
            String name,
            Double price,
            Double b,
            Double j,
            Double u,
            Double kkl
    ) {
        Product p = new Product();
        // TODO check name availability
        p.setName(name);
        p.setPrice(price);
        p.setCreated(LocalDateTime.now());
        p.setLastUpdateDate(LocalDateTime.now());
        p.setVersion(0);
        p.setB(b);
        p.setJ(j);
        p.setU(u);
        p.setKkl(kkl);

        return repo.save(p);
    }

    public Product update(
            Long id,
            String name,
            Double price,
            Double b,
            Double j,
            Double u,
            Double kkl
    ) {
        Product product = repo.findById(id).get();
        // TODO check name availability
        product.setName(name);
        product.setPrice(price);
        product.setB(b);
        product.setJ(j);
        product.setU(u);
        product.setKkl(kkl);
        product.setLastUpdateDate(LocalDateTime.now());
        // TODO use optimistic transaction with version field
        product.setVersion(0);

        return repo.save(product);
    }

    public boolean delete(Product product) {
        try {
            repo.delete(product);
            return true;
        } catch (IllegalArgumentException iae) {
            return false;
        }
    }

    public List<Product> getAllProducts() {
        List<Product> result = new ArrayList<Product>();
        repo.findAll().forEach(result::add);
        return result;
    }
}
