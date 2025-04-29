package com.lock_practice_code.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lock_practice_code.domain.entity.Product;
import com.lock_practice_code.domain.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PessimisticProductService implements ProductService {
	private final ProductRepository productRepository;

	@Transactional
	public void decreaseStock(Long id) {
		Product product = productRepository.findByIdWithPLock(id).orElseThrow(
			() -> new IllegalArgumentException("존재하지 않는 상품입니다.")
		);

		if (product.getStock() < 1) {
			throw new IllegalArgumentException("재고가 부족합니다.");
		}

		Product decreaseProduct = Product.decreaseStock(product);
		productRepository.save(decreaseProduct);
	}
}
