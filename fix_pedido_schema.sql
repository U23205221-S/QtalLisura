-- Script para permitir valores NULL en la columna id_cliente de la tabla Pedido
-- Esto permite crear pedidos sin cliente registrado (consumidor final)

USE dbrestaurant;

-- Modificar la columna id_cliente para permitir valores NULL
ALTER TABLE Pedido
MODIFY COLUMN id_cliente INT NULL;

-- Verificar el cambio
DESCRIBE Pedido;

-- Mostrar mensaje de éxito
SELECT 'La columna id_cliente ahora permite valores NULL' AS Resultado;

