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
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `email` varchar(100) NOT NULL,
  `fname` varchar(100) NOT NULL,
  `lname` varchar(100) NOT NULL,
  `adress` varchar(255) NOT NULL,
  `zipcode` int NOT NULL,
  `city` varchar(255) NOT NULL,
  `telnb` int NOT NULL,
  `password` varchar(100) NOT NULL,
  `Cart_idCart` int NOT NULL,
  PRIMARY KEY (`email`),
  KEY `fk_Customer_Cart1_idx` (`Cart_idCart`),
  CONSTRAINT `fk_Customer_Cart1` FOREIGN KEY (`Cart_idCart`) REFERENCES `cart` (`idCart`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES ('damien.mehaute.pro@gmail.com','Damien','Mehaute','79 avenue de la pie',94100,'Saint-Maur',698523214,'valide',6),('jcvd@gmail.com','Jean-Claude','Van Damne','97 boulevard Noris',97412,'Aulnay',621997563,'jaimelinfo',1),('jean-pierre-segado@ece.fr','jp','segado','47 avenue des champs',75015,'paris',620123212,'123',9),('kevin.zheng@edu.ece.fr','Kevin','Zheng','23 rue de la pioche',94210,'saint maur',620060757,'Password',2),('maxime.attal.gabriel@gmail.com','Maxime','Attal','87 avenue des barres',75015,'Paris',630178963,'youtube',4),('ronald@mc.com','Ronald','McDonald\'s','87 rue du boulanger',94190,'Saint mandé',620300477,'imagine',5),('zheng.kevin.ts@gmail.com','Kévin','Zheng','23 rue Foch',94100,'Saint-Maur',620060757,'123',7),('zhengkevin@gmail.fr','Nolan','Chris','78 avenue de champigny',94000,'Créteil',630123365,'reve',3),('zk94210@hotmail.fr','Pierre','Dupont','78 rue des Lys',75015,'Paris',610040587,'motdepasse',8);
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
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
