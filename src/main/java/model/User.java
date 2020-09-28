package model;

/**
 * User account information.
 * @author LoganDuck
 */
public class User {
	private String username;
	private String password;
	private String balance;

	/* constructor */
	public User(String username, String password, String balance) {
		this.username = username;
		this.password = password;
		this.balance = balance;
	}

	/**
	 * Returns the username.
	 * @return the username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Returns the password.
	 * @return the password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Returns the balance.
	 * @return the balance.
	 */
	public String getBalance() {
		return balance;
	}

	/**
	 * Returns account summary info.
	 */
	@Override
	public String toString() {
		return "Username: " + getUsername() + ", Password: " + getPassword() + ", Balance: " + getBalance();
	}
}