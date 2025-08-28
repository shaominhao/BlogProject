package myblogpractice.com.models.dao;

import myblogpractice.com.models.entity.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentDao extends JpaRepository<Comment, Long> {

	// 指定ブログIDのコメント一覧を取得（並び順は引数のSortで指定する：例 Sort.by("created_at").ascending()）
	List<Comment> findByBlogId(Long blogId, Sort sort);

	// ルートコメント（親なし）のみ取得（並び順はSortで指定）
	List<Comment> findByBlogIdAndParentCommentIdIsNull(Long blogId, Sort sort);

	// 指定コメントIDに対する返信一覧を取得（並び順はSortで指定）
	List<Comment> findByParentCommentId(Long parentCommentId, Sort sort);

	// commentIdで削除し、削除件数を返す
	
	List<Comment> findByBlogIdOrderByCreatedAtAsc(Long blogId);

	List<Comment> findByBlogIdAndParentCommentIdIsNullOrderByCreatedAtAsc(Long blogId);

	List<Comment> findByParentCommentIdOrderByCreatedAtAsc(Long parentCommentId);
	
	int deleteByCommentId( Long commentId);
}


