package myblogpractice.com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import myblogpractice.com.models.entity.Account;
import myblogpractice.com.services.BlogService;

@Controller
public class WelcomeController {

	@Autowired
	private BlogService blogService;

	@Autowired
	private HttpSession session;

	@GetMapping("/artical")
	public String articalIn(Model model) {
		Account login = (Account) session.getAttribute("loginAccountInfo");
		if (login == null) {
			return "redirect:/login";
		} else {
			model.addAttribute("userName", login.getAccountName());
			model.addAttribute("posts", blogService.findRecentPostsByUser(login.getAccountId()));
			return "artical";
		}
	}

	@GetMapping("/list")
	public String listIn(Model model) {
		Account login = (Account) session.getAttribute("loginAccountInfo");
		if (login == null) {
			return "redirect:/login";
		} else {
			model.addAttribute("userName", login.getAccountName());
			model.addAttribute("posts", blogService.findRecentPostsByUser(login.getAccountId()));
			return "list";
		}
	}

}
