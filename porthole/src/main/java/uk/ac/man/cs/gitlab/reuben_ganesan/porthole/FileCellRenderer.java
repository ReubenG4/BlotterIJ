package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.awt.Component;
import java.io.File;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

class FileCellRenderer extends DefaultListCellRenderer
{
    public Component getListCellRendererComponent(
        JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        File file = (File)value;
        setText( file.getName() );
        return this;
    }
}