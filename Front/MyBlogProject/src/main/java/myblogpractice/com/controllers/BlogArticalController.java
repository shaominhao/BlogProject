package myblogpractice.com.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import myblogpractice.com.services.BlogService;

@Controller
public class BlogArticalController {

	@Autowired
	private BlogService blogService;

	@Autowired
	private HttpSession session;

	@PostMapping("/blog/artical")
	public String submitBlog(@RequestParam String blogTitle, @RequestParam String categoryName,
			@RequestParam(name = "blogImage", required = false) MultipartFile blogImage, @RequestParam String article,
			@RequestParam(name = "rolerank", defaultValue = "0") int rolerank,
			@RequestParam(name = "visibility", defaultValue = "3") int visibility, RedirectAttributes ra,
			@SessionAttribute("uid") Long uid) throws IOException {

		String imageName = null;
		if (blogImage != null && !blogImage.isEmpty()) {
			String ext = StringUtils.getFilenameExtension(blogImage.getOriginalFilename());
	        String base = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
	        imageName = base + "-" + UUID.randomUUID()
	                + (ext != null && !ext.isEmpty() ? "." + ext.toLowerCase() : "");
	        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
	        Files.createDirectories(dir);
			
			//ファイルの保存作業
	        try (var in = blogImage.getInputStream()) {
	            Files.copy(in, dir.resolve(imageName), StandardCopyOption.REPLACE_EXISTING);
	        }
	    }

	    if (visibility < rolerank) {
	        visibility = 3; 
	    }

	    boolean ok = blogService.createArticle(blogTitle, categoryName, imageName, article, uid, visibility);
	    if (!ok) {
	        ra.addFlashAttribute("error", "同じタイトルが既に存在します。");
	        return "redirect:/blog/register";
	    }
	    ra.addFlashAttribute("ok", "記事を投稿しました。");
	    return "redirect:/blog/welcome";
	}

}
