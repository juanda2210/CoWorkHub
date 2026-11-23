-- SEDES
INSERT INTO locations (id, name, city, address, opening_time, closing_time, created_at, updated_at) VALUES (1, 'Sede Centro', 'Bucaramanga', 'Calle 35 #19-41', '08:00:00', '20:00:00', NOW(), NOW());
INSERT INTO locations (id, name, city, address, opening_time, closing_time, created_at, updated_at) VALUES (2, 'Sede Norte', 'Bogotá', 'Carrera 15 #93-60', '07:00:00', '22:00:00', NOW(), NOW());

-- EQUIPAMIENTO
INSERT INTO equipment (id, name, quantity, created_at, updated_at) VALUES (1, 'Proyector', 5, NOW(), NOW());
INSERT INTO equipment (id, name, quantity, created_at, updated_at) VALUES (2, 'Pizarra', 8, NOW(), NOW());
INSERT INTO equipment (id, name, quantity, created_at, updated_at) VALUES (3, 'TV 55 pulgadas', 4, NOW(), NOW());
INSERT INTO equipment (id, name, quantity, created_at, updated_at) VALUES (4, 'Videoconferencia', 3, NOW(), NOW());

-- PLANES
INSERT INTO membership_plans (id, name, included_hours_per_month, overage_discount, max_active_bookings, created_at, updated_at) VALUES (1, 'Básico', 5, 10.00, 2, NOW(), NOW());
INSERT INTO membership_plans (id, name, included_hours_per_month, overage_discount, max_active_bookings, created_at, updated_at) VALUES (2, 'Profesional', 20, 20.00, 5, NOW(), NOW());
INSERT INTO membership_plans (id, name, included_hours_per_month, overage_discount, max_active_bookings, created_at, updated_at) VALUES (3, 'Premium', 50, 30.00, 10, NOW(), NOW());

-- SERVICIOS ADICIONALES
INSERT INTO additional_services (id, name, unit_price, created_at, updated_at) VALUES (1, 'Café y refrigerios', 12000.00, NOW(), NOW());
INSERT INTO additional_services (id, name, unit_price, created_at, updated_at) VALUES (2, 'Impresiones', 5000.00, NOW(), NOW());
INSERT INTO additional_services (id, name, unit_price, created_at, updated_at) VALUES (3, 'Soporte técnico', 25000.00, NOW(), NOW());

-- SALAS (la 6 está inactiva)
INSERT INTO rooms (id, name, type, capacity, hourly_rate, active, location_id, created_at, updated_at) VALUES (1, 'Sala Andes', 'MEETING_ROOM', 10, 40000.00, true, 1, NOW(), NOW());
INSERT INTO rooms (id, name, type, capacity, hourly_rate, active, location_id, created_at, updated_at) VALUES (2, 'Sala Caribe', 'MEETING_ROOM', 6, 30000.00, true, 1, NOW(), NOW());
INSERT INTO rooms (id, name, type, capacity, hourly_rate, active, location_id, created_at, updated_at) VALUES (3, 'Oficina Privada 101', 'PRIVATE_OFFICE', 4, 50000.00, true, 1, NOW(), NOW());
INSERT INTO rooms (id, name, type, capacity, hourly_rate, active, location_id, created_at, updated_at) VALUES (4, 'Sala Pacífico', 'MEETING_ROOM', 12, 45000.00, true, 2, NOW(), NOW());
INSERT INTO rooms (id, name, type, capacity, hourly_rate, active, location_id, created_at, updated_at) VALUES (5, 'Oficina Privada 201', 'PRIVATE_OFFICE', 3, 55000.00, true, 2, NOW(), NOW());
INSERT INTO rooms (id, name, type, capacity, hourly_rate, active, location_id, created_at, updated_at) VALUES (6, 'Escritorio Flex B', 'DESK', 1, 9000.00, false, 2, NOW(), NOW());

-- SALAS <-> EQUIPAMIENTO
INSERT INTO room_equipment (room_id, equipment_id) VALUES (1, 1);
INSERT INTO room_equipment (room_id, equipment_id) VALUES (1, 2);
INSERT INTO room_equipment (room_id, equipment_id) VALUES (1, 4);
INSERT INTO room_equipment (room_id, equipment_id) VALUES (2, 1);
INSERT INTO room_equipment (room_id, equipment_id) VALUES (2, 2);
INSERT INTO room_equipment (room_id, equipment_id) VALUES (3, 2);
INSERT INTO room_equipment (room_id, equipment_id) VALUES (4, 1);
INSERT INTO room_equipment (room_id, equipment_id) VALUES (4, 3);
INSERT INTO room_equipment (room_id, equipment_id) VALUES (4, 4);
INSERT INTO room_equipment (room_id, equipment_id) VALUES (5, 2);
INSERT INTO room_equipment (room_id, equipment_id) VALUES (5, 3);
INSERT INTO room_equipment (room_id, equipment_id) VALUES (6, 3);

