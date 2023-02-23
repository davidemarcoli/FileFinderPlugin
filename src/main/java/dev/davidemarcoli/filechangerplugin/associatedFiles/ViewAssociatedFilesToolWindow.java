// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package dev.davidemarcoli.filechangerplugin.associatedFiles;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.util.messages.MessageBus;
import dev.davidemarcoli.filechangerplugin.associatedFiles.settings.AppSettingsState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class ViewAssociatedFilesToolWindow {

    private JList<File> fileList;
    private JPanel myToolWindowContent;

    AppSettingsState settings = AppSettingsState.getInstance();

    public ViewAssociatedFilesToolWindow(ToolWindow toolWindow) {
        MessageBus messageBus = toolWindow.getProject().getMessageBus();
        messageBus.connect().subscribe(FileChangeNotifier.FILE_CHANGE_NOTIFIER_TOPIC, fileName -> {
            System.out.println("File changed: " + fileName);
            ApplicationManager.getApplication().invokeLater(new Thread(() -> getAssociatedFiles(fileName)));
        });

        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                JList<String> theList = (JList) mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 2) {
                    int index = theList.locationToIndex(mouseEvent.getPoint());
                    if (index >= 0) {
                        Object o = theList.getModel().getElementAt(index);
                        System.out.println("Double-clicked on: " + o.toString());
                        File file = (File) o;
                        openFileInEditor(toolWindow.getProject(), file);
                    }
                }
            }
        };
        fileList.addMouseListener(mouseListener);

        // add custom cell renderer
        fileList.setCellRenderer(new FileListRenderer());
    }

//    final String[] keywords = {"Controller", "Service", "Repository", "Component", "Module", "Model", "DTO", "Mapper", "Interface", "Enum", "Class", "Directive", "Pipe", "Guard", "Resolver", "Interceptor", "Service", "Component", "Module", "Model", "Interface", "Enum", "Class", "Directive", "Pipe", "Guard", "Resolver", "Interceptor"};

    public void getAssociatedFiles(String fileName) {

        DataContext dataContext = DataManager.getInstance().getDataContext();
        Project project = (Project) dataContext.getData("project");
//        System.out.println("Project: " + project.getName());
//        System.out.println("Base path: " + project.getBasePath());

        String modifiedFileName = fileName.substring(0, fileName.lastIndexOf("."));
        for (String keyword : settings.fileKeywords) {
            modifiedFileName = modifiedFileName.replaceAll(keyword, "");
        }
        modifiedFileName = modifiedFileName.replaceAll(" ", "");
        modifiedFileName = modifiedFileName.replaceAll("-", "");
        modifiedFileName = modifiedFileName.replaceAll("_", "");

        System.out.println("Modified file name: " + modifiedFileName);

        ArrayList<File> files = new ArrayList<>();

        for (String folderName : settings.searchedFolders) {
            files.addAll(searchFiles(new File(project.getBasePath() + "/" + folderName), modifiedFileName));
        }
        fileList.setListData(files.toArray(File[]::new));

//        ArrayList<String> fileNames = new ArrayList<>();
//        for (File file : files) {
//            fileNames.add(file.getName());
//        }
//        fileList.setListData(fileNames.toArray(String[]::new));
    }

    ArrayList<File> searchFiles(File file, String search) {

        ArrayList<File> found = new ArrayList<>();

//        String[] possibleFilePattern = {search + "Service.java", search + "ServiceImpl.java", search + "Controller.java", search + "Repository.java", search + "DTO.java", search + "Mapper.java",
//                search + ".ts", search + ".service.ts", search + "-component.ts", search + "-component.html", search + "-component.scss", search + "-component.spec.ts"};


        String[] possibleFilePattern = Arrays.stream(settings.searchedFiles).map(s -> s.replaceAll("%", search)
        ).toArray(String[]::new);

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

    private void openFileInEditor(Project project, File file) {
        VirtualFile fileToOpen = LocalFileSystem.getInstance().findFileByIoFile(new File(file.getAbsolutePath()));

        assert fileToOpen != null;
        FileEditorManager.getInstance(project).openTextEditor(
                new OpenFileDescriptor(
                        project,
                        fileToOpen
                ),
                true // request focus to editor
        );
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
