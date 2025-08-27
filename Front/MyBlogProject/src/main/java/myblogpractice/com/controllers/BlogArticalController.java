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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import myblogpractice.com.models.entity.Blog;
import myblogpractice.com.services.BlogService;

@Controller
public class BlogArticalController {

	@Autowired
	private BlogService blogService;

	@Autowired
	private HttpSession session;

	@PostMapping("/blog/artical")
	public String submitBlog(@RequestParam(name = "blogId", required = false) Long blogId,
			@RequestParam String blogTitle, @RequestParam String categoryName,
			@RequestParam(name = "blogImage", required = false) MultipartFile blogImage, @RequestParam String article,
			@RequestParam(name = "rolerank", defaultValue = "0") int rolerank,
			@RequestParam(name = "visibility", defaultValue = "3") int visibility,
			@RequestParam(name = "oldImage", required = false) String oldImage, RedirectAttributes ra,
			@SessionAttribute("uid") Long uid) throws IOException {

		// image処理
		String imageName = null;
		if (blogImage != null && !blogImage.isEmpty()) {
			String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-").format(new Date())
					+ blogImage.getOriginalFilename();
			imageName = fileName;
			try {
				Files.copy(blogImage.getInputStream(), Path.of("src/main/resources/static/blog-img/" + fileName));
			} catch (IOException e) {
				e.printStackTrace();
				ra.addFlashAttribute("error", "画像の保存に失敗しました。");
				return (blogId == null) ? "redirect:/blog/register" : "redirect:/blog/edit/" + blogId;
			}
		} else {

			if (blogId != null && oldImage != null && !oldImage.isBlank()) {
				imageName = oldImage;
			}
		}

		// 公開性の設定
		if (visibility < rolerank) {
			visibility = 3;
		}

		// new or update
		if (blogId == null) {
			// ｎｅｗ
			boolean ok = blogService.createArticle(blogTitle, categoryName, imageName, article, uid, visibility);
			if (!ok) {
				ra.addFlashAttribute("error", "同じタイトルが既に存在します。");
				return "redirect:/blog/register";
			}
			ra.addFlashAttribute("ok", "記事を投稿しました。");
			return "redirect:/blog/welcome";
		} else {
			// update
			boolean ok = blogService.updateArticle(blogId, blogTitle, categoryName, imageName, article, uid,
					visibility);
			if (!ok) {
				ra.addFlashAttribute("error", "更新に失敗しました。権限またはタイトル重複をご確認ください。");
				return "redirect:/blog/edit/" + blogId;
			}
			ra.addFlashAttribute("ok", "記事を更新しました。");
			return "redirect:/blog/welcome";
		}
	}

}