-- MIEMBROS (el 4 está suspendido)
INSERT INTO members (id, document_number, full_name, email, status, membership_plan_id, created_at, updated_at) VALUES (1, '1098765432', 'Ana Gómez', 'ana.gomez@coworkhub.com', 'ACTIVE', 2, NOW(), NOW());
INSERT INTO members (id, document_number, full_name, email, status, membership_plan_id, created_at, updated_at) VALUES (2, '1087654321', 'Carlos Ruiz', 'carlos.ruiz@coworkhub.com', 'ACTIVE', 1, NOW(), NOW());
INSERT INTO members (id, document_number, full_name, email, status, membership_plan_id, created_at, updated_at) VALUES (3, '1076543210', 'Laura Pérez', 'laura.perez@coworkhub.com', 'ACTIVE', 3, NOW(), NOW());
INSERT INTO members (id, document_number, full_name, email, status, membership_plan_id, created_at, updated_at) VALUES (4, '1065432109', 'Diego Torres', 'diego.torres@coworkhub.com', 'SUSPENDED', 1, NOW(), NOW());
INSERT INTO members (id, document_number, full_name, email, status, membership_plan_id, created_at, updated_at) VALUES (5, '1054321098', 'Sofía Marín', 'sofia.marin@coworkhub.com', 'ACTIVE', 2, NOW(), NOW());

-- USUARIOS (password con BCrypt; admin y recep no son miembros; Laura, Diego y Sofía no tienen cuenta)
INSERT INTO user_accounts (id, username, password, role, member_id, created_at, updated_at) VALUES (1, 'admin', '$2a$10$UO4JoNf5by55qWoA3SXpx.GRYMoKZjBWm89WUEA58I7nUvJz.U5UC', 'ADMIN', NULL, NOW(), NOW());
INSERT INTO user_accounts (id, username, password, role, member_id, created_at, updated_at) VALUES (2, 'recep', '$2a$10$/AXx8oj7Nbd011yn7YUB8O63VteXML6LBDyNd87vBEcJ54jVk6atW', 'RECEPTIONIST', NULL, NOW(), NOW());
INSERT INTO user_accounts (id, username, password, role, member_id, created_at, updated_at) VALUES (3, 'ana', '$2a$10$AXtltnKrhlVcE/kkPlO/0O45LzgNvCnRKLe1ewRxdFXdNB4I6qpS6', 'MEMBER', 1, NOW(), NOW());
INSERT INTO user_accounts (id, username, password, role, member_id, created_at, updated_at) VALUES (4, 'carlos', '$2a$10$oOJGRXKYu018QCVjNjyJrusKY07lzSgfvEtIqjgX8B4fqhstDKBSC', 'MEMBER', 2, NOW(), NOW());

-- RESERVAS COMPLETADAS (pasadas)
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, member_id, room_id, created_at, updated_at) VALUES (1, TIMESTAMP(CURDATE() - INTERVAL 10 DAY, '09:00:00'), TIMESTAMP(CURDATE() - INTERVAL 10 DAY, '11:00:00'), 6, 'COMPLETED', 0.00, 12000.00, 12000.00, NULL, 1, 1, NOW(), NOW());
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, member_id, room_id, created_at, updated_at) VALUES (2, TIMESTAMP(CURDATE() - INTERVAL 9 DAY, '14:00:00'), TIMESTAMP(CURDATE() - INTERVAL 9 DAY, '16:00:00'), 4, 'COMPLETED', 0.00, 0.00, 0.00, NULL, 2, 3, NOW(), NOW());
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, member_id, room_id, created_at, updated_at) VALUES (3, TIMESTAMP(CURDATE() - INTERVAL 8 DAY, '10:00:00'), TIMESTAMP(CURDATE() - INTERVAL 8 DAY, '13:00:00'), 8, 'COMPLETED', 0.00, 25000.00, 25000.00, NULL, 3, 4, NOW(), NOW());
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, member_id, room_id, created_at, updated_at) VALUES (4, TIMESTAMP(CURDATE() - INTERVAL 7 DAY, '15:00:00'), TIMESTAMP(CURDATE() - INTERVAL 7 DAY, '17:00:00'), 5, 'COMPLETED', 0.00, 10000.00, 10000.00, NULL, 5, 2, NOW(), NOW());
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, member_id, room_id, created_at, updated_at) VALUES (5, TIMESTAMP(CURDATE() - INTERVAL 3 DAY, '09:00:00'), TIMESTAMP(CURDATE() - INTERVAL 3 DAY, '13:00:00'), 7, 'COMPLETED', 72000.00, 0.00, 72000.00, NULL, 2, 1, NOW(), NOW());

