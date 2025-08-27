package myblogpractice.com.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import myblogpractice.com.models.dao.AccountDao;
import myblogpractice.com.models.entity.Account;
import myblogpractice.com.models.entity.Blog;
import myblogpractice.com.services.BlogService;

@Controller
public class AccountBarController {

	@Autowired
	private BlogService blogService;

	@Autowired
	private HttpSession session;

	@Autowired
	private AccountDao accountDao;

	// ログアウト処理
	@GetMapping("/logout")
	public String adminLogout() {
		// sessionの無効化
		session.invalidate();
		return "redirect:/login";
	}

	@RequestMapping(value = "/logout", method = { RequestMethod.GET, RequestMethod.POST })
	public String adminLogoutdouble() {
		session.invalidate();
		return "redirect:/login";
	}

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

			List<Blog> posts = blogService.accessWithUser(login.getAccountId());
			Map<Long, String> accountNames = new HashMap<>();
			for (Blog b : posts) {
				Account author = accountDao.findByAccountId(b.getAccountId());
				if (author != null) {
					accountNames.put(b.getAccountId(), author.getAccountName());
				}
			}
			model.addAttribute("post", posts);
			model.addAttribute("accountNames", accountNames);
			model.addAttribute("userName", login.getAccountName());
			model.addAttribute("recentPosts", posts);
			model.addAttribute("userName", login.getAccountName());
			model.addAttribute("posts", blogService.findRecentPostsByUser(login.getAccountId()));
			model.addAttribute("recentPosts", blogService.findRecentPostsByUser(login.getAccountId()));
			return "welcome.html";
		}
	}
}
