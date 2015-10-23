package i5.las2peer.services.userPreference;

import i5.las2peer.api.Service;
import i5.las2peer.restMapper.HttpResponse;
import i5.las2peer.restMapper.MediaType;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.ContentParam;
import i5.las2peer.restMapper.annotations.GET;
import i5.las2peer.restMapper.annotations.POST;
import i5.las2peer.restMapper.annotations.Path;
import i5.las2peer.restMapper.annotations.PathParam;
import i5.las2peer.restMapper.annotations.Produces;
import i5.las2peer.restMapper.annotations.QueryParam;
import i5.las2peer.restMapper.annotations.Version;
import i5.las2peer.restMapper.annotations.swagger.ApiInfo;
import i5.las2peer.restMapper.annotations.swagger.ApiResponse;
import i5.las2peer.restMapper.annotations.swagger.ApiResponses;
import i5.las2peer.restMapper.annotations.swagger.Summary;
import i5.las2peer.restMapper.tools.ValidationResult;
import i5.las2peer.restMapper.tools.XMLCheck;
import i5.las2peer.services.userPreference.database.DatabaseManager;
import i5.las2peer.services.userPreference.util.OIDC;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import com.arangodb.entity.GraphEntity;


/**
 * LAS2peer Service
 * 
 * 
 * 
 * 
 */
@Path("preference")
@Version("0.1")
@ApiInfo(title = "User Preference Service", 
	description = "<p>A RESTful service for Adaptation for Vaptor.</p>", 
	termsOfServiceUrl = "", 
	contact = "siddiqui@dbis.rwth-aachen.de", 
	license = "MIT", 
	licenseUrl = "") 
	
public class PreferenceClass extends Service {

	private String port;
	//private String host;
	private String username;
	private String password;
	private String database;
	private String databaseServer;
	private String driverName;
	private String hostName;
	private String useUniCode;
	private String charEncoding;
	private String charSet;
	private String collation;
	

	private DatabaseManager dbm;
	private String epUrl;
	private String userinfo;
	
	GraphEntity graphNew;
	
	
	

	public PreferenceClass() {
		// read and set properties values
		setFieldValues();

		if (!epUrl.endsWith("/")) {
			epUrl += "/";
		}
		// instantiate a database manager to handle database connection pooling
		// and credentials
		//dbm = new DatabaseManager(username, password, host, port, database);
	}

	@POST
	@Path("")
	//@Consumes(MediaType.TEXT_PLAIN)
	//public String postUserProfile(@HeaderParam(name="username" , defaultValue = "*") String username, @HeaderParam(name = "location", defaultValue = "*" ) String location, 
			//@HeaderParam(name = "language", defaultValue = "*" ) String language, @HeaderParam(name = "duration", defaultValue = "*" ) String duration){

	public HttpResponse postPreferences(@ContentParam String jsonPreferences){
	
		JSONObject preferenceObject = new JSONObject(jsonPreferences.toString());
		
		System.out.println("LOCATION: "+preferenceObject.getString("location"));
		System.out.println("language: "+preferenceObject.getString("language"));
		//System.out.println("username: "+preferenceObject.getString("username"));
		
		String token = preferenceObject.getString("Authorization");
		System.out.println("TOKEN: "+token);
		String username = null;
		if(token!=null){
			   token = token.replace("Bearer ","");
			   username = OIDC.verifyAccessToken(token, userinfo);
			   
			   if(username.isEmpty() || username.equals("undefined") || 
						username.equals("error")){
					HttpResponse r = new HttpResponse("User is not signed in!");
					r.setStatus(401);
					return r;
				}
			   
		}
		else{
			HttpResponse r = new HttpResponse("User is not signed in!");
			r.setStatus(401);
			return r;
		}
		
		dbm = new DatabaseManager();
		dbm.init(driverName, databaseServer, port, database, this.username, password, hostName);
		
		String [] preferences = {
				username,
				preferenceObject.getString("location"),
				preferenceObject.getString("language"),
				preferenceObject.getString("duration"),
		};
		for(int i=0; i<preferences.length;i++){
			if(preferences[i].equals("*"))
					preferences[i]=null;
		}
		int noOfExp = Integer.parseInt(preferenceObject.getString("noOfExp").replace("\n", ""));
		
		System.out.println("noOfExp: "+noOfExp);
		
		String[] domain = new String[noOfExp]; 
		String[] level = new String[noOfExp];
		
		for(int i=0; i<noOfExp; i++){
			domain[i] = preferenceObject.getString("exp"+(i+1));
			level[i] = preferenceObject.getString("lvl"+(i+1));
			
			System.out.println("domain"+(i+1)+": "+domain[i]);
			System.out.println("level"+(i+1)+": "+level[i]);
		}
		for(int i=0; i<noOfExp;i++){
			if(domain[i].equals("*")) domain[i]=null;
			if(level[i].equals("*")) level[i]=null;
		}
		
		dbm.userExists(preferences[0]);
		
		dbm.update(preferences);
		dbm.updateExpertise(username, domain, level);

		HttpResponse r = new HttpResponse("Preferences successfully saved!");
		r.setStatus(200);
		return r;
	}
	
		
	@GET
	@Path("")
	public HttpResponse getPreferences(@QueryParam(name = "Authorization", defaultValue = "") String token
			//@QueryParam(name="username" , defaultValue = "*") String username
			){

		String username = null;
		
		System.out.println("TOKEN: "+token);
		
		if(token!=null){
			   token = token.replace("Bearer ","");
			   username = OIDC.verifyAccessToken(token, userinfo);
			   
			   if(username.isEmpty() || username.equals("undefined") || 
						username.equals("error")){
					HttpResponse r = new HttpResponse("User is not signed in!");
					r.setStatus(401);
					return r;
				}
		}
		else{
			HttpResponse r = new HttpResponse("User is not signed in!");
			r.setStatus(401);
			return r;
		}
		
		System.out.println("USER: "+username);
		dbm = new DatabaseManager();
		dbm.init(driverName, databaseServer, port, database, this.username, password, hostName);
		
		dbm.userExists(username);
		String[] preferences = dbm.getPreferences(username);
		
		//JSONArray preferencesJSON = new JSONArray(Arrays.asList(preferences));
		JSONObject preferencesJSON = new JSONObject();
		preferencesJSON.put("interests", preferences[0]);
		preferencesJSON.put("language", preferences[1]);
		preferencesJSON.put("domain", preferences[2]);
		preferencesJSON.put("duration", preferences[3]);
		preferencesJSON.put("location", preferences[4]);
		
		
		HttpResponse r = new HttpResponse(preferencesJSON.toString());
		r.setStatus(200);
		return r;
	}
	
