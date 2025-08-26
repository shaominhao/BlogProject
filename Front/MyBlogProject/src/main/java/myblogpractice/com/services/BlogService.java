package myblogpractice.com.services;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import jakarta.servlet.http.HttpSession;
import myblogpractice.com.models.dao.AccountDao;
import myblogpractice.com.models.dao.BlogDao;
import myblogpractice.com.models.entity.Account;
import myblogpractice.com.models.entity.Blog;

@Service
public class BlogService {

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private BlogDao blogDao;

	public List<Blog> findRecentPostsByUser(Long userId) {
		return blogDao.findTop6ByAccountIdOrderByCreatedAtDesc(userId);
	}

	// blog登録処理

	public boolean createArticle(String blogTitle, String categoryName, String blogImage, String article,
			Long accountId, Integer visibility) {

		if (blogDao.existsByBlogTitleAndAccountId(blogTitle, accountId)) {
			return false;
		}

		Blog b = new Blog();
		b.setBlogTitle(blogTitle);
		b.setCategoryName(categoryName);
		b.setBlogImage(blogImage);
		b.setArticle(article);
		b.setAccountId(accountId);
		if (visibility != null)
			b.setVisibility(visibility);

		b.setCreatedAt(Timestamp.from(Instant.now()));
		b.setUpdatedAt(Timestamp.from(Instant.now()));

		blogDao.save(b);
		return true;
	}

	// 更新処理
	public boolean updateArticle(Long blogId, String blogTitle, String categoryName, String blogImage, String article,
			Long uid, Integer visibility) {
		Blog b = blogDao.findById(blogId).orElseThrow();

		if (!Objects.equals(b.getAccountId(), uid)) {
			throw new SecurityException("not allowed");
		}

		if (blogTitle != null && !blogTitle.isBlank()
				&& blogDao.existsByBlogTitleAndAccountIdAndBlogIdNot(blogTitle, uid, blogId)) {
			return false;
		}

		if (blogTitle != null)
			b.setBlogTitle(blogTitle);
		if (categoryName != null)
			b.setCategoryName(categoryName);
		if (blogImage != null)
			b.setBlogImage(blogImage);
		if (article != null)
			b.setArticle(article);
		if (visibility != null)
			b.setVisibility(visibility);

		b.setUpdatedAt(Timestamp.from(Instant.now()));
		blogDao.save(b);
		return true;
	}

	// 削除処理
	public void deleteAtrical(Long blogId, Long uid) {
		long n = blogDao.deleteByBlogIdAndAccountId(blogId, uid);
		if (n == 0)
			throw new SecurityException("not allowed to delete this blog");
	}

	// Userから訪問可能なblogを選ぶ
	public List<Blog> accessWithUser(Long uid) {
		var mine = blogDao.findByAccountIdAndVisibilityNot(uid, 2);
		var others = blogDao.findByAccountIdNotAndVisibility(uid, 1);
		return java.util.stream.Stream.concat(mine.stream(), others.stream())
				.sorted(java.util.Comparator.comparing(Blog::getCreatedAt).reversed()).toList();
	}

	// Adminから訪問可能なblogを選ぶ
	public List<Blog> accessWithAdmin(Long uid) {
		return blogDao.findByAccountIdOrderByCreatedAtDesc(uid);
	}
	
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
        registry.addResourceHandler("/blog-img/**")
                .addResourceLocations("file:" + root.toString() + "/");
    }
}
