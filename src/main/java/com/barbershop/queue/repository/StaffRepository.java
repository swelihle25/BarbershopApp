package com.barbershop.queue.repository;


import com.barbershop.queue.entity.Staff;
import com.barbershop.queue.enums.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    List<Staff> findByIsActiveTrue();

    List<Staff> findByRoleAndIsActiveTrue(StaffRole role);

    Optional<Staff> findByEmployeeId(String employeeId);

    @Query("SELECT s FROM Staff s WHERE s.isActive = true AND s.role = 'BARBER'")
    List<Staff> findAvailableBarbers();
}