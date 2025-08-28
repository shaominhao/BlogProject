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
import myblogpractice.com.models.dao.BlogDao;
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
	
	@Autowired
	private BlogDao blogDao;

	@GetMapping("/blog/list")
	public String bloglist(@RequestParam(value = "q", required = false) String q,Model model) {
		Account login = (Account) session.getAttribute("loginAccountInfo");
		Long uid = (Long) session.getAttribute("uid");

		if (login == null || uid == null) {
			return "redirect:/login";
		}

		model.addAttribute("userName", login.getAccountName());

		 List<Blog> posts;
		    if (q != null && !q.trim().isEmpty()) {
		        String kw = q.trim();
		        posts = blogService.searchMyPosts(uid, kw);   
		        if (posts.size() == 1) {
		            return "redirect:/blog/detail/" + posts.get(0).getBlogId();
		        }
		        model.addAttribute("q", kw);
		    } else {
		        posts = blogService.accessWithAdmin(uid);
		    }

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

//	@GetMapping("/blog/list")
//	public String list(@RequestParam(value = "mode", required = false) String mode,
//	                   Model model) {
//	    Account login = (Account) session.getAttribute("loginAccountInfo");
//	    Long uid = (Long) session.getAttribute("uid");
//	    if (login == null || uid == null) {
//	        return "redirect:/login";
//	    }
//	    model.addAttribute("userName", login.getAccountName());
//
//	    List<Blog> posts;
//	    if ("manage".equals(mode)) {
//	        
//	        posts = blogDao.findByAccountIdOrderByCreatedAtDesc(uid);
//	    } else {
//	        
//	        posts = blogService.accessWithAdmin(uid);
//	    }
//
//	    Map<Long, String> accountNames = new java.util.HashMap<>();
//	    for (Blog b : posts) {
//	        Account author = accountDao.findByAccountId(b.getAccountId());
//	        if (author != null) accountNames.put(b.getAccountId(), author.getAccountName());
//	    }
//	    model.addAttribute("posts", posts);
//	    model.addAttribute("accountNames", accountNames);
//	    return "list";
//	}

}
