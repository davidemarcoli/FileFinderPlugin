// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package dev.davidemarcoli.filechangerplugin.associatedFiles;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.util.messages.MessageBus;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ViewAssociatedFilesToolWindow {

    private JList<String> fileList;
    private JPanel myToolWindowContent;

    public ViewAssociatedFilesToolWindow(ToolWindow toolWindow) {
        MessageBus messageBus = toolWindow.getProject().getMessageBus();
        messageBus.connect().subscribe(FileChangeNotifier.FILE_CHANGE_NOTIFIER_TOPIC, new FileChangeNotifier() {
            @Override
            public void fileChanged(String fileName) {
                System.out.println("File changed: " + fileName);
                ApplicationManager.getApplication().invokeLater(() -> {
                    getAssociatedFiles(fileName);
                });
//                getAssociatedFiles(fileName);
            }
        });
    }

    final String[] keywords = {"Controller", "Service", "Repository", "Component", "Module", "Model", "Interface", "Enum", "Class", "Directive", "Pipe", "Guard", "Resolver", "Interceptor", "Service", "Component", "Module", "Model", "Interface", "Enum", "Class", "Directive", "Pipe", "Guard", "Resolver", "Interceptor"};

    public void getAssociatedFiles(String fileName) {

        DataContext dataContext = DataManager.getInstance().getDataContext();
        Project project = (Project) dataContext.getData("project");
        System.out.println("Project: " + project.getName());
        System.out.println("Base path: " + project.getBasePath());

        String modifiedFileName = fileName.substring(0, fileName.lastIndexOf("."));
        for (String keyword : keywords) {
            modifiedFileName = modifiedFileName.replaceAll(keyword, "");
        }
        modifiedFileName = modifiedFileName.replaceAll(" ", "");
        modifiedFileName = modifiedFileName.replaceAll("-", "");
        modifiedFileName = modifiedFileName.replaceAll("_", "");

        ArrayList<File> files = searchFiles(new File(project.getBasePath()), modifiedFileName);
        ArrayList<String> fileNames = new ArrayList<>();
        for (File file : files) {
            fileNames.add(file.getName());
        }
        fileList.setListData(fileNames.toArray(String[]::new));
    }

    ArrayList<File> searchFiles(File file, String search) {

        ArrayList<File> found = new ArrayList<>();

        String[] possibleFilePattern = {search + "Service.java", search + "ServiceImpl.java", search + "Controller.java", search + "Repository.java",
                search + ".ts", search + ".service.ts", search + "-component.ts", search + "-component.html", search + "-component.scss", search + "-component.spec.ts"};

        if (file.isDirectory()) {
//            System.out.println("Searching in: " + file.getName());
            File[] filesInDir = file.listFiles();
            for (File f : filesInDir) {
                ArrayList<File> foundFiles = searchFiles(f, search);
                found.addAll(foundFiles);
            }
        } else {
//            System.out.println("File: " + file.getName());
            for (String pattern : possibleFilePattern) {
                Pattern regexPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
                if (regexPattern.matcher(file.getName()).matches()) {
                    System.out.println("Found: " + file.getName());
                    found.add(file);
                }
            }
        }
        return found;
    }

    public Dimension getMinimumSize() {
        return new Dimension(200, 400);
    }

    public Dimension getPreferredSize() {
        return new Dimension(200, 400);
    }

    public JPanel getContent() {
        return myToolWindowContent;
    }

}
