package com.smartshelf.services;

import com.smartshelf.dto.SupplierDTO;
import com.smartshelf.exceptions.DuplicateResourceException;
import com.smartshelf.exceptions.ResourceNotFoundException;
import com.smartshelf.models.Supplier;
import com.smartshelf.repositories.ProductRepository;
import com.smartshelf.repositories.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    public SupplierService(SupplierRepository supplierRepository, ProductRepository productRepository) {
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
    }

    public List<SupplierDTO> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<SupplierDTO> getActiveSuppliers() {
        return supplierRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public SupplierDTO getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        return mapToDTO(supplier);
    }

    @Transactional
    public SupplierDTO createSupplier(SupplierDTO request) {
        if (supplierRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new DuplicateResourceException("Supplier '" + request.getName() + "' already exists");
        }

        Supplier supplier = Supplier.builder()
                .name(request.getName().trim())
                .company(request.getCompany())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .gstNumber(request.getGstNumber())
                .active(true)
                .build();

        supplier = supplierRepository.save(supplier);
        return mapToDTO(supplier);
    }

    @Transactional
    public SupplierDTO updateSupplier(Long id, SupplierDTO request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));

        supplier.setName(request.getName().trim());
        supplier.setCompany(request.getCompany());
        supplier.setPhone(request.getPhone());
        supplier.setEmail(request.getEmail());
        supplier.setAddress(request.getAddress());
        supplier.setGstNumber(request.getGstNumber());
        supplier.setActive(request.isActive());

        supplier = supplierRepository.save(supplier);
        return mapToDTO(supplier);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        supplier.setActive(false);
        supplierRepository.save(supplier);
    }

    public SupplierDTO mapToDTO(Supplier supplier) {
        long count = productRepository.findByActiveTrue().stream()
                .filter(p -> p.getSupplier() != null && p.getSupplier().getId().equals(supplier.getId()))
                .count();

        return SupplierDTO.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .company(supplier.getCompany())
                .phone(supplier.getPhone())
                .email(supplier.getEmail())
                .address(supplier.getAddress())
                .gstNumber(supplier.getGstNumber())
                .active(supplier.isActive())
                .suppliedProductCount(count)
                .createdAt(supplier.getCreatedAt())
                .build();
    }
}
