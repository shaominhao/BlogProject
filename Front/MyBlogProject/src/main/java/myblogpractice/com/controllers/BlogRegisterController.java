package myblogpractice.com.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpSession;
import myblogpractice.com.models.entity.Account;
import myblogpractice.com.models.entity.Blog;
import myblogpractice.com.services.BlogService;

@Controller
public class BlogRegisterController {
	@Autowired
	private BlogService blogService;

	@Autowired
	private HttpSession session;

	@GetMapping("/blogregister")
	public String showRegister(Model model) {
		Account login = (Account) session.getAttribute("loginAccountInfo");
		if (login != null) {
			model.addAttribute("userName", login.getAccountName());
		}

		if (!model.containsAttribute("post")) {

			Long uid = (Long) session.getAttribute("uid");
			List<Blog> posts = blogService.accessWithAdmin(uid);

			if (posts != null && !posts.isEmpty()) {

				model.addAttribute("posts", posts.get(0));
			} else {

				Blog post = new Blog();
				post.setVisibility(1);
				model.addAttribute("posts", post);
			}
		}
		return "blog_Register";
	}

	@GetMapping("/blog/edit/{id}")
	public String editBlog(@PathVariable("id") Long id, Model model) {
		Account login = (Account) session.getAttribute("loginAccountInfo");
		if (login != null) {
			model.addAttribute("userName", login.getAccountName());
		}
		Blog blog = blogService.findByblogId(id);
		if (blog == null) {
			throw new IllegalArgumentException("Invalid blog Id:" + id);
		}
		model.addAttribute("blog", blog);
		return "artical";
	}
}