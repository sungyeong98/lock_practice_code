package com.lock_practice_code.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lock_practice_code.domain.entity.Product;

import jakarta.persistence.LockModeType;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	@Lock(LockModeType.OPTIMISTIC)
	@Query("select p from Product p where p.id = :id")
	Optional<Product> findByIdWithOptimisticLock(@Param("id") Long id);

	@Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
	@Query("select p from Product p where p.id = :id")
	Optional<Product> findByIdWithPLock(@Param("id") Long id);

}
