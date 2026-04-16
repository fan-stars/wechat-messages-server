#!/bin/bash
# ==============================================================
# Docker 配置迁移脚本
# 用途：将 script/docker 目录下的配置文件复制到指定目标目录
# 使用方式：
#   bash move.sh <目标目录>    # 命令行指定目录
#   bash move.sh              # 交互式输入目录
#
# 示例：
#   bash move.sh /opt/fan-docker
#   bash move.sh
# ==============================================================

# 检查参数数量，如果未提供目标目录则交互式输入
if [ $# -eq 0 ]; then
  echo ""
  echo "===== Docker 配置迁移工具 ====="
  read -p "请输入目标目录路径: " TARGET_DIR
  if [ -z "$TARGET_DIR" ]; then
    echo "错误：目录路径不能为空"
    exit 1
  fi
else
  TARGET_DIR="$1"
fi

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# 计算项目根目录
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "===== Docker 配置迁移工具 ====="
echo "源目录: $SCRIPT_DIR"
echo "目标目录: $TARGET_DIR"

# 检查目标目录是否已有文件
if [ -d "$TARGET_DIR" ] && [ "$(ls -A "$TARGET_DIR" 2>/dev/null)" ]; then
  echo "错误：目标目录已存在且非空，请先清空或使用空目录"
  exit 1
fi

# 创建目标目录
mkdir -p "$TARGET_DIR"

# 复制配置文件
echo "正在复制配置文件..."
cp "$SCRIPT_DIR/.env" "$TARGET_DIR/.env"
echo "  已复制: .env"
cp "$SCRIPT_DIR/docker-compose.yaml" "$TARGET_DIR/docker-compose.yaml"
echo "  已复制: docker-compose.yaml"
cp "$SCRIPT_DIR/pull.sh" "$TARGET_DIR/pull.sh"
echo "  已复制: pull.sh"
if [ -f "$SCRIPT_DIR/restart.sh" ]; then
  cp "$SCRIPT_DIR/restart.sh" "$TARGET_DIR/restart.sh"
  echo "  已复制: restart.sh"
else
  echo "  跳过: restart.sh（源文件不存在）"
fi

# 从源 .env 读取原始的 DOCKER_FILE_DIR 相对路径
# 将所有 ../ 替换为实际路径
ORIGINAL_DOCKER_FILE_DIR_REL=$(grep "^DOCKER_FILE_DIR=" "$SCRIPT_DIR/.env" | cut -d'=' -f2)
# 计算相对路径的绝对路径（相对于 PROJECT_ROOT）
# 如果是 ../../xxx 这种形式，去掉 ../.. 前缀
# 统计 ../ 的数量并移除
temp_path="$ORIGINAL_DOCKER_FILE_DIR_REL"
while [[ "$temp_path" == ../* ]]; do
  temp_path="${temp_path#../}"
done
# temp_path 现在是去掉了所有 ../ 前缀后的路径
# 将其拼接到 PROJECT_ROOT 上
DOCKER_FILE_DIR="$PROJECT_ROOT/$temp_path"

# 重写 .env 文件，使用绝对路径
cat > "$TARGET_DIR/.env" << ENVEOF
# Docker 部署配置文件
# 用途：定义 docker-compose.yaml 中使用的环境变量

# 项目根目录的绝对路径
PROJECT_ROOT=$PROJECT_ROOT

# Docker 配置文件所在目录的绝对路径
DOCKER_DIR=$TARGET_DIR

# Maven 安装路径（可选）
MAVEN_HOME=

# Dockerfile 所在目录的绝对路径
DOCKER_FILE_DIR=$DOCKER_FILE_DIR

# Docker 容器对外暴露的端口号
PORT=48080
ENVEOF

echo "已更新 .env 文件（使用绝对路径）"
echo "  PROJECT_ROOT=$PROJECT_ROOT"
echo "  DOCKER_DIR=$TARGET_DIR"
echo "  DOCKER_FILE_DIR=$DOCKER_FILE_DIR"

echo ""
echo "===== 迁移完成 ====="
echo "文件已复制到: $TARGET_DIR"
echo ""
echo "可执行以下命令启动服务："
echo "  cd $TARGET_DIR"
echo "  bash pull.sh"