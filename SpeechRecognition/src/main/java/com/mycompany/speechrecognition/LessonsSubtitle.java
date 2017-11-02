/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.speechrecognition;

import AudioEdit.WavFile;
import com.google.cloud.speech.spi.v1.SpeechClient;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 *
 * @author MQGuilherme
 */
public class LessonsSubtitle {
  
    public LessonsSubtitle(String path, String fileName) throws Exception{
        int count_Pieces = getNumberOfPieces(path+"pieces/");
        getLegend(count_Pieces, path, fileName);
    
    }
    
    private void getLegend(int count_Pieces, String legend_Path, String legend_Name) throws IOException, Exception{
        //criando um arquivo de legenda
        FileWriter arquivo = new FileWriter(new File(legend_Path+legend_Name+".srt"));
        BufferedWriter print = new BufferedWriter( arquivo );
        
        //montando o conteudo
        // Instantiates a client
        SpeechClient speech = SpeechClient.create();
        
        // Builds the sync recognize request
        RecognitionConfig config = RecognitionConfig.newBuilder()
        .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
        .setSampleRateHertz(44100)
        .setLanguageCode("pt-BR")   
        .build();
        
        //formating time
        double curr_time = 0.0;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss,SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        //para cada pedaÃ§o de audio escreva no .srt
        for (int i = 0; i < count_Pieces; i++) {
            // The path to the audio file to transcribe
            String fileName = legend_Path+"pieces/"+(i)+".wav";
            System.out.println("peÃ§a nÂº "+i);
            
            //looking for info
            File curr_Audio = new File(legend_Path+"pieces/"+(i)+".wav");
            WavFile wavFile = WavFile.openWavFile(curr_Audio);
            double tempoEmMiliSegundos = (double)wavFile.getNumFrames()*1000/44100;
            
            // Reads the audio file into memory                
            Path piece_Path = Paths.get(fileName);
            byte[] data = Files.readAllBytes(piece_Path);
            ByteString audioBytes = ByteString.copyFrom(data);
            
            RecognitionAudio audio = RecognitionAudio.newBuilder()
            .setContent(audioBytes)
            .build();

            // Performs speech recognition on the audio file
            RecognizeResponse response = speech.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();
         
            
            print.newLine();
            System.out.println("legenda numero"+i);
            print.write(Integer.toString(i));
            print.newLine();
            print.write(sdf.format(curr_time)+"-->"+sdf.format(curr_time+tempoEmMiliSegundos));
            print.newLine();
            
            for (SpeechRecognitionResult result: results) {
                List<SpeechRecognitionAlternative> alternatives = result.getAlternativesList();
               
                print.write(alternatives.get(0).getTranscript());
            }
            print.newLine();
            //Criando o conteÃºdo do arquivo
            print.flush();
            
            //updating the current time
            curr_time += tempoEmMiliSegundos;
        }
        
        
        speech.close();
        arquivo.close(); 
        excludePieces(legend_Path+"pieces/");
    }
    
    private void excludePieces(String way){
        File pasta = new File(way);    
        File[] arquivos = pasta.listFiles();    
        for(File arquivo : arquivos) {
                arquivo.delete();
        }
    }
    private static ArrayList <String> getNamePieces(String way){
        ArrayList <String> name_Pieces = new ArrayList();
         
         
        File folder = new File(way);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                name_Pieces.add(file.getName());
               
            }
        }
         
         return name_Pieces;
    }
    
    private static int getNumberOfPieces(String pieces_Path){
        int count=0;
        
        File folder = new File(pieces_Path);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                count++;
            }
        }
        
        return count;
    }
}
