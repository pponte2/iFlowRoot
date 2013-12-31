DROP TABLE IF EXISTS `calendar`;
CREATE TABLE  `calendar` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` int(11) NOT NULL,
  `name` varchar(256) NOT NULL,
  `monday` smallint(5) unsigned NOT NULL COMMENT '0 false, 1 true',
  `tuesday` smallint(5) unsigned NOT NULL COMMENT '0 false, 1 true',
  `wednesday` smallint(5) unsigned NOT NULL COMMENT '0 false, 1 true',
  `thursday` smallint(5) unsigned NOT NULL COMMENT '0 false, 1 true',
  `friday` smallint(5) unsigned NOT NULL COMMENT '0 false, 1 true',
  `saturday` smallint(5) unsigned NOT NULL COMMENT '0 false, 1 true',
  `sunday` smallint(5) unsigned NOT NULL COMMENT '0 false, 1 true',
  `valid` smallint(5) unsigned NOT NULL COMMENT '0 false, 1 true',
  `day_hours` int(11) DEFAULT NULL,
  `week_hours` int(11) DEFAULT NULL,
  `month_hours` int(11) DEFAULT NULL,
  `create_date` datetime NOT NULL,
  PRIMARY KEY (`id`,`version`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `calendar_history`;
CREATE TABLE  `calendar_history` (
  `flowid` int(11) NOT NULL,
  `pid` int(11) NOT NULL,
  `subpid` int(11) NOT NULL DEFAULT '1',
  `mid` int(11) NOT NULL,
  `calendar_id` int(11) NOT NULL DEFAULT '-1',
  `calendar_version` int(11) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`flowid`,`pid`,`subpid`,`mid`,`calendar_id`,`calendar_version`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `calendar_holidays`;
CREATE TABLE  `calendar_holidays` (
  `calendar_id` int(11) NOT NULL,
  `holiday` datetime NOT NULL,
  KEY `fk_calendar_holidays_calendar` (`calendar_id`),
  CONSTRAINT `fk_calendar_holidays_calendar` FOREIGN KEY (`calendar_id`) REFERENCES `calendar` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `calendar_periods`;
CREATE TABLE  `calendar_periods` (
  `calendar_id` int(11) NOT NULL,
  `period_start` time NOT NULL,
  `period_end` time NOT NULL,
  KEY `fk_calendar_periods_calendar` (`calendar_id`),
  CONSTRAINT `fk_calendar_periods_calendar` FOREIGN KEY (`calendar_id`) REFERENCES `calendar` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;