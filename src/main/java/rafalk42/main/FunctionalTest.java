package rafalk42.main;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.LoggerFactory;


public class FunctionalTest
{
	private final org.slf4j.Logger log = LoggerFactory.getLogger(FunctionalTest.class);
	//	private final String targetAddress;
//	private final int targetPort;
	private final String baseUrl;
	
	public FunctionalTest(String targetAddress, int targetPort)
	{
//		this.targetAddress = targetAddress;
//		this.targetPort = targetPort;
		
		baseUrl = String.format("http://%s:%d/bank/",
								targetAddress, targetPort);
	}
	
	public void run()
	{
		log.info(String.format("Base URL is %s",
							   baseUrl));
		
		try
		{
			HttpResponse<JsonNode> response = Unirest.get(baseUrl + "accounts")
																 .asJson();
			
			JsonNode body = response.getBody();
			
			body.getArray();
			
			log.info(body.toString());
		}
		catch (UnirestException ex)
		{
			log.error("Error occurred", ex);
		}
	}
	
}
