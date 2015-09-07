package i5.las2peer.services.userPreference.database;


import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet; 
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


//import de.dbis.util.GetProperty;

/**
 * 
 * Establishes connection to the MySql database.
 * Uses 'dbconnection.properties' file for configuration.
 *
 */
public class DatabaseManager
{
	private final static String INPUT_FILE = "etc/i5.las2peer.services.videoAdapter.dbconnection.properties";
	
	private static String url;
	private static String host;
	private static String port;
	private static String dbName;
	private static String driver;
	private static String userName;
	private static String password;
	private static String databaseServer;
	/*private static String useUniCode;
	private static String charEncoding;
	private static String charSet;
	private static String collation;*/
	
	public void init(String driver, String databaseServer, String port, String dbName, String userName, String password, String host) {

		
		System.out.println("DB CHECK: ");
		
		
		this.driver = driver;
		this.databaseServer = databaseServer;
		this.port = port;
		this.dbName = dbName;
		this.userName = userName;
		this.password = password;
		this.host=host;
		
		//this.useUniCode = useUniCode;
		//this.charEncoding = charEncoding;
		//this.charSet = charSet;
		//this.collation = collation;
		
		url = "jdbc:" + this.databaseServer + "://" + this.host +":"+ this.port + "/";
		
		System.out.println("DB URL: "+url);
	}

