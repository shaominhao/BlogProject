package myblogpractice.com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import myblogpractice.com.models.entity.Account;
import myblogpractice.com.services.BlogService;

@Controller
public class AccountBarController {

	@Autowired
	private BlogService blogService;

	@Autowired
	private HttpSession session;

	// ログアウト処理
	@GetMapping("/logout")
	public String adminLogout() {
		// sessionの無効化
		session.invalidate();
		return "redirect:/login";
	}
	@RequestMapping(value="/logout", method={RequestMethod.GET, RequestMethod.POST})
	public String adminLogoutdouble() { session.invalidate();
	return "redirect:/login"; }

	@GetMapping("/blog/register")
	public String blogRegisterAccess(Model model) {
		Account login = (Account) session.getAttribute("loginAccountInfo");
		if (login == null) {
			return "redirect:/login";
		} else {
			model.addAttribute("userName", login.getAccountName());
			model.addAttribute("posts", blogService.findRecentPostsByUser(login.getAccountId()));
			return "blog_register.html";
		}
	}

	@GetMapping("/blog_Register")
	public String blogRegisterAlias() {
		return "redirect:/blog/register";
	}

	@GetMapping("/blog/welcome")
	public String blogWelcomeAccess(Model model) {
		Account login = (Account) session.getAttribute("loginAccountInfo");
		if (login == null) {
			return "redirect:/login";
		} else {
			model.addAttribute("userName", login.getAccountName());
			model.addAttribute("posts", blogService.findRecentPostsByUser(login.getAccountId()));
			return "welcome.html";
		}
	}
}
