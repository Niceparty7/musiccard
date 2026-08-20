# Nacos 配置中心导入说明

当前项目已经移除 Eureka，服务注册发现和配置管理统一使用 Nacos。

## Nacos 基础信息

- Nacos 地址：`127.0.0.1:8848`
- 默认 Namespace：`local`
- Group：`DEFAULT_GROUP`
- 配置格式：`properties`

## 启动 Nacos

进入 Nacos 的 `bin` 目录，在 Windows 下执行：

```powershell
startup.cmd -m standalone
```

然后访问：

```text
http://127.0.0.1:8848/nacos
```

## 导入配置

在 Nacos 控制台的“配置管理 -> 配置列表”中，切换到 `local` Namespace，分别创建以下四个配置：

| Data ID | Group | 配置文件 |
|---|---|---|
| `music-app.properties` | `DEFAULT_GROUP` | 当前目录同名文件 |
| `music-console.properties` | `DEFAULT_GROUP` | 当前目录同名文件 |
| `music-module.properties` | `DEFAULT_GROUP` | 当前目录同名文件 |
| `user.properties` | `DEFAULT_GROUP` | 当前目录同名文件 |

创建时选择 `Properties` 格式，并将同名文件内容完整复制到 Nacos。

当前本地运行默认使用 `local` Namespace。服务中的 Namespace 配置为：

```properties
spring.cloud.nacos.discovery.namespace=${NACOS_NAMESPACE:local}
spring.cloud.nacos.config.namespace=${NACOS_NAMESPACE:local}
```

如果需要切换环境，只需设置 `NACOS_NAMESPACE`，例如：

```powershell
$env:NACOS_NAMESPACE="dev"
```

然后在对应 Namespace 中创建同名的 4 条配置。`public`、`dev`、`test`、`pro` 之间的配置不会互相读取。

## 启动业务服务

```text
1. Nacos
2. user
3. music-module
4. music-app
5. music-console
```

业务服务启动时会通过 `spring.config.import` 读取对应 Data ID；如果 Nacos 未启动或配置不存在，服务会启动失败，这是配置中心强依赖的预期行为。

## 注意事项

- 外部接口 URL 和请求方式没有改变。
- OpenFeign 的 `@FeignClient(name = "music-module")`、`@FeignClient(name = "user")` 不需要修改。
- `X-Internal-Token`、数据库密码、Redis 密码和 OSS 配置已进入 Nacos 配置，应在真实环境中替换为安全值。
- Nacos 账号密码可以通过环境变量 `NACOS_USERNAME`、`NACOS_PASSWORD` 覆盖，默认值为本地 Nacos 常用账号配置。
- Nacos 配置中心中的 `local` Namespace 必须存在 `music-app.properties`、`music-console.properties`、`music-module.properties`、`user.properties` 四条配置，否则服务无法启动。
