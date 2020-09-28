package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.User;

/**
 * Used to connect to the database holding user account information.
 * 
 * @author LoganDuck
 */
public class DatabaseAdapter {
	public static final String DB_NAME = "data.db";
	public static final String DB_PATH = "src/main/java/resources/";
	public static final String DATABASE = DB_PATH + DB_NAME;
	public static final String CONNECTION_STRING = "jdbc:sqlite:" + DATABASE;
	
	/* TABLE INFO */
	public static final String TABLE_ACCOUNTS = "accounts";
	public static final String COL_USERNAME = "username";
	public static final String COL_PASSWORD = "password";
	public static final String COL_BALANCE = "balance";
	public static final int INDEX_USER_ID = 1;
	public static final int INDEX_USER_USERNAME = 2;
	public static final int INDEX_USER_PASSWORD = 3;
	public static final int INDEX_USER_BALANCE = 4;
	
	public static final String QUERY_ACCOUNT_INFO = "SELECT " + COL_USERNAME + ", " + COL_PASSWORD + ", " + COL_BALANCE
			+ " FROM " + TABLE_ACCOUNTS + " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?";

	private PreparedStatement queryAccountInfo;
	
	private Connection connection;

	/**
	 * Attempts to establish a connection to the given database URL.
	 * 
	 * @return true if connection established or false if <code>SQLException</code> has occurred or the URL is null.
	 */
	public boolean open() {
		try {
			connection = DriverManager.getConnection(CONNECTION_STRING);
			queryAccountInfo = connection.prepareStatement(QUERY_ACCOUNT_INFO);
			return true;
		} catch (SQLException e) {
			System.out.println("Unable to open database: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Releases database connection and JDBC resources for <code>Connection</code> object.
	 */
	public void close() {
		try {
			if (queryAccountInfo != null) {
				queryAccountInfo.close();
			}
			
			if (connection != null) {
				connection.close();
			}

		} catch (SQLException e) {
			System.out.println("Unable to close connection: " + e.getMessage());
		}
	}

//	/**
//	 * *********************************************** UNSAFE EXAMPLE ***********************************************
//	 * 
//	 * 
//	 * Although querying data like this is unlikely, it is being used as an example to describe the vulnerabilities and
//	 * and dangers simple coding mistakes can make. This method does not use parameterized statements or input sanitation
//	 * when calling the database, leaving it open to SQL injections. 
//	 * For example, leaving the username field blank and entering <code>' or '1' = '1</code> into the password field manipulates the
//	 * statement as successful, therefore outputting account information for all users.
//	 * 
//	 * @param username - username input.
//	 * @param password - password input. 
//	 * @return a list of <code>User</code> that meet the criteria.
//	 */
//	public List<User> queryAccountInfo(String username, String password) {
//		String sql = "SELECT " + COL_USERNAME + ", " + COL_PASSWORD + ", " + COL_BALANCE + " FROM " + TABLE_ACCOUNTS + " WHERE "
//				+ COL_USERNAME + " = '" + username + "' AND " + COL_PASSWORD + " = '" + password + "'";
//		
//		StringBuilder sb = new StringBuilder(sql);
//
//		try (Statement statement = connection.createStatement(); ResultSet results = statement.executeQuery(sb.toString())) {
//			List<User> account = new ArrayList<User>();
//			while (results.next()) {
//				String _username = results.getString(1);
//				String _password = results.getString(2);
//				String _balance = results.getString(3);
//				User user = new User(_username, _password, _balance);
//				account.add(user);
//			}
//
//			return account;
//
//		} catch (SQLException e) {
//			System.out.println("Cannot get account info: " + e.getMessage());
//			return null;
//		}
//	}
	/**
	 * 
	 * ************************************************ SAFE EXAMPLE ************************************************ 
	 * 
	 * 
	 * Although querying data like this is unlikely, it is being used as an example to describe the vulnerabilities and
	 * and dangers simple coding mistakes can make. 
	 * 
	 * The unsafe example method allows a SQL injection by entering <code>' or '1' = '1</code> in the password field, which
	 * outputs all user's account information. This safe method example successfully implements parameterized statements to avoid
	 * the possibility of SQL injections.
	 *  
	 * @param username - username input.
	 * @param password - password input.
	 * @return a list of <code>User</code> that meet the criteria.
	 */
	public List<User> queryAccountInfo(String username, String password) {
		try {
			
			queryAccountInfo.setString(1, username);
			queryAccountInfo.setString(2, password);
			
			ResultSet results = queryAccountInfo.executeQuery();
			
			List<User> account = new ArrayList<User>();
			
			while(results.next()) {
				String _username = results.getString(1);
				String _password = results.getString(2);
				String _balance = results.getString(3);
				User user = new User(_username, _password, _balance);
				account.add(user);
			}
			return account;
			
		} catch (SQLException e) {
			System.out.println("Cannot get account info: " + e.getMessage());
			return null;
		}
	}
}