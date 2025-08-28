package myblogpractice.com.controllers;
import myblogpractice.com.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import myblogpractice.com.models.dao.CommentDao;
import myblogpractice.com.models.entity.Account;
import myblogpractice.com.models.entity.Comment;
import myblogpractice.com.services.BlogService;

@Controller
public class DeleteController {

	@Autowired
	public CommentService commentService;

	@Autowired
	public BlogService blogService;
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private CommentDao commentDao;


    DeleteController(CommentService commentService) {
        this.commentService = commentService;
    }
	
	
	//
	@PostMapping("/blog/delete")
	public String deleteBlog(@RequestParam Long blogId) {
		// blogServiceを呼び出して記事を削除する（削除処理の実行）
			blogService.deleteAtrical(blogId);
			// 削除完了後、記事一覧画面（/blog/register）にリダイレクトする
		return "redirect:/blog/register";
	}
	
	@PostMapping("/blog/comments/{cid}/delete")
	public String deleteComment(@PathVariable("cid") Long commentId,RedirectAttributes ra) {
		Account login = (Account) session.getAttribute("loginAccountInfo");
	    Long uid = (Long) session.getAttribute("uid");
	    if (login == null || uid == null) {
	        ra.addFlashAttribute("err", "ログインが必要です。");
	        return "redirect:/login";
	    }

	    // コメント存在確認
	    Comment target = commentDao.findById(commentId).orElse(null);
	    if (target == null) {
	        ra.addFlashAttribute("err", "コメントが見つかりません。");
	        return "redirect:/blog/list";
	    }
	    
	    Long blogId = target.getBlogId();
	    if (blogId == null) {
	        ra.addFlashAttribute("err", "ブログ情報が不正です。");
	        return "redirect:/blog/list";
	    }


	    // 削除実行
	    commentService.deleteOne(commentId);

	    ra.addFlashAttribute("ok", "コメントを削除しました。");
	    return "redirect:/blog/detail/" + blogId;
	}
}
