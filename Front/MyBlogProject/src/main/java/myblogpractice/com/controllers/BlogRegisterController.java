package myblogpractice.com.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import myblogpractice.com.models.dao.AccountDao;
import myblogpractice.com.models.dao.BlogDao;
import myblogpractice.com.models.entity.Account;
import myblogpractice.com.models.entity.Blog;
import myblogpractice.com.services.BlogService;

@Controller
public class BlogRegisterController {
	@Autowired
	private BlogService blogService;

	@Autowired
	private BlogDao blogDao;

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private HttpSession session;

	// 旧URLから新URLへのリダイレクト
	@GetMapping("/blogregister")
	public String legacyRegisterRedirect() {
		return "redirect:/blog/register/user";
	}

	// ブログ登録画面の表示
	@GetMapping("/blog/register/user")
	public String blogRegister(@RequestParam(value = "editId", required = false) Long editId, Model model) {
		// ログイン確認
		Account login = (Account) session.getAttribute("loginAccountInfo");
		Long uid = (Long) session.getAttribute("uid");
		if (login == null || uid == null) {
			return "redirect:/login";
		}
		model.addAttribute("userName", login.getAccountName());

		// 一覧用
		List<Blog> list = blogService.accessWithAdmin(uid);
		model.addAttribute("posts", list);

		// フォーム用
		if (!model.containsAttribute("post")) {
			Blog form;
			if (editId != null) {
				form = blogDao.findByBlogId(editId);
				// 所有者チェック
				if (form == null || !uid.equals(form.getAccountId())) {
					// 不正アクセス時は新規にフォールバック
					form = new Blog();
					form.setVisibility(1);
				}
			} else {
				// 新規作成フォーム
				form = new Blog();
				form.setVisibility(1);
			}
			model.addAttribute("post", form);
		}

		return "blog_register";
	}
	
	// ブログ編集画面の表示
	@GetMapping("/blog/edit/{id}")
	public String editBlog(@PathVariable("id") Long id, Model model) {
		// ログイン済みならユーザー名を画面に渡す
		Account login = (Account) session.getAttribute("loginAccountInfo");
		if (login != null) {
			model.addAttribute("userName", login.getAccountName());
		}
		// 編集対象の投稿を取得
		Blog blog = blogService.findByblogId(id);
		if (blog == null) {
			
			// 不正IDならエラーをスロー
			throw new IllegalArgumentException("Invalid blog Id:" + id);
		}
		model.addAttribute("blog", blog);
		
		// 編集画面（artical.html）を表示
		return "artical";
	}

	// ステータス更新処理（公開・非公開など）
	@PostMapping("/blog/status/update")
	public String updateStatus(@RequestParam Long blogId, @RequestParam Integer visibility,
			@SessionAttribute("uid") Long uid, RedirectAttributes ra, Model model) {
		 // ステータス更新（サービス層に処理を委譲）
		blogService.updateStatus(blogId, visibility, uid);
		// ログインチェック
		Long id = (Long) session.getAttribute("uid");
		if (id == null) {
			return "redirect:/login";
		}
		// 更新後の投稿一覧を再取得
		List<Blog> posts = blogService.accessWithAdmin(uid);
		// 投稿者名をマッピング
		Map<Long, String> accountNames = new HashMap<>();
		for (Blog b : posts) {
			Account author = accountDao.findByAccountId(b.getAccountId());
			if (author != null) {
				accountNames.put(b.getAccountId(), author.getAccountName());
			}
		}
		model.addAttribute("posts", posts);
		model.addAttribute("accountNames", accountNames);
		// 更新完了メッセージをFlash属性で渡す
		ra.addFlashAttribute("msg", "ステータスを更新しました");
		 // 登録画面へリダイレクト
		return "redirect:/blog/register";
	}
}