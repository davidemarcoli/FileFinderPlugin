package dev.davidemarcoli.filechangerplugin.associatedFiles;

import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FileListRenderer extends JLabel implements ListCellRenderer<File> {
    @Override
    public Component getListCellRendererComponent(JList<? extends File> list, File value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value.getName());
        if (value.getName().endsWith(".java")) {
            setBackground(JBColor.RED);
        } else if (value.getName().endsWith(".ts")) {
            setBackground(JBColor.GREEN);
        }
        return this;
    }
}
