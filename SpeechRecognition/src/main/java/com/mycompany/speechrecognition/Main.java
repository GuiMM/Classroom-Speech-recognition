/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.speechrecognition;

    // Imports the Google Cloud client library
/*import AudioEdit.WavFile;
import com.google.cloud.speech.spi.v1.SpeechClient;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

public class Main {
  public static void main(String... args) throws Exception {
    
      FileWriter arquivo = new FileWriter(new File("./resources/aulas/redes/"+"aula1"+".srt"));
        BufferedWriter print = new BufferedWriter( arquivo );
        
//looking for info
            File curr_Audio = new File("./resources/aulas/redes/pieces/0"+".wav");
            WavFile wavFile = WavFile.openWavFile(curr_Audio);
            double tSegundos = (double)wavFile.getNumFrames()/44100;
            double tempoEmMiliSegundos = tSegundos *1000;
      System.out.println(tSegundos);

      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss,SSS");
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
      
      System.out.println("Resultado: " + sdf.format(tempoEmMiliSegundos));
      print.write("Resultado: " + sdf.format(tempoEmMiliSegundos));
      //Criando o conteúdo do arquivo
      print.flush();
      arquivo.close();
/*
    // Instantiates a client
    SpeechClient speech = SpeechClient.create();

    // The path to the audio file to transcribe
    String fileName = "./resources/aulas/redes/pieces/Aula_001 Slice 15.wav";
    
    // Reads the audio file into memory                
    Path path = Paths.get(fileName);
    byte[] data = Files.readAllBytes(path);
    ByteString audioBytes = ByteString.copyFrom(data);

    // Builds the sync recognize request
    RecognitionConfig config = RecognitionConfig.newBuilder()
        .setEncoding(AudioEncoding.LINEAR16)
        .setSampleRateHertz(44100)
        .setLanguageCode("pt-BR")   
        .build();
    RecognitionAudio audio = RecognitionAudio.newBuilder()
        .setContent(audioBytes)
        .build();

    // Performs speech recognition on the audio file
    RecognizeResponse response = speech.recognize(config, audio);
    List<SpeechRecognitionResult> results = response.getResultsList();

    for (SpeechRecognitionResult result: results) {
      List<SpeechRecognitionAlternative> alternatives = result.getAlternativesList();
      for (SpeechRecognitionAlternative alternative: alternatives) {
        System.out.printf("Transcription: %s%n", alternative.getTranscript());
      }
    }
    speech.close();

      
      
  }
        
}
*/
