/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AudioEdit;

import com.mycompany.speechrecognition.ClassRecognition;
import com.mycompany.speechrecognition.LessonsSubtitle;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author MQGuilherme
 */
public class AudioTreatment {
    
    public static void main(String[] args) throws Exception {
        
        //for (String arg : args) {
        
            String path = "./resources/aulas/redes/";//arg;
            ArrayList <String> name_videos = getNamePieces(path);
            for (String name : name_videos) {
                File audio = new File(path+name);
                ArrayList<Long> periods_Of_Silence = periodOfSilenceDetect(audio);
                cutAudiosOnSilence(audio,periods_Of_Silence,path);
                
                LessonsSubtitle sub = new LessonsSubtitle(path,name.substring(0, name.length()-4));  
                
            }
            
        //}
    }
    
    
    
    private static ArrayList <String> getNamePieces(String way){
        ArrayList <String> name_videos = new ArrayList();
         
        File folder = new File(way);
        
        File[] listOfFiles = folder.listFiles(new AudioFileFilter());
        File[] listOfFilesAlreadLegended = folder.listFiles(new LegendFileFilter());
        
        for (File file : listOfFiles) {
            if (file.isFile()) {
                name_videos.add(file.getName());
               
            }
        }
        
        for (File file : listOfFilesAlreadLegended) {
            if (file.isFile()) {
               String aux = file.getName().substring(0, file.getName().length()-4);
               aux += ".wav";
               name_videos.remove(aux);
            }
        }
         
         return name_videos;
    }

    
    
    private static void cutAudiosOnSilence(File aula, ArrayList<Long> periods_Of_Silence, String path) {
        

        //making an list of audio pieces
        ArrayList<WavFile> pieces = new ArrayList<>();
        
        File piece;
        try
	{    
            for (int i = 0; i < periods_Of_Silence.size(); i++) {
                
                int sampleRate = 44100;		// Samples per second
                // Calculate the number of frames required for specified duration
                long numFrames;
                if (i==0) {
                    numFrames = periods_Of_Silence.get(i);		// Seconds
                }else{
                    numFrames = periods_Of_Silence.get(i) - periods_Of_Silence.get(i-1);		// Seconds
                }
		
                piece = new File(path+"pieces/"+(i)+".wav");
		// Create a wav file 
		WavFile wavFile = WavFile.newWavFile(piece, 1, numFrames, 16, sampleRate);
                
                pieces.add(wavFile);
            }
            
            // now we're going to cut/////////////////////////////////////////////////////////////////////// 
            
            WavFile original = WavFile.openWavFile(aula);
                    
            // taking the sample rate 
            int sampleRate = (int)original.getSampleRate();
           
            // Calculate the number of frames required for specified duration
            long numFrames = original.getNumFrames();
            
            // Create a buffer of 44100(number of sampleRate) frames
            double[] buffer_To_Read = new double[sampleRate];
            double[] buffer_To_Write = new double[sampleRate];
            
            int framesRead;
            
            for (int i = 0; i < pieces.size(); i++) {
                long frameCounter = 0;

                // Loop until all frames written
                while (frameCounter < pieces.get(i).getNumFrames())
                {
                         // Read frames into buffer
                        framesRead = original.readFrames(buffer_To_Read, sampleRate);
                    
                        // Determine how many frames to write, up to a maximum of the buffer size
                        long remaining = pieces.get(i).getFramesRemaining();
                        int toWrite = (remaining > sampleRate) ? sampleRate : (int) remaining;

                        // Fill the buffer, one tone per channel
                        for (int s=0 ; s<toWrite ; s++, frameCounter++)
                        {       
                                //coppying from the original audio file
                                buffer_To_Write[s] = buffer_To_Read[s];
                              
                        }

                        // Write the buffer
                        pieces.get(i).writeFrames(buffer_To_Write, toWrite);
                }
                
                pieces.get(i).close();
            }
          
                // Close the wavFile
                original.close();
            
        }
        catch (Exception e)
        {
                System.err.println(e);
        }
    }
    
    public static ArrayList<Long> periodOfSilenceDetect(File aula){
        ArrayList<Long> periods_Of_Silence = new ArrayList<>();
        ArrayList<Long> periods_Of_Silence_Per_Minute = new ArrayList<>();
        try
		{
                    
			
                        WavFile wavFile = WavFile.openWavFile(aula);
                        
                        //visualizando informações do arquivo
                        wavFile.display();
                        
                        // taking the sample rate 
                        int sampleRate = (int)wavFile.getSampleRate();
                        
                        // Calculate the number of frames required for specified duration
                        long numFrames = wavFile.getNumFrames();
                        
                        
			// Create a buffer of 44100(number of sampleRate) frames
			double[] buffer = new double[sampleRate];

			int framesRead;
			double min = Double.MAX_VALUE;
                        double threshold_Signal = 0.03;
                        
                        //have to be decremented every frame of silence
                        int verifySilence = sampleRate/4;
                        
			do
			{
				// Read frames into buffer
				framesRead = wavFile.readFrames(buffer, sampleRate);
                                
                                //getting the current time
                                long time_Frame = (numFrames-wavFile.getFramesRemaining());
                                //System.out.println("time: "+time+"  ////////////////////////////////////////");
				
                                // Loop through frames and look for minimum value
                                for (int s=0 ; s<framesRead ; s++)
				{
                                    //setting the value to positive(pegando o módulo)
                                    if (buffer[s]<0) {
                                        buffer[s]=buffer[s]*-1;
                                    }
                                   
                                    //verifying a period of silence
                                    if (buffer[s] < threshold_Signal)
                                    {    
                                       verifySilence--;
                                    }
                                    else verifySilence = sampleRate/4;
                                    
                                    //storing a period of silence or the end of file
                                    if(verifySilence==0)
                                    {
                                        verifySilence = sampleRate/4;
                                        //addying the time immediatly before the period of silence
                                        periods_Of_Silence.add(time_Frame);
                                        
                                    }
				}
                                //addying also, the last audio period, even when the length is less than a minute
                                if(framesRead == 0)periods_Of_Silence.add(numFrames);
			}
			while (framesRead != 0);

			// Close the wavFile
			wavFile.close();
                       
                        
                        long limit_Of_Silence = 0;
                        //While the minutes passes, we have to update
                        long last_period_per_minute = 0;
                        
                        //filtering the periods by minute
                        for (int i = 0; i < periods_Of_Silence.size()-1; i++) {
                            limit_Of_Silence = periods_Of_Silence.get(i+1) - last_period_per_minute;
                            
                           
                            //limiting the period of silence by 10 seconds and //addying also, the last audio period, even when the length is less than a minute
                            if (limit_Of_Silence>=441000|i==periods_Of_Silence.size()-2) {
                                periods_Of_Silence_Per_Minute.add(periods_Of_Silence.get(i));
                                last_period_per_minute = periods_Of_Silence.get(i);
                                if (limit_Of_Silence>=60*44100) {
                                    System.out.println("nao deu: "+i);
                                }
                            }
                            
                            
                        }
                        
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
        
        
        
       return periods_Of_Silence_Per_Minute;
    }
}
