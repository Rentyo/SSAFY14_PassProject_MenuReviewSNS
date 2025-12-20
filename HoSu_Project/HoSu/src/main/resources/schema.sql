-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: hosu_db
-- ------------------------------------------------------
-- Server version	8.0.42

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
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_no` bigint NOT NULL AUTO_INCREMENT,
  `user_email` varchar(100) NOT NULL,
  `user_pw` varchar(255) NOT NULL,
  `nickname` varchar(50) NOT NULL,
  `user_img` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `gender` varchar(10) DEFAULT NULL,
  `role` varchar(20) DEFAULT 'USER',
  `phone_number` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`user_no`),
  UNIQUE KEY `user_email` (`user_email`),
  UNIQUE KEY `nickname` (`nickname`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'hong@example.com','password123','홍길동',NULL,'2025-11-28 06:48:49','2025-11-28 06:48:49','MALE','USER','010-1234-5678'),(2,'kim@example.com','password456','김철수',NULL,'2025-11-28 06:48:49','2025-11-28 06:48:49','MALE','USER','010-2345-6789'),(3,'lee@example.com','password789','이영희',NULL,'2025-11-28 06:48:49','2025-11-28 06:48:49','FEMALE','USER','010-3456-7890'),(4,'admin@example.com','admin1234','관리자',NULL,'2025-11-28 06:48:49','2025-11-28 06:48:49','OTHER','ADMIN','010-9999-9999'),(5,'park@example.com','password111','박민수',NULL,'2025-11-28 06:48:49','2025-11-28 06:48:49','MALE','USER','010-4567-8901');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-28 17:40:25

--
-- Table structure for table `sns_comment`
--

DROP TABLE IF EXISTS `sns_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sns_comment` (
  `comment_id` bigint NOT NULL AUTO_INCREMENT,
  `board_id` bigint NOT NULL,
  `user_no` bigint NOT NULL,
  `parent_id` bigint DEFAULT NULL,
  `content` varchar(500) NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`comment_id`),
  KEY `fk_comment_board` (`board_id`),
  KEY `fk_comment_user` (`user_no`),
  KEY `fk_comment_parent` (`parent_id`),
  CONSTRAINT `fk_comment_board` FOREIGN KEY (`board_id`) REFERENCES `sns_board` (`board_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `sns_comment` (`comment_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_no`) REFERENCES `user` (`user_no`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sns_comment`
--

LOCK TABLES `sns_comment` WRITE;
/*!40000 ALTER TABLE `sns_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `sns_comment` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-28 17:40:25

--
-- Table structure for table `sns_like`
--

DROP TABLE IF EXISTS `sns_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sns_like` (
  `like_id` bigint NOT NULL AUTO_INCREMENT,
  `board_id` bigint NOT NULL,
  `user_no` bigint NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`like_id`),
  UNIQUE KEY `unique_like` (`board_id`,`user_no`),
  KEY `fk_like_user` (`user_no`),
  CONSTRAINT `fk_like_board` FOREIGN KEY (`board_id`) REFERENCES `sns_board` (`board_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_like_user` FOREIGN KEY (`user_no`) REFERENCES `user` (`user_no`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sns_like`
--

LOCK TABLES `sns_like` WRITE;
/*!40000 ALTER TABLE `sns_like` DISABLE KEYS */;
/*!40000 ALTER TABLE `sns_like` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-28 17:40:24

--
-- Table structure for table `sns_board`
--

DROP TABLE IF EXISTS `sns_board`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sns_board` (
  `board_id` bigint NOT NULL AUTO_INCREMENT,
  `user_no` bigint NOT NULL,
  `content` text,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`board_id`),
  KEY `fk_board_user` (`user_no`),
  CONSTRAINT `fk_board_user` FOREIGN KEY (`user_no`) REFERENCES `user` (`user_no`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sns_board`
--

LOCK TABLES `sns_board` WRITE;
/*!40000 ALTER TABLE `sns_board` DISABLE KEYS */;
/*!40000 ALTER TABLE `sns_board` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-28 17:40:25

--
-- Table structure for table `sns_board_image`
--

DROP TABLE IF EXISTS `sns_board_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sns_board_image` (
  `image_id` bigint NOT NULL AUTO_INCREMENT,
  `board_id` bigint NOT NULL,
  `image_url` varchar(500) NOT NULL,
  `image_order` int DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`image_id`),
  KEY `fk_image_board` (`board_id`),
  CONSTRAINT `fk_image_board` FOREIGN KEY (`board_id`) REFERENCES `sns_board` (`board_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sns_board_image`
--

LOCK TABLES `sns_board_image` WRITE;
/*!40000 ALTER TABLE `sns_board_image` DISABLE KEYS */;
/*!40000 ALTER TABLE `sns_board_image` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-28 17:40:25

--
-- Table structure for table `sido`
--

DROP TABLE IF EXISTS `sido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sido` (
  `sido_no` varchar(4) NOT NULL,
  `sido_name` varchar(50) NOT NULL,
  PRIMARY KEY (`sido_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sido`
--

LOCK TABLES `sido` WRITE;
/*!40000 ALTER TABLE `sido` DISABLE KEYS */;
INSERT INTO `sido` VALUES ('1','서울'),('2','인천'),('3','대전'),('31','경기도'),('32','강원특별자치도'),('33','충청북도'),('34','충청남도'),('35','경상북도'),('36','경상남도'),('37','전북특별자치도'),('38','전라남도'),('39','제주특별자치도'),('4','대구'),('5','광주'),('6','부산'),('7','울산'),('8','세종특별자치시');
/*!40000 ALTER TABLE `sido` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-28 17:40:24

--
-- Table structure for table `restaurants`
--

DROP TABLE IF EXISTS `restaurants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `restaurants` (
  `restaurant_id` bigint NOT NULL AUTO_INCREMENT,
  `restaurant_name` varchar(255) NOT NULL,
  `category` enum('KOREAN','CHINESE','JAPANESE','WESTERN','SNACK','CHICKEN','MEAT','SEAFOOD','CAFE','PUB','BAKERY','FASTFOOD','ASIAN','CURRY') NOT NULL COMMENT '''KOREAN'', ''CHINESE'', ''JAPANESE'', ''WESTERN'', ''SNACK'', ''CHICKEN'', ''MEAT'', ''SEAFOOD'', ''CAFE'', ''PUB'', ''BAKERY'', ''FASTFOOD'', ''ASIAN'', ''CURRY''\n',
  `address` varchar(500) NOT NULL,
  `sido_no` varchar(4) NOT NULL COMMENT '시',
  `gungu_code` varchar(5) NOT NULL COMMENT '구',
  `lat` double NOT NULL,
  `lng` double NOT NULL,
  `phone` varchar(20) NOT NULL,
  `main_image` blob,
  `avg_rating` decimal(3,1) DEFAULT NULL,
  `total_contents` int DEFAULT '0',
  `view_count` int DEFAULT '0',
  PRIMARY KEY (`restaurant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `restaurants`
--

LOCK TABLES `restaurants` WRITE;
/*!40000 ALTER TABLE `restaurants` DISABLE KEYS */;
INSERT INTO `restaurants` VALUES (1,'홍콩반점0410','CHINESE','서울 서초구 서초대로77길 45','1','15',37.12345,127.98765,'02-123-4567',_binary 'null',4.3,0,0),(2,'부산어묵전문점 오뎅바','KOREAN','서울 강남구 테헤란로 212','1','101',37.50112,127.03223,'02-987-6543',_binary 'null',4.5,12,52),(3,'스시미도리','JAPANESE','서울 송파구 백제고분로 359','1','118',37.51001,127.12241,'02-3412-9988',_binary 'null',4.8,21,120),(4,'이태리파스타하우스','WESTERN','서울 마포구 독막로 152','1','113',37.54871,126.91895,'02-332-8833',_binary 'null',4.1,5,48),(5,'스타벅스 홍대입구역점','CAFE','서울 마포구 양화로 165','1','113',37.55778,126.92335,'02-123-0001',_binary 'null',4.6,100,999),(6,'노브랜드버거 강남역점','FASTFOOD','서울 강남구 강남대로 396','1','101',37.49795,127.02762,'02-555-3331',_binary 'null',4.0,33,201),(7,'봉추찜닭 대학로점','KOREAN','서울 종로구 대학로 129','1','123',37.58171,127.00298,'02-741-8855',_binary 'null',4.2,8,77),(8,'백종원 한신포차 신림점','KOREAN','서울 관악구 신림로 340','1','105',37.48476,126.92915,'02-878-2333',_binary 'null',3.9,13,101),(9,'백종원 한신포차 신림점','KOREAN','서울 관악구 신림로 340','1','105',37.48476,126.92915,'02-878-2333',_binary 'null',3.9,13,101),(10,'빽다방 신촌점','CAFE','서울 서대문구 연세로 8-1','1','114',37.55918,126.93691,'02-392-8585',_binary 'null',4.0,12,174),(11,'서울맛집1','KOREAN','서울 강남구 도로 1','1','1',37.501001,127.001001,'02-0001-0001',_binary 'null',4.2,5,21),(12,'서울맛집2','CHINESE','서울 강동구 도로 2','1','2',37.502002,127.002002,'02-0002-0002',_binary 'null',4.1,2,10),(13,'서울맛집3','JAPANESE','서울 강북구 도로 3','1','3',37.503003,127.003003,'02-0003-0003',_binary 'null',4.7,3,30),(14,'서울맛집4','WESTERN','서울 강서구 도로 4','1','4',37.504004,127.004004,'02-0004-0004',_binary 'null',3.9,1,12),(15,'서울맛집5','SNACK','서울 관악구 도로 5','1','5',37.505005,127.005005,'02-0005-0005',_binary 'null',4.3,10,44),(16,'서울맛집6','CHICKEN','서울 광진구 도로 6','1','6',37.506006,127.006006,'02-0006-0006',_binary 'null',4.0,0,7),(17,'서울맛집7','MEAT','서울 구로구 도로 7','1','7',37.507007,127.007007,'02-0007-0007',_binary 'null',4.6,12,51),(18,'서울맛집8','SEAFOOD','서울 금천구 도로 8','1','8',37.508008,127.008008,'02-0008-0008',_binary 'null',4.1,1,13),(19,'서울맛집9','CAFE','서울 노원구 도로 9','1','9',37.509009,127.009009,'02-0009-0009',_binary 'null',4.9,9,120),(20,'서울맛집10','PUB','서울 도봉구 도로 10','1','10',37.51001,127.01001,'02-0010-0010',_binary 'null',3.7,4,66),(21,'서울맛집11','BAKERY','서울 동대문구 도로 11','1','11',37.511011,127.011011,'02-0011-0011',_binary 'null',4.3,0,44),(22,'서울맛집12','FASTFOOD','서울 동작구 도로 12','1','12',37.512012,127.012012,'02-0012-0012',_binary 'null',4.6,3,25),(23,'서울맛집13','ASIAN','서울 마포구 도로 13','1','13',37.513013,127.013013,'02-0013-0013',_binary 'null',4.5,6,78),(24,'서울맛집14','CURRY','서울 서대문구 도로 14','1','14',37.514014,127.014014,'02-0014-0014',_binary 'null',4.1,1,40),(25,'서울맛집15','KOREAN','서울 서초구 도로 15','1','15',37.515015,127.015015,'02-0015-0015',_binary 'null',4.8,5,111),(26,'서울맛집16','CHINESE','서울 성동구 도로 16','1','16',37.516016,127.016016,'02-0016-0016',_binary 'null',4.0,2,39),(27,'서울맛집17','JAPANESE','서울 성북구 도로 17','1','17',37.517017,127.017017,'02-0017-0017',_binary 'null',4.3,0,28),(28,'서울맛집18','WESTERN','서울 송파구 도로 18','1','18',37.518018,127.018018,'02-0018-0018',_binary 'null',4.1,14,41),(29,'서울맛집19','SNACK','서울 양천구 도로 19','1','19',37.519019,127.019019,'02-0019-0019',_binary 'null',4.4,12,70),(30,'서울맛집20','CHICKEN','서울 영등포구 도로 20','1','20',37.52002,127.02002,'02-0020-0020',_binary 'null',4.2,4,37),(31,'서울맛집21','MEAT','서울 용산구 도로 21','1','21',37.521021,127.021021,'02-0021-0021',_binary 'null',4.9,7,133),(32,'서울맛집22','SEAFOOD','서울 은평구 도로 22','1','22',37.522022,127.022022,'02-0022-0022',_binary 'null',3.8,0,26),(33,'서울맛집23','CAFE','서울 종로구 도로 23','1','23',37.523023,127.023023,'02-0023-0023',_binary 'null',4.6,6,90),(34,'서울맛집24','PUB','서울 중구 도로 24','1','24',37.524024,127.024024,'02-0024-0024',_binary 'null',4.1,1,56),(35,'서울맛집25','BAKERY','서울 중랑구 도로 25','1','25',37.525025,127.025025,'02-0025-0025',_binary 'null',4.3,2,18),(36,'서울맛집26','FASTFOOD','서울 강남구 도로 26','1','1',37.526026,127.026026,'02-0026-0026',_binary 'null',4.0,6,41),(37,'서울맛집27','ASIAN','서울 강동구 도로 27','1','2',37.527027,127.027027,'02-0027-0027',_binary 'null',4.7,3,77),(38,'서울맛집28','CURRY','서울 강북구 도로 28','1','3',37.528028,127.028028,'02-0028-0028',_binary 'null',3.9,0,31),(39,'서울맛집29','KOREAN','서울 강서구 도로 29','1','4',37.529029,127.029029,'02-0029-0029',_binary 'null',4.1,8,61),(40,'서울맛집30','CHINESE','서울 관악구 도로 30','1','5',37.53003,127.03003,'02-0030-0030',_binary 'null',4.3,1,22),(41,'서울맛집31','JAPANESE','서울 광진구 도로 31','1','6',37.531031,127.031031,'02-0031-0031',_binary 'null',4.9,10,120),(42,'서울맛집32','WESTERN','서울 구로구 도로 32','1','7',37.532032,127.032032,'02-0032-0032',_binary 'null',3.8,3,49),(43,'서울맛집33','SNACK','서울 금천구 도로 33','1','8',37.533033,127.033033,'02-0033-0033',_binary 'null',4.4,4,55),(44,'서울맛집34','CHICKEN','서울 노원구 도로 34','1','9',37.534034,127.034034,'02-0034-0034',_binary 'null',4.1,2,33),(45,'서울맛집35','MEAT','서울 도봉구 도로 35','1','10',37.535035,127.035035,'02-0035-0035',_binary 'null',4.7,3,100),(46,'서울맛집36','SEAFOOD','서울 동대문구 도로 36','1','11',37.536036,127.036036,'02-0036-0036',_binary 'null',3.9,0,22),(47,'서울맛집37','CAFE','서울 동작구 도로 37','1','12',37.537037,127.037037,'02-0037-0037',_binary 'null',4.3,3,51),(48,'서울맛집38','PUB','서울 마포구 도로 38','1','13',37.538038,127.038038,'02-0038-0038',_binary 'null',4.6,6,70),(49,'서울맛집39','BAKERY','서울 서대문구 도로 39','1','14',37.539039,127.039039,'02-0039-0039',_binary 'null',4.1,1,27),(50,'서울맛집40','FASTFOOD','서울 서초구 도로 40','1','15',37.54004,127.04004,'02-0040-0040',_binary 'null',4.5,4,88),(51,'서울맛집41','ASIAN','서울 성동구 도로 41','1','16',37.541041,127.041041,'02-0041-0041',_binary 'null',4.0,0,34),(52,'서울맛집42','CURRY','서울 성북구 도로 42','1','17',37.542042,127.042042,'02-0042-0042',_binary 'null',4.2,6,57),(53,'서울맛집43','KOREAN','서울 송파구 도로 43','1','18',37.543043,127.043043,'02-0043-0043',_binary 'null',4.7,2,103),(54,'서울맛집44','CHINESE','서울 양천구 도로 44','1','19',37.544044,127.044044,'02-0044-0044',_binary 'null',4.1,3,40),(55,'서울맛집45','JAPANESE','서울 영등포구 도로 45','1','20',37.545045,127.045045,'02-0045-0045',_binary 'null',4.8,7,90),(56,'서울맛집46','WESTERN','서울 용산구 도로 46','1','21',37.546046,127.046046,'02-0046-0046',_binary 'null',4.1,1,61),(57,'서울맛집47','SNACK','서울 은평구 도로 47','1','22',37.547047,127.047047,'02-0047-0047',_binary 'null',4.0,0,18),(58,'서울맛집48','CHICKEN','서울 종로구 도로 48','1','23',37.548048,127.048048,'02-0048-0048',_binary 'null',4.5,3,77),(59,'서울맛집49','MEAT','서울 중구 도로 49','1','24',37.549049,127.049049,'02-0049-0049',_binary 'null',4.1,1,43),(60,'서울맛집50','SEAFOOD','서울 중랑구 도로 50','1','25',37.55005,127.05005,'02-0050-0050',_binary 'null',4.3,2,21),(61,'서울맛집51','CAFE','서울 강남구 도로 51','1','1',37.551051,127.051051,'02-0051-0051',_binary 'null',4.2,4,33),(62,'서울맛집52','PUB','서울 강동구 도로 52','1','2',37.552052,127.052052,'02-0052-0052',_binary 'null',4.0,1,28),(63,'서울맛집53','BAKERY','서울 강북구 도로 53','1','3',37.553053,127.053053,'02-0053-0053',_binary 'null',4.4,3,40),(64,'서울맛집54','FASTFOOD','서울 강서구 도로 54','1','4',37.554054,127.054054,'02-0054-0054',_binary 'null',4.1,2,56),(65,'서울맛집55','ASIAN','서울 관악구 도로 55','1','5',37.555055,127.055055,'02-0055-0055',_binary 'null',4.3,4,71),(66,'서울맛집56','CURRY','서울 광진구 도로 56','1','6',37.556056,127.056056,'02-0056-0056',_binary 'null',4.0,1,44),(67,'서울맛집57','KOREAN','서울 구로구 도로 57','1','7',37.557057,127.057057,'02-0057-0057',_binary 'null',4.7,6,103),(68,'서울맛집58','CHINESE','서울 금천구 도로 58','1','8',37.558058,127.058058,'02-0058-0058',_binary 'null',4.2,3,50),(69,'서울맛집59','JAPANESE','서울 노원구 도로 59','1','9',37.559059,127.059059,'02-0059-0059',_binary 'null',4.9,12,130),(70,'서울맛집60','WESTERN','서울 도봉구 도로 60','1','10',37.56006,127.06006,'02-0060-0060',_binary 'null',3.9,0,29),(71,'서울맛집61','SNACK','서울 동대문구 도로 61','1','11',37.561061,127.061061,'02-0061-0061',_binary 'null',4.3,3,51),(72,'서울맛집62','CHICKEN','서울 동작구 도로 62','1','12',37.562062,127.062062,'02-0062-0062',_binary 'null',4.6,5,78),(73,'서울맛집63','MEAT','서울 마포구 도로 63','1','13',37.563063,127.063063,'02-0063-0063',_binary 'null',4.7,4,90),(74,'서울맛집64','SEAFOOD','서울 서대문구 도로 64','1','14',37.564064,127.064064,'02-0064-0064',_binary 'null',4.1,1,40),(75,'서울맛집65','CAFE','서울 서초구 도로 65','1','15',37.565065,127.065065,'02-0065-0065',_binary 'null',4.5,9,110),(76,'서울맛집66','PUB','서울 성동구 도로 66','1','16',37.566066,127.066066,'02-0066-0066',_binary 'null',4.2,3,46),(77,'서울맛집67','BAKERY','서울 성북구 도로 67','1','17',37.567067,127.067067,'02-0067-0067',_binary 'null',4.0,0,18),(78,'서울맛집68','FASTFOOD','서울 송파구 도로 68','1','18',37.568068,127.068068,'02-0068-0068',_binary 'null',4.3,4,77),(79,'서울맛집69','ASIAN','서울 양천구 도로 69','1','19',37.569069,127.069069,'02-0069-0069',_binary 'null',4.1,2,39),(80,'서울맛집70','CURRY','서울 영등포구 도로 70','1','20',37.57007,127.07007,'02-0070-0070',_binary 'null',4.6,6,99),(81,'서울맛집71','KOREAN','서울 용산구 도로 71','1','21',37.571071,127.071071,'02-0071-0071',_binary 'null',4.9,5,133),(82,'서울맛집72','CHINESE','서울 은평구 도로 72','1','22',37.572072,127.072072,'02-0072-0072',_binary 'null',3.9,0,30),(83,'서울맛집73','JAPANESE','서울 종로구 도로 73','1','23',37.573073,127.073073,'02-0073-0073',_binary 'null',4.8,9,127),(84,'서울맛집74','WESTERN','서울 중구 도로 74','1','24',37.574074,127.074074,'02-0074-0074',_binary 'null',4.1,1,55),(85,'서울맛집75','SNACK','서울 중랑구 도로 75','1','25',37.575075,127.075075,'02-0075-0075',_binary 'null',4.3,4,80),(86,'서울맛집76','CHICKEN','서울 강남구 도로 76','1','1',37.576076,127.076076,'02-0076-0076',_binary 'null',4.1,2,44),(87,'서울맛집77','MEAT','서울 강동구 도로 77','1','2',37.577077,127.077077,'02-0077-0077',_binary 'null',4.6,6,98),(88,'서울맛집78','SEAFOOD','서울 강북구 도로 78','1','3',37.578078,127.078078,'02-0078-0078',_binary 'null',4.2,1,37),(89,'서울맛집79','CAFE','서울 강서구 도로 79','1','4',37.579079,127.079079,'02-0079-0079',_binary 'null',4.5,5,80),(90,'서울맛집80','PUB','서울 관악구 도로 80','1','5',37.58008,127.08008,'02-0080-0080',_binary 'null',3.9,2,29),(91,'서울맛집81','BAKERY','서울 광진구 도로 81','1','6',37.581081,127.081081,'02-0081-0081',_binary 'null',4.3,4,60),(92,'서울맛집82','FASTFOOD','서울 구로구 도로 82','1','7',37.582082,127.082082,'02-0082-0082',_binary 'null',4.0,1,24),(93,'서울맛집83','ASIAN','서울 금천구 도로 83','1','8',37.583083,127.083083,'02-0083-0083',_binary 'null',4.4,5,71),(94,'서울맛집84','CURRY','서울 노원구 도로 84','1','9',37.584084,127.084084,'02-0084-0084',_binary 'null',4.7,2,104),(95,'서울맛집85','KOREAN','서울 도봉구 도로 85','1','10',37.585085,127.085085,'02-0085-0085',_binary 'null',4.1,0,33),(96,'서울맛집86','CHINESE','서울 동대문구 도로 86','1','11',37.586086,127.086086,'02-0086-0086',_binary 'null',4.3,1,41),(97,'서울맛집87','JAPANESE','서울 동작구 도로 87','1','12',37.587087,127.087087,'02-0087-0087',_binary 'null',4.8,8,118),(98,'서울맛집88','WESTERN','서울 마포구 도로 88','1','13',37.588088,127.088088,'02-0088-0088',_binary 'null',4.0,2,39),(99,'서울맛집89','SNACK','서울 서대문구 도로 89','1','14',37.589089,127.089089,'02-0089-0089',_binary 'null',4.5,6,77),(100,'서울맛집90','CHICKEN','서울 서초구 도로 90','1','15',37.59009,127.09009,'02-0090-0090',_binary 'null',4.1,3,53),(101,'서울맛집91','MEAT','서울 성동구 도로 91','1','16',37.591091,127.091091,'02-0091-0091',_binary 'null',4.7,6,104),(102,'서울맛집92','SEAFOOD','서울 성북구 도로 92','1','17',37.592092,127.092092,'02-0092-0092',_binary 'null',4.1,2,47),(103,'서울맛집93','CAFE','서울 송파구 도로 93','1','18',37.593093,127.093093,'02-0093-0093',_binary 'null',4.6,5,88),(104,'서울맛집94','PUB','서울 양천구 도로 94','1','19',37.594094,127.094094,'02-0094-0094',_binary 'null',4.0,1,33),(105,'서울맛집95','BAKERY','서울 영등포구 도로 95','1','20',37.595095,127.095095,'02-0095-0095',_binary 'null',4.3,4,71),(106,'서울맛집96','FASTFOOD','서울 용산구 도로 96','1','21',37.596096,127.096096,'02-0096-0096',_binary 'null',4.1,0,25),(107,'서울맛집97','ASIAN','서울 은평구 도로 97','1','22',37.597097,127.097097,'02-0097-0097',_binary 'null',4.4,3,57),(108,'서울맛집98','CURRY','서울 종로구 도로 98','1','23',37.598098,127.098098,'02-0098-0098',_binary 'null',4.7,8,112),(109,'서울맛집99','KOREAN','서울 중구 도로 99','1','24',37.599099,127.099099,'02-0099-0099',_binary 'null',4.3,1,48),(110,'서울맛집100','CHINESE','서울 중랑구 도로 100','1','25',37.6001,127.1001,'02-0100-0100',_binary 'null',4.2,2,44);
/*!40000 ALTER TABLE `restaurants` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-28 17:40:25

--
-- Table structure for table `menu`
--

DROP TABLE IF EXISTS `menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT,
  `restaurant_id` bigint NOT NULL,
  `menu_name` varchar(255) NOT NULL,
  `price` int NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`menu_id`),
  KEY `fk_menu_restaurant` (`restaurant_id`),
  CONSTRAINT `fk_menu_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`restaurant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=512 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu`
--

LOCK TABLES `menu` WRITE;
/*!40000 ALTER TABLE `menu` DISABLE KEYS */;
INSERT INTO `menu` VALUES (1,1,'유산슬',15000,NULL),(2,1,'탕수육',12000,NULL),(3,1,'짬뽕',8500,NULL),(4,1,'짜장면',8000,NULL),(5,2,'비빔밥',10000,NULL),(6,2,'불고기',11000,NULL),(7,2,'제육덮밥',9500,NULL),(8,2,'김치찌개',9000,NULL),(9,3,'우동',9000,NULL),(10,3,'연어초밥',13000,NULL),(11,3,'가츠동',10000,NULL),(12,3,'돈코츠라멘',11000,NULL),(13,4,'스테이크',20000,NULL),(14,4,'리조또',13500,NULL),(15,4,'크림파스타',13000,NULL),(16,4,'페퍼로니피자',14000,NULL),(17,5,'바닐라라떼',5800,NULL),(18,5,'카페모카',5500,NULL),(19,5,'카페라떼',5000,NULL),(20,5,'아메리카노',4500,NULL),(21,6,'콜라',2000,NULL),(22,6,'치킨너겟',5000,NULL),(23,6,'후렌치프라이',4000,NULL),(24,6,'치즈버거',6500,NULL),(25,7,'비빔밥',10000,NULL),(26,7,'불고기',11000,NULL),(27,7,'제육덮밥',9500,NULL),(28,7,'김치찌개',9000,NULL),(29,8,'비빔밥',10000,NULL),(30,8,'불고기',11000,NULL),(31,8,'제육덮밥',9500,NULL),(32,8,'김치찌개',9000,NULL),(33,9,'비빔밥',10000,NULL),(34,9,'불고기',11000,NULL),(35,9,'제육덮밥',9500,NULL),(36,9,'김치찌개',9000,NULL),(37,10,'바닐라라떼',5800,NULL),(38,10,'카페모카',5500,NULL),(39,10,'카페라떼',5000,NULL),(40,10,'아메리카노',4500,NULL),(41,11,'비빔밥',10000,NULL),(42,11,'불고기',11000,NULL),(43,11,'제육덮밥',9500,NULL),(44,11,'김치찌개',9000,NULL),(45,12,'유산슬',15000,NULL),(46,12,'탕수육',12000,NULL),(47,12,'짬뽕',8500,NULL),(48,12,'짜장면',8000,NULL),(49,13,'우동',9000,NULL),(50,13,'연어초밥',13000,NULL),(51,13,'가츠동',10000,NULL),(52,13,'돈코츠라멘',11000,NULL),(53,14,'스테이크',20000,NULL),(54,14,'리조또',13500,NULL),(55,14,'크림파스타',13000,NULL),(56,14,'페퍼로니피자',14000,NULL),(57,15,'라면',4500,NULL),(58,15,'튀김',4000,NULL),(59,15,'순대',4000,NULL),(60,15,'떡볶이',5000,NULL),(61,16,'치즈볼',5000,NULL),(62,16,'간장치킨',18000,NULL),(63,16,'양념치킨',18000,NULL),(64,16,'후라이드치킨',17000,NULL),(65,17,'곱창',20000,NULL),(66,17,'돼지갈비',16000,NULL),(67,17,'소고기구이',22000,NULL),(68,17,'삼겹살',15000,NULL),(69,18,'해물찜',19000,NULL),(70,18,'해물파스타',15000,NULL),(71,18,'광어사시미',18000,NULL),(72,18,'회덮밥',11000,NULL),(73,19,'바닐라라떼',5800,NULL),(74,19,'카페모카',5500,NULL),(75,19,'카페라떼',5000,NULL),(76,19,'아메리카노',4500,NULL),(77,20,'나초',12000,NULL),(78,20,'소시지플래터',17000,NULL),(79,20,'치킨윙',16000,NULL),(80,20,'감자튀김',9000,NULL),(81,21,'피자빵',4500,NULL),(82,21,'소금빵',3000,NULL),(83,21,'바게트',4000,NULL),(84,21,'크루아상',3500,NULL),(85,22,'콜라',2000,NULL),(86,22,'치킨너겟',5000,NULL),(87,22,'후렌치프라이',4000,NULL),(88,22,'치즈버거',6500,NULL),(89,23,'마라샹궈',14000,NULL),(90,23,'월남쌈',13000,NULL),(91,23,'나시고랭',11000,NULL),(92,23,'팟타이',12000,NULL),(93,24,'카레우동',9000,NULL),(94,24,'스파이스카레',10500,NULL),(95,24,'포크카레',9500,NULL),(96,24,'치킨카레',10000,NULL),(97,25,'비빔밥',10000,NULL),(98,25,'불고기',11000,NULL),(99,25,'제육덮밥',9500,NULL),(100,25,'김치찌개',9000,NULL),(101,26,'유산슬',15000,NULL),(102,26,'탕수육',12000,NULL),(103,26,'짬뽕',8500,NULL),(104,26,'짜장면',8000,NULL),(105,27,'우동',9000,NULL),(106,27,'연어초밥',13000,NULL),(107,27,'가츠동',10000,NULL),(108,27,'돈코츠라멘',11000,NULL),(109,28,'스테이크',20000,NULL),(110,28,'리조또',13500,NULL),(111,28,'크림파스타',13000,NULL),(112,28,'페퍼로니피자',14000,NULL),(113,29,'라면',4500,NULL),(114,29,'튀김',4000,NULL),(115,29,'순대',4000,NULL),(116,29,'떡볶이',5000,NULL),(117,30,'치즈볼',5000,NULL),(118,30,'간장치킨',18000,NULL),(119,30,'양념치킨',18000,NULL),(120,30,'후라이드치킨',17000,NULL),(121,31,'곱창',20000,NULL),(122,31,'돼지갈비',16000,NULL),(123,31,'소고기구이',22000,NULL),(124,31,'삼겹살',15000,NULL),(125,32,'해물찜',19000,NULL),(126,32,'해물파스타',15000,NULL),(127,32,'광어사시미',18000,NULL),(128,32,'회덮밥',11000,NULL),(129,33,'바닐라라떼',5800,NULL),(130,33,'카페모카',5500,NULL),(131,33,'카페라떼',5000,NULL),(132,33,'아메리카노',4500,NULL),(133,34,'나초',12000,NULL),(134,34,'소시지플래터',17000,NULL),(135,34,'치킨윙',16000,NULL),(136,34,'감자튀김',9000,NULL),(137,35,'피자빵',4500,NULL),(138,35,'소금빵',3000,NULL),(139,35,'바게트',4000,NULL),(140,35,'크루아상',3500,NULL),(141,36,'콜라',2000,NULL),(142,36,'치킨너겟',5000,NULL),(143,36,'후렌치프라이',4000,NULL),(144,36,'치즈버거',6500,NULL),(145,37,'마라샹궈',14000,NULL),(146,37,'월남쌈',13000,NULL),(147,37,'나시고랭',11000,NULL),(148,37,'팟타이',12000,NULL),(149,38,'카레우동',9000,NULL),(150,38,'스파이스카레',10500,NULL),(151,38,'포크카레',9500,NULL),(152,38,'치킨카레',10000,NULL),(153,39,'비빔밥',10000,NULL),(154,39,'불고기',11000,NULL),(155,39,'제육덮밥',9500,NULL),(156,39,'김치찌개',9000,NULL),(157,40,'유산슬',15000,NULL),(158,40,'탕수육',12000,NULL),(159,40,'짬뽕',8500,NULL),(160,40,'짜장면',8000,NULL),(161,41,'우동',9000,NULL),(162,41,'연어초밥',13000,NULL),(163,41,'가츠동',10000,NULL),(164,41,'돈코츠라멘',11000,NULL),(165,42,'스테이크',20000,NULL),(166,42,'리조또',13500,NULL),(167,42,'크림파스타',13000,NULL),(168,42,'페퍼로니피자',14000,NULL),(169,43,'라면',4500,NULL),(170,43,'튀김',4000,NULL),(171,43,'순대',4000,NULL),(172,43,'떡볶이',5000,NULL),(173,44,'치즈볼',5000,NULL),(174,44,'간장치킨',18000,NULL),(175,44,'양념치킨',18000,NULL),(176,44,'후라이드치킨',17000,NULL),(177,45,'곱창',20000,NULL),(178,45,'돼지갈비',16000,NULL),(179,45,'소고기구이',22000,NULL),(180,45,'삼겹살',15000,NULL),(181,46,'해물찜',19000,NULL),(182,46,'해물파스타',15000,NULL),(183,46,'광어사시미',18000,NULL),(184,46,'회덮밥',11000,NULL),(185,47,'바닐라라떼',5800,NULL),(186,47,'카페모카',5500,NULL),(187,47,'카페라떼',5000,NULL),(188,47,'아메리카노',4500,NULL),(189,48,'나초',12000,NULL),(190,48,'소시지플래터',17000,NULL),(191,48,'치킨윙',16000,NULL),(192,48,'감자튀김',9000,NULL),(193,49,'피자빵',4500,NULL),(194,49,'소금빵',3000,NULL),(195,49,'바게트',4000,NULL),(196,49,'크루아상',3500,NULL),(197,50,'콜라',2000,NULL),(198,50,'치킨너겟',5000,NULL),(199,50,'후렌치프라이',4000,NULL),(200,50,'치즈버거',6500,NULL),(201,51,'마라샹궈',14000,NULL),(202,51,'월남쌈',13000,NULL),(203,51,'나시고랭',11000,NULL),(204,51,'팟타이',12000,NULL),(205,52,'카레우동',9000,NULL),(206,52,'스파이스카레',10500,NULL),(207,52,'포크카레',9500,NULL),(208,52,'치킨카레',10000,NULL),(209,53,'비빔밥',10000,NULL),(210,53,'불고기',11000,NULL),(211,53,'제육덮밥',9500,NULL),(212,53,'김치찌개',9000,NULL),(213,54,'유산슬',15000,NULL),(214,54,'탕수육',12000,NULL),(215,54,'짬뽕',8500,NULL),(216,54,'짜장면',8000,NULL),(217,55,'우동',9000,NULL),(218,55,'연어초밥',13000,NULL),(219,55,'가츠동',10000,NULL),(220,55,'돈코츠라멘',11000,NULL),(221,56,'스테이크',20000,NULL),(222,56,'리조또',13500,NULL),(223,56,'크림파스타',13000,NULL),(224,56,'페퍼로니피자',14000,NULL),(225,57,'라면',4500,NULL),(226,57,'튀김',4000,NULL),(227,57,'순대',4000,NULL),(228,57,'떡볶이',5000,NULL),(229,58,'치즈볼',5000,NULL),(230,58,'간장치킨',18000,NULL),(231,58,'양념치킨',18000,NULL),(232,58,'후라이드치킨',17000,NULL),(233,59,'곱창',20000,NULL),(234,59,'돼지갈비',16000,NULL),(235,59,'소고기구이',22000,NULL),(236,59,'삼겹살',15000,NULL),(237,60,'해물찜',19000,NULL),(238,60,'해물파스타',15000,NULL),(239,60,'광어사시미',18000,NULL),(240,60,'회덮밥',11000,NULL),(241,61,'바닐라라떼',5800,NULL),(242,61,'카페모카',5500,NULL),(243,61,'카페라떼',5000,NULL),(244,61,'아메리카노',4500,NULL),(245,62,'나초',12000,NULL),(246,62,'소시지플래터',17000,NULL),(247,62,'치킨윙',16000,NULL),(248,62,'감자튀김',9000,NULL),(249,63,'피자빵',4500,NULL),(250,63,'소금빵',3000,NULL),(251,63,'바게트',4000,NULL),(252,63,'크루아상',3500,NULL),(253,64,'콜라',2000,NULL),(254,64,'치킨너겟',5000,NULL),(255,64,'후렌치프라이',4000,NULL),(256,64,'치즈버거',6500,NULL),(257,65,'마라샹궈',14000,NULL),(258,65,'월남쌈',13000,NULL),(259,65,'나시고랭',11000,NULL),(260,65,'팟타이',12000,NULL),(261,66,'카레우동',9000,NULL),(262,66,'스파이스카레',10500,NULL),(263,66,'포크카레',9500,NULL),(264,66,'치킨카레',10000,NULL),(265,67,'비빔밥',10000,NULL),(266,67,'불고기',11000,NULL),(267,67,'제육덮밥',9500,NULL),(268,67,'김치찌개',9000,NULL),(269,68,'유산슬',15000,NULL),(270,68,'탕수육',12000,NULL),(271,68,'짬뽕',8500,NULL),(272,68,'짜장면',8000,NULL),(273,69,'우동',9000,NULL),(274,69,'연어초밥',13000,NULL),(275,69,'가츠동',10000,NULL),(276,69,'돈코츠라멘',11000,NULL),(277,70,'스테이크',20000,NULL),(278,70,'리조또',13500,NULL),(279,70,'크림파스타',13000,NULL),(280,70,'페퍼로니피자',14000,NULL),(281,71,'라면',4500,NULL),(282,71,'튀김',4000,NULL),(283,71,'순대',4000,NULL),(284,71,'떡볶이',5000,NULL),(285,72,'치즈볼',5000,NULL),(286,72,'간장치킨',18000,NULL),(287,72,'양념치킨',18000,NULL),(288,72,'후라이드치킨',17000,NULL),(289,73,'곱창',20000,NULL),(290,73,'돼지갈비',16000,NULL),(291,73,'소고기구이',22000,NULL),(292,73,'삼겹살',15000,NULL),(293,74,'해물찜',19000,NULL),(294,74,'해물파스타',15000,NULL),(295,74,'광어사시미',18000,NULL),(296,74,'회덮밥',11000,NULL),(297,75,'바닐라라떼',5800,NULL),(298,75,'카페모카',5500,NULL),(299,75,'카페라떼',5000,NULL),(300,75,'아메리카노',4500,NULL),(301,76,'나초',12000,NULL),(302,76,'소시지플래터',17000,NULL),(303,76,'치킨윙',16000,NULL),(304,76,'감자튀김',9000,NULL),(305,77,'피자빵',4500,NULL),(306,77,'소금빵',3000,NULL),(307,77,'바게트',4000,NULL),(308,77,'크루아상',3500,NULL),(309,78,'콜라',2000,NULL),(310,78,'치킨너겟',5000,NULL),(311,78,'후렌치프라이',4000,NULL),(312,78,'치즈버거',6500,NULL),(313,79,'마라샹궈',14000,NULL),(314,79,'월남쌈',13000,NULL),(315,79,'나시고랭',11000,NULL),(316,79,'팟타이',12000,NULL),(317,80,'카레우동',9000,NULL),(318,80,'스파이스카레',10500,NULL),(319,80,'포크카레',9500,NULL),(320,80,'치킨카레',10000,NULL),(321,81,'비빔밥',10000,NULL),(322,81,'불고기',11000,NULL),(323,81,'제육덮밥',9500,NULL),(324,81,'김치찌개',9000,NULL),(325,82,'유산슬',15000,NULL),(326,82,'탕수육',12000,NULL),(327,82,'짬뽕',8500,NULL),(328,82,'짜장면',8000,NULL),(329,83,'우동',9000,NULL),(330,83,'연어초밥',13000,NULL),(331,83,'가츠동',10000,NULL),(332,83,'돈코츠라멘',11000,NULL),(333,84,'스테이크',20000,NULL),(334,84,'리조또',13500,NULL),(335,84,'크림파스타',13000,NULL),(336,84,'페퍼로니피자',14000,NULL),(337,85,'라면',4500,NULL),(338,85,'튀김',4000,NULL),(339,85,'순대',4000,NULL),(340,85,'떡볶이',5000,NULL),(341,86,'치즈볼',5000,NULL),(342,86,'간장치킨',18000,NULL),(343,86,'양념치킨',18000,NULL),(344,86,'후라이드치킨',17000,NULL),(345,87,'곱창',20000,NULL),(346,87,'돼지갈비',16000,NULL),(347,87,'소고기구이',22000,NULL),(348,87,'삼겹살',15000,NULL),(349,88,'해물찜',19000,NULL),(350,88,'해물파스타',15000,NULL),(351,88,'광어사시미',18000,NULL),(352,88,'회덮밥',11000,NULL),(353,89,'바닐라라떼',5800,NULL),(354,89,'카페모카',5500,NULL),(355,89,'카페라떼',5000,NULL),(356,89,'아메리카노',4500,NULL),(357,90,'나초',12000,NULL),(358,90,'소시지플래터',17000,NULL),(359,90,'치킨윙',16000,NULL),(360,90,'감자튀김',9000,NULL),(361,91,'피자빵',4500,NULL),(362,91,'소금빵',3000,NULL),(363,91,'바게트',4000,NULL),(364,91,'크루아상',3500,NULL),(365,92,'콜라',2000,NULL),(366,92,'치킨너겟',5000,NULL),(367,92,'후렌치프라이',4000,NULL),(368,92,'치즈버거',6500,NULL),(369,93,'마라샹궈',14000,NULL),(370,93,'월남쌈',13000,NULL),(371,93,'나시고랭',11000,NULL),(372,93,'팟타이',12000,NULL),(373,94,'카레우동',9000,NULL),(374,94,'스파이스카레',10500,NULL),(375,94,'포크카레',9500,NULL),(376,94,'치킨카레',10000,NULL),(377,95,'비빔밥',10000,NULL),(378,95,'불고기',11000,NULL),(379,95,'제육덮밥',9500,NULL),(380,95,'김치찌개',9000,NULL),(381,96,'유산슬',15000,NULL),(382,96,'탕수육',12000,NULL),(383,96,'짬뽕',8500,NULL),(384,96,'짜장면',8000,NULL),(385,97,'우동',9000,NULL),(386,97,'연어초밥',13000,NULL),(387,97,'가츠동',10000,NULL),(388,97,'돈코츠라멘',11000,NULL),(389,98,'스테이크',20000,NULL),(390,98,'리조또',13500,NULL),(391,98,'크림파스타',13000,NULL),(392,98,'페퍼로니피자',14000,NULL),(393,99,'라면',4500,NULL),(394,99,'튀김',4000,NULL),(395,99,'순대',4000,NULL),(396,99,'떡볶이',5000,NULL),(397,100,'치즈볼',5000,NULL),(398,100,'간장치킨',18000,NULL),(399,100,'양념치킨',18000,NULL),(400,100,'후라이드치킨',17000,NULL),(401,101,'곱창',20000,NULL),(402,101,'돼지갈비',16000,NULL),(403,101,'소고기구이',22000,NULL),(404,101,'삼겹살',15000,NULL),(405,102,'해물찜',19000,NULL),(406,102,'해물파스타',15000,NULL),(407,102,'광어사시미',18000,NULL),(408,102,'회덮밥',11000,NULL),(409,103,'바닐라라떼',5800,NULL),(410,103,'카페모카',5500,NULL),(411,103,'카페라떼',5000,NULL),(412,103,'아메리카노',4500,NULL),(413,104,'나초',12000,NULL),(414,104,'소시지플래터',17000,NULL),(415,104,'치킨윙',16000,NULL),(416,104,'감자튀김',9000,NULL),(417,105,'피자빵',4500,NULL),(418,105,'소금빵',3000,NULL),(419,105,'바게트',4000,NULL),(420,105,'크루아상',3500,NULL),(421,106,'콜라',2000,NULL),(422,106,'치킨너겟',5000,NULL),(423,106,'후렌치프라이',4000,NULL),(424,106,'치즈버거',6500,NULL),(425,107,'마라샹궈',14000,NULL),(426,107,'월남쌈',13000,NULL),(427,107,'나시고랭',11000,NULL),(428,107,'팟타이',12000,NULL),(429,108,'카레우동',9000,NULL),(430,108,'스파이스카레',10500,NULL),(431,108,'포크카레',9500,NULL),(432,108,'치킨카레',10000,NULL),(433,109,'비빔밥',10000,NULL),(434,109,'불고기',11000,NULL),(435,109,'제육덮밥',9500,NULL),(436,109,'김치찌개',9000,NULL),(437,110,'유산슬',15000,NULL),(438,110,'탕수육',12000,NULL),(439,110,'짬뽕',8500,NULL),(440,110,'짜장면',8000,NULL);
/*!40000 ALTER TABLE `menu` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-28 17:40:25

--
-- Table structure for table `menu_tags`
--

DROP TABLE IF EXISTS `menu_tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu_tags` (
  `tag_id` bigint NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(100) NOT NULL,
  `category` enum('KOREAN','CHINESE','JAPANESE','WESTERN','SNACK','CHICKEN','MEAT','SEAFOOD','CAFE','PUB','BAKERY','FASTFOOD','ASIAN','CURRY') DEFAULT NULL,
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `tag_name` (`tag_name`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu_tags`
--

LOCK TABLES `menu_tags` WRITE;
/*!40000 ALTER TABLE `menu_tags` DISABLE KEYS */;
INSERT INTO `menu_tags` VALUES (1,'찌개','KOREAN'),(2,'제육','KOREAN'),(3,'불고기','KOREAN'),(4,'비빔밥','KOREAN'),(5,'짜장면','CHINESE'),(6,'짬뽕','CHINESE'),(7,'탕수육','CHINESE'),(8,'중식요리','CHINESE'),(9,'라멘','JAPANESE'),(10,'가츠동','JAPANESE'),(11,'초밥','JAPANESE'),(12,'우동','JAPANESE'),(13,'피자','WESTERN'),(14,'파스타','WESTERN'),(15,'리조또','WESTERN'),(16,'스테이크','WESTERN'),(17,'떡볶이','SNACK'),(18,'순대','SNACK'),(19,'분식튀김','SNACK'),(20,'라면','SNACK'),(21,'치킨','CHICKEN'),(22,'치즈볼','CHICKEN'),(23,'삼겹살','MEAT'),(24,'소고기','MEAT'),(25,'갈비','MEAT'),(26,'곱창','MEAT'),(27,'회덮밥','SEAFOOD'),(28,'사시미','SEAFOOD'),(29,'해물요리','SEAFOOD'),(30,'커피','CAFE'),(31,'라떼','CAFE'),(32,'모카','CAFE'),(33,'바닐라라떼','CAFE'),(34,'감자튀김','PUB'),(35,'소시지','PUB'),(36,'나초','PUB'),(37,'크루아상','BAKERY'),(38,'바게트','BAKERY'),(39,'소금빵','BAKERY'),(40,'피자빵','BAKERY'),(41,'버거','FASTFOOD'),(42,'탄산음료','FASTFOOD'),(43,'팟타이','ASIAN'),(44,'나시고랭','ASIAN'),(45,'월남쌈','ASIAN'),(46,'마라','ASIAN'),(47,'카레','CURRY');
/*!40000 ALTER TABLE `menu_tags` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-28 17:40:25

--
-- Table structure for table `gungu`
--

DROP TABLE IF EXISTS `gungu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gungu` (
  `sido_no` varchar(4) NOT NULL,
  `gungu_code` varchar(5) NOT NULL,
  `gungu_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`gungu_code`,`sido_no`),
  KEY `sido_gungu_idx` (`sido_no`),
  CONSTRAINT `sido_gungu` FOREIGN KEY (`sido_no`) REFERENCES `sido` (`sido_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gungu`
--

LOCK TABLES `gungu` WRITE;
/*!40000 ALTER TABLE `gungu` DISABLE KEYS */;
INSERT INTO `gungu` VALUES ('1','1','강남구'),('2','1','강화군'),('3','1','대덕구'),('31','1','가평군'),('32','1','강릉시'),('33','1','괴산군'),('34','1','공주시'),('35','1','경산시'),('36','1','거제시'),('37','1','고창군'),('38','1','강진군'),('39','1','남제주군'),('4','1','남구'),('5','1','광산구'),('6','1','강서구'),('7','1','중구'),('8','1','세종특별자치시'),('1','10','도봉구'),('2','10','중구'),('31','10','동두천시'),('32','10','인제군'),('33','10','청주시'),('35','10','성주군'),('36','10','양산시'),('37','10','임실군'),('38','10','보성군'),('6','10','사하구'),('1','11','동대문구'),('31','11','부천시'),('32','11','정선군'),('33','11','충주시'),('34','11','예산군'),('35','11','안동시'),('37','11','장수군'),('38','11','순천시'),('6','11','서구'),('1','12','동작구'),('31','12','성남시'),('32','12','철원군'),('33','12','증평군'),('34','12','천안시'),('35','12','영덕군'),('36','12','의령군'),('37','12','전주시'),('38','12','신안군'),('6','12','수영구'),('1','13','마포구'),('31','13','수원시'),('32','13','춘천시'),('34','13','청양군'),('35','13','영양군'),('36','13','진주시'),('37','13','정읍시'),('38','13','여수시'),('6','13','연제구'),('1','14','서대문구'),('31','14','시흥시'),('32','14','태백시'),('34','14','태안군'),('35','14','영주시'),('36','14','진해시'),('37','14','진안군'),('6','14','영도구'),('1','15','서초구'),('31','15','안산시'),('32','15','평창군'),('34','15','홍성군'),('35','15','영천시'),('36','15','창녕군'),('6','15','중구'),('1','16','성동구'),('31','16','안성시'),('32','16','홍천군'),('34','16','계룡시'),('35','16','예천군'),('36','16','창원시'),('38','16','영광군'),('6','16','해운대구'),('1','17','성북구'),('31','17','안양시'),('32','17','화천군'),('35','17','울릉군'),('36','17','통영시'),('38','17','영암군'),('1','18','송파구'),('31','18','양주시'),('32','18','횡성군'),('35','18','울진군'),('36','18','하동군'),('38','18','완도군'),('1','19','양천구'),('31','19','양평군'),('35','19','의성군'),('36','19','함안군'),('38','19','장성군'),('1','2','강동구'),('2','2','계양구'),('3','2','동구'),('31','2','고양시'),('32','2','고성군'),('33','2','단양군'),('34','2','금산군'),('35','2','경주시'),('36','2','거창군'),('37','2','군산시'),('38','2','고흥군'),('39','2','북제주군'),('4','2','달서구'),('5','2','남구'),('6','2','금정구'),('7','2','남구'),('1','20','영등포구'),('31','20','여주시'),('35','20','청도군'),('36','20','함양군'),('38','20','장흥군'),('1','21','용산구'),('31','21','연천군'),('35','21','청송군'),('36','21','합천군'),('38','21','진도군'),('1','22','은평구'),('31','22','오산시'),('35','22','칠곡군'),('38','22','함평군'),('1','23','종로구'),('31','23','용인시'),('35','23','포항시'),('38','23','해남군'),('1','24','중구'),('31','24','의왕시'),('38','24','화순군'),('1','25','중랑구'),('31','25','의정부시'),('31','26','이천시'),('31','27','파주시'),('31','28','평택시'),('31','29','포천시'),('1','3','강북구'),('2','3','미추홀구'),('3','3','서구'),('31','3','과천시'),('32','3','동해시'),('33','3','보은군'),('34','3','논산시'),('35','3','고령군'),('36','3','고성군'),('37','3','김제시'),('38','3','곡성군'),('39','3','서귀포시'),('4','3','달성군'),('5','3','동구'),('6','3','기장군'),('7','3','동구'),('31','30','하남시'),('31','31','화성시'),('1','4','강서구'),('2','4','남동구'),('3','4','유성구'),('31','4','광명시'),('32','4','삼척시'),('33','4','영동군'),('34','4','당진시'),('35','4','구미시'),('36','4','김해시'),('37','4','남원시'),('38','4','광양시'),('39','4','제주시'),('4','4','동구'),('5','4','북구'),('6','4','남구'),('7','4','북구'),('1','5','관악구'),('2','5','동구'),('3','5','중구'),('31','5','광주시'),('32','5','속초시'),('33','5','옥천군'),('34','5','보령시'),('36','5','남해군'),('37','5','무주군'),('38','5','구례군'),('4','5','북구'),('5','5','서구'),('6','5','동구'),('7','5','울주군'),('1','6','광진구'),('2','6','부평구'),('31','6','구리시'),('32','6','양구군'),('33','6','음성군'),('34','6','부여군'),('35','6','김천시'),('36','6','마산시'),('37','6','부안군'),('38','6','나주시'),('4','6','서구'),('6','6','동래구'),('1','7','구로구'),('2','7','서구'),('31','7','군포시'),('32','7','양양군'),('33','7','제천시'),('34','7','서산시'),('35','7','문경시'),('36','7','밀양시'),('37','7','순창군'),('38','7','담양군'),('4','7','수성구'),('6','7','부산진구'),('1','8','금천구'),('2','8','연수구'),('31','8','김포시'),('32','8','영월군'),('33','8','진천군'),('34','8','서천군'),('35','8','봉화군'),('36','8','사천시'),('37','8','완주군'),('38','8','목포시'),('4','8','중구'),('6','8','북구'),('1','9','노원구'),('2','9','옹진군'),('31','9','남양주시'),('32','9','원주시'),('33','9','청원군'),('34','9','아산시'),('35','9','상주시'),('36','9','산청군'),('37','9','익산시'),('38','9','무안군'),('4','9','군위군'),('6','9','사상구');
/*!40000 ALTER TABLE `gungu` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-28 17:40:25

--
-- Table structure for table `menu_tags_mapping`
--

DROP TABLE IF EXISTS `menu_tags_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu_tags_mapping` (
  `mapping_id` bigint NOT NULL AUTO_INCREMENT,
  `menu_id` bigint NOT NULL,
  `tag_id` bigint NOT NULL,
  PRIMARY KEY (`mapping_id`),
  KEY `fk_mtm_menu` (`menu_id`),
  KEY `fk_mtm_tag` (`tag_id`),
  CONSTRAINT `fk_mtm_menu` FOREIGN KEY (`menu_id`) REFERENCES `menu` (`menu_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_mtm_tag` FOREIGN KEY (`tag_id`) REFERENCES `menu_tags` (`tag_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=512 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu_tags_mapping`
--

LOCK TABLES `menu_tags_mapping` WRITE;
/*!40000 ALTER TABLE `menu_tags_mapping` DISABLE KEYS */;
INSERT INTO `menu_tags_mapping` VALUES (1,1,8),(2,2,7),(3,3,6),(4,4,5),(5,5,4),(6,6,3),(7,7,2),(8,8,1),(9,9,12),(10,10,11),(11,11,10),(12,12,9),(13,13,16),(14,14,15),(15,15,14),(16,16,13),(17,17,33),(18,18,32),(19,19,31),(20,20,30),(21,21,42),(22,22,21),(23,23,34),(24,24,41),(25,25,4),(26,26,3),(27,27,2),(28,28,1),(29,29,4),(30,30,3),(31,31,2),(32,32,1),(33,33,4),(34,34,3),(35,35,2),(36,36,1),(37,37,33),(38,38,32),(39,39,31),(40,40,30),(41,41,4),(42,42,3),(43,43,2),(44,44,1),(45,45,8),(46,46,7),(47,47,6),(48,48,5),(49,49,12),(50,50,11),(51,51,10),(52,52,9),(53,53,16),(54,54,15),(55,55,14),(56,56,13),(57,57,20),(58,58,19),(59,59,18),(60,60,17),(61,61,22),(62,62,21),(63,63,21),(64,64,21),(65,65,26),(66,66,25),(67,67,24),(68,68,23),(69,69,29),(70,70,14),(71,71,28),(72,72,27),(73,73,33),(74,74,32),(75,75,31),(76,76,30),(77,77,36),(78,78,35),(79,79,21),(80,80,34),(81,81,40),(82,82,39),(83,83,38),(84,84,37),(85,85,42),(86,86,21),(87,87,34),(88,88,41),(89,89,46),(90,90,45),(91,91,44),(92,92,43),(93,93,47),(94,94,47),(95,95,47),(96,96,47),(97,97,4),(98,98,3),(99,99,2),(100,100,1),(101,101,8),(102,102,7),(103,103,6),(104,104,5),(105,105,12),(106,106,11),(107,107,10),(108,108,9),(109,109,16),(110,110,15),(111,111,14),(112,112,13),(113,113,20),(114,114,19),(115,115,18),(116,116,17),(117,117,22),(118,118,21),(119,119,21),(120,120,21),(121,121,26),(122,122,25),(123,123,24),(124,124,23),(125,125,29),(126,126,14),(127,127,28),(128,128,27),(129,129,33),(130,130,32),(131,131,31),(132,132,30),(133,133,36),(134,134,35),(135,135,21),(136,136,34),(137,137,40),(138,138,39),(139,139,38),(140,140,37),(141,141,42),(142,142,21),(143,143,34),(144,144,41),(145,145,46),(146,146,45),(147,147,44),(148,148,43),(149,149,47),(150,150,47),(151,151,47),(152,152,47),(153,153,4),(154,154,3),(155,155,2),(156,156,1),(157,157,8),(158,158,7),(159,159,6),(160,160,5),(161,161,12),(162,162,11),(163,163,10),(164,164,9),(165,165,16),(166,166,15),(167,167,14),(168,168,13),(169,169,20),(170,170,19),(171,171,18),(172,172,17),(173,173,22),(174,174,21),(175,175,21),(176,176,21),(177,177,26),(178,178,25),(179,179,24),(180,180,23),(181,181,29),(182,182,14),(183,183,28),(184,184,27),(185,185,33),(186,186,32),(187,187,31),(188,188,30),(189,189,36),(190,190,35),(191,191,21),(192,192,34),(193,193,40),(194,194,39),(195,195,38),(196,196,37),(197,197,42),(198,198,21),(199,199,34),(200,200,41),(201,201,46),(202,202,45),(203,203,44),(204,204,43),(205,205,47),(206,206,47),(207,207,47),(208,208,47),(209,209,4),(210,210,3),(211,211,2),(212,212,1),(213,213,8),(214,214,7),(215,215,6),(216,216,5),(217,217,12),(218,218,11),(219,219,10),(220,220,9),(221,221,16),(222,222,15),(223,223,14),(224,224,13),(225,225,20),(226,226,19),(227,227,18),(228,228,17),(229,229,22),(230,230,21),(231,231,21),(232,232,21),(233,233,26),(234,234,25),(235,235,24),(236,236,23),(237,237,29),(238,238,14),(239,239,28),(240,240,27),(241,241,33),(242,242,32),(243,243,31),(244,244,30),(245,245,36),(246,246,35),(247,247,21),(248,248,34),(249,249,40),(250,250,39),(251,251,38),(252,252,37),(253,253,42),(254,254,21),(255,255,34),(256,256,41),(257,257,46),(258,258,45),(259,259,44),(260,260,43),(261,261,47),(262,262,47),(263,263,47),(264,264,47),(265,265,4),(266,266,3),(267,267,2),(268,268,1),(269,269,8),(270,270,7),(271,271,6),(272,272,5),(273,273,12),(274,274,11),(275,275,10),(276,276,9),(277,277,16),(278,278,15),(279,279,14),(280,280,13),(281,281,20),(282,282,19),(283,283,18),(284,284,17),(285,285,22),(286,286,21),(287,287,21),(288,288,21),(289,289,26),(290,290,25),(291,291,24),(292,292,23),(293,293,29),(294,294,14),(295,295,28),(296,296,27),(297,297,33),(298,298,32),(299,299,31),(300,300,30),(301,301,36),(302,302,35),(303,303,21),(304,304,34),(305,305,40),(306,306,39),(307,307,38),(308,308,37),(309,309,42),(310,310,21),(311,311,34),(312,312,41),(313,313,46),(314,314,45),(315,315,44),(316,316,43),(317,317,47),(318,318,47),(319,319,47),(320,320,47),(321,321,4),(322,322,3),(323,323,2),(324,324,1),(325,325,8),(326,326,7),(327,327,6),(328,328,5),(329,329,12),(330,330,11),(331,331,10),(332,332,9),(333,333,16),(334,334,15),(335,335,14),(336,336,13),(337,337,20),(338,338,19),(339,339,18),(340,340,17),(341,341,22),(342,342,21),(343,343,21),(344,344,21),(345,345,26),(346,346,25),(347,347,24),(348,348,23),(349,349,29),(350,350,14),(351,351,28),(352,352,27),(353,353,33),(354,354,32),(355,355,31),(356,356,30),(357,357,36),(358,358,35),(359,359,21),(360,360,34),(361,361,40),(362,362,39),(363,363,38),(364,364,37),(365,365,42),(366,366,21),(367,367,34),(368,368,41),(369,369,46),(370,370,45),(371,371,44),(372,372,43),(373,373,47),(374,374,47),(375,375,47),(376,376,47),(377,377,4),(378,378,3),(379,379,2),(380,380,1),(381,381,8),(382,382,7),(383,383,6),(384,384,5),(385,385,12),(386,386,11),(387,387,10),(388,388,9),(389,389,16),(390,390,15),(391,391,14),(392,392,13),(393,393,20),(394,394,19),(395,395,18),(396,396,17),(397,397,22),(398,398,21),(399,399,21),(400,400,21),(401,401,26),(402,402,25),(403,403,24),(404,404,23),(405,405,29),(406,406,14),(407,407,28),(408,408,27),(409,409,33),(410,410,32),(411,411,31),(412,412,30),(413,413,36),(414,414,35),(415,415,21),(416,416,34),(417,417,40),(418,418,39),(419,419,38),(420,420,37),(421,421,42),(422,422,21),(423,423,34),(424,424,41),(425,425,46),(426,426,45),(427,427,44),(428,428,43),(429,429,47),(430,430,47),(431,431,47),(432,432,47),(433,433,4),(434,434,3),(435,435,2),(436,436,1),(437,437,8),(438,438,7),(439,439,6),(440,440,5);
/*!40000 ALTER TABLE `menu_tags_mapping` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-28 17:40:25

--
-- Table structure for table `follow`
--

DROP TABLE IF EXISTS `follow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `follow` (
  `follow_id` bigint NOT NULL AUTO_INCREMENT,
  `follower_no` bigint NOT NULL,
  `following_no` bigint NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`follow_id`),
  UNIQUE KEY `unique_follow` (`follower_no`,`following_no`),
  KEY `idx_follower` (`follower_no`),
  KEY `idx_following` (`following_no`),
  CONSTRAINT `fk_follow_follower` FOREIGN KEY (`follower_no`) REFERENCES `user` (`user_no`) ON DELETE CASCADE,
  CONSTRAINT `fk_follow_following` FOREIGN KEY (`following_no`) REFERENCES `user` (`user_no`) ON DELETE CASCADE,
  CONSTRAINT `check_not_self_follow` CHECK ((`follower_no` <> `following_no`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `follow`
--

LOCK TABLES `follow` WRITE;
/*!40000 ALTER TABLE `follow` DISABLE KEYS */;
/*!40000 ALTER TABLE `follow` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-28 17:40:25










