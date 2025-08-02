package com.lucasalmeida.dscatalog.servicies;

import com.lucasalmeida.dscatalog.dto.CategoryDTO;
import com.lucasalmeida.dscatalog.dto.ProductDTO;
import com.lucasalmeida.dscatalog.entities.Category;
import com.lucasalmeida.dscatalog.entities.Product;
import com.lucasalmeida.dscatalog.projections.ProductProjection;
import com.lucasalmeida.dscatalog.repository.CategoryRepository;
import com.lucasalmeida.dscatalog.repository.ProductRepository;
import com.lucasalmeida.dscatalog.repository.ProductRepository;
import com.lucasalmeida.dscatalog.servicies.exceptions.DatabaseException;
import com.lucasalmeida.dscatalog.servicies.exceptions.ResourceNotFoundException;
import com.lucasalmeida.dscatalog.util.Utils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(String name, String categoryId, Pageable pageRequest) {

        List<Long> categoryIds = List.of();
        if(!"0".equals(categoryId)) {
            categoryIds = Arrays.stream(categoryId.split(",")).map(Long :: parseLong).toList();
        }

        Page<ProductProjection> page = productRepository.searchProducts(categoryIds, name.trim(), pageRequest);
        List<Long> productIds = page.map(ProductProjection :: getId).toList();

        List<Product> entities = productRepository.searchProductsWithCategories(productIds);

        //noinspection unchecked
        entities = (List<Product>) Utils.replace(page.getContent(), entities);
        List<ProductDTO> dtos = entities.stream().map(p -> new ProductDTO(p, p.getCategories())).toList();

        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Optional<Product> obj = productRepository.findById(id);
        Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));

        return new ProductDTO(entity,entity.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product entity = new Product();
        copyDtoToEntity(dto, entity);
//        entity.setName(dto.getName());

        entity = productRepository.save(entity);

        return new ProductDTO(entity);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product entity = productRepository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = productRepository.save(entity);

            return new ProductDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {

        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try {
            productRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    private void copyDtoToEntity(ProductDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setDate(dto.getDate());
        entity.setImgUrl(dto.getImgUrl());
        entity.setPrice(dto.getPrice());

        entity.getCategories().clear();
        for (CategoryDTO catDto : dto.getCategories()) {
            Category category = categoryRepository.getReferenceById(catDto.getId());
            entity.getCategories().add(category);
        }
    }
}
