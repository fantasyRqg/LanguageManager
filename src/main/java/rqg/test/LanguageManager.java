package rqg.test;

import rqg.xml.Resources;
import rqg.xml.XmlString;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import javax.xml.bind.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rqg on 3/10/16.
 */
public class LanguageManager extends JFrame implements FilePanel.OnFileLoaded {


    private TableModelListener modelListener;

    private JTable mLanguageTable = new JTable() {
        @Override
        public TableCellRenderer getCellRenderer(int row, int column) {
            return mCellRenderer;
        }

    };

    private DefaultTableCellRenderer mCellRenderer = new DefaultTableCellRenderer() {

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (row < languageArray.length && languageArray[row].check()) {
                setForeground(Color.BLACK);
            } else {
                setForeground(Color.RED);
            }


            return super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);
        }
    };


    private static final int CHINESE = 1, ENGLISH = 2;

    Language[] languageArray = null;

    Language[] completeArray = null;

    private Resources chineseResourse;
    private Resources englishResourse;

    private JCheckBox jCheckBox = new JCheckBox();

    private FilePanel chineseFilePanel = new FilePanel("Chinese", CHINESE, this);
    private FilePanel englishFilePanel = new FilePanel("English", ENGLISH, this);

    public LanguageManager() throws HeadlessException {
        super();


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();//获取屏幕尺寸对象

        initTable();
        initView();

        setLocationRelativeTo(null);

        setSize(screen.width, screen.height);
        setVisible(true);

    }

    private void initView() {


        BorderLayout borderLayout = new BorderLayout();
        setLayout(borderLayout);


        JPanel leftPanel = new JPanel();

        leftPanel.setLayout(new GridLayout(6, 1));

        leftPanel.add(chineseFilePanel);
        leftPanel.add(englishFilePanel);

        englishFilePanel.setEnabled(false);


        jCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (jCheckBox.isSelected()) {


                    if (languageArray == null)
                        return;


                    List<Language> filtered = new ArrayList<Language>();

                    for (Language l : languageArray) {
                        if (!l.check()) {
                            filtered.add(l);
                        }
                    }


                    languageArray = new Language[filtered.size()];

                    for (int i = 0; i < languageArray.length; i++) {
                        languageArray[i] = filtered.get(i);
                    }


                    mLanguageTable.addNotify();
                } else {

                    languageArray = completeArray;

                    mLanguageTable.addNotify();

                }
            }
        });


        jCheckBox.setText("只显示缺失部分");

        leftPanel.add(jCheckBox);


        JPanel exportPanel = new JPanel();
        exportPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        exportPanel.setBorder(BorderFactory.createTitledBorder("Export"));
        JButton exportChinese = new JButton();
        JButton exportEnglish = new JButton();


        exportChinese.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportChinese();
            }
        });


        exportEnglish.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportEnglish();
            }
        });

        exportChinese.setText("Export Chinese");
        exportEnglish.setText("Export English");


        exportPanel.add(exportChinese);
        exportPanel.add(exportEnglish);


        leftPanel.add(exportPanel);
        leftPanel.setMaximumSize(new Dimension(400, 600));

        JScrollPane scrollPane = new JScrollPane(mLanguageTable);

        add(scrollPane, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);


    }


    private void initTable() {


        TableModel tableModel = new TableModel() {


            public int getRowCount() {
                if (languageArray == null)
                    return 0;
                return languageArray.length;
            }

            public int getColumnCount() {
                return 3;
            }

            public String getColumnName(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return "Key";
                    case 1:
                        return "Chinese";
                    case 2:
                        return "English";
                    default:
                        return null;
                }
            }

            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex != 0;
            }

            public Object getValueAt(int rowIndex, int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return languageArray != null && rowIndex < languageArray.length ? languageArray[rowIndex].getKey() : null;
                    case 1:
                        return languageArray != null && rowIndex < languageArray.length ? languageArray[rowIndex].getChinese() : null;
                    case 2:
                        return languageArray != null && rowIndex < languageArray.length ? languageArray[rowIndex].getEnglish() : null;
                    default:
                        return null;
                }
            }

            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                if (aValue instanceof String) {

                    String s = (String) aValue;
                    if (rowIndex < languageArray.length) {
                        switch (columnIndex) {
                            case 0:
                                languageArray[rowIndex].setKey(s);
                                break;
                            case 1:
                                languageArray[rowIndex].setChinese(s);
                                break;
                            case 2:
                                languageArray[rowIndex].setEnglish(s);
                                break;
                            default:

                        }
                    }
                }
            }

            public void addTableModelListener(TableModelListener l) {
                modelListener = l;
            }

            public void removeTableModelListener(TableModelListener l) {
                modelListener = null;
            }
        };


        mLanguageTable.setShowGrid(true);
        mLanguageTable.setGridColor(new Color(206, 206, 206));

        mLanguageTable.setModel(tableModel);
        mLanguageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        mLanguageTable.setSelectionBackground(new Color(173, 213, 255));
        mLanguageTable.setSelectionForeground(Color.BLACK);
    }


    public void onFileLoaded(File file, int type) {
        switch (type) {
            case ENGLISH:
                handleEnglishFile(file);
                break;
            case CHINESE:
                handleChineseFile(file);
                break;
        }
    }


    private void handleChineseFile(File file) {
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance(Resources.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Resources resources = (Resources) jaxbUnmarshaller.unmarshal(file);


            chineseResourse = resources;


            englishFilePanel.setEnabled(true);

            initChineseData();
            mLanguageTable.addNotify();

        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }


    private void initChineseData() {

        if (chineseResourse == null)
            return;

        List<XmlString> strings = chineseResourse.getString();


        if (strings != null) {
            System.out.println("chinese = " + strings.size());
            languageArray = new Language[strings.size()];

            completeArray = languageArray;

            for (int i = 0; i < languageArray.length; i++) {
                XmlString xs = strings.get(i);
                Language language = new Language();


                language.setKey(xs.getName());
                language.setChinese(xs.getContent());

                languageArray[i] = language;
            }
        }

    }

    private void initEnglishData() {

        if (englishResourse == null)
            return;

        List<XmlString> strings = englishResourse.getString();


        if (strings != null && languageArray != null) {

            System.out.println("english = " + strings.size());
            for (XmlString xs : strings) {
                for (Language aLanguageArray : languageArray) {
                    if (aLanguageArray.getKey().equals(xs.getName())) {

                        aLanguageArray.setEnglish(xs.getContent());
                        break;
                    }
                }
            }
        }


    }


    private void exportChinese() {

        if (completeArray == null)
            return;

        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);


        jfc.setSelectedFile(new File("strings.xml"));


        int retval = jfc.showSaveDialog(this);//显示“保存文件”对话框
        if (retval == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(Resources.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

                // output pretty printed
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                Resources resources = new Resources();

                List<XmlString> xmlStringList = new ArrayList<XmlString>(completeArray.length);

                for (Language aCompleteArray : completeArray) {
                    XmlString xs = new XmlString();
                    xs.setName(aCompleteArray.getKey());
                    xs.setContent(aCompleteArray.getChinese());

                    xmlStringList.add(xs);
                }

                resources.setString(xmlStringList);


                jaxbMarshaller.marshal(resources, file);


            } catch (PropertyException e) {
                e.printStackTrace();
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
    }

    private void exportEnglish() {

        if (completeArray == null)
            return;

        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);


        jfc.setSelectedFile(new File("strings.xml"));


        int retval = jfc.showSaveDialog(this);//显示“保存文件”对话框
        if (retval == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();

            try {

                JAXBContext jaxbContext = JAXBContext.newInstance(Resources.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

                // output pretty printed
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                Resources resources = new Resources();

                List<XmlString> xmlStringList = new ArrayList<XmlString>(completeArray.length);

                for (Language aCompleteArray : completeArray) {
                    XmlString xs = new XmlString();
                    xs.setName(aCompleteArray.getKey());
                    xs.setContent(aCompleteArray.getEnglish());

                    xmlStringList.add(xs);
                }

                resources.setString(xmlStringList);


                jaxbMarshaller.marshal(resources, file);

            } catch (JAXBException e) {
                e.printStackTrace();
            }


        }
    }

    private void handleEnglishFile(File file) {
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance(Resources.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Resources resources = (Resources) jaxbUnmarshaller.unmarshal(file);

            englishResourse = resources;

            initEnglishData();

            mLanguageTable.addNotify();

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        LanguageManager lm = new LanguageManager();
    }


}

