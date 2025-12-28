--liquibase formatted sql


--changeset program:001-create-table-payments
--comment: Создание таблицы payments
CREATE TABLE IF NOT EXISTS payments (
    id SERIAL PRIMARY KEY,
    order_id INT NOT NULL,
    user_id INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
    payment_amount DECIMAL(10, 2) NOT NULL
    );
--rollback DROP TABLE IF EXISTS payments;


--changeset program:002-create-index-on-payments-order-id
--comment: Создание индекса на колонку order_id в payments
CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payments(order_id);
--rollback DROP INDEX IF EXISTS idx_payments_order_id;


--changeset program:003-create-index-on-payments-user-id
--comment: Создание индекса на колонку user_id в payments
CREATE INDEX IF NOT EXISTS idx_payments_user_id ON payments(user_id);
--rollback DROP INDEX IF EXISTS idx_payments_user_id;


--changeset program:004-create-index-on-payments-status
--comment: Создание индекса на колонку status в payments
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);
--rollback DROP INDEX IF EXISTS idx_payments_status;
