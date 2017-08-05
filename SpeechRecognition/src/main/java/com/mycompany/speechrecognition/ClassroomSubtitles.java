/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.speechrecognition;

import com.google.cloud.speech.v1.RecognitionConfig;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author MQGuilherme
 */
public class ClassroomSubtitles {
    
    public static void main(String[] args) {
        ArrayList <String> name_Pieces = getNamePieces("./resources/aulas/redes/pieces/");
        //System.out.println(name_Pieces.toString());
        /*
        // Builds the sync recognize request
        RecognitionConfig config = RecognitionConfig.newBuilder()
        .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
        .setSampleRateHertz(44100)
        .setLanguageCode("pt-BR")   
        .build();
        
        */
    }
    
    private static ArrayList <String> getNamePieces(String way){
        ArrayList <String> name_Pieces = new ArrayList();
         
         
        File folder = new File(way);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println(file.getName());
            }
        }
         
         return name_Pieces;
    }
}
