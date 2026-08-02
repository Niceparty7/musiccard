CREATE TABLE `music`
(
    `id`           bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `cover_images` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '封面图，$拼接',
    `music_name`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci   NOT NULL COMMENT '歌曲名',
    `singer_name`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci   NOT NULL COMMENT '歌手名',
    `music_desc`   varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '歌曲介绍',
    `album_title`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '专辑名称',
    `release_date` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '发布日期',
    `create_time`  int(10) unsigned NOT NULL,
    `update_time`  int(10) unsigned NOT NULL,
    `is_deleted`   tinyint(1) unsigned NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    KEY            `idx_music_name` (`music_name`),
    KEY            `idx_singer_name` (`singer_name`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='音乐表'