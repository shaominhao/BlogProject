package myblogpractice.com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import myblogpractice.com.services.BlogService;

@Controller
public class DeleteController {

	@Autowired
	public BlogService blogService;
	
	//
	@PostMapping("/blog/delete")
	public String deleteBlog(@RequestParam Long blogId) {
		// blogServiceを呼び出して記事を削除する（削除処理の実行）
			blogService.deleteAtrical(blogId);
			// 削除完了後、記事一覧画面（/blog/register）にリダイレクトする
		return "redirect:/blog/register";
	}
}
