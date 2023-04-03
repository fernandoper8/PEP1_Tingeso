INSERT INTO proveedor(codigo, categoria, nombre, retencion) VALUES ('1003', 'A', 'Armin van Buuren', 'Si');
INSERT INTO proveedor(codigo, categoria, nombre, retencion) VALUES ('1011', 'A', 'Carl Cox', 'No');
INSERT INTO proveedor(codigo, categoria, nombre, retencion) VALUES ('1025', 'C', 'Diegos Olima', 'No');
INSERT INTO proveedor(codigo, categoria, nombre, retencion) VALUES ('1078', 'B', 'Arthur Morgan', 'Si');

INSERT INTO acopio(id_acopio, fecha, id_proveedor, kls_leche, turno) VALUES ('1', '17-03-2023', '1003', '50', 'M');
INSERT INTO acopio(id_acopio, fecha, id_proveedor, kls_leche, turno) VALUES ('2', '17-03-2023', '1003', '45', 'T');
INSERT INTO acopio(id_acopio, fecha, id_proveedor, kls_leche, turno) VALUES ('3', '18-03-2023', '1011', '30', 'M');
INSERT INTO acopio(id_acopio, fecha, id_proveedor, kls_leche, turno) VALUES ('4', '18-03-2023', '1025', '35', 'M');
INSERT INTO acopio(id_acopio, fecha, id_proveedor, kls_leche, turno) VALUES ('5', '18-03-2023', '1078', '25', 'T');

INSERT INTO datos(id_datos, id_proveedor, por_grasa, por_solidos) VALUES ('1','1003','25','14');
INSERT INTO datos(id_datos, id_proveedor, por_grasa, por_solidos) VALUES ('2','1011','8','11');
INSERT INTO datos(id_datos, id_proveedor, por_grasa, por_solidos) VALUES ('3','1025','30','50');
INSERT INTO datos(id_datos, id_proveedor, por_grasa, por_solidos) VALUES ('4','1078','40','27');
