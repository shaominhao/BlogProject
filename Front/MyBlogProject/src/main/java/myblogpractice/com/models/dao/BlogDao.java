package myblogpractice.com.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import myblogpractice.com.models.entity.Blog;

@Repository
@Transactional
public interface BlogDao extends JpaRepository<Blog, Long> {

	List<Blog> findTop6ByAccountIdOrderByCreatedAtDesc(Long accountId);

	Blog save(Blog blog);

}
