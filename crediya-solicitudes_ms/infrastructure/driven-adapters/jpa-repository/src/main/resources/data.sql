INSERT INTO tipos_prestamo (codigo, nombre, plazo_minimo_meses, plazo_maximo_meses, activo)
SELECT 'LIBRE_INVERSION', 'Crédito de libre inversión', 6, 60, TRUE
WHERE NOT EXISTS (SELECT 1 FROM tipos_prestamo WHERE codigo = 'LIBRE_INVERSION');

INSERT INTO tipos_prestamo (codigo, nombre, plazo_minimo_meses, plazo_maximo_meses, activo)
SELECT 'LIBRANZA', 'Crédito de libranza', 72, 96, TRUE
WHERE NOT EXISTS (SELECT 1 FROM tipos_prestamo WHERE codigo = 'LIBRANZA');

INSERT INTO tipos_prestamo (codigo, nombre, plazo_minimo_meses, plazo_maximo_meses, activo)
SELECT 'VIVIENDA', 'Crédito de vivienda', 60, 360, TRUE
WHERE NOT EXISTS (SELECT 1 FROM tipos_prestamo WHERE codigo = 'VIVIENDA');