	@GET
	@Path("expertise")
	public HttpResponse getExpertise(@QueryParam(name = "Authorization", defaultValue = "") String token,
			//@QueryParam(name="username" , defaultValue = "*") String username,
			@QueryParam(name="domain" , defaultValue = "*") String videoDomain){

		String username = null;
		
		System.out.println("TOKEN: "+token);
		
		if(token!=null){
			   token = token.replace("Bearer ","");
			   username = OIDC.verifyAccessToken(token, userinfo);
			   
			   if(username.isEmpty() || username.equals("undefined") || 
						username.equals("error")){
					HttpResponse r = new HttpResponse("User is not signed in!");
					r.setStatus(401);
					return r;
				}
		}
		else{
			HttpResponse r = new HttpResponse("User is not signed in!");
			r.setStatus(401);
			return r;
		}
		
		//System.out.println("USER: "+username);
		dbm = new DatabaseManager();
		dbm.init(driverName, databaseServer, port, database, this.username, password, hostName);
		
		int expertise = dbm.getExpertise(videoDomain, username);
		//String[] preferences = dbm.getPreferences(username);
		
		HttpResponse r = new HttpResponse(String.valueOf(expertise));
		r.setStatus(200);
		return r;
		
	}
	
	
	
	// ================= Swagger Resource Listing & API Declarations
	// =====================

	

	@GET
	@Path("api-docs")
	@Summary("retrieve Swagger 1.2 resource listing.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Swagger 1.2 compliant resource listing"),
			@ApiResponse(code = 404, message = "Swagger resource listing not available due to missing annotations."), })
	@Produces(MediaType.APPLICATION_JSON)
	public HttpResponse getSwaggerResourceListing() {
		return RESTMapper.getSwaggerResourceListing(this.getClass());
	}

	@GET
	@Path("api-docs/{tlr}")
	@Produces(MediaType.APPLICATION_JSON)
	@Summary("retrieve Swagger 1.2 API declaration for given top-level resource.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Swagger 1.2 compliant API declaration"),
			@ApiResponse(code = 404, message = "Swagger API declaration not available due to missing annotations."), })
	public HttpResponse getSwaggerApiDeclaration(@PathParam("tlr") String tlr) {
		return RESTMapper.getSwaggerApiDeclaration(this.getClass(), tlr, epUrl);
	}

	/**
	 * Method for debugging purposes. Here the concept of restMapping validation
	 * is shown. It is important to check, if all annotations are correct and
	 * consistent. Otherwise the service will not be accessible by the
	 * WebConnector. Best to do it in the unit tests. To avoid being
	 * overlooked/ignored the method is implemented here and not in the test
	 * section.
	 * 
	 * @return true, if mapping correct
	 */
	public boolean debugMapping() {
		String XML_LOCATION = "./restMapping.xml";
		String xml = getRESTMapping();

		try {
			RESTMapper.writeFile(XML_LOCATION, xml);
		} catch (IOException e) {
			e.printStackTrace();
		}

		XMLCheck validator = new XMLCheck();
		ValidationResult result = validator.validate(xml);

		if (result.isValid())
			return true;
		return false;
	}

	/**
	 * This method is needed for every RESTful application in LAS2peer. There is
	 * no need to change!
	 * 
	 * @return the mapping
	 */
	public String getRESTMapping() {
		String result = "";
		try {
			result = RESTMapper.getMethodsAsXML(this.getClass());
		} catch (Exception e) {

			e.printStackTrace();
		}
		return result;
	}

}
