// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package dev.davidemarcoli.filefinder.associatedFiles;

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
import dev.davidemarcoli.filefinder.associatedFiles.settings.AppSettingsState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class ViewAssociatedFilesToolWindow {

    AppSettingsState settings = AppSettingsState.getInstance();
    private JList<Object> fileList;
    private JPanel myToolWindowContent;

    public ViewAssociatedFilesToolWindow(ToolWindow toolWindow) {
        MessageBus messageBus = toolWindow.getProject().getMessageBus();
        messageBus.connect().subscribe(FileChangeNotifier.FILE_CHANGE_NOTIFIER_TOPIC, fileName -> {
            System.out.println("File changed: " + fileName);
            if (fileName == null) {
                fileList.setListData(new File[]{});
                return;
            }
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
                        if (o instanceof String) return; // ignore headers (file extensions)
                        File file = (File) o;
                        openFileInEditor(toolWindow.getProject(), file);
                    }
                }
            }
        };

        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
            }

            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    Object o = fileList.getSelectedValue();
                    if (o instanceof String) return; // ignore headers (file extensions)
                    File file = (File) o;
                    openFileInEditor(toolWindow.getProject(), file);
                }
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
            }
        };

        fileList.addMouseListener(mouseListener);
        fileList.addKeyListener(keyListener);

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
            modifiedFileName = modifiedFileName.replaceAll("(?i)" + keyword, "");
        }
        modifiedFileName = modifiedFileName.replaceAll(" ", "");
        modifiedFileName = modifiedFileName.replaceAll("-", "");
        modifiedFileName = modifiedFileName.replaceAll("_", "");
        modifiedFileName = modifiedFileName.replaceAll("\\.", "");

        System.out.println("Modified file name: " + modifiedFileName);

        ArrayList<Object> groupedFiles = new ArrayList<>();

// Use a TreeMap to automatically sort by file extension
        Map<String, java.util.List<File>> filesByExtension = new TreeMap<>();

        for (String folderName : settings.searchedFolders) {
            java.util.List<File> foundFiles = searchFiles(new File(project.getBasePath() + "/" + folderName), modifiedFileName);
            for (File file : foundFiles) {
                String extension = getFileExtension(file);
                filesByExtension.putIfAbsent(extension, new ArrayList<>());
                filesByExtension.get(extension).add(file);
            }
        }

// Now add the files to the groupedFiles list, prefixed by their extension
        for (Map.Entry<String, java.util.List<File>> entry : filesByExtension.entrySet()) {
            String extension = entry.getKey();
            java.util.List<File> filesWithSameExtension = entry.getValue();

            // Add the file extension as a header/group
            groupedFiles.add(extension.toUpperCase() + " Files:");

            // Add all files with the same extension under the header
            groupedFiles.addAll(filesWithSameExtension);
        }

        fileList.setListData(groupedFiles.toArray());

//        ArrayList<String> fileNames = new ArrayList<>();
//        for (File file : files) {
//            fileNames.add(file.getName());
//        }
//        fileList.setListData(fileNames.toArray(String[]::new));
    }

    // Helper method to get file extension
    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "Unknown"; // handle case where file has no extension
        }
        return name.substring(lastIndexOf + 1);
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
