package myblogpractice.com.models.entity;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Comment {
	// idの設定
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long commentId;

	// userid
	private Long accountId;

	// blogid
	private Long blogId;

	// コメント内の図
	private String commentImage;

	// コメントの本文
	private String commentArticle;

	// 前一つのコメント
	private Long parentCommentId;

	// コメント時間
	private Timestamp created_at;

	// コンストラクタ
	public Comment() {

	}

	public Comment(Long accountId, Long blogId, String commentImage, String commentArticle, Long parentCommentId,
			Timestamp created_at) {
		super();
		this.accountId = accountId;
		this.blogId = blogId;
		this.commentImage = commentImage;
		this.commentArticle = commentArticle;
		this.parentCommentId = parentCommentId;
		this.created_at = created_at;
	}

	// getter&setter
	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getBlogId() {
		return blogId;
	}

	public void setBlogId(Long blogId) {
		this.blogId = blogId;
	}

	public String getCommentImage() {
		return commentImage;
	}

	public void setCommentImage(String commentImage) {
		this.commentImage = commentImage;
	}

	public String getCommentArticle() {
		return commentArticle;
	}

	public void setCommentArticle(String commentArticle) {
		this.commentArticle = commentArticle;
	}

	public Long getParentCommentId() {
		return parentCommentId;
	}

	public void setParentCommentId(Long parentCommentId) {
		this.parentCommentId = parentCommentId;
	}

	public Timestamp getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Timestamp created_at) {
		this.created_at = created_at;
	}

}
