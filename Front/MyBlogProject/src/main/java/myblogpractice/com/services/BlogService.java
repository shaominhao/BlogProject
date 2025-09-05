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
	public void deleteAtrical(Long blogId) {
		long n = blogDao.deleteByBlogId(blogId);
		if (n == 0)
			throw new SecurityException("not allowed to delete this blog");
	}

	// Userから訪問可能なblogを選ぶ
	public List<Blog> accessWithUser(Long uid) {
	    //（ 1/2/3）
	    List<Blog> mine = blogDao.findByAccountIdOrderByCreatedAtDesc(uid);

	    // 公開(コメント不可)
	    List<Blog> othersPub  = blogDao.findByAccountIdNotAndVisibility(uid, 1);
	    List<Blog> othersNoCmt = blogDao.findByAccountIdNotAndVisibility(uid, 3);

	    List<Blog> all = new java.util.ArrayList<>();
	    if (mine != null) {all.addAll(mine);}
	    if (othersPub != null) all.addAll(othersPub);
	    if (othersNoCmt != null) all.addAll(othersNoCmt);


	    all.sort(java.util.Comparator.comparing(
	        Blog::getCreatedAt,
	        java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())
	    ).reversed());

	    return all.size() > 50 ? all.subList(0, 50) : all;
	}

	// Adminから訪問可能なblogを選ぶ
	public List<Blog> accessWithAdmin(Long uid) {
		return blogDao.findByAccountIdOrderByCreatedAtDesc(uid);
	}

	public Blog findByblogId(Long blogid) {
		return blogDao.findByBlogId(blogid);
	}

	public Blog updateStatus(Long blogId, int visibility, Long uid) {

		Blog blog = blogDao.findByBlogId(blogId);
		blog.setVisibility(visibility);
		return blog;
	}

	public List<Blog> searchMyPosts(Long uid, String keyword) {
		return blogDao.searchMyPosts(uid, keyword);
	}
	
	public List<Blog> listAllAccessible(Long uid) {
        List<Blog> mine   = blogDao.findByAccountIdOrderByCreatedAtDesc(uid);
        List<Blog> others = blogDao.findByAccountIdNotAndVisibilityInOrderByCreatedAtDesc(
                uid, java.util.List.of(1, 2)
        );

        java.util.ArrayList<Blog> all = new java.util.ArrayList<>();
        if (mine   != null) all.addAll(mine);
        if (others != null) all.addAll(others);

        all.sort(java.util.Comparator.comparing(
                Blog::getCreatedAt,
                java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())
        ).reversed());
        return all;
    }

    // 


    private static final List<Integer> PUBLIC_VIS = List.of(1, 3);

    public List<Blog> searchAllAccessible(Long uid, String kw) {
        final String k = kw == null ? "" : kw.trim();
        List<Blog> mine   = blogDao.searchMine(uid, k);
        List<Blog> others = blogDao.searchOthersVisible(uid, PUBLIC_VIS, k);
        return java.util.stream.Stream.concat(mine.stream(), others.stream())
                .sorted(java.util.Comparator.comparing(Blog::getCreatedAt).reversed())
                .toList();
    }
}
