START TRANSACTION;
use sic;
SET SQL_SAFE_UPDATES=0;
ALTER TABLE  configuraciondelsistema
ADD certificadoAfip LONGBLOB, ADD facturaElectronicaHabilitada BIT(1) NOT NULL,
ADD firmanteCertificadoAfip VARCHAR(255), ADD nroPuntoDeVentaAfip INT(11) NOT NULL,
ADD passwordCertificadoAfip VARCHAR(255);
ALTER TABLE factura
ADD CAE BIGINT(20) NOT NULL, ADD vencimientoCAE DATETIME;
UPDATE factura INNER JOIN facturaventa ON factura.id_Factura = facturaventa.id_Factura
SET numSerie = 0 WHERE numSerie = 1;
SET SQL_SAFE_UPDATES=1;
COMMIT;
