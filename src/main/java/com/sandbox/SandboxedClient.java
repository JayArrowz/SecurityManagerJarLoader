package com.sandbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.marceloaguiarr.valkyrie.Valkyrie;
import com.github.marceloaguiarr.valkyrie.enums.SecurityManagers;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.nio.file.Paths;
import java.util.jar.JarFile;

public class SandboxedClient {
    private static ClientSecurityProfile pluginSecurityProfile;
    private static PolicyFile policyFile;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, UnknownHostException {
        JFrame jframe = new JFrame();//creating instance of JFrame
        jframe.setResizable(false);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setTitle("Client Sandboxer");
        jframe.setSize(390,100);

        JButton runButton = new JButton("Run");//creating instance of JButton
        runButton.setBounds(20,35,340, 20);//x axis, y axis, width, height
        jframe.add(runButton);

        JTextArea policyFilePath = new JTextArea();
        policyFilePath.setEditable(false);
        policyFilePath.setBounds(20, 10, 230, 20);
        jframe.add(policyFilePath);

        JButton setPolicyButton = new JButton();
        setPolicyButton.setBounds(260, 10, 100, 20);
        setPolicyButton.setText("Pick Policy");
        jframe.add(setPolicyButton);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("json", "json"));
        setPolicyButton.addActionListener((e) -> {
            int dialogueResponse = fileChooser.showDialog(jframe, "Open");
            if(dialogueResponse == JFileChooser.APPROVE_OPTION) {
                try {
                    policyFile = mapper.readValue(fileChooser.getSelectedFile(), PolicyFile.class);
                    policyFilePath.setText(fileChooser.getSelectedFile().getAbsolutePath());
                } catch(Exception exception) {
                    JOptionPane.showMessageDialog(jframe, "Error: Cannot read policy file. Message: " + exception.getMessage());
                }
            }
        });

        runButton.addActionListener(e -> {
            try {
                if(policyFile == null) {
                    JOptionPane.showMessageDialog(jframe, "Error: Cannot read policy file.");
                    return;
                }
                loadPolicy(policyFilePath.getText());
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(jframe, "Error: Cannot execute client. Message: " + exception.getMessage());
            }
        });

        jframe.setLayout(null);
        jframe.setVisible(true);
    }
    
    static URLClassLoader loadPolicy(String policyPath) throws ClassNotFoundException, IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        policyFile = mapper.readValue(new File(policyPath), PolicyFile.class);
        URL jarUrl = new URL(policyFile.getJarUrl());
        String clientPath = Paths.get("clients/", jarUrl.getPath()).toAbsolutePath().toString();
        File jarFile = new File(clientPath);
        if(!jarFile.exists()) {
            jarFile.getParentFile().mkdirs();
            try (BufferedInputStream in = new BufferedInputStream(jarUrl.openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(clientPath)) {
                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        pluginSecurityProfile = new ClientSecurityProfile(policyFile);
        Valkyrie.addProfile(URLClassLoader.class, pluginSecurityProfile);
        Valkyrie.setSecurityManager(SecurityManagers.DEFAULT);

        JarFile jarFileLoaded = new JarFile(jarFile);
        String mainClass = jarFileLoaded.getManifest().getMainAttributes().getValue("Main-Class");

        URLClassLoader child = new URLClassLoader(
                new URL[] { new URL("file:///" + jarFile.getAbsolutePath()) },
                SandboxedClient.class.getClassLoader()
        );
        Valkyrie.start();
        Class<?> clazz = Class.forName(mainClass, false, child);
        clazz.getMethod("main", String[].class).invoke(null, (Object) policyFile.getMainArgs());
        return child;
    }
}
