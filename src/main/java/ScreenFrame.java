import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.SwingDispatchService;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

class NewFrame extends JFrame implements NativeKeyListener {
    private static final long serialVersionUID = 1L;
    /*
     * 创建一个小的窗口。点击按钮来截屏。
     */
    JButton button;

    private HashSet<Integer> keyStack = new HashSet<>();

    NewFrame() {
        setLayout(new FlowLayout());
        setBounds(1000, 600, 200, 200);
        setResizable(false);
        setUndecorated(true);
        setVisible(false);

        createSystemTray();
        initKeyListener();
    }

    private void initKeyListener() {
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);

        GlobalScreen.setEventDispatcher(new SwingDispatchService());
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }

    private void createSystemTray() {
        // 判断系统是否支持托盘
        if (SystemTray.isSupported()) {
            // 创建托盘
            SystemTray tray = SystemTray.getSystemTray();
            PopupMenu popupMenu = new PopupMenu();
            Font font = new Font(Font.SERIF, Font.PLAIN, 20);
            popupMenu.setFont(font);
            MenuItem mi1 = new MenuItem("教程");
            MenuItem mi2 = new MenuItem("关闭");

            // 添加点击事件
            mi1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            });

            mi2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });

            popupMenu.add(mi1);
            popupMenu.add(mi2);
            Image image = Toolkit.getDefaultToolkit().getImage("src/main/resources/剪刀.png");
            TrayIcon trayIcon = new TrayIcon(image, "截屏工具", popupMenu);
            trayIcon.addMouseListener(new java.awt.event.MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        // 显示窗口
                        setVisible(true);
                    }
                }
            });
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        keyStack.add(nativeKeyEvent.getKeyCode());
        if (keyStack.size() == 1) return;
        else {
            // 判断快捷键
            /* 29 左ctrl
               56 alt 3640
               42 shift
               3613 右ctrl
               28 回车
               57 空格
            * */
            boolean ctrl = keyStack.contains(29);
            boolean block = keyStack.contains(57);
            if (ctrl && block) {
                ScreenFrame sf = new ScreenFrame();
                sf.setAlwaysOnTop(true);
                sf.setUndecorated(true);
                sf.setVisible(true);
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
        keyStack.remove(nativeKeyEvent.getKeyCode());
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }
}

class ScreenFrame extends JFrame {
    private static final long serialVersionUID = 2L;
    /*
     * 创建一个全屏的窗口，将全屏的图像放在JFrame的窗口上，以供来截屏。
     */
    Dimension di = Toolkit.getDefaultToolkit().getScreenSize();

    ScreenFrame() {
        //设置大小，即全屏
        setSize(di);
        //返回此窗体的 contentPane对象
        getContentPane().add(new DrawRect());
    }

    class DrawRect extends JPanel implements MouseMotionListener, MouseListener {
        private static final long serialVersionUID = 3L;
        /*
         * 将全屏的图像放在JPanel 上， 可以通过new DrawRect来获得JPanel，并且JPanel上有全屏图像
         */
        int sx, sy, ex, ey;
        int count = 1;
        File file = null;
        BufferedImage image, getImage;
        boolean flag = true;

        public DrawRect() {
            try {//获取全屏图像数据，返回给image
                Robot robot = new Robot();
                image = robot.createScreenCapture(new Rectangle(0, 0, di.width, di.height));
            } catch (Exception e) {
                throw new RuntimeException("截图失败");
            }
            //添加 鼠标活动事件
            addMouseListener(this);
            addMouseMotionListener(this);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }

        //重写paintComponent，通过repaint 显示出来截屏的范围
        public void paintComponent(Graphics g) {
            g.drawImage(image, 0, 0, di.width, di.height, this);
            g.setColor(Color.blue);
            if (sx < ex && sy < ey)//右下角
                g.drawRect(sx, sy, ex - sx, ey - sy);
            else                 //左上角
                g.drawRect(ex, ey, sx - ex, sy - ey);
        }

        //以下都是鼠标事件
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3)//右键退出程序
                dispose();
            else if (e.getClickCount() == 2)   //双击截屏
            {
                try {
                    cutScreens();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                dispose();
            }
        }

        //自定义截屏函数
        private void cutScreens() throws Exception {
            Robot ro = new Robot();
            if (sx < ex && sy < ey)//右下角
                getImage = ro.createScreenCapture(new Rectangle(sx, sy, ex - sx, ey - sy));
            else                  //左上角
                getImage = ro.createScreenCapture(new Rectangle(ex, ey, sx - ex, sy - ey));
            String name = "jietu" + count + ".bmp";
            file = new File("capture/" + name);
            while (file.exists()) {
                String na = "jietu" + (count++) + ".bmp";
                file = new File("capture/" + na);
            }
            ImageIO.write(getImage, "bmp", file);
        }

        public void mousePressed(MouseEvent e) {
            if (flag) {
                sx = e.getX();
                sy = e.getY();
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (flag) {
                flag = false;
                // 绘制按钮选项
                System.out.println("鼠标松开");
            }
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        //鼠标移动中，通过repaint 画出要截屏的范围
        public void mouseDragged(MouseEvent e) {
            ex = e.getX();
            ey = e.getY();
            repaint();
        }

        public void mouseMoved(MouseEvent e) {
        }

    }
}
