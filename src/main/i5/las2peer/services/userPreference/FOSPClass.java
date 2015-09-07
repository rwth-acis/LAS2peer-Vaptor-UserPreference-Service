package i5.las2peer.services.userPreference;

import i5.las2peer.api.Service;
import i5.las2peer.restMapper.HttpResponse;
import i5.las2peer.restMapper.MediaType;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.Consumes;
import i5.las2peer.restMapper.annotations.ContentParam;
import i5.las2peer.restMapper.annotations.DELETE;
import i5.las2peer.restMapper.annotations.POST;
import i5.las2peer.restMapper.annotations.PUT;
import i5.las2peer.restMapper.annotations.QueryParam;
import i5.las2peer.restMapper.annotations.swagger.Notes;
import i5.las2peer.restMapper.annotations.swagger.ResourceListApi;
import i5.las2peer.security.Context;
import i5.las2peer.security.UserAgent;
import i5.las2peer.services.userPreference.database.DatabaseManager;
import i5.las2peer.services.userPreference.idGenerateClient.IdGenerateClientClass;
import i5.las2peer.restMapper.annotations.GET;
import i5.las2peer.restMapper.annotations.Path;
import i5.las2peer.restMapper.annotations.PathParam;
import i5.las2peer.restMapper.annotations.Produces;
import i5.las2peer.restMapper.annotations.Version;
import i5.las2peer.restMapper.annotations.swagger.ApiInfo;
import i5.las2peer.restMapper.annotations.swagger.ApiResponses;
import i5.las2peer.restMapper.annotations.swagger.ApiResponse;
import i5.las2peer.restMapper.annotations.swagger.Summary;
import i5.las2peer.restMapper.tools.ValidationResult;
import i5.las2peer.restMapper.tools.XMLCheck;
//import i5.las2peer.services.videoCompiler.idGenerateClient.IdGenerateClientClass;





import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

//import org.junit.experimental.theories.ParametersSuppliedBy;















import net.minidev.json.JSONValue;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpMethod;
//import org.apache.commons.httpclient.HttpStatus;
//import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import com.arangodb.ArangoDriver;
import com.arangodb.ArangoException;
import com.arangodb.CursorResultSet;
import com.arangodb.entity.CursorEntity;
import com.arangodb.entity.DeletedEntity;
import com.arangodb.entity.DocumentEntity;
import com.arangodb.entity.EdgeEntity;
import com.arangodb.entity.GraphEntity;
import com.arangodb.util.MapBuilder;

import org.json.*;

import com.google.gson.Gson;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * LAS2peer Service
 * 
 * 
 * 
 * 
 */

public class FOSPClass extends Service {

	//private String port;
	//private String host;
	//private String username;
	//private String password;
	//private String database;
	private DatabaseManager dbm;
	//private String epUrl;
	
	GraphEntity graphNew;

	public FOSPClass() {
		// read and set properties values
		//setFieldValues();
		
		
		
		//if (!epUrl.endsWith("/")) {
			//epUrl += "/";
		//}
		// instantiate a database manager to handle database connection pooling
		// and credentials
		//dbm = new DatabaseManager(username, password, host, port, database);
	}

	
	public JSONArray applyPreferences(JSONArray finalResult, String user, String driverName, String databaseServer, String port, String database, String username, String password, String hostName){
		System.out.println("Apply");
		//DatabaseManager dbm = new DatabaseManager();
		dbm = new DatabaseManager();
		dbm.init(driverName, databaseServer, port, database, username, password, hostName);
		String[] preferences = dbm.getPreferences(user);
		System.out.println("PREFERENCES: "+preferences[0]+" "+preferences[1]);
		
		int i=0;
		while(!finalResult.isNull(i)){
			JSONObject object = finalResult.getJSONObject(i);
			if(!preferences[1].equals(object.getString("lang"))){
				System.out.println("LANGUAGE: "+preferences[1]+" "+object.getString("lang"));
				finalResult.remove(i);
		    }
			else{
				i++;
			}
		}
		
		
		//finalResult.remove(2);
		
		//String annotations = getAnnotations(searchString);
		return finalResult;
	}

}
