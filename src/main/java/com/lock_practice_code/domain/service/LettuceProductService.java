package com.lock_practice_code.domain.service;

import org.springframework.stereotype.Service;

import com.lock_practice_code.domain.entity.Product;
import com.lock_practice_code.domain.repository.ProductRepository;
import com.lock_practice_code.global.redis.RedisLockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LettuceProductService implements ProductService{
	private final ProductRepository productRepository;
	private final RedisLockRepository redisLockRepository;

	@Override
	public void decreaseStock(Long id) {
		while (!redisLockRepository.lock(id)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		try {
			Product product = productRepository.findById(id).orElseThrow(
				() -> new IllegalArgumentException("존재하지 않는 상품입니다.")
			);

			if (product.getStock() < 1) {
				throw new IllegalArgumentException("재고가 부족합니다.");
			}

			Product decreaseProduct = Product.decreaseStock(product);
			productRepository.save(decreaseProduct);
		} finally {
			redisLockRepository.unlock(id);
		}
	}
}
