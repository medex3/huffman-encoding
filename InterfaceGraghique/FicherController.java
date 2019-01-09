package InterfaceGraghique;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class FicherController extends FileFilter {
        private  final String extention;
        private  final String description;
         private  final String extention1;
        private  final String description1;

    public FicherController(String extention,String extention1, String description,String description1) {
        this.extention = extention;
        this.description = description;
            this.extention1 = extention1;
        this.description1 = description1;
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
