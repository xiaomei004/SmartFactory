# Git 开发学习指南

> 基于 SmartFactory 项目的实际操作记录总结，面向初学者。
>
> 上次更新：2026-06-30

---

## 一、你的操作回顾 ✅

从 SmartFactory 项目的 git 历史中，可以看到你完成了以下操作：

```
8093813  合并master分支代码到main主干    ← 合并分支
3566ab1  搭建springFramework             ← 功能开发提交
1c96981  Initial commit                  ← 项目初始化
```

**你当前项目状态：**

| 项目 | 详情 |
|------|------|
| 当前分支 | `feature/springHello` |
| 远程仓库 | `https://github.com/xiaomei004/SmartFactory.git` |
| 本地分支 | `main`, `feature/springHello` |
| 远程分支 | `origin/main` |

**操作逐条分析：**

| 你的操作 | 对应的 Git 命令 | 是否正确 | 说明 |
|----------|----------------|----------|------|
| 初始化项目并首次提交 | `git init` → `git add .` → `git commit` | ✅ | Initial commit 没问题 |
| 在 master 分支开发 Spring 框架 | `git checkout master` → 写代码 → `git commit` | ✅ | 正常的功能开发流程 |
| 将 master 合并到 main | `git checkout main` → `git merge master` | ✅ | 合并分支代码到主干 |
| 创建 feature 分支 | `git checkout -b feature/springHello` | ✅ | 标准的 feature 分支命名 |
| 关联 GitHub 远程仓库 | `git remote add origin <url>` | ✅ | 与远程仓库建立了连接 |

> **总结：你的操作都是正确的，已经掌握了 Git 最基本的用法！** 👍

---

## 二、核心概念速览

### 2.1 Git 是什么

Git 是一个**分布式版本控制系统**，用来追踪代码的每一次改动。你可以把它理解为代码的"时光机"——随时可以回到任何一个历史版本。

### 2.2 三个关键区域

```
┌─────────────┐    git add    ┌─────────────┐   git commit   ┌─────────────┐
│  工作目录    │  ──────────→  │  暂存区      │  ──────────→   │  本地仓库    │
│ (Workspace) │               │ (Staging)   │                │ (Repository)│
└─────────────┘               └─────────────┘                └──────┬──────┘
                                                                    │
                                                              git push
                                                                    │
                                                                    ▼
                                                             ┌─────────────┐
                                                             │  远程仓库    │
                                                             │  (GitHub)   │
                                                             └─────────────┘
```

- **工作目录**：你电脑上看到的项目文件夹，正在编辑的文件在这里
- **暂存区**：`git add` 之后的中间区域，相当于"购物车"，决定哪些文件要提交
- **本地仓库**：`git commit` 之后，改动正式存入本地版本库
- **远程仓库**：GitHub 上的仓库，`git push` 把本地 commit 同步上去

### 2.3 分支是什么

分支就像一条独立的开发线。`main` 是主干（稳定版本），你可以创建自己的分支在上面开发新功能，不影响主干的稳定性。

```
main:     ● ──── ● ──── ● ──── ● ──── ●  (稳定版本)
                        \
feature/hello:           ● ──── ● ──── ●  (开发中的新功能)
```

---

## 三、常用 Git 命令速查

### 3.1 基础配置（只需做一次）

```bash
# 设置你的身份信息（会出现在每次 commit 里）
git config --global user.name "你的名字"
git config --global user.email "你的邮箱@example.com"
```

### 3.2 创建/克隆仓库

```bash
# 在本地文件夹中初始化一个 git 仓库
git init

# 从 GitHub 克隆一个已有的仓库到本地
git clone https://github.com/用户名/仓库名.git
```

### 3.3 日常开发流程（最常用）

```bash
# 查看当前状态：哪些文件改了、哪些在暂存区
git status

# 将文件加入暂存区
git add 文件名          # 添加指定文件
git add .               # 添加当前目录下所有改动

# 提交到本地仓库
git commit -m "描述你做了什么"

# 推送到 GitHub
git push origin 分支名

# 从 GitHub 拉取最新代码
git pull origin 分支名
```

### 3.4 分支操作

```bash
# 查看所有分支
git branch              # 本地分支
git branch -a           # 包含远程分支

# 创建分支
git branch 分支名

# 切换分支
git checkout 分支名      # 传统方式
git switch 分支名        # 新版推荐方式

# 创建 + 切换（一步到位）
git checkout -b 分支名   # 传统方式
git switch -c 分支名     # 新版推荐方式

# 删除分支
git branch -d 分支名     # 删除已合并的分支
git branch -D 分支名     # 强制删除
```

