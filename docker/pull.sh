#!/bin/bash

# 获取脚本所在的绝对目录（无论从哪个目录执行脚本，都能准确定位）
SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" &>/dev/null && pwd)

# 拼接 .env 文件的绝对路径
ENV_FILE="$SCRIPT_DIR/.env"

# 检查 .env 文件是否存在
if [ -f "$ENV_FILE" ]; then
  # 加载 .env 文件
  source "$ENV_FILE"
else
  echo "Error: $ENV_FILE not found!"
  exit 1
fi

if [ -n "$JAVA_HOME" ]; then  # -n 检查字符串非空（长度大于0）
  export JAVA_HOME
  export PATH="$JAVA_HOME/bin:$PATH"
fi

if [ -n "$MAVEN_HOME" ]; then  # -n 检查字符串非空（长度大于0）
  export PATH="$MAVEN_HOME/bin:$PATH"
fi

mkdir -p ./logs && chmod 777 ./logs

# ========== 1. 拉取最新 Git 代码 ==========
echo "===== 开始拉取最新代码 ====="
cd "$PROJECT_ROOT" || exit  # 进入项目根目录
git pull
if [ $? -ne 0 ]; then
  echo "错误：Git 拉取代码失败，终止流程"
  exit 1
fi


# ========== 2. 打包 fan-application 模块（Maven） ==========
echo "===== 开始打包 fan-application 模块 ====="
mvn clean package -DskipTests  # 清理并打包（跳过测试，如需测试可删除 -DskipTests）
if [ $? -ne 0 ]; then
  echo "错误：Maven 打包失败，终止流程"
  exit 1
fi


# ========== 3. build Docker 镜像（docker-compose build） ==========
echo "===== 开始build Docker 镜像 ====="
cd "$DOCKER_DIR" || exit  # 进入 Docker 配置目录
docker-compose build --no-cache
if [ $? -ne 0 ]; then
  echo "错误：Docker build失败，终止流程"
  exit 1
fi


# ========== 4. 重启 Docker 服务（docker-compose restart） ==========
echo "===== 开始重启服务 ====="
docker-compose up -d
if [ $? -ne 0 ]; then
  echo "错误：服务重启失败，终止流程"
  exit 1
fi


echo "===== 所有操作执行成功！ ====="