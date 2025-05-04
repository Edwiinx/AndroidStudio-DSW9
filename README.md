# AndroidStudio-DSW9
Versión de Android Studio de el proyecto de Gestión de tecnología de la materia Desarrollo de Software IX

miApp es donde esta la conexion y todo lo de xampp, eso debes ponerlo en htdocs

aparte hay una linea que esta en LoginActivity.kt, Captura.kt y Registro.kt

en LoginActivity es la 55
            val url = URL("http://192.168.0.13/miApp/login.php")
en Registro es la 46
                val url = URL("http://192.168.0.13/miApp/registro.php")
y en Captura es la 76
            val url = "http://192.168.0.13/miApp/captura.php"

en estas si estas usando tu telefono pon la ip de tu pc normal, pero si es un emulador de android como el de android studio pon 10.0.2.2 y te debe servir


lo unico que falta es que siendo COMPRADOR, desde login te pase a index pero eso no lo puedo poner porque hice push antes de mario uu asi que de login si eres administrador te manda a Captura, puedes ir a registro, registrarte, regresar despues y en captura registrar el producto.
