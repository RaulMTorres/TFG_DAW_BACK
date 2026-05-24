package com.LoQueHay.project.service;

import com.LoQueHay.project.dto.category_dtos.CategoryRequestDTO;
import com.LoQueHay.project.exception.BadRequestException;
import com.LoQueHay.project.exception.ResourceNotFoundException;
import com.LoQueHay.project.model.Category;
import com.LoQueHay.project.model.MyUserEntity;
import com.LoQueHay.project.repository.CategoryRepository;
import com.LoQueHay.project.repository.ProductRepository;
import com.LoQueHay.project.util.AuthUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final AuthUtils authUtils;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository, AuthUtils authUtils) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.authUtils = authUtils;
    }

    public Page<Category> getCategories(int page, int size, String search) {
        MyUserEntity currentUser = authUtils.getCurrentUser();
        Long ownerId = currentUser.getOwner() != null ? currentUser.getOwner().getId() : currentUser.getId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        if (search == null || search.isEmpty()) {
            return categoryRepository.findByOwnerId(ownerId, pageable);
        } else {
            return categoryRepository.findByOwnerIdAndNameContainingIgnoreCase(ownerId, search, pageable);
        }
    }

    public Category getById(Long id){
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    @Transactional
    public Category create(CategoryRequestDTO dto) {
        MyUserEntity currentUser = authUtils.getCurrentUser();

        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setOwner(currentUser.getOwner() != null ? currentUser.getOwner() : currentUser);
        category.setCreatedBy(currentUser);

        return categoryRepository.save(category);
    }

    public List<Category> getAll() {
        MyUserEntity currentUser = authUtils.getCurrentUser();
        Long ownerId = currentUser.getOwner() != null
                ? currentUser.getOwner().getId()
                : currentUser.getId();

        return categoryRepository.findByOwnerId(ownerId);
    }
    @Transactional
    public Category update(Long id, CategoryRequestDTO dto) {
        Category existing = this.getById(id);

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());

        return categoryRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Category existing = this.getById(id);
        categoryRepository.delete(existing);
    }

    @Transactional
    public void deleteMultiple(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;

        MyUserEntity currentUser = authUtils.getCurrentUser();
        Long ownerId = currentUser.getOwner() != null ? currentUser.getOwner().getId() : currentUser.getId();

        List<Long> deletableIds = new ArrayList<>();

        for (Long id : ids) {
            if (id == null) continue;

            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));

            // owner check
            if (category.getOwner() == null || !ownerId.equals(category.getOwner().getId())) {
                throw new ResourceNotFoundException("Category not found: " + id);
            }

            // products check (más eficiente con exists)
            boolean hasProducts = productRepository.existsByOwnerIdAndCategoryId(ownerId, id);
            if (!hasProducts) {
                deletableIds.add(id);
            }else{
                throw new BadRequestException("The category: "+category.getName()+" cannot be removed because there are products associated with it. ");
            }
        }

        if (!deletableIds.isEmpty()) {
            categoryRepository.deleteAllById(deletableIds);
        }
    }




}
