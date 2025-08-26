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

	// SELECT * FROM admin WHERE admin_name = ?
	// 用途：管理者の登録処理をするときに、同じusernameがあったらば登録させないようにする
	// 1行だけしかレコードは取得できない
	Account findByAccountName(String accountName);

	String findNamesByAccountId(Long accountId);

	Account findByAccountId(Long accountId);

	// 判断同じメールアドレスがあるのか
	Boolean existsByAccountEmail(String accountEmail);

	// 判断同じusernameがあるのか
	Boolean existsByAccountName(String accountName);

	// 用途：login登録の確認
	Account findByAccountNameAndAccountEmail(String accountName, String accountEmail);

	// SELECT * FROM admin WHERE admin_email=? AND password=?
	// 用途：ログイン処理に使用。入力したメールアドレスとパスワードが一致してるときだげデータを取得できない
	Account findByAccountEmailAndPassword(String accountEmail, String password);
}