CREATE SCHEMA `custom_logger_test` DEFAULT CHARACTER SET utf8 ;

CREATE TABLE `custom_logger_test`.`log_values` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `message` VARCHAR(100) NULL,
  `logtype` INT NOT NULL,
  PRIMARY KEY (`id`));
