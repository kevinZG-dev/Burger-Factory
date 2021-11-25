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
-- Table structure for table `salesrecord_item`
--

DROP TABLE IF EXISTS `salesrecord_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `salesrecord_item` (
  `salesrecord_idSalesRecord` int NOT NULL,
  `item_idItem` int NOT NULL,
  `quantity` int NOT NULL,
  `type` varchar(45) NOT NULL,
  PRIMARY KEY (`salesrecord_idSalesRecord`,`item_idItem`),
  KEY `fk_salesrecord_has_item_item1_idx` (`item_idItem`),
  KEY `fk_salesrecord_has_item_salesrecord1_idx` (`salesrecord_idSalesRecord`),
  CONSTRAINT `fk_salesrecord_has_item_item1` FOREIGN KEY (`item_idItem`) REFERENCES `item` (`idItem`),
  CONSTRAINT `fk_salesrecord_has_item_salesrecord1` FOREIGN KEY (`salesrecord_idSalesRecord`) REFERENCES `salesrecord` (`idSalesRecord`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `salesrecord_item`
--

LOCK TABLES `salesrecord_item` WRITE;
/*!40000 ALTER TABLE `salesrecord_item` DISABLE KEYS */;
INSERT INTO `salesrecord_item` VALUES (1050,2,4,'burger'),(1050,4,2,'burger'),(1050,8,1,'burger'),(1050,9,1,'burger'),(1050,11,1,'burger'),(1051,2,8,'burger'),(1051,7,6,'burger'),(1051,8,9,'burger'),(1052,3,8,'topping'),(1052,6,7,'drink'),(1052,14,4,'topping'),(1052,15,4,'topping'),(1052,17,4,'drink'),(1052,19,6,'drink'),(1052,21,5,'dessert'),(1052,22,3,'dessert'),(1052,23,5,'dessert'),(1053,3,4,'topping'),(1053,7,3,'burger'),(1053,9,3,'burger'),(1053,13,3,'burger'),(1053,16,4,'drink'),(1053,21,4,'dessert'),(1054,9,1,'burger'),(1054,10,1,'burger'),(1054,11,2,'burger'),(1054,28,1,'burger'),(1055,4,1,'burger'),(1055,9,2,'burger'),(1055,11,2,'burger'),(1056,2,1,'burger'),(1056,9,1,'burger'),(1057,2,23,'burger'),(1057,8,1,'burger'),(1057,10,1,'burger'),(1058,2,33,'burger'),(1059,9,9,'burger'),(1059,10,4,'burger'),(1059,28,2,'burger'),(1059,32,2,'burger'),(1060,4,1,'burger'),(1060,9,2,'burger'),(1060,11,1,'burger'),(1061,9,4,'burger'),(1062,2,4,'burger'),(1062,3,5,'topping'),(1062,6,1,'drink'),(1062,10,1,'burger'),(1062,13,1,'burger'),(1062,17,1,'drink'),(1062,18,1,'drink'),(1062,20,1,'dessert'),(1062,21,1,'dessert'),(1062,24,1,'dessert'),(1063,4,2,'burger'),(1063,9,4,'burger'),(1063,11,2,'burger'),(1064,2,20,'burger'),(1065,32,20,'burger'),(1066,2,4,'burger'),(1066,3,5,'topping'),(1066,4,2,'burger'),(1066,6,1,'drink'),(1066,10,2,'burger'),(1066,17,1,'drink'),(1066,18,1,'drink'),(1066,20,1,'dessert'),(1066,21,1,'dessert'),(1067,2,4,'burger'),(1067,3,5,'topping'),(1067,4,2,'burger'),(1067,6,2,'drink'),(1067,7,1,'burger'),(1067,8,1,'burger'),(1067,16,1,'drink'),(1067,17,1,'drink'),(1067,20,1,'dessert'),(1067,21,1,'dessert'),(1067,24,1,'dessert'),(1068,8,1,'burger'),(1068,9,1,'burger'),(1068,10,2,'burger'),(1068,13,1,'burger'),(1068,28,1,'burger'),(1069,4,6,'burger'),(1070,7,9,'burger'),(1070,9,1,'burger'),(1070,11,2,'burger'),(1071,2,4,'burger'),(1071,3,5,'topping'),(1071,4,1,'burger'),(1071,6,2,'drink'),(1071,9,2,'burger'),(1071,10,1,'burger'),(1071,17,1,'drink'),(1071,18,1,'drink'),(1071,21,2,'dessert'),(1071,24,1,'dessert'),(1072,28,1,'burger'),(1073,11,1,'burger'),(1074,2,4,'burger'),(1074,3,5,'topping'),(1074,4,1,'burger'),(1074,6,2,'drink'),(1074,7,1,'burger'),(1074,10,1,'burger'),(1074,16,1,'drink'),(1074,18,1,'drink'),(1074,21,2,'dessert'),(1074,24,1,'dessert'),(1075,4,1,'burger'),(1075,8,1,'burger'),(1075,10,1,'burger'),(1075,11,1,'burger'),(1075,13,11,'burger'),(1076,2,4,'burger'),(1076,3,5,'topping'),(1076,6,3,'drink'),(1076,7,1,'burger'),(1076,8,1,'burger'),(1076,9,2,'burger'),(1076,17,2,'drink'),(1076,18,1,'drink'),(1076,20,2,'dessert'),(1076,21,1,'dessert'),(1077,4,8,'burger'),(1077,8,1,'burger'),(1077,10,1,'burger'),(1077,11,2,'burger'),(1077,14,9,'topping'),(1077,16,8,'drink'),(1077,22,9,'dessert'),(1077,28,1,'burger'),(1078,4,24,'burger'),(1078,15,22,'topping'),(1078,19,12,'drink'),(1078,20,10,'dessert'),(1079,2,4,'burger'),(1079,3,2,'topping'),(1080,2,4,'burger'),(1080,3,2,'topping'),(1081,2,4,'burger'),(1081,3,5,'topping'),(1081,4,1,'burger'),(1081,6,3,'drink'),(1081,8,1,'burger'),(1081,9,2,'burger'),(1081,13,1,'burger'),(1081,16,1,'drink'),(1081,17,2,'drink'),(1081,21,2,'dessert'),(1081,24,1,'dessert'),(1082,2,4,'burger'),(1082,3,5,'topping'),(1082,4,1,'burger'),(1082,6,3,'drink'),(1082,7,1,'burger'),(1082,9,2,'burger'),(1082,16,1,'drink'),(1082,18,1,'drink'),(1082,20,1,'dessert'),(1082,21,2,'dessert'),(1083,7,1,'burger'),(1083,9,1,'burger'),(1083,11,1,'burger'),(1083,16,1,'drink'),(1084,2,4,'burger'),(1084,3,5,'topping'),(1084,4,1,'burger'),(1084,6,2,'drink'),(1084,7,1,'burger'),(1084,9,2,'burger'),(1084,13,1,'burger'),(1084,17,1,'drink'),(1084,18,1,'drink'),(1084,21,1,'dessert'),(1084,24,1,'dessert'),(1085,4,1,'burger'),(1085,9,1,'burger'),(1085,11,1,'burger'),(1086,2,4,'burger'),(1086,3,5,'topping'),(1086,4,1,'burger'),(1086,6,2,'drink'),(1086,7,1,'burger'),(1086,9,2,'burger'),(1086,17,1,'drink'),(1086,24,1,'dessert'),(1087,8,1,'burger'),(1087,10,1,'burger'),(1087,14,7,'topping'),(1088,8,1,'burger'),(1088,10,1,'burger'),(1088,13,1,'burger'),(1089,3,5,'topping'),(1089,7,1,'burger'),(1089,16,1,'drink'),(1089,18,1,'drink'),(1089,21,1,'dessert'),(1089,23,1,'dessert'),(1089,28,1,'burger'),(1090,9,1,'burger'),(1090,11,1,'burger'),(1090,30,1,'burger'),(1091,8,6,'burger');
/*!40000 ALTER TABLE `salesrecord_item` ENABLE KEYS */;
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
