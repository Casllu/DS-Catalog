package com.lucasalmeida.dscatalog.servicies;

import com.lucasalmeida.dscatalog.entities.Category;
import com.lucasalmeida.dscatalog.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    public List<Category> findAll() {
        return repository.findAll();
    }

}
