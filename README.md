# LazyLogin

一个 Fabric 服务器端 Mod。

这个 Mod（仓库）是在 [Londiuh 的 login](https://github.com/Londiuh/login) 之上修改和添加而来的。原仓库的许可证文件名为 `LINCENSE_login`。

## 提供的功能

- 玩家在进入服务器的时候必须使用 `/register` 命令注册账号，或者使用 `/login` 登录已注册的用户名。（基于 [login](https://github.com/Londiuh/login)）
- 服务器根据玩家的用户名，把密码的 SHA-256 记录在 `register.json` 中。（更安全）
- 服务器通过识别玩家的用户名来实现白名单管理（而不是 UUID）。
- 可以通过修改 `.jar` 内 `\assets\lazylogin\lang.json` 自定义文本，或者翻译成其他语言。
