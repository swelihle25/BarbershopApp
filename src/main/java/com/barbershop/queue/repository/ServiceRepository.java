package com.barbershop.queue.repository;

import com.barbershop.queue.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByOrderByPriceAsc();

    @Query("SELECT s FROM Service s WHERE s.estimatedDurationMinutes <= :maxDuration ORDER BY s.price")
    List<Service> findQuickServices(@Param("maxDuration") Integer maxDuration);
}


