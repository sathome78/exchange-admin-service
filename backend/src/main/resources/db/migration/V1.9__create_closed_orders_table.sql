CREATE TABLE IF NOT EXISTS CLOSED_ORDERS
(
    id                 INTEGER        NOT NULL,
    currency_pair_name VARCHAR(55)    NOT NULL,
    user_id            INTEGER        NOT NULL,
    user_acceptor_id   INTEGER        NOT NULL,
    rate               DECIMAL(18, 8) NOT NULL,
    amount_base        DECIMAL(40, 9) NOT NULL,
    amount_convert     DECIMAL(40, 9) NOT NULL,
    amount_usd         DECIMAL(12, 2) DEFAULT 0,
    closed             DATE           NOT NULL,
    base_type          VARCHAR(100)   DEFAULT 'LIMIT',
    PRIMARY KEY (id)
);

create index idx_user_id
    on CLOSED_ORDERS (user_id);

create index idx_user_acceptor_id
    on CLOSED_ORDERS (user_acceptor_id);

create index idx_user_id_date
    on CLOSED_ORDERS (user_id, closed);

create index idx_user_acceptor_id_date
    on CLOSED_ORDERS (user_acceptor_id, closed);
