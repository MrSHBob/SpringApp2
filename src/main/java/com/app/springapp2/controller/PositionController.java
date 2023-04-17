package com.app.springapp2.controller;

import com.app.springapp2.model.Position;
import com.app.springapp2.model.PositionRequest;
import com.app.springapp2.model.Product;
import com.app.springapp2.model.ProductRequest;
import com.app.springapp2.repo.UserRepo;
import com.app.springapp2.service.PositionService;
import com.app.springapp2.service.ProductService;
import com.app.springapp2.utilities.JsonSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/position")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;
    private final UserRepo userRepo;

    @PostMapping("/add")
    public ResponseEntity<String> addPosition(
            @RequestBody PositionRequest req
    ) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        Long owner = userRepo.findByUsername(username).getId();

        Position position = null;
        try {
            position = positionService.create(
                    owner, req.getProduct(), req.getDate(), req.getMass()
            );
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.toString());
        }

        if ((position != null) && (position.getId() > 0)) {
            position.getOwner().setPassword(null);
            String jsonResponse = JsonSerializer.gson().toJson(position);
            return ResponseEntity.ok(jsonResponse);
        }
        return ResponseEntity.status(400).body("Product creation failed");
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deletePosition(
            @RequestBody PositionRequest req
    ) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        Long owner = userRepo.findByUsername(username).getId();
        boolean isDeleted = false;
        try {
            isDeleted = positionService.delete(req.getId(), owner);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.toString());
        }

        if (isDeleted) {
            return ResponseEntity.ok("Position deleted");
        }
        return ResponseEntity.status(400).body("Position deletion failed");
    }

    @PostMapping("/update")
    public ResponseEntity<String> updatePosition(
            @RequestBody PositionRequest req
    ) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        Long owner = userRepo.findByUsername(username).getId();
        Position position = null;
        try {
            position = positionService.update(
                    owner, req.getId(), req.getProduct(), req.getDate(), req.getMass()
            );
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.toString());
        }

        if ((position != null) && (position.getId() > 0)) {
            position.getOwner().setPassword(null);
            String jsonResponse = JsonSerializer.gson().toJson(position);
            return ResponseEntity.ok(jsonResponse);
        }
        return ResponseEntity.status(400).body("Product creation failed");
    }

    // TODO get all positions for user/date POST
    @GetMapping("/list")
    public ResponseEntity<String> getListOfPositions() {
        List<Position> positions = positionService.getAllPositions();
        return ResponseEntity.ok(positions.toString());
    }

    // TODO update position (by position row_id) POST
    @PostMapping("/dailylist")
    public ResponseEntity<String> getAllPositionForUserAndDate(
            @RequestBody PositionRequest req
    ) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        Long owner = userRepo.findByUsername(username).getId();
        List <Position> positions = null;
        try {
            positions = positionService.getAllByDateAndOwner(owner, req.getDate());
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.toString());
        }

        if ((positions != null) && (!positions.isEmpty())) {
            positions.forEach(position -> {
                position.getOwner().setPassword(null);
            });

            String jsonResponse = JsonSerializer.gson().toJson(positions);
            return ResponseEntity.ok(jsonResponse);
        }
        return ResponseEntity.status(400).body("Product creation failed");
    }
}
