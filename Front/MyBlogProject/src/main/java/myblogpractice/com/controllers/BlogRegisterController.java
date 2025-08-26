package myblogpractice.com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
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
		if (!model.containsAttribute("post")) {
			Blog post = new Blog();
			post.setVisibility(1);
			model.addAttribute("post", post);
		}
		return "blog_register";
	}
}
