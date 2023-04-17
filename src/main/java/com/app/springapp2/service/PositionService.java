package com.app.springapp2.service;

import com.app.springapp2.model.Position;
import com.app.springapp2.model.Product;
import com.app.springapp2.model.User;
import com.app.springapp2.repo.PositionRepo;
import com.app.springapp2.repo.ProductRepo;
import com.app.springapp2.repo.UserRepo;
import jakarta.persistence.ManyToOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PositionService {

    @Autowired
    private PositionRepo repo;
//    @Autowired
//    private PositionRepo2 repo2;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ProductRepo productRepo;

    public Position create(
            Long owner,
            Long product,
            LocalDate date,
            Double mass
    ) {
        Position position = new Position();
        position.setOwner(userRepo.findById(owner).get());
        Product pr = null;
        try {
            pr = productRepo.findById(product).get();
        } catch (NoSuchElementException nsee) {
            throw new RuntimeException("Product with ID = " + product + " not found.");
        }
        position.setProduct(pr);
        position.setDate(date);
        position.setMass(mass);
        position.setSum(mass * pr.getPrice());
        position.setCalcB(mass * pr.getB());
        position.setCalcJ(mass * pr.getJ());
        position.setCalcU(mass * pr.getU());
        position.setTotalenergy(mass * pr.getKkl());

        position.setCreated(LocalDateTime.now());
        position.setLastUpdateDate(LocalDateTime.now());
        position.setVersion(0);

        return repo.save(position);
    }

    public Position update(
            Long owner,
            Long id,
            Long product,
            LocalDate date,
            Double mass
    ) {

        Position position = repo.findById(id).get();
        Product pr = null;
        if (position.getOwner().getId() != owner) {
            throw new RuntimeException("Only owned position update allowed.");
        }
        try {
            pr = productRepo.findById(product).get();
        } catch (NoSuchElementException nsee) {
            throw new RuntimeException("Product with ID = " + product + " not found.");
        }
        if (pr.getId() != position.getProduct().getId()) {
            position.setProduct(pr);
        }

        position.setDate(date);
        position.setMass(mass);
        position.setSum(mass * pr.getPrice());
        position.setCalcB(mass * pr.getB());
        position.setCalcJ(mass * pr.getJ());
        position.setCalcU(mass * pr.getU());
        position.setTotalenergy(mass * pr.getKkl());

        position.setLastUpdateDate(LocalDateTime.now());
        // TODO use optimistic transaction with version field
        position.setVersion(0);

        return repo.save(position);
    }

    public boolean delete(Long posId, Long ownerId) {
        try {
            Position pos = repo.findById(posId).get();
            if (!(pos.getOwner().getId().equals(ownerId))) {
                throw new RuntimeException("Wrong owner, not enough rights");
            }
            repo.delete(pos);
            return true;
        } catch (IllegalArgumentException iae) {
            return false;
        }
    }

    public List<Position> getAllPositions() {
        List<Position> result = new ArrayList<Position>();
        repo.findAll().forEach(result::add);
        return result;
    }

    public List<Position> getAllByDateAndOwner(
            Long owner,
            LocalDate date
    ) {
        return repo.findAllByDateAndOwner(date, userRepo.findById(owner).get());
    }
}
