INSERT INTO proveedor(codigo, categoria, nombre, retencion) VALUES ('1003', 'A', 'Armin van Buuren', 'No');
INSERT INTO proveedor(codigo, categoria, nombre, retencion) VALUES ('2001', 'B', 'Oliver Heldens', 'Si');
INSERT INTO proveedor(codigo, categoria, nombre, retencion) VALUES ('3001', 'A', 'Colun', 'No');
INSERT INTO proveedor(codigo, categoria, nombre, retencion) VALUES ('4001', 'B', 'Dom Dolla', 'Si');
INSERT INTO proveedor(codigo, categoria, nombre, retencion) VALUES ('5001', 'A', 'Mark Kinchen', 'No');
INSERT INTO proveedor(codigo, categoria, nombre, retencion) VALUES ('6001', 'C', 'Jamie Jones', 'Si');
INSERT INTO proveedor(codigo, categoria, nombre, retencion) VALUES ('7001', 'B', 'John Summit', 'Si');
INSERT INTO proveedor(codigo, categoria, nombre, retencion) VALUES ('8001', 'A', 'Joris Voorn', 'No');

INSERT INTO acopio(id_acopio, fecha, id_proveedor, kls_leche, turno) VALUES ('1', '17-03-2023', '1003', '1700', 'M');
INSERT INTO acopio(id_acopio, fecha, id_proveedor, kls_leche, turno) VALUES ('2', '17-03-2023', '1003', '120', 'M');
INSERT INTO acopio(id_acopio, fecha, id_proveedor, kls_leche, turno) VALUES ('3', '19-03-2023', '2001', '150', 'M');
INSERT INTO acopio(id_acopio, fecha, id_proveedor, kls_leche, turno) VALUES ('4', '23-03-2023', '2001', '75', 'T');

INSERT INTO datos(id_datos, id_proveedor, por_grasa, por_solidos) VALUES ('1','1003','72','70');
INSERT INTO datos(id_datos, id_proveedor, por_grasa, por_solidos) VALUES ('2','2001','42','38');