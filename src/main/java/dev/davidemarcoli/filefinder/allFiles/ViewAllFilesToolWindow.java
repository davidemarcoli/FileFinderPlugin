// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package dev.davidemarcoli.filefinder.allFiles;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Vector;

public class ViewAllFilesToolWindow {

    private JButton refreshToolWindowButton;
    private JButton hideToolWindowButton;

    private JTree tree;
    private JPanel myToolWindowContent;

    public ViewAllFilesToolWindow(ToolWindow toolWindow) {
        hideToolWindowButton.addActionListener(e -> toolWindow.hide(null));
        refreshToolWindowButton.addActionListener(e -> getFileTree());

        this.getFileTree();
    }

    public void getFileTree() {
//    tree = new Tree(addNodes(null, new File(".")));

        DataContext dataContext = DataManager.getInstance().getDataContext();
        Project project = (Project) dataContext.getData("project");
        System.out.println(project.getBasePath());

        tree.setModel(new DefaultTreeModel(addNodes(null, new File(project.getBasePath())), false));

        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
                    .getPath().getLastPathComponent();
            System.out.println(Arrays.toString(e.getPath().getPath()));
            System.out.println(e.getPath());
            String[] paths = Arrays.stream(e.getPath().getPath()).map(Object::toString).toArray(String[]::new);
//      String[] paths = Arrays.stream(e.getPath().getPath()).map(o -> (String) o).toArray(String[]::new);
            System.out.println(Arrays.toString(paths));
            System.out.println(String.join("/", paths));
            System.out.println("You selected " + node);

            System.out.println(String.join("/", paths));
            System.out.println(Arrays.toString(String.join("/", paths).split("/", 2)));
            System.out.println(project.getBasePath() + "/" + String.join("/", paths).split("/", 2)[1]);

            VirtualFile fileToOpen = LocalFileSystem.getInstance().findFileByIoFile(new File(project.getBasePath() + "/" + String.join("/", paths).split("/", 2)[1]));
//      VirtualFile fileToOpen = LocalFileSystem.getInstance().findFileByNioFile(Path.of(String.join("/", paths).split("/", 2)[1]));
//      VirtualFile fileToOpen = project.getProjectFile().findFileByRelativePath(String.join("/", paths).split("/", 2)[1]);


            FileEditorManager.getInstance(project).openTextEditor(
                    new OpenFileDescriptor(
                            project,
                            fileToOpen
                    ),
                    true // request focus to editor
            );
        });
    }

    /**
     * Add nodes from under "dir" into curTop. Highly recursive.
     */
    DefaultMutableTreeNode addNodes(DefaultMutableTreeNode curTop, File dir) {
        String curPath = dir.getPath();
        String[] dirPathSplit = curPath.split("/");
        DefaultMutableTreeNode curDir = new DefaultMutableTreeNode(dirPathSplit[dirPathSplit.length - 1]);
        if (curTop != null) { // should only be null at root
            curTop.add(curDir);
        }
        Vector<String> folderNamesVector = new Vector<>();
        String[] folderContentNames = dir.list();
        for (String folderContentName : folderContentNames) {
            //System.out.println("2 - " + folderContentName);
            folderNamesVector.addElement(folderContentName);
        }
        folderNamesVector.sort(String.CASE_INSENSITIVE_ORDER);
        File currentFile;
        Vector<String> files = new Vector<>();
        // Make two passes, one for Dirs and one for Files. This is #1.
        for (int i = 0; i < folderNamesVector.size(); i++) {
            String thisObject = folderNamesVector.elementAt(i);
            String newPath;
            if (curPath.equals("."))
                newPath = thisObject;
            else
                newPath = curPath + File.separator + thisObject;
            if ((currentFile = new File(newPath)).isDirectory()) {
                //System.out.println("Recursion with - " + currentFile.getName());
                addNodes(curDir, currentFile);
            } else {
                //System.out.println("2 - " + thisObject);
                files.addElement(thisObject);
            }
        }
        // Pass two: for files.
        for (int fnum = 0; fnum < files.size(); fnum++) {
            //System.out.println("3 - " + files.elementAt(fnum));
            curDir.add(new DefaultMutableTreeNode(files.elementAt(fnum)));
        }
        return curDir;
    }

    File searchFile(File file, String search) {
        if (file.isDirectory()) {
            File[] arr = file.listFiles();
            for (File f : arr) {
                File found = searchFile(f, search);
                if (found != null)
                    return found;
            }
        } else {
            if (file.getName().equals(search)) {
                return file;
            }
        }
        return null;
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
