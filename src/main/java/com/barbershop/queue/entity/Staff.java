package com.barbershop.queue.entity;
import com.barbershop.queue.enums.StaffRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "staff")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "employee_id", unique = true, length = 20)
    private String employeeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StaffRole role;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}

