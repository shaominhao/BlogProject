package myblogpractice.com.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import jakarta.servlet.http.HttpSession;
import myblogpractice.com.models.dao.AccountDao;
import myblogpractice.com.models.entity.Account;
import myblogpractice.com.models.entity.Blog;
import myblogpractice.com.services.AccountService;
import myblogpractice.com.services.BlogService;

@Controller
public class BlogListController {

	@Autowired
	private BlogService blogService;

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private AccountService accountService;

	@Autowired
	private HttpSession session;

	@GetMapping("/blog/list")
	public String list(Model model) {

		Long uid = (Long) session.getAttribute("uid");
		if (uid == null) {
			return "redirect:/login";
		}

		List<Blog> posts = blogService.accessWithAdmin(uid);
		Map<Long, String> accountNames = new HashMap<>();
		for (Blog b : posts) {
			Account author = accountDao.findByAccountId(b.getAccountId());
			if (author != null) {
				accountNames.put(b.getAccountId(), author.getAccountName());
			}
		}
		model.addAttribute("posts", posts);
		model.addAttribute("accountNames", accountNames);
		return "list";
	}
}
