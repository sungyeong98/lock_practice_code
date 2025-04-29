package com.lock_practice_code.domain.service;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lock_practice_code.domain.entity.Product;
import com.lock_practice_code.domain.repository.ProductRepository;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptimisticProductService implements ProductService{
	private final ProductRepository productRepository;

	@Override
	@Retryable(
		retryFor = { ObjectOptimisticLockingFailureException.class, OptimisticLockException.class},
		maxAttempts = 20,
		backoff = @Backoff(delay = 50, multiplier = 1.5)
	)
	@Transactional
	public void decreaseStock(Long id) {
		Product product = productRepository.findByIdWithOptimisticLock(id).orElseThrow(
			() -> new IllegalArgumentException("존재하지 않는 상품입니다.")
		);

		if (product.getStock() < 1) {
			throw new IllegalArgumentException("재고가 부족합니다.");
		}

		Product decreaseProduct = Product.decreaseStock(product);
		productRepository.save(decreaseProduct);
	}

	@Recover
	public void recoverFromOptimisticLockFailure(Exception e, Long id) {
		log.error("재고 감소 작업 중 실패 발생...\n상품 ID : {}", id, e);
		throw new RuntimeException("재고 감소 작업 중 실패 발생...", e);
	}
}
