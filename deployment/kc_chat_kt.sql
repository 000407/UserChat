CREATE DATABASE kc_chat_v1;
USE kc_chat_v1;

CREATE TABLE User (
	id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	username VARCHAR(45) NOT NULL UNIQUE,
	password VARCHAR(255) NOT NULL,
	status TINYINT NOT NULL DEFAULT 1
);

CREATE TABLE UserRole(
	id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userId INT(11) NOT NULL,
    role VARCHAR(20) NOT NULL,
    CONSTRAINT uc_id_role UNIQUE (userId, role),
    CONSTRAINT fk_UserRole_User FOREIGN KEY (userId) REFERENCES User(id),
    INDEX idx_userid (userId)
);

INSERT INTO User(username, password, status) VALUES
('priya','priya', true),
('naveen','naveen', true),
('raj','raj', true),
('john','john', true);

INSERT INTO UserRole (userId, role) VALUES
(1, 'ROLE_USER'),
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER'),
(3, 'ROLE_USER'),
(4, 'ROLE_ADMIN');