package com.barbershop.queue.service;

import com.barbershop.queue.entity.Staff;
import com.barbershop.queue.repository.StaffRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
@Slf4j
//@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Cacheable(value = "staff", key = "'all'")
    public List<Staff> getAllStaff() {
        log.info("Fetching all staff from database");
        return staffRepository.findAll();
    }

    @Cacheable(value = "staff", key = "#id")
    public Optional<Staff> getStaffById(Long id) {
        log.info("Fetching staff with id: {}", id);
        return staffRepository.findById(id);
    }

    @Cacheable(value = "staff", key = "'active'")
    public Staff saveStaff(Staff staff) {
        log.info("Fetching active staff from database");
        return staffRepository.save(staff);
    }

    @CacheEvict(value = "staff", allEntries = true)
    @Transactional
    public Staff updateStaff(Long id, Staff staffDetails) {
        log.info("Updating staff with id: {} and clearing cache", id);
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + id));

        staff.setName(staffDetails.getName());
        staff.setRole(staffDetails.getRole());
//        staff.setPhoneNumber(staffDetails.getPhoneNumber());

        return staffRepository.save(staff);
    }

    @CacheEvict(value = "staff", allEntries = true)
    @Transactional
    public void deleteStaff(Long id) {
        log.info("Deleting staff with id: {} and clearing cache", id);
        staffRepository.deleteById(id);
    }
}
