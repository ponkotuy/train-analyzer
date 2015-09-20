

CREATE TABLE line(
        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        time_table_period INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE station(
        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        line_id BIGINT NOT NULL,
        FOREIGN KEY (line_id) REFERENCES line(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE pattern(
        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        line_id BIGINT NOT NULL,
        name VARCHAR(255) NOT NULL,
        FOREIGN KEY (line_id) REFERENCES line(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE train(
        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        pattern_id BIGINT NOT NULL,
        train_class VARCHAR(64) NOT NULL,
        name VARCHAR(255) NOT NULL,
        FOREIGN KEY (pattern_id) REFERENCES pattern(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `time_table`(
        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        train_id BIGINT NOT NULL,
        station_id BIGINT NOT NULL,
        minutes INT NOT NULL,
        is_arrive BOOLEAN NOT NULL
        UNIQUE INDEX train_station_arrive(train_id, station_id, is_arrive),
        FOREIGN KEY (train_id) REFERENCES train(id),
        FOREIGN KEY (station_id) REFERENCES station(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
