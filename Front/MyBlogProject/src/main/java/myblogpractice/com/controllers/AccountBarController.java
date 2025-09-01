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
import org.springframework.web.bind.annotation.RequestParam;

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
	
	// ログアウト処理（GETとPOST両方に対応するための別メソッド）
	@RequestMapping(value = "/logout", method = { RequestMethod.GET, RequestMethod.POST })
	public String adminLogoutdouble() {
		 // sessionの無効化
		session.invalidate();
		return "redirect:/login";
	}

	// ブログ投稿登録画面にアクセス
	@GetMapping("/blog/register")
	public String blogRegisterAccess(
	        @RequestParam(value = "editId", required = false) Long editId,
	        Model model) {

	    Account login = (Account) session.getAttribute("loginAccountInfo");
	    if (login == null) {
	        return "redirect:/login";
	    }

	    model.addAttribute("userName", login.getAccountName());

	    
	    List<Blog> posts = blogService.findRecentPostsByUser(login.getAccountId());
	    model.addAttribute("posts", posts);

	    
	    if (!model.containsAttribute("post")) {
	        Blog form = null;
	        if (editId != null) {
	            form = blogService.findByblogId(editId);
	            // 所有者チェック
	            if (form == null || !login.getAccountId().equals(form.getAccountId())) {
	                form = new Blog();
	                form.setVisibility(1);
	            }
	        } else {
	            form = new Blog();
	            form.setVisibility(1);
	        }
	        model.addAttribute("post", form);
	    }

	    return "blog_register"; 
	}

	// /blog_Register にアクセスした場合、/blog/register にリダイレクト
	@GetMapping("/blog_Register")
	public String blogRegisterAlias() {
	    return "redirect:/blog/register";
	}

	// ブログのウェルカム画面にアクセス
	@GetMapping("/blog/welcome")
	public String blogWelcomeAccess(Model model) {
	    Account login = (Account) session.getAttribute("loginAccountInfo");
	    if (login == null) {
	        return "redirect:/login";
	    }

	    model.addAttribute("userName", login.getAccountName());

	   
	    List<Blog> posts = blogService.accessWithUser(login.getAccountId());
	    model.addAttribute("posts", posts);

	    
	    Map<Long, String> accountNames = new HashMap<>();
	    for (Blog b : posts) {
	        Account author = accountDao.findByAccountId(b.getAccountId());
	        if (author != null) {
	            accountNames.put(b.getAccountId(), author.getAccountName());
	        }
	    }
	    model.addAttribute("accountNames", accountNames);

	    return "welcome"; 
	}
}
