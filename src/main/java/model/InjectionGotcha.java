package model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.io.IOException;
import java.util.List;

import database.DatabaseAdapter;

/**
 * Creates a <code>DatabaseAdapter</code> object to log in to view account info for a user.
 * @author LoganDuck
 */
public class InjectionGotcha {
	
	private static final DatabaseAdapter db = new DatabaseAdapter();
	private static List<User> account;

	/**
	 * Calls the <code>DatabaseAdapter</code> object to verify user login. A successful login is determined if the size of
	 * <code>account</code> is greater than 0, otherwise there was a login failure.
	 * @param username - <code>User</code>'s input username.
	 * @param password - <code>User</code>'s input password.
	 * @return true if login is successful, false otherwise.
	 * @throws SQLException 
	 */
	public static boolean canLogin(String username, String password) {
		account = db.queryAccountInfo(username, password);
		return account.size() > 0;
	}

	public static void main(String[] args) {
		// attempts to establish a connection
		if (!db.open()) {
			System.out.println("Can't open database.");
			return;
		}

		// standard buffering default character input stream
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		boolean loginSuccess = false;
		try {
			// [attempts] increments if failed to login. 3 unsuccessful attempted logins are granted before program ends. 
			int attempts = 0;
			while (attempts++ < 3) {
				System.out.print("Username: ");
				String username = br.readLine().trim();
				System.out.print("Password: ");
				String password = br.readLine().trim();
				if (canLogin(username, password)) {
					loginSuccess = true;
					break;
				} else {
					System.out.println("Login failed....\n");
				}
			}
			if (loginSuccess) {
				System.out.println("Sucessfully logged in... here is your account info:");
				for (User param : account) {
					System.out.println("\t" + param.toString());
				}
			} else {
				System.out.println("3 attempts made and you failed to log in. Bye.");
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		// close connection to [DatabaseAdapter] object.
		db.close();
	}
}