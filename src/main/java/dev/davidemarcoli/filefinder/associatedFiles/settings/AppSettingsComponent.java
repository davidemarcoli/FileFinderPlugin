package dev.davidemarcoli.filefinder.associatedFiles.settings;

// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.Border;
import java.util.Arrays;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent {

    private final JPanel myMainPanel;
    private final JTextField searchedFolders = new JBTextField(50);
    private final JTextField searchedFiles = new JTextField(50);
    private final JTextField fileKeywords = new JTextField(50);

    public AppSettingsComponent() {
//        Border newBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);

//        searchedFolders.setMargin(JBUI.insetsBottom(100));
//        searchedFiles.setMargin(JBUI.insetsBottom(100));
//        fileKeywords.setMargin(JBUI.insetsBottom(100));
//        searchedFolders.setBorder(newBorder);

        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("The Folders that should be searched"), searchedFolders, 15, true)
                .addLabeledComponent(new JBLabel("The File Patters that should be searched (% is replaced with the name)"), searchedFiles, 15, true)
                .addLabeledComponent(new JBLabel("The Keywords that should be removed when parsing the name"), fileKeywords, 15, true)
//                .addComponent(myIdeaUserStatus, 1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return searchedFolders;
    }

    public String[] getSearchedFolders() {
        return searchedFolders.getText().substring(1, searchedFolders.getText().length() - 1).split(", ");
    }

    public void setSearchedFolders(String[] folders) {
        searchedFolders.setText(Arrays.toString(folders));
    }

    public String[] getSearchedFiles() {
        return searchedFiles.getText().substring(1, searchedFiles.getText().length() - 1).split(", ");
    }

    public void setSearchedFiles(String[] files) {
        searchedFiles.setText(Arrays.toString(files));
    }

    public String[] getFileKeywords() {
        return fileKeywords.getText().substring(1, fileKeywords.getText().length() - 1).split(", ");
    }

    public void setFileKeywords(String[] keywords) {
        fileKeywords.setText(Arrays.toString(keywords));
    }
}