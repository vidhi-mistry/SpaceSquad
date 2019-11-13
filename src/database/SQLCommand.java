package database;

//abstract class cannot be instantiated. It can only be inherited.
public abstract class SQLCommand  {
	public static final String DB_ADDRESS = "jdbc:mysql://localhost:3306/";
	public static final String DB_NAME = "spacesquad";
	public static final String DRIVER = "com.mysql.jdbc.Driver";
	public static final String USER = "root";
	public static final String PASSWORD = "";

	
	public abstract boolean execute();
}
