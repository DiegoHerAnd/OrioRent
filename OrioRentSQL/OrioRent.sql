CREATE DATABASE IF NOT EXISTS OrioRent;
USE orioRent;

-- ========================================
-- Tabla USUARIO
-- ========================================
CREATE TABLE USUARIO (
	id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    contrasena VARCHAR(100), 
    fecha_registro DATE NOT NULL
);

-- ========================================
-- Tabla CatEGORIA
-- ========================================
CREATE TABLE CATEGORIA (
    id_categoria INT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);
-- ========================================
-- Tabla LOCAL
-- ========================================
CREATE TABLE LOCAL (
    id_local INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    direccion VARCHAR(300),
    capacidad INT,
    precio_base DECIMAL(10,2),
    id_propietario INT,
    id_categoria INT,
    FOREIGN KEY (id_propietario) REFERENCES USUARIO(id_usuario),
    FOREIGN KEY (id_categoria) REFERENCES CATEGORIA(id_categoria)
);

-- ========================================
-- Tabla RESERVA
-- ========================================
CREATE TABLE RESERVA (
    id_reserva INT AUTO_INCREMENT PRIMARY KEY,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado VARCHAR(20) NOT NULL,
    precio_total DECIMAL(10,2) NOT NULL,
    id_usuario INT,
    id_local INT,
    FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario),
    FOREIGN KEY (id_local) REFERENCES LOCAL(id_local)
);

-- ========================================
-- Tabla METODOPAGO
-- ========================================
CREATE TABLE METODOPAGO (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    metodo_pago VARCHAR(50),
    hora_fecha TIMESTAMP,
    importe DECIMAL(10,2),
    estado VARCHAR(20),
    id_usuario INT,
    id_reserva INT,
    FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario),
    FOREIGN KEY (id_reserva) REFERENCES RESERVA(id_reserva)
);

-- ========================================
-- Tabla SERVICIO
-- ========================================
CREATE TABLE SERVICIO (
    id_servicio INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2),
    id_local INT,
    fecha DATE,
    FOREIGN KEY (id_local) REFERENCES LOCAL(id_local)
);

-- ========================================
-- Tabla FAVORITO
-- ========================================
CREATE TABLE FAVORITO (
    id_fav INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT,
    id_local INT,
    FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario),
    FOREIGN KEY (id_local) REFERENCES LOCAL(id_local)
);

-- ========================================
-- Tabla VALORACION
-- ========================================
CREATE TABLE VALORACION (
    id_valoracion INT AUTO_INCREMENT PRIMARY KEY,
    puntuacion INT NOT NULL,
    comentario TEXT NOT NULL,
    fecha DATE NOT NULL,
    id_usuario INT,
    id_local INT,
    FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario),
    FOREIGN KEY (id_local) REFERENCES LOCAL(id_local)
);

-- ========================================
-- Tabla IMAGENLOCAL
-- ========================================
CREATE TABLE IMAGENLOCAL (
    id_imagen INT AUTO_INCREMENT PRIMARY KEY,
    url_imagen VARCHAR(200) NOT NULL,
    descripcion TEXT,
    id_local INT,
    FOREIGN KEY (id_local) REFERENCES LOCAL(id_local)
);

-- ========================================
-- Tabla HORARIOLOCAL
-- ========================================
CREATE TABLE HORARIOLOCAL (
    id_horario INT AUTO_INCREMENT PRIMARY KEY,
    dia_semana VARCHAR(20) NOT NULL,
    hora_apertura TIME NOT NULL,
    hora_cierre TIME NOT NULL,
    id_local INT,
    FOREIGN KEY (id_local) REFERENCES LOCAL(id_local)
);
