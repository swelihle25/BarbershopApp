package com.barbershop.queue.repository;

import com.barbershop.queue.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByPhoneNumber(String phoneNumber);

    Optional<Customer> findByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE c.phoneNumber = :phoneNumber OR c.email = :email")
    Optional<Customer> findByPhoneNumberOrEmail(@Param("phoneNumber") String phoneNumber,
                                                @Param("email") String email);
}

