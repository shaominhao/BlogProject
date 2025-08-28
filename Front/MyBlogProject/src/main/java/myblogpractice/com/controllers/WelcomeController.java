package myblogpractice.com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import myblogpractice.com.models.entity.Account;
import myblogpractice.com.services.BlogService;

@Controller
public class WelcomeController {

	@Autowired
	private BlogService blogService;

	@Autowired
	private HttpSession session;
	
	 // URL「/artical」にGETアクセスがあった場合の処理
	@GetMapping("/artical")
	public String articalIn(Model model) {
		
		// セッションからログイン中のアカウント情報を取得
		Account login = (Account) session.getAttribute("loginAccountInfo");
		// ログインしていない場合
		if (login == null) {
			
			// ログイン画面にリダイレクトする
			return "redirect:/login";
		} else {
			// モデルにユーザー名を格納（画面表示用）
			model.addAttribute("userName", login.getAccountName());
			 // モデルにログインユーザーの最新記事を格納
			model.addAttribute("posts", blogService.findRecentPostsByUser(login.getAccountId()));
			return "artical";
		}
	}

	// URL「/list」にGETアクセスがあった場合の処理
	@GetMapping("/list")
	public String listIn(Model model) {
		 // セッションからログイン中のアカウント情報を取得
		Account login = (Account) session.getAttribute("loginAccountInfo");
		 // ログインしていない場合
		if (login == null) {
			 // ログイン画面にリダイレクトする
			return "redirect:/login";
		} else {
			// モデルにユーザー名を格納
			model.addAttribute("userName", login.getAccountName());
			// モデルにログインユーザーの最新記事を格納
			model.addAttribute("posts", blogService.findRecentPostsByUser(login.getAccountId()));
			return "list";
		}
	}
		
	//自己紹介pageへ
	 @GetMapping("/about")
	    public String about(Model model) {
	        // ヘッダーの表示名
	        Account login = (Account) session.getAttribute("loginAccountInfo");
	        if (login != null) {
	            model.addAttribute("userName", login.getAccountName());
	            model.addAttribute("profileName", login.getAccountName());
	        } else {
	            model.addAttribute("profileName", "ゲスト");
	        }
	        // 自己紹介文（あとでDBに載せ替えてOK）
	        model.addAttribute("bio", "ここに自己紹介文を追加します。趣味、スキル、連絡先など。");
	        return "about"; 
	    }
}
