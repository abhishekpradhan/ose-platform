/*
SQLyog Community Edition- MySQL GUI v8.05 
MySQL - 5.1.41-community : Database - objectsearch
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

/*Table structure for table `DocInfo` */

CREATE TABLE `DocInfo` (
  `DocId` int(11) NOT NULL,
  `Url` varchar(512) DEFAULT NULL,
  `IndexId` int(11) NOT NULL,
  `Title` varchar(512) DEFAULT NULL,
  `BodyText` mediumtext,
  `Html` mediumtext,
  PRIMARY KEY (`DocId`,`IndexId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `DocTag` */

CREATE TABLE `DocTag` (
  `TagId` int(11) NOT NULL AUTO_INCREMENT,
  `IndexId` int(11) DEFAULT NULL,
  `DocId` int(11) DEFAULT NULL,
  `FieldId` int(11) DEFAULT NULL,
  `Value` varchar(4096) DEFAULT NULL,
  PRIMARY KEY (`TagId`),
  KEY `IndexDoc` (`IndexId`,`DocId`),
  KEY `NewIndex1` (`IndexId`,`DocId`,`Value`(255))
) ENGINE=InnoDB AUTO_INCREMENT=155557 DEFAULT CHARSET=utf8;

/*Table structure for table `DomainInfo` */

CREATE TABLE `DomainInfo` (
  `DomainId` int(11) NOT NULL,
  `Name` varchar(63) DEFAULT NULL,
  `Description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`DomainId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `FeatureInfo` */

CREATE TABLE `FeatureInfo` (
  `FeatureId` int(11) NOT NULL AUTO_INCREMENT,
  `FieldId` int(11) DEFAULT NULL,
  `Template` varchar(255) DEFAULT NULL,
  `Weight` double DEFAULT '1',
  `DateCreated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `IsDeleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`FeatureId`)
) ENGINE=InnoDB AUTO_INCREMENT=1655 DEFAULT CHARSET=utf8;

/*Table structure for table `Feedback` */

CREATE TABLE `Feedback` (
  `QueryId` int(11) NOT NULL,
  `DocId` int(11) NOT NULL,
  `IndexId` int(11) NOT NULL DEFAULT '0',
  `Relevant` tinyint(1) DEFAULT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `DomainId` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`QueryId`,`DocId`,`IndexId`,`DomainId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `FieldInfo` */

CREATE TABLE `FieldInfo` (
  `FieldId` int(11) NOT NULL,
  `DomainId` int(11) DEFAULT NULL,
  `Name` varchar(63) DEFAULT NULL,
  `Type` varchar(15) DEFAULT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `TrainingSessionId` int(11) DEFAULT '-1',
  PRIMARY KEY (`FieldId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `IndexInfo` */

CREATE TABLE `IndexInfo` (
  `IndexId` int(11) NOT NULL,
  `Name` varchar(255) DEFAULT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `IndexPath` varchar(255) DEFAULT NULL,
  `CachePath` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`IndexId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `ModelInfo` */

CREATE TABLE `ModelInfo` (
  `ModelId` int(11) NOT NULL AUTO_INCREMENT,
  `FieldId` int(11) DEFAULT NULL,
  `Path` varchar(255) DEFAULT NULL,
  `Weight` float DEFAULT NULL,
  PRIMARY KEY (`ModelId`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8;

/*Table structure for table `QueryInfo` */

CREATE TABLE `QueryInfo` (
  `QueryId` int(11) NOT NULL AUTO_INCREMENT,
  `QueryString` varchar(1000) DEFAULT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `DomainId` int(11) DEFAULT NULL,
  PRIMARY KEY (`QueryId`)
) ENGINE=InnoDB AUTO_INCREMENT=3011 DEFAULT CHARSET=utf8;

/*Table structure for table `TagRuleInfo` */

CREATE TABLE `TagRuleInfo` (
  `RuleId` int(11) NOT NULL AUTO_INCREMENT,
  `FieldId` int(11) DEFAULT NULL,
  `Value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`RuleId`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;

/*Table structure for table `TrainingFeedback` */

CREATE TABLE `TrainingFeedback` (
  `QueryId` int(11) NOT NULL,
  `DocId` int(11) NOT NULL,
  `IndexId` int(11) NOT NULL DEFAULT '0',
  `Relevant` tinyint(1) DEFAULT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `DomainId` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`QueryId`,`DocId`,`IndexId`,`DomainId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `TrainingQueryInfo` */

CREATE TABLE `TrainingQueryInfo` (
  `AttributeId` int(11) NOT NULL AUTO_INCREMENT,
  `FieldId` int(11) DEFAULT NULL,
  `Value` varchar(127) DEFAULT NULL,
  PRIMARY KEY (`AttributeId`)
) ENGINE=InnoDB AUTO_INCREMENT=1172 DEFAULT CHARSET=utf8;

/*Table structure for table `lbjseCache` */

CREATE TABLE `lbjseCache` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `QueryId` int(11) DEFAULT NULL,
  `IndexId` int(11) DEFAULT NULL,
  `CacheFile` varchar(255) DEFAULT NULL,
  `DateCreated` datetime DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=143 DEFAULT CHARSET=utf8;

/*Table structure for table `lbjseCandidateFeatureInfo` */

CREATE TABLE `lbjseCandidateFeatureInfo` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Datatype` varchar(31) NOT NULL,
  `Feature` varchar(63) NOT NULL,
  `Argument` smallint(6) NOT NULL DEFAULT '0',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;

/*Table structure for table `lbjseData` */

CREATE TABLE `lbjseData` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Path` varchar(256) DEFAULT NULL,
  `Description` varchar(256) DEFAULT NULL,
  `IndexId` int(11) NOT NULL,
  `DateCreated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

/*Table structure for table `lbjseFeatureInfo` */

CREATE TABLE `lbjseFeatureInfo` (
  `FeatureId` int(11) NOT NULL AUTO_INCREMENT,
  `FieldId` int(11) DEFAULT NULL,
  `Template` varchar(255) DEFAULT NULL,
  `Weight` double DEFAULT '1',
  `DateCreated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `IsDeleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`FeatureId`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8;

/*Table structure for table `lbjseFeatureTemplate` */

CREATE TABLE `lbjseFeatureTemplate` (
  `TemplateId` int(11) NOT NULL AUTO_INCREMENT,
  `SessionId` int(11) DEFAULT NULL,
  `Template` varchar(255) DEFAULT NULL,
  `Weight` double DEFAULT '1',
  `DateCreated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `IsDeleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`TemplateId`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

/*Table structure for table `lbjseQuery` */

CREATE TABLE `lbjseQuery` (
  `QueryId` int(11) NOT NULL,
  `FieldId` int(11) DEFAULT NULL,
  `Value` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`QueryId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `lbjseQueryValue` */

CREATE TABLE `lbjseQueryValue` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `SessionId` int(11) DEFAULT NULL,
  `Value` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=131 DEFAULT CHARSET=utf8;

/*Table structure for table `lbjseSearchFeature` */

CREATE TABLE `lbjseSearchFeature` (
  `FeatureId` int(11) NOT NULL AUTO_INCREMENT,
  `SessionId` int(11) DEFAULT NULL,
  `Value` varchar(255) DEFAULT NULL,
  `DateCreated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `IsDeleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`FeatureId`)
) ENGINE=InnoDB AUTO_INCREMENT=183 DEFAULT CHARSET=utf8;

/*Table structure for table `lbjseTrainingSession` */

CREATE TABLE `lbjseTrainingSession` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `DomainId` int(11) NOT NULL,
  `FieldId` int(11) NOT NULL,
  `Description` varchar(256) NOT NULL,
  `FeatureGeneratorClass` varchar(128) DEFAULT NULL,
  `ClassifierClass` varchar(128) DEFAULT NULL,
  `CurrentPerformance` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=251 DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
