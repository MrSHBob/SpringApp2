package com.app.springapp2.repo;

import com.app.springapp2.model.Position;
import com.app.springapp2.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

public interface PositionRepo extends CrudRepository<Position, Long>, PagingAndSortingRepository<Position, Long> {
    List<Position> findAllByDateAndOwner(LocalDate date, User owner);
}
