USE `memory_game`;
# CREATE TABLE `card` (
#                         `id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
#                         `card_name` VARCHAR(255),
#                         `image` VARCHAR(255),
#                         PRIMARY KEY (`id`)
# );

-- Tạo bảng `room` (thay ENUM bằng VARCHAR)
# CREATE TABLE `room` (
#                         `id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE,
#                         `player1_id` BIGINT,
#                         `player2_id` BIGINT,
#                         `status` VARCHAR(50), -- Thay thế ENUM bằng VARCHAR
#                         PRIMARY KEY (`id`)
# );

-- Tạo bảng `match`
CREATE TABLE `match` (
                         `id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE,
                         `room_id` BIGINT,
                         `player1_id` BIGINT,
                         `player2_id` BIGINT,
                         `player1_score` BIGINT,
                         `player2_score` BIGINT,  -- Sửa thành player2_score
                         `winner_id` BIGINT,
                         `created_at` DATE,
                         `updated_at` DATE,
                         PRIMARY KEY (`id`)
);

-- Tạo bảng `match_history`
CREATE TABLE `match_history` (
                                 `id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
                                 `user_id` BIGINT,
                                 `match_id` BIGINT,
                                 `result` VARCHAR(255),
                                 `points_earned` INTEGER,
                                 `created_at` DATE,
                                 PRIMARY KEY (`id`)
);


CREATE TABLE `table_id` (
                                 `table_id` BIGINT DEFAULT 0,
                                 `table_name` VARCHAR(500)
);

INSERT INTO `table_id` (table_name) VALUES
    ('room');