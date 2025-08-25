package myblogpractice.com.models.entity;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Account {
	// idの設定
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long accountId;

	private String accountName;

	private String accountEmail;

	private String password;


	private Timestamp register_date;

	public Account() {

	}

	public Account(String accountName, String accountEmail, String password, Timestamp register_date) {
		this.accountName = accountName;
		this.accountEmail = accountEmail;
		this.password = password;
		this.register_date = register_date;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountEmail() {
		return accountEmail;
	}

	public void setAccountEmail(String accountEmail) {
		this.accountEmail = accountEmail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Timestamp getRegister_date() {
		return register_date;
	}

	public void setRegister_date(Timestamp register_date) {
		this.register_date = register_date;
	}

}