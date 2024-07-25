CREATE TABLE Usuarios(
id_usuario INT PRIMARY KEY,
email VARCHAR2(100) UNIQUE NOT NULL,
contrasena VARCHAR2(50) NOT NULL
);

CREATE TABLE Pacientes(
id_paciente INT PRIMARY KEY,
nombres VARCHAR2(100) NOT NULL,
apellidos VARCHAR2(100) NOT NULL,
edad INT NOT NULL,
enfermedad VARCHAR2(200) NOT NULL,
num_habitacion INT NOT NULL,
num_cama INT NOT NULL,
medicamentos Varchar2(100) not null,
fecha_ingreso DATE NOT NULL,
hora_aplicacion varchar2(5)not null
);

-------------------------------------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------------------------------------



CREATE SEQUENCE sec_usuar
START WITH 1
INCREMENT BY 1;

CREATE TRIGGER TrigUsuar
BEFORE INSERT ON Usuarios
FOR EACH ROW
BEGIN
SELECT sec_usuar.NEXTVAL INTO : NEW.id_usuario
FROM DUAL;
END;





CREATE SEQUENCE sec_pacien
START WITH 1
INCREMENT BY 1;

CREATE TRIGGER TrigPacie
BEFORE INSERT ON Pacientes
FOR EACH ROW
BEGIN
SELECT sec_pacien.NEXTVAL INTO : NEW.id_paciente
FROM DUAL;
END;


-------------------------------------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------------------------------------


INSERT INTO Usuarios (email, contrasena) VALUES ('enfermeroNum1@gmail.com', 'Enfermero1');


INSERT ALL
INTO Pacientes(nombres, apellidos, edad, enfermedad, num_habitacion, num_cama, medicamentos, fecha_ingreso, hora_aplicacion) VALUES ('Daniel', 'Brizuela', 18, 'Dolor de cuerpo', 189, 4,'Acetaminofen', '10/10/24', '20:23')
INTO Pacientes(nombres, apellidos, edad, enfermedad, num_habitacion, num_cama, medicamentos, fecha_ingreso, hora_aplicacion) VALUES ('Luz', 'Gaspario', 17, 'Quebradura', 778, 7,'Anti-Inflamatorios', '09/12/24', '06:40')
SELECT * FROM dual;
SELECT * FROM Pacientes;