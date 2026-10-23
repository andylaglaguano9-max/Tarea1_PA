-- Coleccionistas iniciales
INSERT INTO coleccionistas (id, alias_gamer, correo_electronico, nivel_membresia, saldo_boveda, activo, fecha_registro) 
VALUES (1, 'RetroMaster99', 'retromaster@pixelvault.com', 'CURADOR_LEGENDARIO', 5000.00, true, CURRENT_TIMESTAMP);

INSERT INTO coleccionistas (id, alias_gamer, correo_electronico, nivel_membresia, saldo_boveda, activo, fecha_registro) 
VALUES (2, 'N64Kid_Official', 'n64kid@pixelvault.com', 'ENTUSIASTA', 800.00, true, CURRENT_TIMESTAMP);

INSERT INTO coleccionistas (id, alias_gamer, correo_electronico, nivel_membresia, saldo_boveda, activo, fecha_registro) 
VALUES (3, 'ArcadeNoob', 'noob@pixelvault.com', 'NOOB', 50.00, true, CURRENT_TIMESTAMP);

-- Videojuegos Retro en la Boveda
INSERT INTO videojuegos_retro (id, titulo, plataforma, anio_lanzamiento, precio, estado_conservacion, rareza, serial_retro, stock) 
VALUES (1, 'Chrono Trigger', 'SNES', 1995, 850.00, 'MINT_IN_BOX', 'SANTO_GRIAL', 'SNES-1995-USA', 2);

INSERT INTO videojuegos_retro (id, titulo, plataforma, anio_lanzamiento, precio, estado_conservacion, rareza, serial_retro, stock) 
VALUES (2, 'The Legend of Zelda: Ocarina of Time (Gold Collector Edition)', 'N64', 1998, 450.00, 'MINT_IN_BOX', 'SANTO_GRIAL', 'N64-1998-USA', 1);

INSERT INTO videojuegos_retro (id, titulo, plataforma, anio_lanzamiento, precio, estado_conservacion, rareza, serial_retro, stock) 
VALUES (3, 'Super Mario 64', 'N64', 1996, 95.00, 'CIB', 'RARO', 'N64-1996-USA', 5);

INSERT INTO videojuegos_retro (id, titulo, plataforma, anio_lanzamiento, precio, estado_conservacion, rareza, serial_retro, stock) 
VALUES (4, 'Castlevania: Symphony of the Night', 'PS1', 1997, 180.00, 'CIB', 'EPICO', 'PS1-1997-USA', 3);

INSERT INTO videojuegos_retro (id, titulo, plataforma, anio_lanzamiento, precio, estado_conservacion, rareza, serial_retro, stock) 
VALUES (5, 'Sonic the Hedgehog 2', 'GEN', 1992, 35.00, 'LOOSE', 'COMUN', 'GEN-1992-USA', 10);

INSERT INTO videojuegos_retro (id, titulo, plataforma, anio_lanzamiento, precio, estado_conservacion, rareza, serial_retro, stock) 
VALUES (6, 'EarthBound', 'SNES', 1994, 1200.00, 'MINT_IN_BOX', 'SANTO_GRIAL', 'SNES-1994-USA', 2);
