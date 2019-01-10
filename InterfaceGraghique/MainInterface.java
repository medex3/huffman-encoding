package InterfaceGraghique;

import javax.swing.*;
import javax.swing.text.Position;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static java.awt.Color.lightGray;

public class MainInterface {
    private JFrame frame;
    private JButton button, button1;
    private JPanel panel,panel2;
    private JLabel label;
    private  JFileChooser fileChooser;
        public MainInterface(){
            gui();
        }

    private void gui()  {
            frame =new JFrame();
            frame.setTitle("HuffmanEncoding");
            frame.setVisible(true);
            frame.setSize(400,400);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            panel =new JPanel();
            panel2= new JPanel();
            label =new JLabel();
            panel.setBackground(lightGray); 
        fileChooser =new JFileChooser();
        JImageComponent ic = new JImageComponent(myImageGoesHere);
                imagePanel.add(ic);
        button =new JButton();
        final String[] filename = new String[1];  
                    button.setText("Select");
                        button.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                               
                                    fileChooser.setDialogTitle("Choisie un ficher");
                                        fileChooser.setFileFilter(new FicherController(".txt","ficher texte"));
                                      int fs= fileChooser.showSaveDialog(null);
                                    if(fs ==JFileChooser.APPROVE_OPTION){
                                        String rs=label.getText();
                                        File file= fileChooser.getSelectedFile();
                                                file.getPath();
                                         String filename=new String(file.getAbsolutePath());
                                        label.setText(rs);
                                        frame.repaint();
                                    }
                            }
                        });


                button1 =new JButton("Convert");
                /*fileChooser= new JFileChooser("Select");
                    fileChooser.setDialogTitle("sauvegarder");
                    fileChooser.showSaveDialog(null);*/
                panel.add(label);
                panel.add(button);
                panel.add(button1);
                //panel2.add(fileChooser);
                //frame.add(panel2);
         button1.addActionListener(new ActionListener() {
 @Override
  public void actionPerformed(ActionEvent actionEvent) {
                                new HuffmanEncoding().encode(filename[0], filename[0] +"huffman",1);
                                new HuffmanEncoding().decode(filename[0] +".huffman", filename[0]);
        JOptionPane.showMessageDialog(frame,"conversion est fait.") ;
      
                            }
                        });




                frame.add(panel,BorderLayout.EAST);


    }




    public static void main(String []Args){
            new MainInterface();


    }
}
