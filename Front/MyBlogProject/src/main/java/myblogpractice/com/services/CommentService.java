package myblogpractice.com.services;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import myblogpractice.com.models.dao.CommentDao;
import myblogpractice.com.models.entity.Comment;


@Service
@Transactional
public class CommentService {

	@Autowired
	public CommentDao commentDao;

	// コメントを新規作成（親なし）
	public Comment create(Long accountId, Long blogId, String commentImage, String commentArticle,
			Timestamp createdAt) {
		Comment c = new Comment(accountId, blogId, commentImage, commentArticle, null, Timestamp.from(Instant.now()));
		return commentDao.save(c);
	}

	// 返信コメントを作成（親あり）
	public Comment reply(Long accountId, Long blogId, Long parentCommentId, String commentImage, String commentArticle,
			Timestamp createdAt) {
		Comment c = new Comment(accountId, blogId, commentImage, commentArticle, parentCommentId,
				Timestamp.from(Instant.now()));
		return commentDao.save(c);
	}

	
	
//	public void deleteComment(Long commentId) {
//		long n = commentDao.deleteBycommentId(commentId);
//		if (n == 0)
//			throw new SecurityException("not allowed to delete this blog");
//	}
}
