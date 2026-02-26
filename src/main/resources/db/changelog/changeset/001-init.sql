--liquibase formatted sql
--changeset postgres:001-init

CREATE TABLE if not exists parking(
    id BIGSERIAL PRIMARY KEY,
    number VARCHAR(20) NOT NULL,
    time_in TIMESTAMP WITHOUT TIME ZONE NOT NULL CHECK (time_in < time_out OR time_out IS NULL),
    time_out TIMESTAMP WITHOUT TIME ZONE,
    car_type VARCHAR(15) CHECK (car_type IN ('Легковой', 'Грузовой', 'Мотоцикл', 'Автобус'))
);
create index idx_parking_time_in on parking(time_in);
create index idx_parking_time_out_range on parking(time_out, time_in);