package myblogpractice.com.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import myblogpractice.com.models.dao.AccountDao;
import myblogpractice.com.models.dao.BlogDao;
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
}
