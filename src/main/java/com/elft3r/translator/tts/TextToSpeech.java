package com.elft3r.translator.tts;

import java.util.Optional;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class TextToSpeech {
	@FunctionName("TextToSpeech")
	public HttpResponseMessage TextToSpeech(
			@HttpTrigger(name = "req", methods = { HttpMethod.GET }, authLevel = AuthorizationLevel.ANONYMOUS)
					HttpRequestMessage<Optional<String>> request, final ExecutionContext context) {
		context.getLogger().info("Java HTTP trigger is processing a request.");

		// Parse query parameter
		String query = request.getQueryParameters().get("input");
		//		String in = request.getBody().orElse(query);
		String in = "Item not found";
		try {
			return request.createResponseBuilder(HttpStatus.OK).body(getAudio(in)).build();
		} catch(Exception e) {
			return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	public byte[] getAudio(String str) throws Exception {
		System.out.println("----- Input: " + str);
		//			String outputFormat = AudioOutputFormat.Raw8Khz8BitMonoMULaw;
				String outputFormat = AudioOutputFormat.Riff8Khz8BitMonoMULaw;
//		String outputFormat = AudioOutputFormat.Raw16Khz16BitMonoPcm;
//					String outputFormat = AudioOutputFormat.Riff16Khz16BitMonoPcm;
		//			String outputFormat = AudioOutputFormat.Riff24Khz16BitMonoPcm;
		String deviceLanguage = "en-US";
		String genderName = Gender.Male;
		String voiceName = "Microsoft Server Speech Text to Speech Voice (en-US, Guy24KRUS)";

		return TTSService.Synthesize(str, outputFormat, deviceLanguage, genderName, voiceName);

		//				ServiceClient client = ServiceClient.createFromConnectionString(Config.iotHubConnectionString,
		//						IotHubServiceClientProtocol.);
		//				client.open();
		//				//				client.send(Config.deviceName, new Message(audioBuffer));
		//				//				return request.createResponseBuilder(HttpStatus.OK).build();
		//
		//				CompletableFuture<Void> done = client.sendAsync(Config.deviceName, new Message(audioBuffer));
		//				done.get();
		//				if(done.isDone()) {
		//					return request.createResponseBuilder(HttpStatus.OK).build();
		//				} else {
		//					return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
		//				}

		//				// write the pcm data to the file
		//				String outputWave = ".\\output.pcm";
		//				File outputAudio = new File(outputWave);
		//				FileOutputStream fstream = new FileOutputStream(outputAudio);
		//				fstream.write(audioBuffer);
		//				fstream.flush();
		//				fstream.close();
		//
		//				// specify the audio format
		//				AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 24000, 16, 1, 1 * 2, 24000,
		//						false);
		//
		//				AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(outputWave));
		//
		//				DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat,
		//						AudioSystem.NOT_SPECIFIED);
		//				SourceDataLine sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);
		//				sourceDataLine.open(audioFormat);
		//				sourceDataLine.start();
		//				System.out.println("start to play the wave:");
		//				/*
		//				 * read the audio data and send to mixer
		//				 */
		//				int count;
		//				byte tempBuffer[] = new byte[4096];
		//				while((count = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) > 0) {
		//					sourceDataLine.write(tempBuffer, 0, count);
		//				}
		//
		//				sourceDataLine.drain();
		//				sourceDataLine.close();
		//				audioInputStream.close();

	}
}