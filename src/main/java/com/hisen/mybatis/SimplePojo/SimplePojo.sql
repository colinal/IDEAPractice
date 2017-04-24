-- auto Generated on 2017-04-19 11:22:16 
-- DROP TABLE IF EXISTS `simple_pojo`; 
CREATE TABLE simple_pojo(
    `id` INTEGER(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name` VARCHAR(50) NOT NULL DEFAULT '' COMMENT 'name',
    `age` INTEGER(12) NOT NULL DEFAULT -1 COMMENT 'age',
    `date` DATETIME NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT 'date',
    PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'simple_pojo';