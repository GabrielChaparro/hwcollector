ALTER TABLE collection_items
  ADD COLUMN IF NOT EXISTS availability VARCHAR(32) NOT NULL DEFAULT 'NOT_FOR_SALE',
  ADD COLUMN IF NOT EXISTS visibility   VARCHAR(32) NOT NULL DEFAULT 'PRIVATE',
  ADD COLUMN IF NOT EXISTS ask_price    NUMERIC(10,2);

CREATE TABLE IF NOT EXISTS wishlist_items (
  id          BIGSERIAL PRIMARY KEY,
  user_id     BIGINT NOT NULL,
  hotwheel_id BIGINT NOT NULL,
  priority    INT NOT NULL DEFAULT 3,
  max_price   NUMERIC(10,2),
  created_at  TIMESTAMP NOT NULL DEFAULT NOW(),

  CONSTRAINT fk_wishlist_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_wishlist_hotwheel FOREIGN KEY (hotwheel_id) REFERENCES hotwheels(id),
  CONSTRAINT uk_wishlist_user_hotwheel UNIQUE (user_id, hotwheel_id)
);

CREATE INDEX IF NOT EXISTS idx_wishlist_user ON wishlist_items(user_id);
