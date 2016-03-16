package rqg.test;


import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by rqg on 3/10/16.
 */
public class FilePanel extends JPanel {
    private OnFileLoaded mListener;
    private int mType;
    JTextField filePath = new JTextField();
    JButton openFileButton = new JButton();
    public Platform platform = Platform.Android;

    public FilePanel(String language, int fileType, OnFileLoaded listener) {
        setLayout(new FlowLayout(FlowLayout.LEADING));


        mType = fileType;
        mListener = listener;

        Border border = BorderFactory.createTitledBorder(language);
        setBorder(border);

        filePath.setColumns(20);
        filePath.setEditable(false);
        openFileButton.setText("Open");


        openFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                String fileSuffix = "xml";
                if (platform == Platform.iOS) {
                    fileSuffix = "strings";
                }
                jfc.setFileFilter(new FileNameExtensionFilter(null, fileSuffix));
                jfc.showDialog(new JLabel(), "选择");

                if (jfc.getSelectedFile() != null) {
                    filePath.setText(jfc.getSelectedFile().getAbsolutePath());
                    if (mListener != null) {
                        mListener.onFileLoaded(jfc.getSelectedFile(), mType);
                    }
                }

            }
        });

        add(filePath);
        add(openFileButton);

    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        openFileButton.setEnabled(enabled);
    }

    public static interface OnFileLoaded {
        void onFileLoaded(File file, int type);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.add(new FilePanel("中文", 1, null));
        frame.setSize(800, 800);
        frame.setVisible(true);
    }
}
