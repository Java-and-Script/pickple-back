CREATE TABLE IF NOT EXISTS `pickpledev`.`address_depth1`
(
    `id`         BIGINT      NOT NULL AUTO_INCREMENT,
    `name`       VARCHAR(10) NOT NULL,
    `created_at` DATETIME    NOT NULL DEFAULT now(),
    `updated_at` DATETIME    NOT NULL DEFAULT now() ON UPDATE now(),
    PRIMARY KEY (`id`)
    );

CREATE TABLE IF NOT EXISTS `pickpledev`.`address_depth2`
(
    `id`                BIGINT      NOT NULL AUTO_INCREMENT,
    `name`              VARCHAR(10) NOT NULL,
    `created_at`        DATETIME    NOT NULL DEFAULT now(),
    `updated_at`        DATETIME    NOT NULL DEFAULT now() ON UPDATE now(),
    `address_depth1_id` BIGINT      NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_address_depth1_TO_address_depth2`
    FOREIGN KEY (`address_depth1_id`)
    REFERENCES `pickpledev`.`address_depth1` (`id`)
    );


CREATE TABLE IF NOT EXISTS `pickpledev`.`member`
(
    `id`                 BIGINT        NOT NULL AUTO_INCREMENT,
    `email`              VARCHAR(100)  NOT NULL,
    `nickname`           VARCHAR(20)   NOT NULL,
    `introduction`       VARCHAR(1000) NULL,
    `profile_image_url`  VARCHAR(300)  NOT NULL,
    `status`             VARCHAR(10)   NOT NULL,
    `manner_score`       INT           NOT NULL DEFAULT '0',
    `manner_score_count` INT           NOT NULL DEFAULT '0',
    `oauth_id`           BIGINT        NOT NULL,
    `oauth_provider`     VARCHAR(10)   NOT NULL,
    `created_at`         DATETIME      NOT NULL DEFAULT now(),
    `updated_at`         DATETIME      NOT NULL DEFAULT now() ON UPDATE now(),
    `address_depth1_id`  BIGINT        NOT NULL,
    `address_depth2_id`  BIGINT        NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_address_depth1_TO_member_1`
    FOREIGN KEY (`address_depth1_id`)
    REFERENCES `pickpledev`.`address_depth1` (`id`),
    CONSTRAINT `FK_address_depth2_TO_member_1`
    FOREIGN KEY (`address_depth2_id`)
    REFERENCES `pickpledev`.`address_depth2` (`id`)
    );

CREATE TABLE IF NOT EXISTS `pickpledev`.`crew`
(
    `id`                   BIGINT        NOT NULL AUTO_INCREMENT,
    `name`                 VARCHAR(20)   NOT NULL,
    `content`              VARCHAR(1000) NULL     DEFAULT NULL,
    `member_count`         TINYINT       NOT NULL DEFAULT '1',
    `profile_image_url`    VARCHAR(300)  NOT NULL,
    `background_image_url` VARCHAR(300)  NOT NULL,
    `status`               VARCHAR(10)   NOT NULL,
    `like_count`           INT           NOT NULL DEFAULT '0',
    `max_member_count`     TINYINT       NOT NULL DEFAULT '1',
    `competition_point`    INT           NOT NULL DEFAULT '0',
    `created_at`           DATETIME      NOT NULL DEFAULT now(),
    `updated_at`           DATETIME      NOT NULL DEFAULT now() ON UPDATE now(),
    `leader_id`            BIGINT        NOT NULL,
    `address_depth1_id`    BIGINT        NOT NULL,
    `address_depth2_id`    BIGINT        NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_address_depth1_TO_crew_1`
    FOREIGN KEY (`address_depth1_id`)
    REFERENCES `pickpledev`.`address_depth1` (`id`),
    CONSTRAINT `FK_address_depth2_TO_crew_1`
    FOREIGN KEY (`address_depth2_id`)
    REFERENCES `pickpledev`.`address_depth2` (`id`),
    CONSTRAINT `FK_member_TO_crew_1`
    FOREIGN KEY (`leader_id`)
    REFERENCES `pickpledev`.`member` (`id`)
    );

CREATE TABLE IF NOT EXISTS `pickpledev`.`crew_member`
(
    `id`         BIGINT      NOT NULL AUTO_INCREMENT,
    `status`     VARCHAR(10) NOT NULL,
    `created_at` DATETIME    NOT NULL DEFAULT now(),
    `updated_at` DATETIME    NOT NULL DEFAULT now() ON UPDATE now(),
    `member_id`  BIGINT      NOT NULL,
    `crew_id`    BIGINT      NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_crew_TO_crew_member_1`
    FOREIGN KEY (`crew_id`)
    REFERENCES `pickpledev`.`crew` (`id`),
    CONSTRAINT `FK_member_TO_crew_member_1`
    FOREIGN KEY (`member_id`)
    REFERENCES `pickpledev`.`member` (`id`)
    );

