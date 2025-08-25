package myblogpractice.com.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import myblogpractice.com.models.entity.Account;
import java.util.Optional;

@Repository
@Transactional
public interface AccountDao extends JpaRepository<Account, Long> {

	// 保存処理と更新処理 insertとupdate
	Account save(Account account);

	// SELECT * FROM admin WHERE admin_email = ?
	// 用途：管理者の登録処理をするときに、同じメールアドレスがあったらば登録させないようにする
	// 1行だけしかレコードは取得できない
	Account findByAccountEmail(String accountEmail);

	Account findByAccountName(String accountName);

	Boolean existsByAccountEmail(String accountEmail);

	Boolean existsByAccountName(String accountName);

	Account findByAccountNameAndAccountEmail(String accountName, String accountEmail);

	// SELECT * FROM admin WHERE admin_email=? AND password=?
	// 用途：ログイン処理に使用。入力したメールアドレスとパスワードが一致してるときだげデータを取得できない
	Account findByAccountEmailAndPassword(String accountEmail, String password);
}