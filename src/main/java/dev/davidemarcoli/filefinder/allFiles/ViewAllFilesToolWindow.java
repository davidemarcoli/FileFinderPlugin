package dev.davidemarcoli.filefinder.allFiles;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ViewAllFilesToolWindow {

    private JButton refreshToolWindowButton;
    private JButton hideToolWindowButton;

    private JTree tree;
    private JPanel myToolWindowContent;

    public ViewAllFilesToolWindow(ToolWindow toolWindow) {
        hideToolWindowButton.addActionListener(e -> toolWindow.hide(null));
        refreshToolWindowButton.addActionListener(e -> populateFileTree());

        this.populateFileTree();
    }

    public void populateFileTree() {
        DataContext dataContext = DataManager.getInstance().getDataContext();
        Object projectObj = dataContext.getData("project");

        // Check if the object is an instance of Project before casting
        if (!(projectObj instanceof Project)) {
            return; // or handle this scenario as required
        }

        Project project = (Project) projectObj;
        String basePath = project.getBasePath();
        if (basePath == null) {
            return; // or handle this scenario as required
        }

        // Run directory loading in a background thread
        Task.Backgroundable task = new Task.Backgroundable(project, "Loading Files") {
            public void run(ProgressIndicator indicator) {
                DefaultTreeModel model = new DefaultTreeModel(addNodes(null, new File(basePath)), false);
                ApplicationManager.getApplication().invokeLater(() -> tree.setModel(model));
            }
        };
        ProgressManager.getInstance().run(task);
        tree.addTreeSelectionListener(e -> handleFileSelection(e, project));
    }

    private void handleFileSelection(javax.swing.event.TreeSelectionEvent e, Project project) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
        List<Object> pathsList = List.of(e.getPath().getPath());
        String fullPath = constructFullPath(pathsList, project.getBasePath());

        Optional<VirtualFile> fileToOpenOpt = getVirtualFile(fullPath);

        fileToOpenOpt.ifPresent(virtualFile -> FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, virtualFile), true));
    }

    private String constructFullPath(List<Object> pathsList, String basePath) {
        String relativePath = String.join(File.separator, pathsList.stream().map(Object::toString).toList());
        return Paths.get(basePath, relativePath).toString();
    }

    private Optional<VirtualFile> getVirtualFile(String fullPath) {
        return Optional.ofNullable(LocalFileSystem.getInstance().findFileByIoFile(new File(fullPath)));
    }

    DefaultMutableTreeNode addNodes(DefaultMutableTreeNode curTop, File dir) {
        String curPath = dir.getPath();
        String curDirName = Paths.get(curPath).getFileName().toString();
        DefaultMutableTreeNode curDir = new DefaultMutableTreeNode(curDirName);

        if (curTop != null) {
            curTop.add(curDir);
        }

        List<String> folderContentNames = List.of(Optional.ofNullable(dir.list()).orElse(new String[]{}));

        List<String> folderNames = new ArrayList<>();
        List<String> files = new ArrayList<>();

        for (String contentName : folderContentNames) {
            File currentFile = new File(curPath, contentName);
            if (currentFile.isDirectory()) {
                addNodes(curDir, currentFile);
            } else {
                files.add(contentName);
            }
        }

        for (String fileName : files) {
            curDir.add(new DefaultMutableTreeNode(fileName));
        }

        return curDir;
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
