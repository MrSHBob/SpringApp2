package com.app.springapp2.repo;

import com.app.springapp2.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface ProductRepo extends CrudRepository<Product, Long> {

    Product findByName(String name);
}
