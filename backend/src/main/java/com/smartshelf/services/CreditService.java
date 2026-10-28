package com.smartshelf.services;

import com.smartshelf.dto.CreditTransactionResponse;
import com.smartshelf.dto.RecordCreditPaymentRequest;
import com.smartshelf.exceptions.BadRequestException;
import com.smartshelf.exceptions.ResourceNotFoundException;
import com.smartshelf.models.*;
import com.smartshelf.repositories.CreditTransactionRepository;
import com.smartshelf.repositories.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreditService {

    private final CreditTransactionRepository creditTransactionRepository;
    private final CustomerRepository customerRepository;

    public CreditService(CreditTransactionRepository creditTransactionRepository, CustomerRepository customerRepository) {
        this.creditTransactionRepository = creditTransactionRepository;
        this.customerRepository = customerRepository;
    }

    public List<CreditTransactionResponse> getAllCreditTransactions() {
        return creditTransactionRepository.findRecentTransactions().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CreditTransactionResponse> getTransactionsByCustomer(Long customerId) {
        return creditTransactionRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CreditTransactionResponse recordPayment(RecordCreditPaymentRequest request, String performedBy) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        BigDecimal currentCredit = customer.getCreditBalance() != null ? customer.getCreditBalance() : BigDecimal.ZERO;
        if (currentCredit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Customer has no outstanding credit balance");
        }

        BigDecimal newBalance = currentCredit.subtract(request.getAmount());
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            newBalance = BigDecimal.ZERO;
        }

        customer.setCreditBalance(newBalance);
        customerRepository.save(customer);

        CreditTransaction tx = CreditTransaction.builder()
                .customer(customer)
                .type(CreditType.PAYMENT_RECEIVED)
                .amount(request.getAmount())
                .balanceAfter(newBalance)
                .paymentMethod(request.getPaymentMethod())
                .notes(request.getNotes() != null ? request.getNotes() : "Udhar repayment received")
                .recordedBy(performedBy != null ? performedBy : "ADMIN")
                .build();

        tx = creditTransactionRepository.save(tx);
        return mapToResponse(tx);
    }

    public CreditTransactionResponse mapToResponse(CreditTransaction tx) {
        return CreditTransactionResponse.builder()
                .id(tx.getId())
                .customerId(tx.getCustomer().getId())
                .customerName(tx.getCustomer().getName())
                .customerPhone(tx.getCustomer().getPhone())
                .orderId(tx.getOrder() != null ? tx.getOrder().getId() : null)
                .orderNumber(tx.getOrder() != null ? tx.getOrder().getOrderNumber() : null)
                .type(tx.getType())
                .amount(tx.getAmount())
                .balanceAfter(tx.getBalanceAfter())
                .paymentMethod(tx.getPaymentMethod())
                .notes(tx.getNotes())
                .recordedBy(tx.getRecordedBy())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}
