import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.SwingDispatchService;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NewFrame extends JFrame implements NativeKeyListener {
    private static final long serialVersionUID = 1L;
    private Guide guide = null;
    public boolean isShowGuide = false;
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

            Image image = Toolkit.getDefaultToolkit().getImage("src/main/resources/剪刀.png");
            TrayIcon trayIcon = new TrayIcon(image, "截屏工具");
            trayIcon.setImageAutoSize(true);
            try {
                tray.add(trayIcon);
                trayIcon.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (e.isPopupTrigger()) {
                            final JPopupMenu pop = new JPopupMenu();
                            pop.setSize(100, 50);
                            Font font = new Font(Font.SERIF, Font.PLAIN, 20);
                            pop.setFont(font);
                            JMenuItem m1 = new JMenuItem("使用说明");
                            m1.setSize(100, 50);
                            JMenuItem m2 = new JMenuItem("退出");
                            m2.setSize(100, 50);
                            m1.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    // 弹出教程说明
                                    guide = Guide.getInstance();
                                    guide.setVisible(true);
                                }
                            });
                            m2.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    System.exit(0);
                                }
                            });
                            pop.add(m1);
                            pop.add(m2);
                            pop.setLocation(e.getX(), e.getY() - pop.getSize().height);
                            pop.setInvoker(pop);
                            pop.setVisible(true);
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });
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