### 3.5 合并代码

```bash
# 将目标分支的代码合并到当前分支
# 例如：当前在 main，想把 feature/hello 合并进来
git merge feature/hello
```

### 3.6 查看历史

```bash
# 查看提交日志
git log                        # 详细日志
git log --oneline              # 一行一条，简洁
git log --oneline --graph      # 带分支图的简洁日志 👈 推荐
git log --oneline --graph --all # 查看所有分支的历史
```

### 3.7 撤销操作

```bash
# 撤销工作区的修改（回到最近一次 commit 的状态）
git checkout -- 文件名
git restore 文件名              # 新版推荐方式

# 从暂存区移除（但不删除文件）
git reset HEAD 文件名
git restore --staged 文件名     # 新版推荐方式

# 回退到上一个 commit（保留修改）
git reset --soft HEAD~1

# 完全回退到上一个 commit（丢弃修改）⚠️ 谨慎使用
git reset --hard HEAD~1
```

---

## 四、GitHub 协作流程

### 4.1 远程仓库管理

```bash
# 查看远程仓库配置
git remote -v

# 添加远程仓库
git remote add origin https://github.com/用户名/仓库名.git

# 修改远程仓库地址
git remote set-url origin 新地址
```

### 4.2 推荐的开发工作流

以你当前的 SmartFactory 项目为例，推荐的工作流程：

```
第1步：切换到主干，拉取最新代码
  git checkout main
  git pull origin main

第2步：从主干创建功能分支
  git checkout -b feature/你的功能名

第3步：在功能分支上开发...
  写代码 → git add . → git commit -m "做了xxx"

第4步：推送功能分支到 GitHub
  git push origin feature/你的功能名

第5步：在 GitHub 网页上创建 Pull Request（PR）
  让同事 review 代码，通过后合并到 main

第6步：合并后，切回 main 并拉取最新代码
  git checkout main
  git pull origin main

第7步：删除已合并的功能分支（可选）
  git branch -d feature/你的功能名
```

### 4.3 分支命名规范

| 前缀 | 用途 | 示例 |
|------|------|------|
| `feature/` | 新功能开发 | `feature/springHello` ✅ 你已经在用了 |
| `bugfix/` | 修 Bug | `bugfix/login-error` |
| `hotfix/` | 紧急修复 | `hotfix/security-patch` |
| `release/` | 发布准备 | `release/v1.0.0` |

---

## 五、IntelliJ IDEA 中的 Git 操作

IDEA 已经内置了 Git 支持，你不需要敲命令行也可以完成大部分操作。

### 5.1 常用 IDEA Git 操作对照

| 操作 | 命令行 | IDEA 方式 |
|------|--------|-----------|
| 提交代码 | `git commit` | `Ctrl + K`（Windows）/ `Cmd + K`（Mac） |
| 推送代码 | `git push` | `Ctrl + Shift + K` / `Cmd + Shift + K` |
| 拉取代码 | `git pull` | `Ctrl + T` / `Cmd + T` |
| 查看历史 | `git log` | 底部 `Git` 面板 → `Log` 标签 |
| 分支管理 | `git branch` | 右下角点击分支名，或 `Git` 面板 → `Branches` |
| 查看改动 | `git diff` | `Git` 面板 → `Commit` 标签，或双击文件 |

### 5.2 IDEA 常用 Git 界面位置

- **底部工具栏**：`Git` 面板（查看改动、日志、分支）
- **右上角**：绿色的 `✔`（提交）、蓝色的 `↗`（推送）、蓝色的 `↘`（拉取）
- **右下角**：当前分支名称，点击可以切换/创建分支
- **文件颜色标识**：
  - 🔴 红色：新文件，未加入 git
  - 🟢 绿色：已加入 git，内容有改动
  - 🔵 蓝色：文件内容有改动
  - ⚪ 白色/灰色：未改动

### 5.3 IDEA 配置 Git 用户信息

如果发现 commit 的作者名字不对（比如之前出现过 `xiaomei004` 和 `Mei Sure` 两个名字，已统一），可以检查：

- **File → Settings → Version Control → Git**：确认 Git 路径正确
- 或者打开 IDEA 的 Terminal，输入：
  ```bash
  git config user.name    # 查看当前项目的用户名
  git config user.email   # 查看当前项目的邮箱
  ```

