package qd_jpt;

import qd_jpt.patch.Difference;
import qd_jpt.patch.JarAnalyzer;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

public class PatchGenerator {
    public static void main(String[] args) {

        JFrame jFrame = new JFrame("QD-JarPatchTool");
        jFrame.setSize(800, 300);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel jPanel = new JPanel();
        GridBagConstraints c = new GridBagConstraints();
        GridBagLayout layout = new GridBagLayout();
        layout.setConstraints(jPanel, c);
        jPanel.setLayout(layout);
        jPanel.setBorder(BorderFactory.createEmptyBorder(20,10,20,10));

        JLabel oldJarLabel = new JLabel("Old Jar");
        JTextField oldJarTextField = new JTextField("", 50);
        JButton oldJarChooseFileButton = new JButton("Choose File");
        oldJarChooseFileButton.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser("~");
            FileFilter filter = new FileNameExtensionFilter("Jar File", "jar");
            jFileChooser.setFileFilter(filter);
            jFileChooser.showOpenDialog(jFrame);
            if(jFileChooser.getSelectedFile() != null){
                oldJarTextField.setText(jFileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        JLabel newJarLabel = new JLabel("New Jar");
        JTextField newJarTextField = new JTextField("", 50);
        JButton newJarChooseFileButton = new JButton("Choose File");
        newJarChooseFileButton.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser("~");
            FileFilter filter = new FileNameExtensionFilter("Jar File", "jar");
            jFileChooser.setFileFilter(filter);
            jFileChooser.showOpenDialog(jFrame);
            if(jFileChooser.getSelectedFile() != null){
                newJarTextField.setText(jFileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        JButton createPatchButton = new JButton("Create Patch");
        createPatchButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("~");
            fileChooser.showSaveDialog(jFrame);
            if(fileChooser.getSelectedFile() != null){
                try {
                    JarFile jarFileOld = new JarFile(new File(oldJarTextField.getText()));
                    JarFile jarFileNew = new JarFile(new File(newJarTextField.getText()));
                    Difference diff = JarAnalyzer.calculateDifference(jarFileOld, jarFileNew);
                    JarAnalyzer.createPatchFile(diff, jarFileOld, jarFileNew, fileChooser.getSelectedFile());
                    JOptionPane.showMessageDialog(jFrame, "Created Patch File!");
                    oldJarTextField.setText("");
                    newJarTextField.setText("");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        c.gridx = 0;
        c.gridy = 0;
        jPanel.add(oldJarLabel, c);
        c.gridx = 1;
        c.gridy = 0;
        jPanel.add(oldJarTextField, c);
        c.gridx = 2;
        c.gridy = 0;
        jPanel.add(oldJarChooseFileButton, c);
        c.gridx = 0;
        c.gridy = 1;
        jPanel.add(newJarLabel, c);
        c.gridx = 1;
        c.gridy = 1;
        jPanel.add(newJarTextField, c);
        c.gridx = 2;
        c.gridy = 1;
        jPanel.add(newJarChooseFileButton, c);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        jPanel.add(createPatchButton, c);

        jFrame.setContentPane(jPanel);
        jFrame.pack();
        jFrame.setVisible(true);
    }
}
