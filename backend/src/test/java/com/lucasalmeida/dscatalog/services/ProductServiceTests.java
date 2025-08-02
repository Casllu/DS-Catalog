package com.lucasalmeida.dscatalog.services;

import com.lucasalmeida.dscatalog.dto.ProductDTO;
import com.lucasalmeida.dscatalog.entities.Category;
import com.lucasalmeida.dscatalog.entities.Product;
import com.lucasalmeida.dscatalog.projections.ProductProjection;
import com.lucasalmeida.dscatalog.repository.CategoryRepository;
import com.lucasalmeida.dscatalog.repository.ProductRepository;
import com.lucasalmeida.dscatalog.servicies.ProductService;
import com.lucasalmeida.dscatalog.servicies.exceptions.DatabaseException;
import com.lucasalmeida.dscatalog.servicies.exceptions.ResourceNotFoundException;
import com.lucasalmeida.dscatalog.testes.Factory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;
    @Mock
    private ProductRepository repository;
    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private PageImpl<ProductProjection> page;
    private Product product;
    private ProductProjection productProjection;
    ProductDTO productDTO;
    private Category category;

    @BeforeEach
    public void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 4L;
        product = Factory.createProduct();
        productProjection = Factory.createProductProjection();
        page = new PageImpl<>(List.of(productProjection));
        category = Factory.createCategory();
        productDTO = Factory.createProductDTO();

        Mockito.when(repository.searchProducts(eq(List.of(existingId)), eq("0"),any(Pageable.class))).thenReturn(page);


        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        Mockito.when(categoryRepository.getReferenceById(existingId)).thenReturn(category);
        Mockito.when(categoryRepository.getReferenceById(nonExistingId)).thenThrow(ResourceNotFoundException.class);


        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(DatabaseException.class).when(repository).deleteById(dependentId);

        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);

    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
         Assertions.assertThrows(ResourceNotFoundException.class, () -> {
             service.update(nonExistingId, productDTO);
         });
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() {
        ProductDTO result = service.update(existingId, productDTO);
        Assertions.assertNotNull(result);

    }

    @Test
    public void findyByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });

    }

    @Test
    public void findyByIdShouldReturnProductDTOWhenIdExists() {
        ProductDTO result = service.findById(existingId);
        Assertions.assertNotNull(result);

    }

    @Test
    public void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductDTO> result = service.findAllPaged("", "0", pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {

        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

    }
}
