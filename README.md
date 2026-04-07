# FLYu

FLYu 是一个面向FLYme用户 `com.android.systemui` 的 Xposed / lsposed 模块，提供一组偏状态栏、锁屏与时钟方向的轻量化定制能力，并带有独立配置界面。

## 功能概览

### 状态栏图标

- 隐藏 WiFi 图标
- 隐藏 SIM 图标
- 分别隐藏 SIM1 / SIM2 图标
- 隐藏充电指示图标
- 自定义隐藏右侧状态栏图标槽位

### 时钟增强

- 状态栏时钟加粗
- 显示秒数
- 12 小时制
- 中文上午 / 下午
- 显示星期
- 中文星期
- 时辰模式
- 控制中心时钟显示秒数

### 杂项增强

- 低网速时隐藏网速指示器
- 在状态栏左右滑动调节亮度
- 双击状态栏锁屏

### 锁屏精简

- 隐藏锁屏指纹图标
- 隐藏锁屏顶部运营商
- 隐藏锁屏底部手电筒
- 隐藏锁屏底部相机

### 硬件监控

- 状态栏显示 CPU 温度
- 状态栏显示电池温度
- 状态栏显示充放电功率
- 状态栏显示内存占用
- 支持显示在时钟左侧或右侧

硬件监控依赖 root 权限与项目内置的 native daemon。

## 适用范围

- 模块作用域固定为 `com.android.systemui`
- 适合 Flyme / 魅族系 SystemUI 环境
- 在其他 ROM 上能否生效，取决于目标类名和方法签名是否一致

如果框架没有自动授权作用域，请手动把模块作用域勾选到 `SystemUI`。

## 运行要求

- 已解锁并具备 root 权限的设备
- 支持 libxposed API 101 的 Xposed 框架环境
- `arm64-v8a` 设备架构
- Android 侧可正常运行 `SystemUI`

## 安装使用

1. 在 Xposed 框架中安装并激活模块。
2. 确认模块作用域包含 `com.android.systemui`。
3. 打开 FLYu 配置界面，按需启用功能。
4. 修改完成后，重启 `SystemUI` 或重启设备使 Hook 立即生效。

## 从源码构建

### 环境要求

- JDK 17
- Android SDK
- Android NDK
- CMake 3.22.1

## 项目结构

```text
FLYu/
├─ app/
│  ├─ src/main/java/com/yuuki/flyu/
│  │  ├─ hook/                 # 模块入口与各项 Hook
│  │  ├─ ui/                   # 配置界面
│  │  └─ utils/                # 偏好设置、设备信息、硬件监控管理
│  ├─ src/main/cpp/
│  │  └─ hw_monitor/           # 硬件监控 native daemon
│  ├─ src/main/resources/META-INF/xposed/
│  │  ├─ java_init.list        # Xposed 入口类
│  │  ├─ module.prop           # 模块元数据
│  │  └─ scope.list            # 静态作用域
│  └─ proguard-rules.pro       # Release 混淆与裁剪规则
├─ gradle/
└─ README.md
```

## 技术说明

- 模块入口类为 `com.yuuki.flyu.hook.ModuleMain`
- 使用 `libxposed/service` 做作用域申请与远程偏好同步
- 目标进程为 `SystemUI`
- 硬件监控通过 native 可执行文件采集数据，并回传给 Java 层显示

## 注意事项

- Hook 代码依赖系统私有类和方法，系统更新后可能失效。
- 锁屏、状态栏和硬件监控功能都与 ROM 实现强相关。
- 硬件监控和部分系统操作依赖 root 执行 shell 命令。

## 致谢

项目源码中已引用或致谢以下项目 / 资源：

- `libxposed/api`
- `Flassers/Layout Inspect`
- `SoyBeanMilkx/BetterBar`
- `QmDeve/AndroidLiquidGlassView`

