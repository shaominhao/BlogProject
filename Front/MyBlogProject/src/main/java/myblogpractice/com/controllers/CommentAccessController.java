package myblogpractice.com.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import myblogpractice.com.models.dao.AccountDao;
import myblogpractice.com.models.dao.BlogDao;
import myblogpractice.com.models.dao.CommentDao;
import myblogpractice.com.models.entity.Account;
import myblogpractice.com.models.entity.Blog;
import myblogpractice.com.models.entity.Comment;
import myblogpractice.com.services.BlogService;
import myblogpractice.com.services.CommentService;

@Controller
public class CommentAccessController {

	@Autowired
	public HttpSession session;

	@Autowired
	public BlogService blogService;

	@Autowired
	public BlogDao blogDao;

	@Autowired
	public AccountDao accountDao;

	@Autowired
	public CommentDao commentDao;

	@Autowired
	public CommentService commentService;

	@GetMapping("/blog/detail/{id:\\d+}")
	public String commentAccess(@PathVariable("id") Long blogId, Model model) {

		// ログイン確認（uid と アカウント情報）
		Account login = (Account) session.getAttribute("loginAccountInfo");
		Long uid = (Long) session.getAttribute("uid");
		if (login == null || uid == null) {
			return "redirect:/login";
		}
		model.addAttribute("userName", login.getAccountName());
		model.addAttribute("uid", uid);
		
		//
		Blog blog = blogDao.findByBlogId(blogId);
		if (blog == null) {
			model.addAttribute("err", "対象のブログが見つかりません。");
			return "redirect:/blog/list";
		}
		model.addAttribute("blog", blog);

		//
		Account author = accountDao.findByAccountId(blog.getAccountId());
		model.addAttribute("authorName", (author != null) ? author.getAccountName() : "Unknown");

		//
		boolean canDeleteOthersComment = uid.equals(blog.getAccountId());
		model.addAttribute("canDeleteOthersComment", canDeleteOthersComment);

		//
		var sortAsc = org.springframework.data.domain.Sort.by("createdAt").ascending();

		// （parent_comment_id null）
		List<Comment> roots = commentDao.findByBlogIdAndParentCommentIdIsNull(blogId, sortAsc);
		model.addAttribute("comments", roots);

		// parentId
		List<Comment> all = commentDao.findByBlogId(blogId, sortAsc);
		Map<Long, List<Comment>> repliesMap = all.stream().filter(c -> c.getParentCommentId() != null)
				.collect(java.util.stream.Collectors.groupingBy(Comment::getParentCommentId));
		model.addAttribute("repliesMap", repliesMap);

		// Map<accountId, accountName>
		Map<Long, String> commentNames = new java.util.HashMap<>();
		for (Comment c : all) {
			Long accId = c.getAccountId();
			if (accId != null && !commentNames.containsKey(accId)) {
				Account a = accountDao.findByAccountId(accId);
				if (a != null)
					commentNames.put(accId, a.getAccountName());
			}
		}
		model.addAttribute("commentNames", commentNames);

		return "blog_detail";
	}

	@PostMapping("/blog/{blogId}/comments")
	public String submitComment(@PathVariable Long blogId, @RequestParam String commentArticle, 
			@RequestParam(name = "commentImage", required = false) MultipartFile commentImage, 
			@RequestParam(name = "parentCommentId", required = false) Long parentCommentId, 
			RedirectAttributes ra, HttpSession session, Model model) {

		// 1) ログイン確認
		Long uid = (Long) session.getAttribute("uid");
		if (uid == null) {
			ra.addFlashAttribute("err", "ログインが必要です。");
			return "redirect:/login";
		}

		// 2) ブログ存在確認
		Blog blog = blogDao.findByBlogId(blogId);
		if (blog == null) {
			ra.addFlashAttribute("err", "対象のブログが見つかりません。");
			return "redirect:/blog/list";
		}

		// 3) コメント可否
		// visibility==2 を「公開(コメント不可)」として扱う例
		if (blog.getVisibility() == 2) {
			ra.addFlashAttribute("err", "この投稿はコメント不可です。");
			return "redirect:/blog/detail/" + blogId;
		}

		// 4) 入力バリデーション
		String body = (commentArticle != null) ? commentArticle.trim() : "";
		if ((body.isEmpty()) && (commentImage == null || commentImage.isEmpty())) {
			ra.addFlashAttribute("err", "コメント本文または画像を入力してください。");
			return "redirect:/blog/detail/" + blogId;
		}

		// 5) 画像保存
		String imageName = null;
		if (commentImage != null && !commentImage.isEmpty()) {
			try {
				String fileName = new java.text.SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-").format(new java.util.Date())
						+ commentImage.getOriginalFilename();
				java.nio.file.Files.copy(commentImage.getInputStream(),
						java.nio.file.Path.of("src/main/resources/static/comment-img/" + fileName));
				imageName = fileName;
			} catch (java.io.IOException e) {
				e.printStackTrace();
				ra.addFlashAttribute("err", "コメント画像の保存に失敗しました。");
				return "redirect:/blog/detail/" + blogId;
			}
		}

		// 6) 登録処理
		java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
		Comment saved;
		if (parentCommentId != null) {
			// 返信
			saved = commentService.reply(uid, blogId, parentCommentId, imageName, body, now);
			ra.addFlashAttribute("ok", "返信を投稿しました。");
		} else {
			// 新規コメント
			saved = commentService.create(uid, blogId, imageName, body, now);
			ra.addFlashAttribute("ok", "コメントを投稿しました。");
		}

		// 詳細へリダイレクト（新規コメントの位置へアンカーで戻すと親切）
		Long anchorId = (saved != null) ? saved.getCommentId() : null;

		// 根コメント（親なし）
		var sortAsc = org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC,
				"createdAt");
		List<Comment> roots = commentDao.findByBlogIdAndParentCommentIdIsNull(blogId, sortAsc);

		
		List<Comment> all = commentDao.findByBlogId(blogId, sortAsc);
		Map<Long, List<Comment>> repliesMap = all.stream().filter(c -> c.getParentCommentId() != null)
				.collect(java.util.stream.Collectors.groupingBy(Comment::getParentCommentId));

		// コメントしたユーザー名（Map<accountId, accountName>）
		Map<Long, String> commentNames = new java.util.HashMap<>();
		for (Comment c : all) {
			if (c.getAccountId() != null && !commentNames.containsKey(c.getAccountId())) {
				Account a = accountDao.findByAccountId(c.getAccountId());
				if (a != null)
					commentNames.put(c.getAccountId(), a.getAccountName());
			}
		}

		model.addAttribute("comments", roots);
		model.addAttribute("repliesMap", repliesMap);
		model.addAttribute("commentNames", commentNames);

		return "redirect:/blog/detail/" + blogId + (anchorId != null ? ("#c-" + anchorId) : "");
	}
}
