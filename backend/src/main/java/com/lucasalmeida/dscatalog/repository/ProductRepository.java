package com.lucasalmeida.dscatalog.repository;

import com.lucasalmeida.dscatalog.entities.Product;
import com.lucasalmeida.dscatalog.projections.ProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(nativeQuery = true, value = """
	SELECT * FROM (SELECT DISTINCT p.id, p.name
	FROM tb_product as p
	INNER JOIN tb_product_category as pc ON pc.product_id = p.id
	WHERE (:categoryIds IS NULL OR pc.category_id IN (:categoryIds))
	AND (LOWER(p.name) LIKE LOWER(CONCAT('%',:name,'%')))) AS tb_result
	""",
            countQuery = """
	SELECT COUNT(*) FROM (
	SELECT DISTINCT p.id, p.name
	FROM tb_product as p
	INNER JOIN tb_product_category as pc ON pc.product_id = p.id
	WHERE (:categoryIds IS NULL OR pc.category_id IN (:categoryIds))
	AND (LOWER(p.name) LIKE LOWER(CONCAT('%',:name,'%')))
	) AS tb_result
	""")
    Page<ProductProjection> searchProducts(List<Long> categoryIds, String name, Pageable pageable);

	@Query("SELECT obj FROM Product obj " +
			"JOIN FETCH obj.categories " +
			"where obj.id IN :productIds ")
	List<Product> searchProductsWithCategories(List<Long> productIds);
}
