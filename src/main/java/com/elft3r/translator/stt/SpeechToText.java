package com.elft3r.translator.stt;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.elft3r.translator.Config;
import com.elft3r.translator.tts.TextToSpeech;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

/**
 * Azure Functions with HTTP Trigger.
 */
public class SpeechToText {
	/**
	 * This function listens at endpoint "/api/HttpTrigger-Java". Two ways to invoke it using "curl" command in bash: 1.
	 * curl -d "HTTP Body" {your host}/api/HttpTrigger-Java 2. curl {your host}/api/HttpTrigger-Java?name=HTTP%20Query
	 */
	@FunctionName("SpeechToText")
	public HttpResponseMessage SpeechToText(
			@HttpTrigger(name = "req", methods = { HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS,
					dataType = "binary") HttpRequestMessage<Optional<Byte[]>> request, final ExecutionContext context) {
		context.getLogger().info("Java HTTP trigger is processing a request.");

		// Parse query parameter
		Byte[] is = request.getBody().get();

		if(is == null) {
			return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass an audio file").build();
		} else {
			try {
				SpeechClientREST client = new SpeechClientREST(new Authentication(Config.key));

				byte[] bytes = new byte[is.length];
				int i = 0;
				for(Byte b : is) bytes[i++] = b.byteValue();

				InputStream input = new ByteArrayInputStream(bytes);
				JsonParser parser = new JsonParser();
				JsonObject obj = parser.parse(client.process(input)).getAsJsonObject();

				JsonElement text = obj.get("DisplayText");
				byte[] audio = null;
				String txtResult = "";
				if(text != null) {
					txtResult = getResult(text.getAsString());
					System.out.println("---- RESULT: '" + txtResult + "'");
					audio = new TextToSpeech().getAudio(txtResult);
				} else System.out.println("---- RESULT: nothing was said");

				return request.createResponseBuilder(HttpStatus.OK).body(txtResult).build();
			} catch(Exception e) {
				e.printStackTrace();
				return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
			} finally {
				context.getLogger().info("Java HTTP trigger processed a request.");
			}
		}
	}

	private String getResult(String tmp) throws Exception {
		tmp = tmp.toLowerCase();
		tmp = tmp.replace("where", "");
		tmp = tmp.replace("is", "");
		tmp = tmp.replace("are", "");
		tmp = tmp.replace("the", "");
		tmp = tmp.replace("my", "");
		tmp = tmp.replace("?", "");
		tmp = tmp.replace(".", "");
		tmp = tmp.trim();

		System.out.println("---- QUERY: '" + tmp + "'");

		HttpClient http = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://streampipes5270.cloudapp.net:8099/api/v1/hackathon/what/" + tmp);
		HttpResponse response = http.execute(get);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer sb = new StringBuffer();
		String line;
		while((line = rd.readLine()) != null) {
			sb.append(line);
		}
		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(sb.toString()).getAsJsonObject();

		return obj.get("result").getAsString();
	}
}
