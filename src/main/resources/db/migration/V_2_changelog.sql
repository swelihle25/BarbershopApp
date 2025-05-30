INSERT INTO staff (id, employee_id, is_active, name, role )
VALUES ('1', '1', true, 'Swelihle', 'BARBER');

INSERT INTO staff (id, employee_id, is_active, name, role )
VALUES ('2', '2', true, 'Khaya', 'MANAGER');

INSERT INTO staff (id, employee_id, is_active, name, role )
VALUES ('3', '3', true, 'Kwanele', 'RECEPTIONIST');

INSERT INTO customers (id, created_at, email, name, phone_number)
VALUES ('1', '2025-05-29 13:28:25.848823', 'john@gmail.com', 'John Smith', '0821234567');

INSERT INTO customers (id, created_at, email, name, phone_number)
VALUES ('2', '2025-05-29 13:30:59.975771', 'jane@example.com', 'Jane White', '0827654321');

INSERT INTO services (id, description, estimated_duration_minutes, name, price)
VALUES ('1', 'Hair cut', '30', 'Fade', '50');

INSERT INTO services (id, description, estimated_duration_minutes, name, price)
VALUES ('2', 'Brush cut', '35', 'Brush cut', '30');

INSERT INTO queue_entries (id, estimated_wait_time, joined_at, queue_position, service_completed_at, service_started_at, shop_location, status, assigned_staff_id, customer_id, service_id)
VALUES ('1', '10', '2025-06-01 14:30:00', '5', '2025-06-01 15:00:00', '2025-06-01 14:30:00', 'Ferndale', 'COMPLETED', '2', '1', '1');

INSERT INTO queue_entries (id, estimated_wait_time, joined_at, queue_position, service_completed_at, service_started_at, shop_location, status, assigned_staff_id, customer_id, service_id)
VALUES ('2', '60', '2025-05-30 00:22:23.969557', '1', '2025-05-30 03:59:15.241483', '2025-05-30 03:58:53.83464', 'Ferndale', 'COMPLETED', '1', '1', '1');

INSERT INTO queue_entries (id, estimated_wait_time, joined_at, queue_position, service_completed_at, service_started_at, shop_location, status, assigned_staff_id, customer_id, service_id)
VALUES ('3', '30', '2025-05-30 09:30:41.561119', '1', '2025-05-30 09:31:04.967542', '2025-05-30 09:30:56.813738', 'Montana', 'COMPLETED', '1', '1', '1');

INSERT INTO queue_entries (id, estimated_wait_time, joined_at, queue_position, service_completed_at, service_started_at, shop_location, status, assigned_staff_id, customer_id, service_id)
VALUES ('4', '40', '2025-05-30 11:15:19.150664', '1', '2025-05-30 11:16:00', '2025-05-30 11:15:21.503492', 'Montana', 'IN_PROGRESS', '1', '2', '2');

INSERT INTO queue_entries (id, estimated_wait_time, joined_at, queue_position, service_completed_at, service_started_at, shop_location, status, assigned_staff_id, customer_id, service_id)
VALUES ('5', '40', '2025-05-30 04:01:29.102366', '4', '2025-05-30 11:19:05.310205', '2025-05-30 11:18:56.90747', 'Ferndale', 'COMPLETED', '1', '1', '1');

