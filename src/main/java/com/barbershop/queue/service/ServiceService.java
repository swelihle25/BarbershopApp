package com.barbershop.queue.service;
import com.barbershop.queue.entity.Service;
import com.barbershop.queue.repository.ServiceRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
@Slf4j
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Cacheable(value = "services", key = "'all'")
    public List<Service> getAllServices() {
        log.info("Fetching all services from database");
        return serviceRepository.findAll();
    }

    @Cacheable(value = "services", key = "#id")
    public Optional<Service> getServiceById(Long id){
        log.info("Fetching service with id: {}", id);
        return serviceRepository.findById(id);
    }

    @CacheEvict(value = "services", allEntries = true)
    @Transactional
    public Service saveService(Service service) {
        log.info("Saving new service and clearing cache");
        return serviceRepository.save(service);
    }


    @CacheEvict(value = "services", allEntries = true)
    @Transactional
    public Service updateService(Long id, Service serviceDetails) {
        log.info("Updating service with id: {} and clearing cache", id);
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));

        service.setName(serviceDetails.getName());
        service.setDescription(serviceDetails.getDescription());
        service.setPrice(serviceDetails.getPrice());
        service.setEstimatedDurationMinutes(serviceDetails.getEstimatedDurationMinutes());

        return serviceRepository.save(service);
    }

    @CacheEvict(value = "services", allEntries = true)
    @Transactional
    public void deleteService(Long id) {
        log.info("Deleting service with id: {} and clearing cache", id);
        serviceRepository.deleteById(id);
    }

    @CacheEvict(value = "services", allEntries = true)
    public void clearServiceCache() {
        log.info("Manually clearing all service cache");
    }
}


