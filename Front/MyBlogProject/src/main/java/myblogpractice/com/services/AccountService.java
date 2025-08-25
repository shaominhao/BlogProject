package myblogpractice.com.services;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import myblogpractice.com.models.dao.AccountDao;
import myblogpractice.com.models.entity.Account;

@Service
public class AccountService {

	@Autowired
	private AccountDao accountDao;

	// 保存処理（登録処理）
	// もし、findByAdminEmail==nullだったら登録処理をします。
	// saveメソッドを使用して登録処理をする
	// 保存ができたらtrue
	// そうでない場合、保存処理失敗 false

	public boolean registerCheck(String accountName, String accountEmail) {
		Account account = accountDao.findByAccountNameAndAccountEmail(accountName, accountEmail);
		if (account == null) {
			return true;
		} else {
			return false;
		}
	}

	public boolean registerNameCheck(String accountName) {
		Account account = accountDao.findByAccountName(accountName);
		if (account == null) {
			return true;
		} else {
			return false;
		}
	}

	public boolean createAccount(String accountName, String accountEmail, String password) {
		if (!registerCheck(accountName, accountEmail)) {
			return false;
		} else {
			try {
				accountDao.save(new Account(accountEmail, accountName, password, Timestamp.from(Instant.now())));
				return true;
			} catch (org.springframework.dao.DataIntegrityViolationException e) {
				return false;
			}
		}
	}

	public boolean existsEmail(String accountEmail) {
		return accountDao.existsByAccountEmail(accountEmail);
	}

	public boolean existsName(String accountName) {
		return accountDao.existsByAccountName(accountName);
	}

	// ログイン処理
	// もし、emailとpasswordがfindByAdminEmailAndPasswordを使用して存在しなっかた場合==nullの場合.
	// その場合は、存在しないnullであることをコントローラークラスに知らせる
	// そうでない場合ログリンしている人の情報をコントローラークラスに渡す
	public Account loginCheck(String accountEmail, String password) {
		Account account = accountDao.findByAccountEmailAndPassword(accountEmail, password);
		if (account == null) {
			return null;
		} else {
			return account;
		}
	}

}
