package myblogpractice.com.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import myblogpractice.com.models.dao.AccountDao;
import myblogpractice.com.models.entity.Account;
import myblogpractice.com.models.entity.Blog;
import myblogpractice.com.services.AccountService;
import myblogpractice.com.services.BlogService;

@Controller
public class AccountLoginController {

	@Autowired
	private AccountService accountService;

	@Autowired
	public HttpSession session;

	@Autowired
	private BlogService blogService;

	@Autowired
	public AccountDao accountDao;

	// ログリン画面の表示
	@GetMapping("/login")
	public String getAccountLoginPage(Model model) {
		model.addAttribute("error", false);
		return "login.html";
	}

	// ログイン処理
	@PostMapping("/login/process")
	public String accountLoginProcess(@RequestParam String accountEmail, @RequestParam String password, Model model) {
		// loginCheckメソッドを呼び出してその結果をadminという変数に格納
		Account account = accountService.loginCheck(accountEmail, password);
		// もし、あｄみん＝＝ログイン画面にとどまします
		// そうでない場合は、sessionにログイン情報に保存
		// 商品一覧画面にリダイレクトする/product/list
		if (account == null) {
			model.addAttribute("error", true);
			return "login.html";
		} else {
			session.setAttribute("loginAccountInfo", account);
			session.setAttribute("uid", account.getAccountId());

			List<Blog> posts = blogService.accessWithUser(account.getAccountId());
			Map<Long, String> accountNames = new HashMap<>();
			for (Blog b : posts) {
				Account author = accountDao.findByAccountId(b.getAccountId());
				if (author != null) {
					accountNames.put(b.getAccountId(), author.getAccountName());
				}
			}
			model.addAttribute("posts", posts);
			model.addAttribute("accountNames", accountNames);
			model.addAttribute("userName", account.getAccountName());
			model.addAttribute("recentPosts", posts);
			return "welcome";
		}
	}
}