	public void setPreferences(String [] preferences){
		
		int rowCount = 0;
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			
			String insertQuery = " insert into profile (username, location, language, duration) values (?, ?, ?, ?)";
			
			PreparedStatement pstmt = conn.prepareStatement(insertQuery);
			pstmt.setString(1, preferences[0]);
			pstmt.setString(2, preferences[1]);
			pstmt.setString(3, preferences[2]);
			pstmt.setString(4, preferences[3]);
			rowCount = pstmt.executeUpdate();
			
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public String[] getPreferences(String username){
		
		//init();
		System.out.println("in getPreferences, "+ username);
		ResultSet res = null;
		String preferences[]=new String[5];
		String interests, language, domain, duration, location;
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			
			String insertQuery = "SELECT * FROM profile WHERE username = ?";
			
			
			PreparedStatement pstmt = conn.prepareStatement(insertQuery);
			pstmt.setString(1, username);
			res = pstmt.executeQuery();

			if(res.next()){
				
				interests = res.getString("interests");
				language = res.getString("language");
				domain = res.getString("domain");
				duration = res.getString("duration");
				location = res.getString("location");
				
				preferences[0]=interests;
				preferences[1]=language;
				preferences[2]=domain;
				preferences[3]=duration;
				preferences[4]=location;
			}
			
			else{
				System.out.println("User Not Found!");
				//return false;
			}

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return preferences;
	}
	
	
	public void userExists(String username){
		
		System.out.println("in update, "+ username);
		ResultSet res = null;
		
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			
			String insertQuery = "SELECT * FROM profile WHERE username = ?";
			
			
			PreparedStatement pstmt = conn.prepareStatement(insertQuery);
			pstmt.setString(1, username);
			res = pstmt.executeQuery();

			if(!res.next()){
				
				String[] preferences = {username,"aachen","english","5"};
				setPreferences(preferences);
				
			}
			
			else{
				System.out.println("User Exists!");
				//return false;
			}

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update(String [] preferences){
		
		System.out.println("in update, "+ preferences[0]);
		ResultSet res = null;
		
		//String interests, language, domain, duration, location;
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			
			String insertQuery = "SELECT * FROM profile WHERE username = ?";
			
			
			PreparedStatement pstmt = conn.prepareStatement(insertQuery);
			pstmt.setString(1, preferences[0]);
			res = pstmt.executeQuery();

			if(res.next()){
				
				String updateQuery = "UPDATE profile set "
						+ "location= COALESCE(?,?), language=COALESCE(?,?), duration=COALESCE(?,?) where username=?";
				/*SET scan_notes = "data",    
				  scan_entered_by = "some_name",    
				  scan_modified_date = "current_unix_timestamp",
				  scan_created_date = COALESCE(scan_Created_date, "current_unix_timestamp")
				WHERE id = X*/
				
				
				//String updateQuery = " update profile set location=?, language=?, duration=? where username=?";
				//String query = "update users set num_points = ? where first_name = ?";
				//"update budgetreport set sales= ? , cogs= ? where quarter="+ qTracker;
				PreparedStatement pstmt_update = conn.prepareStatement(updateQuery);
				
				pstmt_update.setString(1, preferences[1]);
				pstmt_update.setString(2, res.getString("location"));
				
				pstmt_update.setString(3, preferences[2]);
				pstmt_update.setString(4, res.getString("language"));
				
				pstmt_update.setString(5, preferences[3]);
				pstmt_update.setString(6, res.getString("duration"));
				
				pstmt_update.setString(7, preferences[0]);
				pstmt_update.executeUpdate();
				
			}
			
			else{
				System.out.println("User Exists!");
				//return false;
			}

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateExpertise(String username, String[] domain, String[] level){
		
		//System.out.println("in update, "+ preferences[0]);
		ResultSet res = null;
		int rowCount = 0;
		
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			

			System.out.println("domain: "+domain.length+" domain[0]: "+domain[0]+" level[0] "+level[0]);
			for(int i=0;i<domain.length;i++){
				
				String selectQuery = "SELECT * FROM expertise WHERE username = ? AND expertise_domain = ?";
				
				PreparedStatement pstmt = conn.prepareStatement(selectQuery);
				pstmt.setString(1, username);
				pstmt.setString(2, domain[i]);
				res = pstmt.executeQuery();
				
				//System.out.println("res.next(): "+res.next());
				
				if(domain[i]!=null && level[i]!=null){
					
					if(!res.next()){
						System.out.println("res.next():false");
						String insertQuery = "insert into expertise (username, expertise_domain, expertise_level) values (?, ?, ?)";
						
						PreparedStatement pstmt1 = conn.prepareStatement(insertQuery);
						pstmt1.setString(1, username);
						pstmt1.setString(2, domain[i]);
						pstmt1.setString(3, level[i]);
						
						rowCount = pstmt1.executeUpdate();
					}
					else{
						System.out.println("res.next():true");
						String updateQuery = "UPDATE expertise set expertise_level=?"
								+ "where username=? AND expertise_domain=?";
					
						PreparedStatement pstmt_update = conn.prepareStatement(updateQuery);
						
						pstmt_update.setString(1, level[i]);
						pstmt_update.setString(2, username);
						pstmt_update.setString(3, domain[i]);
						
						System.out.println(level[i]+" "+username+" "+domain[i]);
						
						pstmt_update.executeUpdate();
					
					}
				}
		}
			
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getExpertise(String domain, String username){

		ResultSet res = null;
		int level = 0;
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			
			String insertQuery = "SELECT * FROM expertise WHERE username = ? AND expertise_domain = ?";
			
			
			PreparedStatement pstmt = conn.prepareStatement(insertQuery);
			pstmt.setString(1, username);
			pstmt.setString(2, domain);
			res = pstmt.executeQuery();

			if(res.next()){
				
				level = res.getInt("expertise_level");
				
			}
			
			else{
				System.out.println("User or Domain Not Found!");
				//return false;
			}

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return level;
		
	}
	
	public void deletePrefrences(String username){
		
		//System.out.println("in update, "+ preferences[0]);
		ResultSet res = null;
		
		//String interests, language, domain, duration, location;
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			
			

			//if(res.next()){
				
				String expertiseDeleteQuery = "DELETE FROM expertise WHERE username = ?";
				PreparedStatement pstmt_expertiseDelete = conn.prepareStatement(expertiseDeleteQuery);
				pstmt_expertiseDelete.setString(1, username);
				pstmt_expertiseDelete.execute();
				
				String profileDeleteQuery = "DELETE FROM profile WHERE username = ?";
				PreparedStatement pstmt_profileDelete = conn.prepareStatement(profileDeleteQuery);
				pstmt_profileDelete.setString(1, username);
				pstmt_profileDelete.execute();
				
			//}

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}