package com.aiplus.backend.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aiplus.backend.payment.model.Payment;
import com.aiplus.backend.users.model.ClientAccount;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findByOrderId(String orderId);

    Optional<Payment> findByUser(ClientAccount user);

}