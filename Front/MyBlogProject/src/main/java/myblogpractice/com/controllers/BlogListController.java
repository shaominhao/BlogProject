package myblogpractice.com.controllers;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
		Account login = (Account) session.getAttribute("loginAccountInfo");
		Long uid = (Long) session.getAttribute("uid");

		if (login == null || uid == null) {
			return "redirect:/login";
		}

		model.addAttribute("userName", login.getAccountName());

		List<Blog> posts = blogService.accessWithAdmin(uid);

		Map<Long, String> authorNames = new HashMap<>();
		for (Blog b : posts) {
			Account author = accountDao.findByAccountId(b.getAccountId());
			if (author != null) {
				authorNames.put(b.getAccountId(), author.getAccountName());
			}
		}
		model.addAttribute("posts", posts);
		model.addAttribute("accountNames", authorNames);
		return "list.html";
	}

	@PostMapping("/blog/status/update")
	public String updateStatus(@RequestParam Long blogId, @RequestParam Integer visibility,
			@SessionAttribute("uid") Long uid, RedirectAttributes ra, Model model) {
		blogService.updateStatus(blogId, visibility, uid);

		Long id = (Long) session.getAttribute("uid");
		if (id == null) {
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
		ra.addFlashAttribute("msg", "ステータスを更新しました");
		return "redirect:/blog/register";
	}

}
