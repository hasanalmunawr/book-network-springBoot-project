package com.hasanalmunawr.booknetwork.repository;

import com.hasanalmunawr.booknetwork.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Spliterator;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {


    Optional<RoleEntity> findByName(String name);
}