CREATE TABLE IF NOT EXISTS `pickpledev`.`gameEntity`
(
    `id`                BIGINT        NOT NULL AUTO_INCREMENT,
    `content`           VARCHAR(1000) NOT NULL,
    `play_date`         DATE          NOT NULL,
    `play_start_time`   TIME          NOT NULL,
    `play_end_time`     TIME          NOT NULL,
    `play_time_minutes` SMALLINT      NOT NULL,
    `main_address`      VARCHAR(50)   NOT NULL,
    `detail_address`    VARCHAR(50)   NOT NULL,
    `latitude`          DOUBLE        NULL     DEFAULT NULL,
    `longitude`         DOUBLE        NULL     DEFAULT NULL,
    `status`            VARCHAR(10)   NOT NULL,
    `view_count`        INT           NOT NULL DEFAULT '0',
    `cost`              INT           NOT NULL DEFAULT '0',
    `member_count`      INT           NOT NULL DEFAULT '1',
    `max_member_count`  TINYINT       NOT NULL DEFAULT '1',
    `created_at`        DATETIME      NOT NULL DEFAULT now(),
    `updated_at`        DATETIME      NOT NULL DEFAULT now() ON UPDATE now(),
    `host_id`           BIGINT        NOT NULL,
    `address_depth1_id` BIGINT        NOT NULL,
    `address_depth2_id` BIGINT        NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_address_depth1_TO_game_1`
    FOREIGN KEY (`address_depth1_id`)
    REFERENCES `pickpledev`.`address_depth1` (`id`),
    CONSTRAINT `FK_address_depth2_TO_game_1`
    FOREIGN KEY (`address_depth2_id`)
    REFERENCES `pickpledev`.`address_depth2` (`id`),
    CONSTRAINT `FK_member_TO_game_1`
    FOREIGN KEY (`host_id`)
    REFERENCES `pickpledev`.`member` (`id`)
    );

CREATE TABLE IF NOT EXISTS `pickpledev`.`game_member`
(
    `id`         BIGINT      NOT NULL AUTO_INCREMENT,
    `status`     VARCHAR(10) NOT NULL,
    `created_at` DATETIME    NOT NULL DEFAULT now(),
    `updated_at` DATETIME    NOT NULL DEFAULT now() ON UPDATE now(),
    `member_id`  BIGINT      NOT NULL,
    `game_id`    BIGINT      NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_game_TO_game_member_1`
    FOREIGN KEY (`game_id`)
    REFERENCES `pickpledev`.`gameEntity` (`id`),
    CONSTRAINT `FK_member_TO_game_member_1`
    FOREIGN KEY (`member_id`)
    REFERENCES `pickpledev`.`member` (`id`)
    );

CREATE TABLE IF NOT EXISTS `pickpledev`.`game_position`
(
    `id`         BIGINT     NOT NULL AUTO_INCREMENT,
    `position`   VARCHAR(2) NOT NULL,
    `created_at` DATETIME   NOT NULL DEFAULT now(),
    `updated_at` DATETIME   NOT NULL DEFAULT now() ON UPDATE now(),
    `game_id`    BIGINT     NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_game_TO_game_position_1`
    FOREIGN KEY (`game_id`)
    REFERENCES `pickpledev`.`gameEntity` (`id`)
    );

CREATE TABLE IF NOT EXISTS `pickpledev`.`member_position`
(
    `id`         BIGINT     NOT NULL AUTO_INCREMENT,
    `position`   VARCHAR(2) NOT NULL,
    `created_at` DATETIME   NOT NULL DEFAULT now(),
    `updated_at` DATETIME   NOT NULL DEFAULT now() ON UPDATE now(),
    `member_id`  BIGINT     NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_member_TO_member_position_1`
    FOREIGN KEY (`member_id`)
    REFERENCES `pickpledev`.`member` (`id`)
    );

CREATE TABLE IF NOT EXISTS `pickpledev`.`refresh_token`
(
    `token`      VARCHAR(100) NOT NULL,
    `member_id`  BIGINT       NOT NULL,
    `created_at` DATETIME     NOT NULL DEFAULT now(),
    `updated_at` DATETIME     NOT NULL DEFAULT now() ON UPDATE now(),
    PRIMARY KEY (`token`)
    );
