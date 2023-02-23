// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package dev.davidemarcoli.filechangerplugin.associatedFiles;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Vector;

public class ViewAssociatedFilesToolWindow {

  private JList fileList;
  private JPanel myToolWindowContent;

  public ViewAssociatedFilesToolWindow(ToolWindow toolWindow) {
    getOpenFile();
  }

  public void getOpenFile() {
//    tree = new Tree(addNodes(null, new File(".")));

    DataContext dataContext = DataManager.getInstance().getDataContext();
    Project project = (Project) dataContext.getData("project");
    System.out.println(project.getBasePath());

    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();

    EditorFactory editorFactory = EditorFactory.getInstance();
    editorFactory.addEditorFactoryListener(new EditorFactoryListener() {
      @Override
      public void editorCreated(@NotNull EditorFactoryEvent editorFactoryEvent) {
//        Document currentDoc = FileEditorManager.getInstance(project).getSelectedTextEditor().getDocument();
//        VirtualFile currentFile = FileDocumentManager.getInstance().getFile(currentDoc);
        System.out.println("Editor Created");
        VirtualFile currentFile = FileDocumentManager.getInstance().getFile(editorFactoryEvent.getEditor().getDocument());
        String fileName = currentFile.getPath();
        System.out.println(fileName);

//        System.out.println(editorFactoryEvent.getEditor().getDocument().getText());
      }

    });
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
