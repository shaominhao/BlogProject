package myblogpractice.com.controllers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
// Spring Boot 3.4 で @MockBean が非推奨なら MockitoBean に差し替え可
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import myblogpractice.com.services.AccountService;

/**
 * 単体テスト仕様書（RegisterSystem）に準拠したコントローラテスト - 仕様に合わせ、view() で検証（リダイレクト断言は使用しない） -
 * Session 断言は行わない - モックの when(...) は @BeforeEach に集約
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AccountRegisterControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AccountService accountService; // 仕様書の userService 相当

	@BeforeEach
	public void prepareData() {
		// デフォルトは「登録失敗」：どの引数でも false
		when(accountService.createAccount(anyString(), anyString(), anyString())).thenReturn(false);

		// No.2 成功シナリオ：username=test, email=test@test.com, password=1234 のときのみ true
		when(accountService.createAccount(eq("test"), eq("test@test.com"), eq("1234"))).thenReturn(true);
	}

	// No.1 表示テスト：/account/register への GET
	@Test
	void getRegister_shouldRenderRegisterHtml_withInitialFlags() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/account/register")).andExpect(status().isOk())
				.andExpect(view().name("register.html"))
				// 初期表示フラグ（Controller 初期化値）
				.andExpect(model().attribute("error", false)).andExpect(model().attribute("msg1", false))
				.andExpect(model().attribute("msg2", false));
	}

	// No.2 ユーザー登録テスト（成功）
	// 入力：adminName=test, adminEmail=test@test.com, password=1234
	// 検証：ビュー名 "login.html"、createAccount(...) が 1 回呼ばれる
	@Test
	void postRegister_success_shouldShowLogin_andInvokeCreateOnce() throws Exception {

		when(accountService.registerCheck(eq("test@test.com"), eq("test"))).thenReturn(true);
		when(accountService.createAccount(eq("test@test.com"), eq("test"), eq("1234"))).thenReturn(true);

		mockMvc.perform(MockMvcRequestBuilders.post("/account/register/process").param("adminName", "test")
				.param("adminEmail", "test@test.com").param("password", "1234")).andExpect(status().isOk())
				.andExpect(view().name("login.html"));

		verify(accountService, times(1)).createAccount(eq("test@test.com"), eq("test"), eq("1234"));
	}

	// No.3 ユーザー登録テスト（メール重複）
	// 入力：adminName=Taro, adminEmail=test@test.com（既存）, password=1234abcd
	// 検証：ビュー名 "register.html"、error=true、msg1=true（メール重複）、createAccount は呼ばれない
	@Test
	void postRegister_duplicateEmail_shouldStayRegister_andSetMsg1() throws Exception {
		when(accountService.existsEmail(eq("test@test.com"))).thenReturn(true); // メール重複

		mockMvc.perform(MockMvcRequestBuilders.post("/account/register/process").param("adminName", "Taro")
				.param("adminEmail", "test@test.com").param("password", "1234abcd")).andExpect(status().isOk())
				.andExpect(view().name("register.html")).andExpect(model().attribute("error", true))
				.andExpect(model().attribute("msg1", true)) // emailDup
				.andExpect(model().attribute("msg2", false)); // nameDup ではない

		verify(accountService, never()).createAccount(anyString(), anyString(), anyString());
	}

	// 追加：ユーザー名重複のケース
	// 入力：adminName=test（既存）、adminEmail=other@test.com、password=pass
	// 検証：ビュー名 "register.html"、error=true、msg2=true（名前重複）
	@Test
	void postRegister_duplicateName_shouldStayRegister_andSetMsg2() throws Exception {
		when(accountService.existsName(eq("test"))).thenReturn(true); // 名前重複

		mockMvc.perform(MockMvcRequestBuilders.post("/account/register/process").param("adminName", "test")
				.param("adminEmail", "other@test.com").param("password", "pass")).andExpect(status().isOk())
				.andExpect(view().name("register.html")).andExpect(model().attribute("error", true))
				.andExpect(model().attribute("msg1", false)).andExpect(model().attribute("msg2", true));

		verify(accountService, never()).createAccount(anyString(), anyString(), anyString());
	}

	// 追加：重複なしだが registerCheck が false → register.html（不正 or 最終チェック NG）
	@Test
	void postRegister_registerCheckFalse_shouldStayRegister_withError() throws Exception {
		// デフォルトが registerCheck=false のため上書き不要
		mockMvc.perform(MockMvcRequestBuilders.post("/account/register/process").param("adminName", "someone")
				.param("adminEmail", "someone@test.com").param("password", "pw")).andExpect(status().isOk())
				.andExpect(view().name("register.html")).andExpect(model().attribute("error", true));

		verify(accountService, never()).createAccount(anyString(), anyString(), anyString());
	}

	// 追加：registerCheck=True だが createAccount=False（DBエラー等）→ register.html +
	// msg2=true
	@Test
	void postRegister_createAccountFalse_shouldStayRegister_withMsg2True() throws Exception {
		when(accountService.registerCheck(eq("ok@test.com"), eq("okname"))).thenReturn(true);
		when(accountService.createAccount(eq("ok@test.com"), eq("okname"), eq("pw"))).thenReturn(false); // 作成失敗

		mockMvc.perform(MockMvcRequestBuilders.post("/account/register/process").param("adminName", "okname")
				.param("adminEmail", "ok@test.com").param("password", "pw")).andExpect(status().isOk())
				.andExpect(view().name("register.html")).andExpect(model().attribute("error", true))
				.andExpect(model().attribute("msg2", true)); // Controller が msg2=true を立てる実装

		verify(accountService, times(1)).createAccount(eq("ok@test.com"), eq("okname"), eq("pw"));
	}

	// No.4 画面表示初期表示（/account/register）：入力欄が空であることの軽い確認
	@Test
	void getRegister_initialInputsExist() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/account/register")).andExpect(status().isOk())
				.andExpect(view().name("register.html")).andExpect(model().attribute("error", false))
				.andExpect(model().attribute("msg1", false)).andExpect(model().attribute("msg2", false))
				// テンプレートに name 属性が存在するか（空白検証の代替）
				.andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"adminName\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"adminEmail\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"password\"")));
	}
}
