<?php
$host = "localhost";
$dbname = "NOID_BD";
$usernameDB = "root";
$passwordDB = "";

$est = mysqli_connect($host, $usernameDB, $passwordDB, $dbname);

if (!$est) {
    die("Error de conexión: " . mysqli_connect_error());
}

$username = $_POST['user'];
$password = $_POST['password'];
$tipo = $_POST['type']; // Usamos lo que venga del Android

// Verificar si el usuario ya existe
$sql_check = "SELECT * FROM USUARIO WHERE NOMBRE_USUARIO = '$username'";
$result_check = mysqli_query($est, $sql_check);

if (mysqli_num_rows($result_check) > 0) {
    // Usuario ya existe
    header('Content-Type: application/json');
    echo json_encode(["status" => "error"]);
} else {
    // Insertar nuevo usuario
    $sql_insert = "INSERT INTO USUARIO (NOMBRE_USUARIO, CONTRASENA, TIPO) VALUES ('$username', '$password', 'COMPRADOR')";
    
    if (mysqli_query($est, $sql_insert)) {
        header('Content-Type: application/json');
        echo json_encode(["mensaje_mostrar" => "exito"]);
    } else {
        echo "Error al insertar usuario: " . mysqli_error($est);
    }
}

mysqli_close($est);
?>

