package myblogpractice.com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import jakarta.servlet.http.HttpSession;
import myblogpractice.com.services.BlogService;

@Controller
public class BlogListController {
	
	@Autowired
	private BlogService blogService;

	@Autowired
	private HttpSession session;

}
