package i5.las2peer.services.userPreference;

import i5.las2peer.api.Service;

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

public class RelevanceSorting extends Service {

	//private DatabaseManager dbm;

	GraphEntity graphNew;

	public RelevanceSorting() {
		
	}

	public JSONArray sort(JSONArray finalResult, String SearchQuery){
		System.out.println("Apply");
		
		int i=0;
		int[] relevanceFactor = new int[finalResult.length()];
		List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		JSONArray sortedJsonArray = new JSONArray();
		String [] searchTerms;
		
		//System.out.println(finalResult.toString());
		
		while(!finalResult.isNull(i)){
			JSONObject object = finalResult.getJSONObject(i);
			
			System.out.println("ANNOTATION "+i);
			searchTerms = SearchQuery.split(" ");
			
			for(int l=0; l<searchTerms.length; l++){
			
				System.out.println("SEARCH TERM "+l+" "+ searchTerms[l]);
				relevanceFactor[i]+= getRelevanceFactor(searchTerms[l], object.getString("keywords"));
				relevanceFactor[i]+= getRelevanceFactor(searchTerms[l], object.getString("text"));
				relevanceFactor[i]+= getRelevanceFactor(searchTerms[l], object.getString("title"));
			}
			
			
			object.put("relevanceFactor", relevanceFactor[i]);
			System.out.println("relevanceFactor: "+relevanceFactor[i]);
			
	        jsonValues.add(object);
			
			i++;
		}
		
		Collections.sort( jsonValues, new Comparator<JSONObject>() {
	        //You can change "Name" with "ID" if you want to sort by ID
	        private static final String KEY_NAME = "relevanceFactor";

	        public int compare(JSONObject a, JSONObject b) {
	            
	        	int valA=0;
	        	int valB=0;
	        	

	            try {
	                
	            	valA = (Integer) a.get(KEY_NAME);
	            	valB = (Integer) b.get(KEY_NAME);
	            		            }
	            catch (JSONException e) {
	                //do something
	            }
	            return -Integer.compare(valA, valB);
	            //return valA.compareTo(valB);
	            //if you want to change the sort order, simply use the following:
	            //return -valA.compareTo(valB);
	        }
	    });

	    for (int j = 0; j < finalResult.length(); j++) {
	        sortedJsonArray.put(jsonValues.get(j));
	    }
		
		//System.out.println("SORTED:"+sortedJsonArray.toString());
		
		
		return sortedJsonArray;
	}
	
	private int getRelevanceFactor(String SearchTerm, String Annotation){
		
		SearchTerm = SearchTerm.toLowerCase();
		Annotation = Annotation.toLowerCase();
		
		String[] AnnSubList = Annotation.split(" ");
		int relevanceFactor=0;
		
		//for(int i=0;i<AnnSubList.length;i++){
			if (Annotation.contains(SearchTerm)){
			//if(AnnSubList[i].contains(SearchTerm)){
				relevanceFactor++;
			}
		//}
		return(relevanceFactor);
		
	}
}
