package myblogpractice.com.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import myblogpractice.com.services.AccountService;

@Controller
@Transactional
public class AccountRegisterController {

	@Autowired
	private AccountService accountService;
	
	// 登録画面の表示
	@GetMapping("/account/register")
	public String registerPage(Model model) {
		 // 初期状態ではエラーやメッセージをすべてfalseに設定
		model.addAttribute("error", false);
		model.addAttribute("msg1", false);
		model.addAttribute("msg2", false);
		return "register.html";
	}

	// 登録処理
	@PostMapping("/account/register/process")
	public String adminRegisterProcess(@RequestParam String adminName, @RequestParam String adminEmail,
			@RequestParam String password, Model model) {
		// メールアドレスと名前の重複確認
		boolean emailDup = accountService.existsEmail(adminEmail);
		boolean nameDup = accountService.existsName(adminName);

		if (emailDup || nameDup) {
			// どちらかが重複していた場合、エラー画面を再表示
			model.addAttribute("error", true);
			model.addAttribute("msg1", emailDup);
			model.addAttribute("msg2", nameDup);
			return "register.html";
		}
		
		 // 登録可能かを最終チェック
		if (accountService.registerCheck(adminEmail, adminName)) {
			// アカウント作成処理
			boolean created = accountService.createAccount(adminEmail, adminName, password);
			if (created) {
				// 成功した場合、ログイン画面に遷移
				return "login.html";
			} else {
				 // 作成失敗時（例: DB登録エラーなど）
				model.addAttribute("error", true);
				model.addAttribute("msg2", true);
				return "register.html";
			}
		}
		
		// その他の失敗（不正な入力など）
		model.addAttribute("error", true);
		return "register.html";
	}
}