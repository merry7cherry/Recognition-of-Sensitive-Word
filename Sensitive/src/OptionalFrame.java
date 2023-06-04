import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class OptionalFrame extends JFrame implements ActionListener, MouseListener {
    private JPanel jPanel = new JPanel();
    private JPanel jpl1 = new JPanel();
    private JPanel jpl2 = new JPanel();
    private JPanel jpl3 = new JPanel();
    private JPanel jpl4 = new JPanel();
    private JPanel jpl5 = new JPanel();
    private JPanel jpl6 = new JPanel();
    private JPanel jpl7 = new JPanel();
    private JPanel jpl8 = new JPanel();
    private JLabel siteWarn = new JLabel("输入网址:");
    private JTextField siteField = new JTextField(25);
    private JScrollPane siteSPane = new JScrollPane(siteField);
    private JButton goSpider = new JButton("开始爬取");
    public JTextArea htmlArea = new JTextArea(15, 25);
    private JScrollPane htmlSPane = new JScrollPane(htmlArea);
    public JTextArea textArea = new JTextArea(15, 25);
    private JScrollPane textSPane = new JScrollPane(textArea);
    private JTabbedPane tabPane = new JTabbedPane();
    public JList<String> sensWord = new JList<>();
    private JScrollPane wordPane = new JScrollPane(sensWord);
    private JButton openLib = new JButton(" 导入敏感词库");
    private JButton match = new JButton("匹配");
    private JButton siteLib = new JButton("导入网址库");
    private JComboBox<String> charset = new JComboBox<>();
    public ArrayList<String> wordList = new ArrayList<>();        //保存敏感词
    public ArrayList<Integer> wordNum = new ArrayList<>();    //保存对应敏感词的出现次数
    public static HashMap<String, Color> stringColorHashMap = new HashMap<>();

    public OptionalFrame() {
        this.setTitle("Spider");
        this.setSize(900, 600);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        jPanel.setLayout(new BorderLayout());

        //添加编码方式
        charset.addItem("UTF-8");
        charset.addItem("GBK");
        charset.setEditable(false);    //设置为不可编辑
        //处理其事件,更新编码方式
        charset.addActionListener(new ActionListener() {
            //获取选择的编码方式,默认情况下为UTF-8
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                myWeb.setCodeType((String) charset.getSelectedItem());
            }
        });

        //界面处理，提醒输入网址,爬取按钮,以及编码方式选择
        jpl1.setLayout(new BorderLayout());
        siteWarn.setPreferredSize(new Dimension(70, 30));
        siteSPane.setPreferredSize(new Dimension(300, 30));
        goSpider.setPreferredSize(new Dimension(90, 30));
        jpl5.setLayout(new GridLayout(1, 2, 10, 10));
        jpl5.add(goSpider);
        jpl5.add(charset);
        jpl1.add(siteWarn, BorderLayout.WEST);
        siteWarn.setHorizontalAlignment(JLabel.CENTER);
        siteWarn.setFont(new Font("PingFang SC", Font.PLAIN, 15));
        jpl1.add(siteSPane, BorderLayout.CENTER);
        jpl1.add(jpl5, BorderLayout.EAST);
        //源代码文本,以及处理后的文本框设置
        htmlArea.setEditable(false);
        htmlArea.setLineWrap(true);
        htmlArea.setFont(new Font("宋体", Font.PLAIN, 14));
        jpl2.setLayout(new BorderLayout());
        jpl2.add(htmlSPane, BorderLayout.CENTER);
        //设置布局
        jpl8.setLayout(new GridLayout(2, 1, 10, 5));
        jpl8.add(siteLib);
        jpl8.add(openLib);

        jpl3.setLayout(new BorderLayout());
        sensWord.setFixedCellHeight(50);
        sensWord.setCellRenderer(new MyListRenderer());
        sensWord.setFont(new Font("PingFang SC", Font.PLAIN, 15));
        wordPane.setPreferredSize(new Dimension(6, 400));
        jpl3.add(jpl8, BorderLayout.NORTH);
        jpl3.add(wordPane, BorderLayout.CENTER);
        jpl3.add(match, BorderLayout.SOUTH);

        textArea.setFont(new Font("宋体", Font.PLAIN, 14));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        jpl4.setLayout(new BorderLayout());
        jpl4.add(textSPane, BorderLayout.CENTER);

        tabPane.add("html源代码", jpl2);
        tabPane.add("网页文本", jpl4);
        tabPane.setFont(new Font("PingFang SC", Font.PLAIN, 15));
        jpl7.setLayout(new BorderLayout());
        jpl7.add(tabPane, BorderLayout.CENTER);

        jpl6.setLayout(new BorderLayout());
        jpl6.add(jpl7, BorderLayout.CENTER);
        jpl6.add(jpl3, BorderLayout.EAST);

        jPanel.add(jpl1, BorderLayout.NORTH);
        jPanel.add(jpl6, BorderLayout.CENTER);
        this.add(jPanel);
        this.setVisible(true);

        //事件处理
        goSpider.addActionListener(this);
        siteLib.addActionListener(this);
        openLib.addActionListener(this);
        match.addActionListener(this);

        sensWord.addMouseListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == goSpider) {
            single_SpiderHandler single_spiderHandler = new single_SpiderHandler(this, siteField.getText());
            single_spiderHandler.start();
        } else if (e.getSource() == openLib) {
            getLib();
        } else if (e.getSource() == match) {
            showSensword();
        } else if (e.getSource() == siteLib) {
            if(wordList.size()<=0) {		//判断是否选择了敏感词库
                JOptionPane.showMessageDialog(null, "请先选择敏感词库");
                return;
            }
            JFileChooser fChooser=new JFileChooser();	//选择网库文件
            int ok=fChooser.showOpenDialog(this);
            if(ok!=JFileChooser.APPROVE_OPTION)	return;
            File file=fChooser.getSelectedFile();
            group_SpiderHandler group_spiderHandler=new group_SpiderHandler(this,file);
            group_spiderHandler.start();
        }
    }

    //从文件中读取敏感词
    public void getLib() {
        JFileChooser fChooser = new JFileChooser();    //文件选择框
        int ok = fChooser.showOpenDialog(this);
        if (ok != JFileChooser.APPROVE_OPTION) return;    //判断是否正常选择
        wordList.clear();    //清空之前的记录
        stringColorHashMap.clear();
        sensWord.removeAll();
        File choosenLib = fChooser.getSelectedFile();    //获取选择的文件
        BufferedReader br = null;
        try {    //读取选中文件中的记录
            br = new BufferedReader(new FileReader(choosenLib));
            Vector<String> sensitive = new Vector<>();
            while (true) {
                String str = br.readLine();
                if (str == null) break;
                wordList.add(str);    //添加到记录中
                wordNum.add(0);        //设置对应的初始值
                stringColorHashMap.put(str, Color.YELLOW);
                sensitive.add(str);
            }
            sensWord.setListData(sensitive);
            br.close();    //关闭文件流
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            JOptionPane.showMessageDialog(null, "文件不存在");
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            JOptionPane.showMessageDialog(null, "文件读取失败");
            e1.printStackTrace();
        }
    }

    //高亮显示
    public void showSensword() {

        Highlighter hg = textArea.getHighlighter();    //设置文本框的高亮显示
        hg.removeAllHighlights();    //清除之前的高亮显示记录
        String text = textArea.getText();    //得到文本框的文本
        for (String str : wordList) {    //匹配其中的每一个敏感词
            int index = 0;
            DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(stringColorHashMap.get(str));    //设置高亮显示颜色为该词的选定颜色
            while ((index = text.indexOf(str, index)) >= 0) {
                try {
                    hg.addHighlight(index, index + str.length(), painter);    //高亮显示匹配到的词语
                    index += str.length();    //更新匹配条件继续匹配
                } catch (BadLocationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String[] args) {
        new OptionalFrame();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource() == sensWord) {
            if (sensWord.getSelectedIndex() == -1) {
                return;
            }
            String select = sensWord.getSelectedValue();
            JColorChooser jColorChooser = new JColorChooser();
            Color choose = jColorChooser.showDialog(this, "选择该敏感词高亮颜色", Color.yellow);
            if (choose != null) {
                stringColorHashMap.replace(sensWord.getSelectedValue(), choose);
            }
            System.out.println(select + ":" + choose);
            sensWord.clearSelection();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}

//进度条设计
class MyProgressBar extends JDialog {

    @Serial
    private static final long serialVersionUID = 1L;
    private JPanel jPanel = new JPanel();
    private JProgressBar jpb = new JProgressBar();    //进度条
    private JLabel curSpiding = new JLabel();    //显示当前网址

    //构造函数初始化,设置父窗口以及标题
    public MyProgressBar(JFrame f, String title) {
        super(f, title);
        this.setLocation(f.getWidth() / 2 + (int) f.getLocation().getX() / 2 - 80, f.getHeight() / 2 + (int) f.getLocation().getY() / 2 - 30);
        this.setSize(400, 100);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        jpb.setString("玩命加载中...");
        jpb.setIndeterminate(true);        //设置进度条为不确定模式
        jpb.setStringPainted(true);
        jpb.setBorderPainted(false);
        jpb.setForeground(Color.RED);    //设置进度条颜色
        jpb.setBackground(Color.WHITE);    //设置背景
        curSpiding.setPreferredSize(new Dimension(400, 30));

        //界面布局
        jPanel.setLayout(new BorderLayout());
        jPanel.add(curSpiding, BorderLayout.NORTH);
        jPanel.add(jpb, BorderLayout.CENTER);
        this.add(jPanel);
    }
}

class MyListRenderer extends DefaultListCellRenderer {
    private int hoverIndex = -1;

    public Component getListCellRendererComponent(JList list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {

        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (isSelected) {
            setBackground(OptionalFrame.stringColorHashMap.get(value));
        } else {
            setBackground(index == hoverIndex ? OptionalFrame.stringColorHashMap.get(value) : Color.WHITE);
        }

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                list.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                list.setCursor(Cursor.getDefaultCursor());
            }
        });
        list.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = list.locationToIndex(e.getPoint());
                setHoverIndex(list.getCellBounds(index, index).contains(e.getPoint())
                        ? index : -1);
            }

            private void setHoverIndex(int index) {
                if (hoverIndex == index)
                    return;
                hoverIndex = index;
                list.repaint();
            }
        });
        return this;
    }
}


