-- Tasas mensuales vigentes por tipo de préstamo (HU4).
INSERT INTO tipos_prestamo (codigo, nombre, plazo_minimo_meses, plazo_maximo_meses, tasa_interes, activo)
SELECT 'LIBRE_INVERSION', 'Crédito de libre inversión', 6, 60, 0.019000, TRUE
WHERE NOT EXISTS (SELECT 1 FROM tipos_prestamo WHERE codigo = 'LIBRE_INVERSION');

INSERT INTO tipos_prestamo (codigo, nombre, plazo_minimo_meses, plazo_maximo_meses, tasa_interes, activo)
SELECT 'LIBRANZA', 'Crédito de libranza', 72, 96, 0.015000, TRUE
WHERE NOT EXISTS (SELECT 1 FROM tipos_prestamo WHERE codigo = 'LIBRANZA');

INSERT INTO tipos_prestamo (codigo, nombre, plazo_minimo_meses, plazo_maximo_meses, tasa_interes, activo)
SELECT 'VIVIENDA', 'Crédito de vivienda', 60, 360, 0.011000, TRUE
WHERE NOT EXISTS (SELECT 1 FROM tipos_prestamo WHERE codigo = 'VIVIENDA');

-- Asegura la tasa aun si el tipo ya existía antes de este cambio.
UPDATE tipos_prestamo SET tasa_interes = 0.019000 WHERE codigo = 'LIBRE_INVERSION' AND tasa_interes IS NULL;
UPDATE tipos_prestamo SET tasa_interes = 0.015000 WHERE codigo = 'LIBRANZA' AND tasa_interes IS NULL;
UPDATE tipos_prestamo SET tasa_interes = 0.011000 WHERE codigo = 'VIVIENDA' AND tasa_interes IS NULL;

-- Migración del estado persistido como descripción a su nombre de enum.
UPDATE solicitudes SET estado = 'PENDIENTE_REVISION' WHERE estado = 'Pendiente de revisión';
UPDATE solicitudes SET estado = 'REVISION_MANUAL' WHERE estado = 'Revisión manual';
UPDATE solicitudes SET estado = 'APROBADA' WHERE estado = 'Aprobada';
UPDATE solicitudes SET estado = 'RECHAZADA' WHERE estado = 'Rechazada';

-- Datos de snapshot para solicitudes creadas antes de este cambio.
UPDATE solicitudes SET nombre_cliente = COALESCE(nombre_cliente, 'No disponible') WHERE nombre_cliente IS NULL;
UPDATE solicitudes SET correo_electronico = COALESCE(correo_electronico, 'No disponible') WHERE correo_electronico IS NULL;
UPDATE solicitudes SET salario_base = COALESCE(salario_base, 0) WHERE salario_base IS NULL;
UPDATE solicitudes SET tasa_interes = COALESCE(tasa_interes, 0) WHERE tasa_interes IS NULL;
UPDATE solicitudes SET cuota_mensual = COALESCE(cuota_mensual, 0) WHERE cuota_mensual IS NULL;
