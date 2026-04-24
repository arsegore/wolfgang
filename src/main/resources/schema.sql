CREATE DATABASE IF NOT EXISTS wolfgang_db;
USE wolfgang_db;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT NOW(),
    updated_at DATETIME DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS instruments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS compositions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    tempo INT DEFAULT 120,
    access_type ENUM('public', 'link', 'private') DEFAULT 'private',
    owner_id INT NOT NULL,
    created_at DATETIME DEFAULT NOW(),
    updated_at DATETIME DEFAULT NOW(),
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS tracks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    composition_id INT NOT NULL,
    name VARCHAR(255) NOT NULL DEFAULT 'Piste sans nom',
    instrument_id INT NOT NULL,
    color VARCHAR(7) DEFAULT '#FFFFFF', -- couleur hex
    position TINYINT NOT NULL,
    FOREIGN KEY (composition_id) REFERENCES compositions(id) ON DELETE CASCADE,
    FOREIGN KEY (instrument_id) REFERENCES instruments(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS notes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    track_id INT NOT NULL,
    pitch TINYINT UNSIGNED NOT NULL,
    start_beat FLOAT NOT NULL,
    duration FLOAT NOT NULL,
    velocity TINYINT UNSIGNED DEFAULT 100,
    FOREIGN KEY (track_id) REFERENCES tracks(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS composition_members (
    composition_id INT NOT NULL,
    user_id INT NOT NULL,
    role ENUM('owner', 'editor', 'viewer') DEFAULT 'viewer',
    PRIMARY KEY (composition_id, user_id),
    FOREIGN KEY (composition_id) REFERENCES compositions(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
