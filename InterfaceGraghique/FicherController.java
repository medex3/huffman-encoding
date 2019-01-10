package InterfaceGraghique;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class FicherController extends FileFilter {
        private  final String extention;
        private  final String description;
        private  final String extention2;
        private  final String description2;
         private  final String extention1;
        private  final String description1;
r
r
    public FicherController(String extention, String description ,String extention2,String description2,String extention1,String description1) {
        this.extention = extention;
        this.description = description;
        this.extention = extention2;
        this.description = description2;


         this.extention = extention1;
        this.description = description1;


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
   public void image(){              
 ImageInputStream iis = ImageIO.createImageInputStream(file);

Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);

while (imageReaders.hasNext()) {
    ImageReader reader = (ImageReader) imageReaders.next();
    System.out.printf("formatName: %s%n", reader.getFormatName());}
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
