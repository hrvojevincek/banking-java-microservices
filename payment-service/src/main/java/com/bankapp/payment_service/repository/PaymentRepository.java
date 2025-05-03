package com.bankapp.payment_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bankapp.payment_service.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findBySourceFundingSourceUrl(String sourceFundingSourceUrl);

    List<Payment> findByDestinationFundingSourceUrl(String destinationFundingSourceUrl);

    Optional<Payment> findByStripePaymentId(String stripePaymentId);
}