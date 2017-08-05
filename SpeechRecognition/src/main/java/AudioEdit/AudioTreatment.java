/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AudioEdit;

import com.mycompany.speechrecognition.ClassRecognition;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author MQGuilherme
 */
public class AudioTreatment {
    
    public static void main(String[] args) throws Exception {
        File audio = new File("./resources/aulas/redes/Aula_001(1 canal).wav");
        ArrayList<Integer> periods_Of_Silence = periodOfSilenceDetect(audio);
        cutAudiosOnSilence(audio,periods_Of_Silence);
        
    }

    private static void legendPiece(String way)throws Exception{
    ClassRecognition Class_recon = new ClassRecognition(way);
    
    File folder = new File(way);
    File[] listOfFiles = folder.listFiles();

    for (File file : listOfFiles) {
        if (file.isFile()) {
            System.out.println(file.getName());
        }
    }
    
    
    }
    private static void cutAudiosOnSilence(File aula, ArrayList<Integer> periods_Of_Silence) {
        

        //making an list of audio pieces
        ArrayList<WavFile> pieces = new ArrayList<>();
        
        File piece;
        try
	{    
            for (int i = 0; i < periods_Of_Silence.size(); i++) {
                
                int sampleRate = 44100;		// Samples per second
                int duration;
                if (i==0) {
                    duration = periods_Of_Silence.get(i);		// Seconds
                }else{
                    duration = periods_Of_Silence.get(i) - periods_Of_Silence.get(i-1);		// Seconds
                }
		// Calculate the number of frames required for specified duration
		long numFrames = (long)(duration * sampleRate);
                
                piece = new File("./resources/aulas/redes/pieces/Aula_001 Slice "+(i)+".wav");
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
    
    public static ArrayList<Integer> periodOfSilenceDetect(File aula){
        ArrayList<Integer> periods_Of_Silence = new ArrayList<>();
        ArrayList<Integer> periods_Of_Silence_Per_Minute = new ArrayList<>();
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
                        int verifySilence = sampleRate/2;
                        
			do
			{
				// Read frames into buffer
				framesRead = wavFile.readFrames(buffer, sampleRate);
                                
                                //getting the current time
                                int time = (int)(numFrames-wavFile.getFramesRemaining())/sampleRate;
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
                                    else verifySilence = sampleRate/2;
                                    
                                    //storing a period of silence or the end of file
                                    if(verifySilence==0)
                                    {
                                        verifySilence = sampleRate/2;
                                        //addying the time immediatly before the period of silence
                                        periods_Of_Silence.add(time);
                                    }
				}
                                //addying also, the last audio period, even when the length is less than a minute
                                if(framesRead == 0)periods_Of_Silence.add((int)numFrames/sampleRate);
			}
			while (framesRead != 0);

			// Close the wavFile
			wavFile.close();
                       
                        
                        int limit_Of_Silence = 0;
                        //While the minutes passes, we have to update
                        int last_period_per_minute = 0;
                        
                        //filtering the periods by minute
                        for (int i = 0; i < periods_Of_Silence.size()-1; i++) {
                            limit_Of_Silence = periods_Of_Silence.get(i+1) - last_period_per_minute;
                            
                           
                            //limiting the period of silence by 1 minute and //addying also, the last audio period, even when the length is less than a minute
                            if (limit_Of_Silence>=10|i==periods_Of_Silence.size()-2) {
                                periods_Of_Silence_Per_Minute.add(periods_Of_Silence.get(i));
                                last_period_per_minute = periods_Of_Silence.get(i);
                                
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
