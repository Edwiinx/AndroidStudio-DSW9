<?php
header('Content-Type: application/json');
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

$host = "localhost";
$dbname = "NOID_BD";
$usernameDB = "root";
$passwordDB = "";

$conexion = mysqli_connect($host, $usernameDB, $passwordDB, $dbname);

if (!$conexion) {
    echo json_encode(["mensaje_mostrar" => "Error de conexión a la base de datos"]);
    exit;
}

$nombre = $_POST["nombre"] ?? "";
$precio = $_POST["precio"] ?? "";
$descripcion = $_POST["descripcion"] ?? "";
$numbserie = $_POST["numbserie"] ?? "";
$modelo = $_POST["modelo"] ?? "";
$categoria = $_POST["categoria"] ?? "";
$imagen = $_POST["imagen"] ?? "sin_imagen.png";

if (empty($nombre) || empty($precio) || empty($descripcion) || empty($categoria)) {
    echo json_encode(["mensaje_mostrar" => "Faltan datos requeridos."]);
    exit;
}

$sql = "INSERT INTO PRODUCTOS (ID_CATEGORIA, NOMBRE_PRODUCTO, MODELO, PRECIO_UNITARIO, FECHA_DE_CAPTURA, CANTIDAD, ESTADO, DESCRIPCION, IMAGEN, NUMERO_DE_SERIE)
VALUES ('$categoria', '$nombre', '$modelo', '$precio', NOW(), 100, 'DISPONIBLE', '$descripcion', '$imagen', '$numbserie')";

if (mysqli_query($conexion, $sql)) {
    echo json_encode(["mensaje_mostrar" => "Producto guardado correctamente."]);
} else {
    echo json_encode(["mensaje_mostrar" => "Error en la base de datos: " . mysqli_error($conexion)]);
}

