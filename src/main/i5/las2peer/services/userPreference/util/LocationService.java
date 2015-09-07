package i5.las2peer.services.userPreference.util;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class LocationService {
	
	private static final String GEOCODE_REQUEST_URL = "http://maps.googleapis.com/maps/api/geocode/xml?sensor=false&";
    private static HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
    
	public double[] getLongitudeLatitude(String address) {
		
		double array[] = new double[2];
	    try {
	        StringBuilder urlBuilder = new StringBuilder(GEOCODE_REQUEST_URL);
	        if (StringUtils.isNotBlank(address)) {
	            urlBuilder.append("&address=").append(URLEncoder.encode(address, "UTF-8"));
	            }
	 
	            final GetMethod getMethod = new GetMethod(urlBuilder.toString());
	            try {
	                httpClient.executeMethod(getMethod);
	                Reader reader = new InputStreamReader(getMethod.getResponseBodyAsStream(), getMethod.getResponseCharSet());
	                 
	                int data = reader.read();
	                char[] buffer = new char[1024];
	                Writer writer = new StringWriter();
	                while ((data = reader.read(buffer)) != -1) {
	                        writer.write(buffer, 0, data);
	                }
	 
	                String result = writer.toString();
	                //System.out.println(result.toString());
	 
	                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	                DocumentBuilder db = dbf.newDocumentBuilder();
	                InputSource is = new InputSource();
	                is.setCharacterStream(new StringReader("<"+writer.toString().trim()));
	                Document doc = db.parse(is);
	         
		            String strLatitude = getXpathValue(doc, "//GeocodeResponse/result/geometry/location/lat/text()");
		            System.out.println("Latitude:" + strLatitude);
		             
		            String strLongtitude = getXpathValue(doc,"//GeocodeResponse/result/geometry/location/lng/text()");
		            System.out.println("Longitude:" + strLongtitude);
	                
		            
		            array[0] = (double) Double.valueOf(strLatitude);
		            array[1] = (double) Double.valueOf(strLongtitude);
	                 
	            } finally {
	                getMethod.releaseConnection();
	            }
	        } catch (Exception e) {
	             e.printStackTrace();
	        }
	    
	    	return array;
	    }
	 
	    private String getXpathValue(Document doc, String strXpath) throws XPathExpressionException {
	        XPath xPath = XPathFactory.newInstance().newXPath();
	        XPathExpression expr = xPath.compile(strXpath);
	        String resultData = null;
	        Object result4 = expr.evaluate(doc, XPathConstants.NODESET);
	        NodeList nodes = (NodeList) result4;
	        for (int i = 0; i < nodes.getLength(); i++) {
	            resultData = nodes.item(i).getNodeValue();
	        }
	        return resultData;
	    }
}
