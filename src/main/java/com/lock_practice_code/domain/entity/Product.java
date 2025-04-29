package com.lock_practice_code.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	@Min(0)
	private Long stock;

	// 낙관적 락에 사용될 데이터 버전
	@Version
	private Long version;

	public static Product decreaseStock(Product product) {
		return Product.builder()
			.id(product.getId())
			.name(product.getName())
			.stock(product.getStock() - 1)
			.version(product.getVersion())
			.build();
	}
}
