### 项目开发环境
操作系统：Windows 10
编译环境：jdk1.8、SDK 27
开发工具：Android Studio 3.2
项目支持最低Android版本: 4.4

手动打包最好，先清理下，gradlew clean
在工程根目录下，使用下面的命令来打包对应的APK：
gradlew assemble[Flavor][BuildType]  其中Flavor对应渠道，BuildType对应构建方式
【例如】
构建所有渠道的正式包：
gradlew assembleRelease
构建官网渠道的正式包：
gradlew assembleniiwooRelease
构建华为渠道的正式包：
gradlew assembleniiwoo_hwRelease
构建测试环境的测试包：
gradlew assembleNiiwoo_testDebug_test
构建体验环境的正式包：
gradlew assembleniiwoo_expRelease
构建灰度环境的正式包：
gradlew assembleniiwoo_proRelease