# InjectionGotcha
InjectionGotcha is a project that details the usage of parameterized queries, also known as prepared statements, to securely perfom CRUD operations on a database. Parameterized queries work by inserting user-defined parameters into precompiled SQL statements and is commonly used to prevent SQL injection attacks. This repo was created to add the project to CS499 ePortfolio.

## Specifications
InjectionGotcha is a console application that takes user input for username and password to login and view accounts details (username, password, and balance). Input is validated against the database - a valid username and password login displays accounts details while three unsuccessful attempts will exit the progran.

## Database
* Database name: `data.db`
* Table name: `accounts`
* `accounts` schema:  
```sql
_id INTEGER NOT NULL PRIMARY KEY,
username TEXT NOT NULL,
password TEXT NOT NULL,
balance TEXT NOT NULL
```
* Table entries:
```sql
SELECT * FROM accounts;
```
```
_id|username|password|balance
1|ToddRobbins2|S38vbOzINu|$23,657
2|RandolphPatton8|hrWJHAbWzR|$80,528
3|JeannieChandler1|uyu2iWamG2|$61,503
4|PhilBanks1|6Fmh4csZNk|$6,041
5|LorraineRodriguez5|olURgcM1v2|$80,088
6|BruceSimpson8|caA8CNkkOe|$18,793
7|CalebRivera1|bXnPOuzVMt|$409
8|CliftonPrice1|E4g8qOCrKQ|$76,966
9|AllanAdams9|pkKMlUPiXM|$13,347
10|GeraldOwen8|cNyxTRN6Nt|$77,107
11|logan|duck|$1,000,000
```

## Running InjectionGotcha
### Successful login
```
Username: logan
Password: duck
Sucessfully logged in... here is your account info:
	Username: logan, Password: duck, Balance: $1,000,000
```

### Failed attempts
```
Username: admin
Password: admin
Login failed....

Username: admin
Password: admin
Login failed....

Username: admin
Password: admin
Login failed....

3 attempts made and you failed to log in. Bye.
```

## Looking at safe and unsafe usage:

Variable definitions:
```java
Connection connection = DriverManager.getConnection("jdbc:sqlite:src/main/java/resources/data.db");
PreparedStatement queryAccountInfo = connection.prepareStatement(QUERY_ACCOUNT_INFO);
```

### Safe usage:
```java
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
```

The following commands are what set the parameter inputs:
```java
queryAccountInfo.setString(1, username);
queryAccountInfo.setString(2, password);
```

### Unsafe usage:
```java
	public List<User> queryAccountInfo(String username, String password) {
		String sql = "SELECT " + COL_USERNAME + ", " + COL_PASSWORD + ", " + COL_BALANCE + " FROM " + TABLE_ACCOUNTS + " WHERE "
				+ COL_USERNAME + " = '" + username + "' AND " + COL_PASSWORD + " = '" + password + "'";
		
		StringBuilder sb = new StringBuilder(sql);

		try (Statement statement = connection.createStatement(); ResultSet results = statement.executeQuery(sb.toString())) {
			List<User> account = new ArrayList<User>();
			while (results.next()) {
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
```

This method opens a door to do some damage.

### SQL Injection with unsafe example
Leaving the username field empty and entering `' OR '1' = '1` in the password yields the following return:

```
Username: 
Password: ' OR '1' = '1
Sucessfully logged in... here is your account info:
	Username: ToddRobbins2, Password: S38vbOzINu, Balance: $23,657
	Username: RandolphPatton8, Password: hrWJHAbWzR, Balance: $80,528
	Username: JeannieChandler1, Password: uyu2iWamG2, Balance: $61,503
	Username: PhilBanks1, Password: 6Fmh4csZNk, Balance: $6,041
	Username: LorraineRodriguez5, Password: olURgcM1v2, Balance: $80,088
	Username: BruceSimpson8, Password: caA8CNkkOe, Balance: $18,793
	Username: CalebRivera1, Password: bXnPOuzVMt, Balance: $409
	Username: CliftonPrice1, Password: E4g8qOCrKQ, Balance: $76,966
	Username: AllanAdams9, Password: pkKMlUPiXM, Balance: $13,347
	Username: GeraldOwen8, Password: cNyxTRN6Nt, Balance: $77,107
	Username: logan, Password: duck, Balance: $1,000,000

```

Sensitve user information was leaked because parameterized queries were not used.

### Preventing SQL Injection with safe example
Using the safe example method with username field empty and `' OR '1' = '1` in the password field would yield a failed login:
```
Username: 
Password: ' OR '1' = '1
Login failed....
```
