package myblogpractice.com.controllers;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import myblogpractice.com.models.entity.Account;
import myblogpractice.com.models.entity.Blog;
import myblogpractice.com.services.BlogService;

@Controller
public class BlogArticalController {

	@Autowired
	private BlogService blogService;

	@Autowired
	private HttpSession session;

	@PostMapping("/blog/register")
	public String submitBlog(
	        @RequestParam String blogTitle,
	        @RequestParam(required = false) String categoryName,
	        @RequestParam String article,
	        @RequestParam(name="blogImage", required=false) MultipartFile blogImage,
	        @RequestParam(name="allowComments", defaultValue="false") boolean allowComments,
	        @RequestParam String action,
	        RedirectAttributes ra) {

	    Account login = (Account) session.getAttribute("loginAccountInfo");
	    if (login == null) return "redirect:/login";

	    Blog blog = new Blog();
	    blog.setBlogTitle(blogTitle);
	    blog.setCategoryName(categoryName);
	    blog.setArticle(article);
	    blog.setAccountId(login.getAccountId());
	    blog.setCreatedAt(Timestamp.from(Instant.now()));
	    blog.setUpdatedAt(Timestamp.from(Instant.now()));

	    // 可见性：1 公開, 2 公開(コメント不可), 3 非公開
	    if ("publish".equals(action)) {
	        blog.setVisibility(allowComments ? 1 : 2);
	    } else { // draft
	        blog.setVisibility(3);
	    }

	    // 如果有图片，存储后写入路径（自行实现）
	    if (blogImage != null && !blogImage.isEmpty()) {
	        String stored = blogService.storeImage(blogImage);
	        blog.setBlogImage(stored);
	    }

	    blogService.save(blog);
	    ra.addFlashAttribute("ok", "記事を保存しました。");
	    return "redirect:/blog/register";
	}


}
