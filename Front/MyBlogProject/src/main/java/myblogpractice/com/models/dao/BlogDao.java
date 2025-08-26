package myblogpractice.com.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import myblogpractice.com.models.entity.Blog;

@Repository
@Transactional
public interface BlogDao extends JpaRepository<Blog, Long> {

	List<Blog> findTop6ByAccountIdOrderByCreatedAtDesc(Long accountId);

	Blog findByBlogTitle(String blogTitle);

	Blog findByBlogId(Long blogId);

	Long deleteByBlogIdAndAccountId(Long blogId, Long accountId);

	boolean existsByBlogTitleAndAccountId(String blogTitle, Long accountId);

	boolean existsByBlogTitleAndAccountIdAndBlogIdNot(String blogTitle, Long accountId, Long blogId);

	// 保存処理と更新処理 insertとupdate
	Blog save(Blog blog);

	List<Blog> findByAccountIdAndVisibilityNot(Long accountId, Integer visibility);

	List<Blog> findByAccountIdNotAndVisibility(Long accountId, Integer visibility);

	List<Blog> findByAccountIdAndVisibilityIsNull(Long accountId);

	List<Blog> findByAccountIdOrderByCreatedAtDesc(Long accountId);

}
