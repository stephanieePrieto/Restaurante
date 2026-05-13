CREATE DATABASE  IF NOT EXISTS `restaurante` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `restaurante`;
-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: restaurante
-- ------------------------------------------------------
-- Server version	9.6.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '3f814a17-18e1-11f1-84f0-b05cda369beb:1-251';

--
-- Table structure for table `almacen`
--

DROP TABLE IF EXISTS `almacen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `almacen` (
  `idMateriaPrima` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `stock` decimal(10,2) NOT NULL,
  `unidad` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idMateriaPrima`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `almacen`
--

LOCK TABLES `almacen` WRITE;
/*!40000 ALTER TABLE `almacen` DISABLE KEYS */;
/*!40000 ALTER TABLE `almacen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asistencias`
--

DROP TABLE IF EXISTS `asistencias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asistencias` (
  `idAsistencia` int NOT NULL AUTO_INCREMENT,
  `fechaEntrada` datetime NOT NULL,
  `fechaSalida` datetime DEFAULT NULL,
  `idEmpleado` int NOT NULL,
  `estado` varchar(50) DEFAULT NULL,
  `horas_trabajadas` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`idAsistencia`),
  KEY `fk_asistencia_empleado` (`idEmpleado`),
  CONSTRAINT `fk_asistencia_empleado` FOREIGN KEY (`idEmpleado`) REFERENCES `empleados` (`idEmpleado`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asistencias`
--

LOCK TABLES `asistencias` WRITE;
/*!40000 ALTER TABLE `asistencias` DISABLE KEYS */;
INSERT INTO `asistencias` VALUES (1,'2026-04-27 07:00:00','2026-04-27 14:00:00',1,'Incompleto','07:00 hrs'),(2,'2026-04-27 07:00:00','2026-04-27 15:00:00',2,'Cumplió','08:00 hrs');
/*!40000 ALTER TABLE `asistencias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categoriasmenu`
--

DROP TABLE IF EXISTS `categoriasmenu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoriasmenu` (
  `idCategoria` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idCategoria`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoriasmenu`
--

LOCK TABLES `categoriasmenu` WRITE;
/*!40000 ALTER TABLE `categoriasmenu` DISABLE KEYS */;
INSERT INTO `categoriasmenu` VALUES (1,'Cocina'),(2,'Bebidas'),(3,'Postres'),(4,'Extras'),(5,'Especiales');
/*!40000 ALTER TABLE `categoriasmenu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `id_cliente` varchar(10) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id_cliente`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
INSERT INTO `clientes` VALUES ('CP001','Angel','1234567890');
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detallepedidos`
--

DROP TABLE IF EXISTS `detallepedidos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detallepedidos` (
  `idDetalle` int NOT NULL AUTO_INCREMENT,
  `cantidad` int NOT NULL,
  `estadoPlatillo` varchar(50) DEFAULT 'Normal',
  `idPedido` int NOT NULL,
  `idPlatillo` int NOT NULL,
  PRIMARY KEY (`idDetalle`),
  KEY `fk_detalle_pedido` (`idPedido`),
  KEY `fk_detalle_platillo` (`idPlatillo`),
  CONSTRAINT `fk_detalle_pedido` FOREIGN KEY (`idPedido`) REFERENCES `pedidos` (`idPedido`),
  CONSTRAINT `fk_detalle_platillo` FOREIGN KEY (`idPlatillo`) REFERENCES `platillos` (`idPlatillo`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detallepedidos`
--

LOCK TABLES `detallepedidos` WRITE;
/*!40000 ALTER TABLE `detallepedidos` DISABLE KEYS */;
INSERT INTO `detallepedidos` VALUES (17,1,'Normal',11,21),(18,1,'Normal',10,2),(19,1,'Normal',10,22);
/*!40000 ALTER TABLE `detallepedidos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `empleados`
--

DROP TABLE IF EXISTS `empleados`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `empleados` (
  `idEmpleado` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `usuario` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `idRol` int DEFAULT NULL,
  PRIMARY KEY (`idEmpleado`),
  UNIQUE KEY `usuario` (`usuario`),
  KEY `idRol` (`idRol`),
  CONSTRAINT `empleados_ibfk_1` FOREIGN KEY (`idRol`) REFERENCES `rol` (`idRol`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `empleados`
--

LOCK TABLES `empleados` WRITE;
/*!40000 ALTER TABLE `empleados` DISABLE KEYS */;
INSERT INTO `empleados` VALUES (1,'Stephanie Gerente','admin','123',1),(2,'Juan Mesero','mesero1','123',2),(3,'Pinguino Chef','chef1','123',3),(4,'Ana Cajera','cajero1','123',4),(5,'Beto Recepcion','recepcion1','123',5),(6,'BegoPro','mesero2','124',2),(7,'Richi','Prichi','123',2);
/*!40000 ALTER TABLE `empleados` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mesa`
--

DROP TABLE IF EXISTS `mesa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mesa` (
  `idMesa` int NOT NULL AUTO_INCREMENT,
  `numero` int NOT NULL,
  `capacidad` int NOT NULL,
  `estado` varchar(50) DEFAULT 'Libre',
  `mapa_x` int DEFAULT '0',
  `mapa_y` int DEFAULT '0',
  PRIMARY KEY (`idMesa`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mesa`
--

LOCK TABLES `mesa` WRITE;
/*!40000 ALTER TABLE `mesa` DISABLE KEYS */;
INSERT INTO `mesa` VALUES (1,1,4,'Libre',0,0),(2,2,4,'Libre',0,0),(3,3,2,'Libre',0,0),(4,4,6,'Libre',0,0),(5,5,4,'Libre',0,0),(6,6,4,'Libre',0,0),(7,7,2,'Libre',0,0),(8,8,8,'Libre',0,0),(9,9,2,'Libre',0,0),(10,10,4,'Libre',0,0),(11,11,6,'Libre',0,0),(12,12,4,'Libre',0,0);
/*!40000 ALTER TABLE `mesa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pagos`
--

DROP TABLE IF EXISTS `pagos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pagos` (
  `idPago` int NOT NULL AUTO_INCREMENT,
  `total` decimal(10,2) NOT NULL,
  `metodoPago` varchar(50) NOT NULL,
  `fecha` datetime DEFAULT CURRENT_TIMESTAMP,
  `idPedido` int NOT NULL,
  PRIMARY KEY (`idPago`),
  KEY `fk_pago_pedido` (`idPedido`),
  CONSTRAINT `fk_pago_pedido` FOREIGN KEY (`idPedido`) REFERENCES `pedidos` (`idPedido`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pagos`
--

LOCK TABLES `pagos` WRITE;
/*!40000 ALTER TABLE `pagos` DISABLE KEYS */;
/*!40000 ALTER TABLE `pagos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedidos`
--

DROP TABLE IF EXISTS `pedidos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedidos` (
  `idPedido` int NOT NULL AUTO_INCREMENT,
  `fechaHora` datetime DEFAULT CURRENT_TIMESTAMP,
  `estado` varchar(50) DEFAULT 'Pendiente',
  `idMesa` int NOT NULL,
  `idEmpleado` int NOT NULL,
  PRIMARY KEY (`idPedido`),
  KEY `fk_pedido_mesa` (`idMesa`),
  KEY `fk_pedido_empleado` (`idEmpleado`),
  CONSTRAINT `fk_pedido_empleado` FOREIGN KEY (`idEmpleado`) REFERENCES `empleados` (`idEmpleado`),
  CONSTRAINT `fk_pedido_mesa` FOREIGN KEY (`idMesa`) REFERENCES `mesa` (`idMesa`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedidos`
--

LOCK TABLES `pedidos` WRITE;
/*!40000 ALTER TABLE `pedidos` DISABLE KEYS */;
INSERT INTO `pedidos` VALUES (10,'2026-05-09 14:33:24','Pendiente',1,2),(11,'2026-05-09 14:33:24','Listo',5,2);
/*!40000 ALTER TABLE `pedidos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `platillos`
--

DROP TABLE IF EXISTS `platillos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `platillos` (
  `idPlatillo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `precio` decimal(10,2) NOT NULL,
  `estado` varchar(50) DEFAULT 'Disponible',
  `idCategoria` int DEFAULT NULL,
  PRIMARY KEY (`idPlatillo`),
  KEY `idCategoria` (`idCategoria`),
  CONSTRAINT `platillos_ibfk_1` FOREIGN KEY (`idCategoria`) REFERENCES `categoriasmenu` (`idCategoria`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `platillos`
--

LOCK TABLES `platillos` WRITE;
/*!40000 ALTER TABLE `platillos` DISABLE KEYS */;
INSERT INTO `platillos` VALUES (1,'Pizza Queso',NULL,120.00,'Disponible',1),(2,'Pizza Pepperoni',NULL,140.00,'Disponible',1),(3,'Pizza Vegetariana',NULL,150.00,'Disponible',1),(4,'Pizza Hawaiana',NULL,145.00,'Disponible',1),(5,'Pizza de Carne',NULL,160.00,'Disponible',1),(6,'Pizza BBQ Pollo',NULL,155.00,'Disponible',1),(7,'Pizza Alfredo',NULL,150.00,'Disponible',1),(8,'Pizza Deluxe',NULL,170.00,'Disponible',1),(9,'Pizza 4 Quesos',NULL,165.00,'Disponible',1),(10,'Pizza de Camarones',NULL,180.00,'Disponible',1),(11,'Pizza Corazón',NULL,160.00,'Disponible',1),(12,'Pizza Estrella',NULL,160.00,'Disponible',1),(13,'Pizza Mac & Cheese',NULL,150.00,'Disponible',1),(14,'Pizza Pingüino Especial',NULL,170.00,'Disponible',1),(15,'Pizza de Postre',NULL,140.00,'Disponible',1),(16,'Pizza Taco',NULL,160.00,'Disponible',1),(17,'Pizza Volcán Picante',NULL,190.00,'Disponible',1),(18,'Café',NULL,35.00,'Disponible',2),(19,'Café Helado',NULL,45.00,'Disponible',2),(20,'Café con Leche',NULL,40.00,'Disponible',2),(21,'Capuchino',NULL,50.00,'Disponible',2),(22,'Granizado Azul',NULL,55.00,'Disponible',2),(23,'Jugo de Naranja',NULL,40.00,'Disponible',2),(24,'Malteada de Fresa',NULL,60.00,'Disponible',2),(25,'Batido de Chocolate',NULL,60.00,'Disponible',2),(26,'Soda de Lima',NULL,30.00,'Disponible',2),(27,'Brownie',NULL,45.00,'Disponible',3),(28,'Cheesecake de Fresa',NULL,65.00,'Disponible',3),(29,'Cupcake Chocolate',NULL,30.00,'Disponible',3),(30,'Cupcake Fresa',NULL,30.00,'Disponible',3),(31,'Pastel de Chocolate',NULL,60.00,'Disponible',3),(32,'Pay de Manzana',NULL,50.00,'Disponible',3),(33,'Pay de Zanahoria',NULL,50.00,'Disponible',3),(34,'Rol de Canela',NULL,40.00,'Disponible',3),(35,'Sundae',NULL,55.00,'Disponible',3),(36,'Alitas BBQ',NULL,90.00,'Disponible',4),(37,'Aros de Cebolla',NULL,60.00,'Disponible',4),(39,'Ensalada Fresca',NULL,70.00,'Disponible',4),(40,'Ensalada de Repollo',NULL,40.00,'Disponible',4),(41,'Nuggets de Pollo',NULL,75.00,'Disponible',4),(42,'Pan de Ajo',NULL,45.00,'Disponible',4),(43,'Pan de Ajo con Queso',NULL,55.00,'Disponible',4),(44,'Papas Fritas',NULL,50.00,'Disponible',4),(45,'Sopa del Día',NULL,60.00,'Disponible',4),(46,'Calzone Clásico',NULL,110.00,'Disponible',5),(47,'Pizza de Camarones',NULL,180.00,'Disponible',5),(48,'Pizza Corazón',NULL,160.00,'Disponible',5),(49,'Pizza Estrella',NULL,160.00,'Disponible',5),(50,'Pizza Mac & Cheese',NULL,150.00,'Disponible',5),(51,'Pizza Pingüino Especial',NULL,170.00,'Disponible',5),(52,'Pizza de Postre',NULL,140.00,'Disponible',5),(53,'Pizza Taco',NULL,160.00,'Disponible',5),(54,'Pizza Volcán Picante',NULL,190.00,'Disponible',5);
/*!40000 ALTER TABLE `platillos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservaciones`
--

DROP TABLE IF EXISTS `reservaciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservaciones` (
  `idReservacion` int NOT NULL AUTO_INCREMENT,
  `folioUnico` varchar(50) DEFAULT NULL,
  `id_cliente` varchar(10) NOT NULL,
  `idMesa` int DEFAULT NULL,
  `fecha` date NOT NULL,
  `hora` time NOT NULL,
  `num_personas` int NOT NULL,
  `estado` varchar(50) DEFAULT 'Confirmada',
  PRIMARY KEY (`idReservacion`),
  UNIQUE KEY `folioUnico` (`folioUnico`),
  KEY `idMesa` (`idMesa`),
  KEY `fk_reserva_cliente` (`id_cliente`),
  CONSTRAINT `fk_reserva_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`),
  CONSTRAINT `fk_reserva_mesa` FOREIGN KEY (`idMesa`) REFERENCES `mesa` (`idMesa`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservaciones`
--

LOCK TABLES `reservaciones` WRITE;
/*!40000 ALTER TABLE `reservaciones` DISABLE KEYS */;
INSERT INTO `reservaciones` VALUES (1,'dc3dd597-d5cc-4afd-90a4-57d16873f983','CP001',1,'2026-05-12','00:00:19',2,'Confirmada'),(2,'716614cf-ed9b-4eb1-809e-e75c812077b5','CP001',2,'2026-05-12','00:00:19',2,'Confirmada'),(3,'F5D6FBB5','CP001',2,'2026-05-13','00:30:32',2,'Confirmada'),(4,'A44844FB','CP001',4,'2026-05-13','00:30:37',2,'Confirmada'),(5,'9C606FDD','CP001',7,'2026-05-13','00:36:03',2,'Confirmada'),(6,'14b02c4e','CP001',6,'2026-05-22','18:00:00',5,'Confirmada'),(7,'9F3E5184','CP001',6,'2026-05-21','16:00:00',4,'Confirmada'),(8,'C7F480CB','CP001',3,'2026-05-21','16:00:00',4,'Confirmada'),(9,'F8DB9B9E','CP001',7,'2026-05-01','17:00:00',4,'Confirmada'),(10,'11E56267','CP001',6,'2026-05-23','17:00:00',5,'Confirmada');
/*!40000 ALTER TABLE `reservaciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rol`
--

DROP TABLE IF EXISTS `rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol` (
  `idRol` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`idRol`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rol`
--

LOCK TABLES `rol` WRITE;
/*!40000 ALTER TABLE `rol` DISABLE KEYS */;
INSERT INTO `rol` VALUES (1,'Gerente'),(2,'Mesero'),(3,'Chef'),(4,'Cajero'),(5,'Recepcionista');
/*!40000 ALTER TABLE `rol` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-13  7:59:30
