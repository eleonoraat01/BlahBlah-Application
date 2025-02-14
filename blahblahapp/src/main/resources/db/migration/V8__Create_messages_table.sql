CREATE TABLE messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id BIGINT,
    channel_id BIGINT NULL,
    chat_id BIGINT NULL,
    content TEXT NOT NULL,
    is_active BOOLEAN DEFAULT true,
    is_special BOOLEAN DEFAULT false,
    created_at BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP),
    updated_at BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP),
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (channel_id) REFERENCES channels(id),
    FOREIGN KEY (chat_id) REFERENCES chats(id)
);