-- RESERVAS CANCELADAS
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, member_id, room_id, created_at, updated_at) VALUES (6, TIMESTAMP(CURDATE() + INTERVAL 5 DAY, '10:00:00'), TIMESTAMP(CURDATE() + INTERVAL 5 DAY, '12:00:00'), 4, 'CANCELLED', 0.00, 0.00, 0.00, 0.00, 1, 2, NOW(), NOW());
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, member_id, room_id, created_at, updated_at) VALUES (7, TIMESTAMP(CURDATE() + INTERVAL 2 DAY, '16:00:00'), TIMESTAMP(CURDATE() + INTERVAL 2 DAY, '18:00:00'), 3, 'CANCELLED', 90000.00, 0.00, 90000.00, 45000.00, 2, 3, NOW(), NOW());
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, member_id, room_id, created_at, updated_at) VALUES (8, TIMESTAMP(CURDATE() - INTERVAL 2 DAY, '11:00:00'), TIMESTAMP(CURDATE() - INTERVAL 2 DAY, '12:00:00'), 2, 'CANCELLED', 0.00, 0.00, 0.00, 0.00, 1, 5, NOW(), NOW());

-- RESERVAS CONFIRMADAS (futuras)
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, member_id, room_id, created_at, updated_at) VALUES (9, TIMESTAMP(CURDATE() + INTERVAL 1 DAY, '09:00:00'), TIMESTAMP(CURDATE() + INTERVAL 1 DAY, '11:00:00'), 8, 'CONFIRMED', 0.00, 24000.00, 24000.00, NULL, 1, 1, NOW(), NOW());
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, member_id, room_id, created_at, updated_at) VALUES (10, TIMESTAMP(CURDATE() + INTERVAL 1 DAY, '14:00:00'), TIMESTAMP(CURDATE() + INTERVAL 1 DAY, '16:00:00'), 6, 'CONFIRMED', 0.00, 0.00, 0.00, NULL, 3, 2, NOW(), NOW());
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, member_id, room_id, created_at, updated_at) VALUES (11, TIMESTAMP(CURDATE() + INTERVAL 3 DAY, '10:00:00'), TIMESTAMP(CURDATE() + INTERVAL 3 DAY, '12:00:00'), 3, 'CONFIRMED', 0.00, 25000.00, 25000.00, NULL, 5, 3, NOW(), NOW());
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, member_id, room_id, created_at, updated_at) VALUES (12, TIMESTAMP(CURDATE() + INTERVAL 4 DAY, '13:00:00'), TIMESTAMP(CURDATE() + INTERVAL 4 DAY, '15:00:00'), 10, 'CONFIRMED', 0.00, 36000.00, 36000.00, NULL, 3, 4, NOW(), NOW());

-- RESERVAS PENDIENTES (futuras)
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, member_id, room_id, created_at, updated_at) VALUES (13, TIMESTAMP(CURDATE() + INTERVAL 2 DAY, '09:00:00'), TIMESTAMP(CURDATE() + INTERVAL 2 DAY, '10:30:00'), 3, 'PENDING', 74250.00, 5000.00, 79250.00, NULL, 2, 5, NOW(), NOW());
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, member_id, room_id, created_at, updated_at) VALUES (14, TIMESTAMP(CURDATE() + INTERVAL 6 DAY, '16:00:00'), TIMESTAMP(CURDATE() + INTERVAL 6 DAY, '18:00:00'), 5, 'PENDING', 0.00, 0.00, 0.00, NULL, 1, 1, NOW(), NOW());
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, member_id, room_id, created_at, updated_at) VALUES (15, TIMESTAMP(CURDATE() + INTERVAL 7 DAY, '08:00:00'), TIMESTAMP(CURDATE() + INTERVAL 7 DAY, '09:00:00'), 2, 'PENDING', 0.00, 12000.00, 12000.00, NULL, 5, 2, NOW(), NOW());

-- DETALLE DE RESERVAS (precio aplicado = precio vigente del servicio)
INSERT INTO booking_items (id, quantity, applied_unit_price, booking_id, additional_service_id, created_at, updated_at) VALUES (1, 1, 12000.00, 1, 1, NOW(), NOW());
INSERT INTO booking_items (id, quantity, applied_unit_price, booking_id, additional_service_id, created_at, updated_at) VALUES (2, 1, 25000.00, 3, 3, NOW(), NOW());
INSERT INTO booking_items (id, quantity, applied_unit_price, booking_id, additional_service_id, created_at, updated_at) VALUES (3, 2, 5000.00, 4, 2, NOW(), NOW());
INSERT INTO booking_items (id, quantity, applied_unit_price, booking_id, additional_service_id, created_at, updated_at) VALUES (4, 2, 12000.00, 9, 1, NOW(), NOW());
INSERT INTO booking_items (id, quantity, applied_unit_price, booking_id, additional_service_id, created_at, updated_at) VALUES (5, 1, 25000.00, 11, 3, NOW(), NOW());
INSERT INTO booking_items (id, quantity, applied_unit_price, booking_id, additional_service_id, created_at, updated_at) VALUES (6, 3, 12000.00, 12, 1, NOW(), NOW());
INSERT INTO booking_items (id, quantity, applied_unit_price, booking_id, additional_service_id, created_at, updated_at) VALUES (7, 1, 5000.00, 13, 2, NOW(), NOW());
INSERT INTO booking_items (id, quantity, applied_unit_price, booking_id, additional_service_id, created_at, updated_at) VALUES (8, 1, 12000.00, 15, 1, NOW(), NOW());