package myblogpractice.com.models.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Blog {
	// idの設定
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long blogId;

	private String blogTitle;

	private String categoryName;

	private String blogImage;

	private String article;

	@Column(name = "account_id")
	private Long accountId;

	private Timestamp createdAt;

	private Timestamp updatedAt;

	// 1=公開, 2=公開(コメント不可), 3=非公開
	private int visibility;

	public Blog() {
	}

	public Blog(String blogTitle, String categoryName, String blogImage, String article, Long accountId,
			Timestamp createdAt, Timestamp updatedAt, int visibility) {
		this.blogTitle = blogTitle;
		this.categoryName = categoryName;
		this.blogImage = blogImage;
		this.article = article;
		this.accountId = accountId;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.visibility = visibility;
	}

	public Long getBlogId() {
		return blogId;
	}

	public void setBlogId(Long blogId) {
		this.blogId = blogId;
	}

	public String getBlogTitle() {
		return blogTitle;
	}

	public void setBlogTitle(String blogTitle) {
		this.blogTitle = blogTitle;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getBlogImage() {
		return blogImage;
	}

	public void setBlogImage(String blogImage) {
		this.blogImage = blogImage;
	}

	public String getArticle() {
		return article;
	}

	public void setArticle(String article) {
		this.article = article;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreated_at(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdated_at(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public int getVisibility() {
		return visibility;
	}

	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

}
