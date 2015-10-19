ALTER TABLE `reporting` 
ADD COLUMN `time_reporting` VARCHAR(1024) NULL, 
ADD COLUMN `calendarid` INT NULL ;

ALTER TABLE process 
ADD COLUMN hidden INT(1) NULL DEFAULT '0';

ALTER TABLE organizations ADD calendid INT  default 0;

ALTER TABLE organizational_units ADD calendid INT  default 0;

CREATE TABLE  `iflow`.`calendar` (
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;

CREATE TABLE  `iflow`.`calendar_history` (
  `flowid` int(11) NOT NULL,
  `pid` int(11) NOT NULL,
  `subpid` int(11) NOT NULL DEFAULT '1',
  `mid` int(11) NOT NULL,
  `calendar_id` int(11) DEFAULT '-1',
  `calendar_version` int(11) DEFAULT '-1',
  PRIMARY KEY (`flowid`,`pid`,`subpid`,`mid`,`calendar_id`,`calendar_version`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE  `iflow`.`user_calendar` (
  `userid` int(11) NOT NULL,
  `calendar_id` int(11) NOT NULL,
  PRIMARY KEY (`userid`),
  CONSTRAINT `fk_user_calendar_calendar` FOREIGN KEY (`calendar_id`) REFERENCES `calendar` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_calendar_user` FOREIGN KEY (`userid`) REFERENCES `users` (`userid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE  `iflow`.`profiles_calendar` (
  `profileid` int(11) NOT NULL,
  `calendar_id` int(11) NOT NULL,
  PRIMARY KEY (`calendar_id`,`profileid`),
  CONSTRAINT `fk_profile_calendar_calendar` FOREIGN KEY (`calendar_id`) REFERENCES `calendar` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_profile_calendar_profile` FOREIGN KEY (`profileid`) REFERENCES `profiles` (`profileid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE  `iflow`.`flow_calendar` (
  `flowid` int(11) NOT NULL,
  `calendar_id` int(11) NOT NULL,
  PRIMARY KEY (`calendar_id`,`flowid`),
  CONSTRAINT `fk_flow_calendar_calendar` FOREIGN KEY (`calendar_id`) REFERENCES `calendar` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_flow_calendar_flow` FOREIGN KEY (`flowid`) REFERENCES `flow` (`flowid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE  `iflow`.`calendar_holidays` (
  `calendar_id` int(11) NOT NULL,
  `holiday` datetime NOT NULL,
  CONSTRAINT `fk_calendar_holidays_calendar` FOREIGN KEY (`calendar_id`) REFERENCES `calendar` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE  `iflow`.`calendar_periods` (
  `calendar_id` int(11) NOT NULL,
  `period_start` TIME  NOT NULL,
  `period_end` TIME  NOT NULL,
  CONSTRAINT `fk_calendar_periods_calendar` FOREIGN KEY (`calendar_id`) REFERENCES `calendar` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `iflow`.`documents_support`;
CREATE TABLE  `iflow`.`documents_support` (
  `docid` int(10) unsigned NOT NULL,
  `generation` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`docid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DELIMITER $$

DROP PROCEDURE IF EXISTS `get_next_pid` $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_next_pid`(OUT retpid INTEGER,
                               OUT retsubpid INTEGER,
                               aflowid INTEGER,
                               acreatedate DATETIME,
							   acreator VARCHAR(100))
BEGIN
    DECLARE nowdate DATETIME;
    DECLARE modificationdate DATETIME;
    DECLARE count INT DEfAULT 1000;
    DECLARE afectedrows INT DEFAULT 0;
    DECLARE existingpid INT DEFAULT -1;
    SELECT value, modification INTO retpid, modificationdate FROM counter WHERE name = 'pid';
    WHILE count > 0 AND afectedrows <= 0 DO
        SET nowdate = NOW();
        SET count = count - 1;

        set retpid = retpid + 1;
        SELECT count(pid) INTO existingpid FROM process WHERE pid = retpid;
        IF existingpid = 0 THEN
          update counter set value=retpid, modification=nowdate where name='pid' and modification=modificationdate;
          SET afectedrows = ROW_COUNT();
        END IF;
    END WHILE;
    IF count > 0 THEN
        set retsubpid = 1;
        insert into process (flowid,pid,subpid,mid,created,creator,pnumber,currentuser,lastupdate) values
            (aflowid,retpid,retsubpid,1,acreatedate,acreator,retpid,acreator,nowdate);
        insert into process_history (flowid,pid,subpid,mid,created,creator,pnumber,currentuser,lastupdate) values
            (aflowid,retpid,retsubpid,1,acreatedate,acreator,retpid,acreator,nowdate);
    END IF;
END $$

DELIMITER ;