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

	@GetMapping("/account/register")
	public String registerPage(Model model) {
		model.addAttribute("error", false);
		model.addAttribute("msg1", false);
		model.addAttribute("msg2", false);
		return "register.html";
	}

	@PostMapping("/account/register/process")
	public String adminRegisterProcess(@RequestParam String adminName, @RequestParam String adminEmail,
			@RequestParam String password, Model model) {
		boolean emailDup = accountService.existsEmail(adminEmail);
		boolean nameDup = accountService.existsName(adminName);

		if (emailDup || nameDup) {
			model.addAttribute("error", true);
			model.addAttribute("msg1", emailDup);
			model.addAttribute("msg2", nameDup);
			return "register.html";
		}

		if (accountService.registerCheck(adminEmail, adminName)) {
			boolean created = accountService.createAccount(adminEmail, adminName, password);
			if (created) {
				return "login.html";
			} else {
				model.addAttribute("error", true);
				model.addAttribute("msg2", true);
				return "register.html";
			}
		}
		model.addAttribute("error", true);
		return "register.html";
	}
}