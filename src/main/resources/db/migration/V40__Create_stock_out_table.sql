CREATE TABLE stock_outs (
    id BIGSERIAL PRIMARY KEY,
    stock_out_type VARCHAR(50) NOT NULL,
    item_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    stock_out_date TIMESTAMP NOT NULL,
    note VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stock_out_item FOREIGN KEY (item_id) REFERENCES items(item_id)
);

CREATE INDEX idx_stock_out_item ON stock_outs(item_id);
CREATE INDEX idx_stock_out_type ON stock_outs(stock_out_type);
