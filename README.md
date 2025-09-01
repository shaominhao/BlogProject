# 個人作業「BlogPress」

## 目次
- プロジェクト概要
- 使用技術
- 画面イメージ（省略）
- ユースケース図（省略）
- テーブル設計
- URL設計
- ディレクトリ構成
- 機能一覧／工夫した点
- セットアップ & 実行方法
- 今後の課題
- 作成者情報

---

##  プロジェクト概要
Blog は Spring Boot で開発した個人向けブログアプリです。  
ユーザーはログイン後に記事の **投稿 / 編集 / 削除 / 検索** ができ、他ユーザーの記事に **コメント（画像添付・スレッド返信・削除）** を残せます。  
権限対策などの基本的なセキュリティも備えています。

---

## ⚙ 使用技術
- バックエンド：Java 17 / Spring Boot / JPA
- フロントエンド：HTML / CSS / JavaScript / Thymeleaf
- データベース：PostgreSQL
- ビルド・管理：Maven / GitHub

---

---

##  テーブル設計（サンプル）

```sql
-- users テーブル
CREATE TABLE users (
  id            BIGSERIAL PRIMARY KEY,
  username      VARCHAR(50) UNIQUE NOT NULL,
  password      VARCHAR(255) NOT NULL
);

-- posts テーブル
CREATE TABLE posts (
  id            BIGSERIAL PRIMARY KEY,
  title         VARCHAR(100) NOT NULL,
  content       TEXT NOT NULL,
  author_id     BIGINT NOT NULL REFERENCES users(id),
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP
);

-- comments テーブル
CREATE TABLE comments (
  id               BIGSERIAL PRIMARY KEY,
  content          TEXT,
  image_path       VARCHAR(255),
  post_id          BIGINT NOT NULL REFERENCES posts(id),
  user_id          BIGINT NOT NULL REFERENCES users(id),
  parent_comment_id BIGINT REFERENCES comments(id),
  created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
