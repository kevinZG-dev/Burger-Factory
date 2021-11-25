-- MySQL dump 10.13  Distrib 8.0.22, for Win64 (x86_64)
--
-- Host: localhost    Database: javaprojectdb
-- ------------------------------------------------------
-- Server version	8.0.22

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

--
-- Table structure for table `item_cart`
--

DROP TABLE IF EXISTS `item_cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item_cart` (
  `Item_idItem` int NOT NULL,
  `Cart_idCart` int NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`Item_idItem`,`Cart_idCart`),
  KEY `fk_Item_has_Cart_Cart1_idx` (`Cart_idCart`),
  KEY `fk_Item_has_Cart_Item_idx` (`Item_idItem`),
  CONSTRAINT `fk_Item_has_Cart_Cart1` FOREIGN KEY (`Cart_idCart`) REFERENCES `cart` (`idCart`),
  CONSTRAINT `fk_Item_has_Cart_Item` FOREIGN KEY (`Item_idItem`) REFERENCES `item` (`idItem`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_cart`
--

LOCK TABLES `item_cart` WRITE;
/*!40000 ALTER TABLE `item_cart` DISABLE KEYS */;
INSERT INTO `item_cart` VALUES (2,1,4),(3,1,5),(4,1,1),(6,1,1),(7,2,1),(8,1,5),(9,1,2),(10,2,1),(11,2,1),(16,1,2),(17,1,1),(21,1,3),(24,1,1),(28,2,1);
/*!40000 ALTER TABLE `item_cart` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-11-25 12:34:51
