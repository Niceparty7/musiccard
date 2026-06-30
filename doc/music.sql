CREATE TABLE music(
  id      BIGINT UNSIGNED NOT NULL auto_increment,
  cover_images  VARCHAR(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '封面图，$拼接',
  music_name    VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '歌曲名',
  singer_name   VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '歌手名',
  music_desc    VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '歌曲介绍',
  album_title   VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '专辑名称',
  release_date  VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发布日期',
  create_time   INT UNSIGNED NOT NULL,
  update_time   INT UNSIGNED NOT NULL,
  is_deleted    TINYINT(1) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_music_name (music_name),
  KEY idx_singer_name (singer_name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='音乐表'