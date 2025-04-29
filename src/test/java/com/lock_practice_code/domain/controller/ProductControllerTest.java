package com.lock_practice_code.domain.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.lock_practice_code.domain.entity.Product;
import com.lock_practice_code.domain.repository.ProductRepository;
import com.lock_practice_code.domain.service.OptimisticProductService;
import com.lock_practice_code.domain.service.PessimisticProductService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
//@Transactional
class ProductControllerTest {
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private OptimisticProductService optimisticProductService;
	@Autowired
	private PessimisticProductService pessimisticProductService;

	private Long productId;

	@BeforeEach
	void setUp() {
		productRepository.deleteAll();

		Product product = Product.builder()
			.name("테스트 상품 1")
			.stock(100000L)
			.build();

		productRepository.save(product);
		productId = product.getId();
	}

	@Test
	@DisplayName("재고 감소 테스트 - 낙관적 락")
	void test1() throws InterruptedException {
		int threadCount = 100000;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					optimisticProductService.decreaseStock(productId);
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();

		Product product = productRepository.findById(productId).orElseThrow(
			() -> new IllegalArgumentException("테스트용 상품 검색에서 문제 발생")
		);
		assertThat(product.getStock()).isZero();
		log.debug("잔여 재고 수량 : {}", product.getStock());
	}

	@Test
	@DisplayName("재고 감소 테스트 - 비관적 락")
	void test2() throws InterruptedException {
		int threadCount = 100000;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					pessimisticProductService.decreaseStock(productId);
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();

		Product product = productRepository.findById(productId).orElseThrow(
			() -> new IllegalArgumentException("테스트용 상품 검색에서 문제 발생")
		);
		assertThat(product.getStock()).isZero();
		log.debug("잔여 재고 수량 : {}", product.getStock());
	}
}
