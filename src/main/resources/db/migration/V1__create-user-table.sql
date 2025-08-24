CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role TEXT NOT NULL
);


INSERT INTO users (name, email, password, role) VALUES
('1234', '1234@email.com', '1234', 'COORDENADOR');