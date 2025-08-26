package myblogpractice.com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import myblogpractice.com.services.BlogService;

@Controller
public class DeleteController {

	@Autowired
	public BlogService blogService;

	@PostMapping("/blog/{id}/delete")
	public String deleteBlog(@PathVariable("id") Long blogId, @SessionAttribute("uid") Long uid,
			RedirectAttributes ra) {
		try {
			blogService.deleteAtrical(blogId, uid);
			ra.addFlashAttribute("ok", "記事を削除しました。");
		} catch (SecurityException e) {
			ra.addFlashAttribute("error", "削除権限がありません。");
		}
		return "redirect:/blog/{id}/list";
	}
}
