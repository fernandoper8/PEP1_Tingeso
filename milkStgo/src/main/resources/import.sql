INSERT INTO proveedor(codigo, categoria, nombre, retencion) VALUES ('1003', 'A', 'Armin van Buuren', 'Si');

INSERT INTO acopio(id_acopio, fecha, id_proveedor, kls_leche, turno) VALUES ('1', '17-03-2023', '1003', '25', 'M');
INSERT INTO acopio(id_acopio, fecha, id_proveedor, kls_leche, turno) VALUES ('2', '17-03-2023', '1003', '25', 'T');

INSERT INTO datos(id_datos, id_proveedor, por_grasa, por_solidos) VALUES ('1','1003','50','50');

