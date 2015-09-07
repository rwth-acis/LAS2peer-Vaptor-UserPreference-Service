package i5.las2peer.services.userPreference;

import i5.las2peer.api.Service;
import i5.las2peer.services.userPreference.util.GreatCircleCalculation;
import i5.las2peer.services.userPreference.util.LocationService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.arangodb.entity.GraphEntity;
//import i5.las2peer.services.videoCompiler.idGenerateClient.IdGenerateClientClass;
//import org.junit.experimental.theories.ParametersSuppliedBy;
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpMethod;
//import org.apache.commons.httpclient.HttpStatus;
//import org.apache.commons.httpclient.methods.GetMethod;

/**
 * LAS2peer Service
 * 
 * 
 * 
 * 
 */

public class LocationSorting extends Service {

	//private DatabaseManager dbm;

	GraphEntity graphNew;

	public LocationSorting() {
		
	}
	
	
	public JSONArray sort(JSONArray finalResult, double userLat, double userLong){
		System.out.println("Location Sort");
		LocationService tDirectionService = new LocationService();
		int i=0;
		double[] distance = new double[finalResult.length()];
		List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		JSONArray sortedJsonArray = new JSONArray();
		
		//System.out.println(finalResult.toString());
		
		while(!finalResult.isNull(i)){
			JSONObject object = finalResult.getJSONObject(i);
			
			System.out.println("ANNOTATION "+i);
			
			
		    double array[] = tDirectionService.getLongitudeLatitude(object.getString("location"));
			distance[i] = GreatCircleCalculation.distance(array[0], array[1], userLat, userLong, 'M');
			
			object.put("distance", distance[i]);
			object.put("Latitude", array[0]);
			object.put("Longitude", array[1]);
			System.out.println("distance: "+distance[i]);
			
	        jsonValues.add(object);
			
			i++;
		}
		
		Collections.sort( jsonValues, new Comparator<JSONObject>() {
	        //You can change "Name" with "ID" if you want to sort by ID
	        private static final String KEY_NAME = "distance";

	        public int compare(JSONObject a, JSONObject b) {
	            
	        	double valA=0;
	        	double valB=0;
	        	

	            try {
	                
	            	valA = (Double) a.get(KEY_NAME);
	            	valB = (Double) b.get(KEY_NAME);
	            		            }
	            catch (JSONException e) {
	                //do something
	            }
	            return Double.compare(valA, valB);
	        }
	    });

	    for (int j = 0; j < finalResult.length(); j++) {
	        sortedJsonArray.put(jsonValues.get(j));
	    }
		
		//System.out.println("SORTED:"+sortedJsonArray.toString());
		
		
		return sortedJsonArray;
	}
	
}
