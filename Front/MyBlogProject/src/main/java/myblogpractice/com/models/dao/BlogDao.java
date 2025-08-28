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
	
	// 指定したaccountIdのブログを作成日時の降順で上位6件取得する
	List<Blog> findTop6ByAccountIdOrderByCreatedAtDesc(Long accountId);
	// ブログタイトルで1件取得する
	Blog findByBlogTitle(String blogTitle);
	// blogIdで1件取得する
	Blog findByBlogId(Long blogId);
	// blogIdで削除を行い、削除件数を返す
	Long deleteByBlogId(Long blogId);
	// 指定したアカウントに同じタイトルが存在するか確認する
	boolean existsByBlogTitleAndAccountId(String blogTitle, Long accountId);
	// 指定したアカウントで、指定したblogId以外に同じタイトルが存在するか確認する
	boolean existsByBlogTitleAndAccountIdAndBlogIdNot(String blogTitle, Long accountId, Long blogId);

	// 保存処理と更新処理 insertとupdate
	Blog save(Blog blog);
	// 指定アカウントIDで、非公開でないブログ一覧を取得する
	List<Blog> findByAccountIdAndVisibilityNot(Long accountId, Integer visibility);
	// 指定アカウント以外で、指定可視性のブログ一覧を取得する
	List<Blog> findByAccountIdNotAndVisibility(Long accountId, Integer visibility);
	// 指定アカウントIDで、可視性がnullのブログ一覧を取得する
	List<Blog> findByAccountIdAndVisibilityIsNull(Long accountId);
	// 指定アカウントIDで、作成日時の降順にブログ一覧を取得する
	List<Blog> findByAccountIdOrderByCreatedAtDesc(Long accountId);
	
	@Query("SELECT b FROM Blog b " +
	           "WHERE b.accountId = :uid AND (" +
	           "      LOWER(b.blogTitle)    LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
	           "      LOWER(b.categoryName) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
	           "      LOWER(b.article)      LIKE LOWER(CONCAT('%', :kw, '%'))" +
	           ") ORDER BY b.createdAt DESC")
	List<Blog> searchMyPosts(@Param("uid") Long uid, @Param("kw") String keyward);

}
