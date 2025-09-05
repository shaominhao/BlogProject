package myblogpractice.com.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import myblogpractice.com.models.dao.AccountDao;
import myblogpractice.com.models.entity.Account;
import myblogpractice.com.models.entity.Blog;
import myblogpractice.com.services.AccountService;
import myblogpractice.com.services.BlogService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountLoginControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AccountService accountService;

	@MockBean
	private BlogService blogService;

	@MockBean
	private AccountDao accountDao;

	@BeforeEach
	public void prepareData() {
		// 項目No1: モックアカウント
		Account testAccount = new Account();
		// userId
		testAccount.setAccountId(1L);
		// username
		testAccount.setAccountName("test");
		// email
		testAccount.setAccountEmail("test@test.com");
		// password
		testAccount.setPassword("1234");
		// デフォルトは「全て失敗」：一致しない限り null を返す
		when(accountService.loginCheck(any(), any())).thenReturn(null);

		// 項目No1 完全一致のときのみ「成功」：Account を返す
		when(accountService.loginCheck(eq("test@test.com"), eq("1234"))).thenReturn(testAccount);
	}

	// No.1 表示テスト：/login へのGET
	@Test
	void getLogin_shouldRenderLoginHtml() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/login")).andExpect(status().isOk())
				.andExpect(view().name("login.html")).andExpect(model().attribute("error", false));
	}

	// No.2 成功：正しい email/password で /login/process POST
	// 仕様：リダイレクト先が "welcome.html"
	@Test
	void postLogin_success_shouldRedirectWelcomeHtml() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/login/process").param("accountEmail", "test@test.com")
				.param("password", "1234")).andExpect(view().name("welcome.html"));
	}

	// No.3 失敗：email 間違い + password 正 → /login にリダイレクト
	@Test
	void postLogin_wrongEmail_shouldRedirectLogin() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/login/process").param("accountEmail", "test02@test.com")
				.param("password", "1234")).andExpect(view().name("login.html"))
				.andExpect(model().attribute("error", true));
	}

	// No.4 失敗：email 正 + password 間違い → /login にリダイレクト
	@Test
	void postLogin_wrongPassword_shouldRedirectLogin() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/login/process").param("accountEmail", "test@test.com")
				.param("password", "1234abcd")).andExpect(view().name("login.html"))
				.andExpect(model().attribute("error", true));
	}

	// No.5 失敗：email/password 両方間違い → /login にリダイレクト
	@Test
	void postLogin_bothWrong_shouldRedirectLogin() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/login/process").param("accountEmail", "test02@test.com")
				.param("password", "1234abcd")).andExpect(view().name("login.html"))
				.andExpect(model().attribute("error", true));
		;
	}

	// No.6 初期表示：入力欄が空（ここでは存在の軽い確認）
	@Test
	void loginPage_inputsShouldExist() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/login")).andExpect(status().isOk())
				.andExpect(view().name("login.html"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"accountEmail\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"password\"")));
	}
}
