/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AudioTreatmentAndLegend;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author guilh
 */
public class AudioFileFilter implements FileFilter {
    
    
    
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().endsWith("wav");
            }
}
