package qd_jpt;

import qd_jpt.patch.JarPatcher;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class Patcher {
    public static void main(String[] args) {
        JFrame jFrame = new JFrame("QD-JarPatcher");
        jFrame.setSize(800, 300);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel jPanel = new JPanel();
        GridBagConstraints c = new GridBagConstraints();
        GridBagLayout layout = new GridBagLayout();
        layout.setConstraints(jPanel, c);
        jPanel.setLayout(layout);
        jPanel.setBorder(BorderFactory.createEmptyBorder(20,10,20,10));

        JLabel targetLabel = new JLabel("Target Jar");
        JTextField targetTextField = new JTextField("", 50);
        JButton targetChooseFileButton = new JButton("Choose File");
        targetChooseFileButton.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser("~");
            FileFilter filter = new FileNameExtensionFilter("Jar File", "jar");
            jFileChooser.setFileFilter(filter);
            jFileChooser.showOpenDialog(jFrame);
            if(jFileChooser.getSelectedFile() != null){
                targetTextField.setText(jFileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        JLabel patchLabel = new JLabel("Patch File");
        JTextField patchTextField = new JTextField("", 50);
        JButton patchChooseFileButton = new JButton("Choose File");
        patchChooseFileButton.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser("~");
            jFileChooser.showOpenDialog(jFrame);
            if(jFileChooser.getSelectedFile() != null){
                patchTextField.setText(jFileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        JButton patchButton = new JButton("Execute Patch");
        patchButton.addActionListener(e -> {
            try {
                File targetFile = new File(targetTextField.getText());
                File patchFile = new File(patchTextField.getText());
                JarPatcher.applyPatch(targetFile, patchFile, URI.create(targetFile.getParent() + File.separatorChar + "patched.jar"));

                JOptionPane.showMessageDialog(jFrame, "Patch applied! Please remember to rename the patched file!");
                targetTextField.setText("");
                patchTextField.setText("");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(jFrame, "Error during patch!\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        c.gridx = 0;
        c.gridy = 0;
        jPanel.add(targetLabel, c);
        c.gridx = 1;
        c.gridy = 0;
        jPanel.add(targetTextField, c);
        c.gridx = 2;
        c.gridy = 0;
        jPanel.add(targetChooseFileButton, c);
        c.gridx = 0;
        c.gridy = 1;
        jPanel.add(patchLabel, c);
        c.gridx = 1;
        c.gridy = 1;
        jPanel.add(patchTextField, c);
        c.gridx = 2;
        c.gridy = 1;
        jPanel.add(patchChooseFileButton, c);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        jPanel.add(patchButton, c);

        jFrame.setContentPane(jPanel);
        jFrame.pack();
        jFrame.setVisible(true);



    }
}
