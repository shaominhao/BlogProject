package myblogpractice.com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import myblogpractice.com.models.entity.Account;
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
			model.addAttribute("userName", account.getAccountName());
			model.addAttribute("recentPosts", blogService.findRecentPostsByUser(account.getAccountId()));

			return "welcome.html";
		}
	}
}
