CREATE INDEX IF NOT EXISTS location_position_joined_date_index
ON queue_entries
USING btree (shop_location, queue_position, joined_at);

CREATE INDEX IF NOT EXISTS assigned_staff_status_index
ON queue_entries
USING btree (assigned_staff_id, status);

CREATE INDEX IF NOT EXISTS estimated_duration_index
ON services
USING btree (estimated_duration_minutes, price);

CREATE INDEX IF NOT EXISTS payments_method_paid_at_index
ON payments
USING btree (method, paid_at);


