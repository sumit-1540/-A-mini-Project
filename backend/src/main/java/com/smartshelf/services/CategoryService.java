package com.smartshelf.services;

import com.smartshelf.dto.CategoryDTO;
import com.smartshelf.exceptions.DuplicateResourceException;
import com.smartshelf.exceptions.ResourceNotFoundException;
import com.smartshelf.models.Category;
import com.smartshelf.repositories.CategoryRepository;
import com.smartshelf.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CategoryDTO> getActiveCategories() {
        return categoryRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return mapToDTO(category);
    }

    @Transactional
    public CategoryDTO createCategory(CategoryDTO request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new DuplicateResourceException("Category '" + request.getName() + "' already exists");
        }

        Category category = Category.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .icon(request.getIcon() != null ? request.getIcon() : "package")
                .active(true)
                .build();

        category = categoryRepository.save(category);
        return mapToDTO(category);
    }

    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!category.getName().equalsIgnoreCase(request.getName().trim()) &&
                categoryRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new DuplicateResourceException("Category '" + request.getName() + "' already exists");
        }

        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        if (request.getIcon() != null) {
            category.setIcon(request.getIcon());
        }
        category.setActive(request.isActive());

        category = categoryRepository.save(category);
        return mapToDTO(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        
        long count = productRepository.findByCategoryIdAndActiveTrue(id).size();
        if (count > 0) {
            category.setActive(false);
            categoryRepository.save(category);
        } else {
            categoryRepository.delete(category);
        }
    }

    public CategoryDTO mapToDTO(Category category) {
        long productCount = productRepository.findByCategoryIdAndActiveTrue(category.getId()).size();
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .icon(category.getIcon())
                .active(category.isActive())
                .productCount(productCount)
                .createdAt(category.getCreatedAt())
                .build();
    }
}
