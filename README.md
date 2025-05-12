# AndroidStudio-DSW9
Este proyecto corresponde a la materia Desarrollo de Software IX y consiste en una aplicación de gestión tecnológica desarrollada en Android Studio. Para ejecutar correctamente la aplicación, es necesario realizar algunas modificaciones en el código según el entorno en el que se vaya a ejecutar.

Si vas a utilizar un dispositivo virtual  en Android Studio(Ya por defecto), debes usar la siguiente dirección IP:
http://10.0.2.2/carpeta/archivo.php

Si vas a ejecutar la aplicación en un dispositivo físico, debes reemplazar 10.0.2.2 por la dirección IP local de tu computadora (la que está ejecutando el servidor web con XAMPP o similar), por ejemplo:
http://192.168.1.100/carpeta/archivo.php

Debes modificar manualmente las siguientes líneas en los archivos indicados:

- Captura.kt: línea 77
- Carrito.kt: línea 74
- CarritoAdapter.kt: líneas 85 y 119
- CarritoDetalleActivity.kt: línea 62
- LoginActivity.kt: línea 49
- ProcesoPagoActivity.kt: línea 131
- ProductoAdapter.kt: línea 58
- ReciboActivity.kt: línea 53
- Registro.kt: línea 46


Además, el proyecto requiere que la carpeta miapp se encuentre dentro de la carpeta htdocs del servidor local XAMPP. Esta carpeta contiene los archivos PHP necesarios para la conexión con la base de datos y las consultas que utiliza la aplicación, como por ejemplo conexion.php, captura.php.
Una vez que hayas configurado correctamente las direcciones IP en el código y hayas colocado el directorio miapp en la carpeta htdocs, la aplicación estará lista para ejecutarse sin problemas, ya sea en un emulador o en un dispositivo físico.