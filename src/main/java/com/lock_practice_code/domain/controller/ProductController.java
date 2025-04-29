package com.lock_practice_code.domain.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lock_practice_code.domain.service.OptimisticProductService;
import com.lock_practice_code.domain.service.PessimisticProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
	private final OptimisticProductService optimisticProductService;
	private final PessimisticProductService pessimisticProductService;

	@GetMapping("/optimistic")
	public void decreaseStockWithOptimisticLock(Long productId) {
		optimisticProductService.decreaseStock(productId);
	}

	@GetMapping("/pessimistic")
	public void decreaseStockWithPessimisticLock(Long productId) {
		pessimisticProductService.decreaseStock(productId);
	}
}