> 💡 **提示**：你的仓库里初始 commit 作者是 `xiaomei004`，后面变成了 `Mei Sure`。<br>
> 这说明你可能在 IDEA 首次配置时用了不同的用户名。已帮你统一为 `xiaomei004`，后续提交都会用这个名字。

---

## 六、.gitignore 文件

你已经有了 `.gitignore`（Spring Initializr 自动生成的），这个文件告诉 Git **哪些文件不需要追踪**。

当前你的 `.gitignore` 忽略了：
- `target/` — Maven 编译输出
- `.idea/` — IDEA 配置文件（⚠️ 实际上你的 `.idea` 已被追踪了，说明是先提交再添加的 gitignore 规则）
- `*.iml` — IDEA 模块文件
- `build/` — 构建输出

> **建议**：如果某些 IDEA 配置文件（如 `.idea/workspace.xml`）经常变动但不需要共享，可以把 `.idea/` 加入 gitignore 并停止追踪：
> ```bash
> git rm -r --cached .idea
> git commit -m "停止追踪.idea文件夹"
> ```

---

## 七、常见问题与解决

### 7.1 "Please enter a commit message" 

出现这个是因为 `git commit` 没有加 `-m`。如果你进入了 vim 编辑器：
1. 按 `i` 进入编辑模式
2. 输入提交信息
3. 按 `Esc`，输入 `:wq` 回车保存退出

**建议**：始终使用 `git commit -m "你的描述"` 避免进入 vim。

### 7.2 合并冲突 (Merge Conflict)

当两个分支修改了同一个文件的同一行时，合并会产生冲突。Git 会在文件中标记：

```
<<<<<<< HEAD
当前分支的内容
=======
要合并进来的内容
>>>>>>> feature/xxx
```

**解决步骤**：
1. 手动编辑文件，选择保留哪个版本（删除 `<<<<<<<`、`=======`、`>>>>>>>` 标记）
2. `git add 文件名`
3. `git commit -m "解决合并冲突"`

### 7.3 master vs main

- `master` 是旧版默认分支名
- `main` 是 GitHub 2020年后的新默认名
- 你的项目中出现了两个：`master` 被合并进了 `main`
- **建议**：统一使用 `main` 作为主干分支，删除 `master`

```bash
git branch -d master  # 删除本地 master
```

### 7.4 提交错了想撤回

```bash
# 情况1：刚 commit 但还没 push（最安全）
git reset --soft HEAD~1   # 撤销 commit，保留修改在暂存区

# 情况2：修改了文件但还没 add
git restore 文件名         # 丢弃工作区的修改

# 情况3：已经 add 了但还没 commit
git restore --staged 文件名  # 从暂存区移出
```

---

## 八、每日开发速查表

```
上班第一件事：
  git checkout main          # 切到主干
  git pull origin main       # 拉取最新代码

开始新功能：
  git checkout -b feature/新功能    # 创建并切换分支

开发中反复做：
  git add .                         # 暂存所有改动
  git commit -m "做了什么"           # 提交到本地

一天结束前：
  git push origin feature/新功能     # 推送到 GitHub

功能完成：
  git checkout main                 # 切回主干
  git pull origin main              # 拉取最新
  git merge feature/新功能           # 合并到主干
  git push origin main              # 推送主干
  git branch -d feature/新功能       # 删除功能分支
```

---

## 九、进阶推荐（下一步学习）

掌握了基础后，建议学习：

| 阶段 | 内容 | 说明 |
|------|------|------|
| 现在 ✅ | `add`, `commit`, `push`, `pull`, `branch`, `merge` | 你已经会了 |
| 下一步 | `stash`（暂存工作现场）、`rebase`（整理提交历史） | 让历史更整洁 |
| 再下一步 | Pull Request 流程、Code Review | GitHub 协作核心 |
| 进阶 | `cherry-pick`（挑选提交）、`tag`（版本标记）、CI/CD | 专业开发必备 |
| 团队 | Git Flow / GitHub Flow 工作流 | 团队协作规范 |

---

> 📚 **推荐资源**
> - [Pro Git 中文版](https://git-scm.com/book/zh/v2) — 官方教程
> - [GitHub Skills](https://skills.github.com/) — 交互式学习
> - [Learn Git Branching](https://learngitbranching.js.org/) — 可视化练习分支操作
