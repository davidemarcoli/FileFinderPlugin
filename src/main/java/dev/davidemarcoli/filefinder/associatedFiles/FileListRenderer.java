package dev.davidemarcoli.filefinder.associatedFiles;

import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class FileListRenderer extends JPanel implements ListCellRenderer<Object> {
    private final JLabel label;

    public FileListRenderer() {
        setLayout(new BorderLayout());
        label = new JLabel();
        label.setOpaque(true);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        add(label, BorderLayout.CENTER);

        // Adding padding (or space) around each item
        setBorder(JBUI.Borders.empty(4)); // top, left, bottom, right padding
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof File) {
            File file = (File) value;
            label.setText(file.getName());
            label.setBackground(list.getBackground());
        } else if (value instanceof String) {
            label.setText((String) value);
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            label.setBackground(list.getBackground().darker()); // slightly darker for headers
        }

        if (isSelected) {
            label.setBackground(list.getSelectionBackground());
            label.setForeground(list.getSelectionForeground());
        } else {
            label.setForeground(list.getForeground());
        }

        return this;
    }
}
