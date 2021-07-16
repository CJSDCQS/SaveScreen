import com.sun.awt.AWTUtilities;
import utils.PropertyUtil;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public class Guide extends JFrame {
    private static Guide instance = new Guide();
    private int width = 400, height = 300;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;

    private Guide() {
        initProperties();
        initFrame();
    }

    public static Guide getInstance() {
        return instance;
    }

    private void initFrame() {
        setSize(this.width, this.height);
        int x = Toolkit.getDefaultToolkit().getScreenSize().width - Toolkit.getDefaultToolkit().getScreenInsets(
                this.getGraphicsConfiguration()).right - width - 5;
        int y = Toolkit.getDefaultToolkit().getScreenSize().height - Toolkit.getDefaultToolkit().getScreenInsets(
                this.getGraphicsConfiguration()).bottom - height - 5;
        setBounds(x, y, width, height);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG); // 窗口样式

        jLabel1 = new JLabel("1.ctrl + 空格开始截图");
        jLabel2 = new JLabel("2.在系统托盘中右键可退出程序");
        jLabel3 = new JLabel("3.如发现bug请反馈给torchcqs@qq.com");
        jLabel1.setFont(new Font("宋体", Font.PLAIN, 20));
        jLabel2.setFont(new Font("宋体", Font.PLAIN, 20));
        jLabel3.setFont(new Font("宋体", Font.PLAIN, 20));
        jLabel1.setBounds(0, 20, width, 40);
        jLabel2.setBounds(0, 70, width, 40);
        jLabel3.setBounds(0, 120, width, 40);
        add(jLabel1);
        add(jLabel2);
        add(jLabel3);

        setTitle("使用说明");
        setResizable(false);
        setVisible(true);
    }

    private void initProperties() {
        String width = PropertyUtil.getProperty("width");
        String height = PropertyUtil.getProperty("height");
        this.width = Integer.parseInt(width);
        this.height = Integer.parseInt(height);
    }

}
