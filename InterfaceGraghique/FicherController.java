package InterfaceGraghique;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class FicherController extends FileFilter {
        private  final String extention;
        private  final String description;
        private  final String extention2;
        private  final String description2;
r
    public FicherController(String extention, String description ,String extention2,String description2) {
        this.extention = extention;
        this.description = description;
        this.extention2 = extention2;
        this.description2 = description2;
        


    }
public void playSound() {
    try {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File().getAbsoluteFile());
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.start();
    } catch(Exception ex) {
        System.out.println("Error with playing sound.");
        ex.printStackTrace();
    }
}
    @Override
    public boolean accept(File file) {
                if (file.isDirectory()){
                    return true;
                }
        return file.getName().endsWith(extention);
    }

    @Override
    public String getDescription() {
        return description +String.format("(*%s)",extention);
    }
}
