CREATE TABLE links (
                       id BIGSERIAL PRIMARY KEY,
                       short_code VARCHAR(20) NOT NULL UNIQUE,
                       original_url TEXT NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       expires_at TIMESTAMP NOT NULL,
                       click_count BIGINT NOT NULL DEFAULT 0,
                       user_id BIGINT NOT NULL,
                       deleted BOOLEAN NOT NULL DEFAULT FALSE,
                       CONSTRAINT fk_links_user FOREIGN KEY (user_id) REFERENCES users(id)
);