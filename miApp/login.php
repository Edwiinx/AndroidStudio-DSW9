<?php
// Conexión a la base de datos
$host = "localhost";
$dbname = "NOID_BD";
$usernameDB = "root";
$passwordDB = "";

$est = mysqli_connect($host, $usernameDB, $passwordDB, $dbname);

if ($est) {
    // Asegurarse de recibir los datos del formulario
    $username = $_POST['username'];  // Suponiendo que el formulario usa 'username'
    $password = $_POST['password'];  // Suponiendo que el formulario usa 'password'

    // Usar sentencia preparada para evitar SQL Injection
    $stmt = $est->prepare("SELECT * FROM USUARIO WHERE NOMBRE_USUARIO = ?");
    $stmt->bind_param("s", $username);  // 's' para string
    $stmt->execute();
    $result = $stmt->get_result();

    if (mysqli_num_rows($result) > 0) {
        $row = mysqli_fetch_assoc($result);
        if (trim($password) == trim($row['CONTRASENA'])) {
  // Verificar la contraseña
            $type = $row['TIPO'];
            // Enviar respuesta de éxito
            echo "success|$type|$username";
        } else {
            echo "error|Usuario o contraseña incorrecta $password";
        }
    } else {
        echo "error|El usuario no existe";
    }
} else {
    error_log("Error de conexión: " . mysqli_connect_error());  // Registra el error de conexión
    echo "error|No se pudo conectar al servidor de base de datos.";
}

?>

