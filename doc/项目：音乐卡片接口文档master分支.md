# 项目：音乐卡片_接口文档

## App端

## baseUrl:http://localhost:8080

### 1、音乐列表

/music/list

参数：

page:[Int]

keyword:[String],(required = false)

返回：

```json
{
    list:[
        {
            id:[Long],
            wallImage:[String],
            musicName:[String],
            singerName:[String],
            musicDesc:[String]
        }
    ],
    isEnd:[Boolean]
}
```

### 2、音乐详情

/music/info

参数：

id:[Long]

返回：

```json
{
    coverImages:[String[]],
    musicName:[String],
    singerName:[String],
    albumTitle:[String],
    releaseDate:[String],
    musicDesc:[String]
}
```

## Console端

## baseUrl:http://localhost:8081

### 1、音乐列表

/music/list

参数：

page:[Int]

keyword:[String],(required = false)

返回：

```json
{
    list:[
        {
            id:[Long],
            wallImage:[String],
            musicName:[String],
            singerName:[String],
            musicDesc:[String]
        }
    ]
    total:[Long],
	pageSize:[Int]
}
```

### 2、音乐详情

/music/info

参数：

id:[Long]

返回：

```json
{
    coverImages:[String[]],
    musicName:[String],
    singerName:[String],
    albumTitle:[String],
    releaseDate:[String],
    musicDesc:[String],
    createTime:[String],//yyyy-MM-dd hh:mm:ss
    updateTime:[String] //yyyy-MM-dd hh:mm:ss
}
```

### 3、音乐新增

/music/create

参数：
coverImages:[String]
musicName:[String]
singerName:[String]
albumTitle:[String],(required = false)
releaseDate:[String],(required = false)
musicDesc:[String],(required = false)

返回：

插入成功，id为id:[Long] or 失败

### 4、音乐修改

/music/update

参数：

id:[Long]
coverImages:[String],(required = false)
musicName:[String],(required = false)
singerName:[String],(required = false)
albumTitle:[String],(required = false)
releaseDate:[String],(required = false)
musicDesc:[String],(required = false)


返回：

成功 or 失败

### 5、音乐删除

/music/delete

参数：

id:[Long]


返回：

成功 or 失败

## 数据库表设计

```mysql
CREATE TABLE `music` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `cover_images` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '封面图，$拼接',
  `music_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '歌曲名',
  `singer_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '歌手名',
  `music_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '歌曲介绍',
  `album_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '专辑名称',
  `release_date` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发布日期',
  `create_time` int(10) unsigned NOT NULL,
  `update_time` int(10) unsigned NOT NULL,
  `is_deleted` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_music_name` (`music_name`),
  KEY `idx_singer_name` (`singer_name`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='音乐表'
```
