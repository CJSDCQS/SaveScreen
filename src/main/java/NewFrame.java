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

    private Guide guide = null;
    private HashSet<Integer> keyStack = new HashSet<>();
    private boolean isOpenNetRecord = false;

    NewFrame() {
        setBounds(1000, 600, 200, 200);
        setResizable(false);
        setUndecorated(true);
        setVisible(false);

        showGuide();

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
                trayIcon.addMouseListener(new TrayMouseListener());
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    private class TrayMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                showPopMenu(e);
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    private void showPopMenu(MouseEvent e) {
        final JPopupMenu pop = new JPopupMenu();
        final int[] popSize = {100, 50};
        final int[] globalE = {e.getX(), e.getY()};
        pop.setSize(popSize[0], popSize[1]);
        Font font = new Font(Font.SERIF, Font.PLAIN, 20);
        JMenuItem m1 = new JMenuItem("使用说明");
        m1.setSize(popSize[0], popSize[1] / 2);
        m1.setFont(font);
        JMenuItem m2 = new JMenuItem("退出应用");
        m2.setSize(popSize[0], popSize[1] / 2);
        m2.setFont(font);
        JMenuItem m3 = new JMenuItem();
        if (isOpenNetRecord) m3.setText("关闭云图片记录");
        else m3.setText("启动云图片记录");
        m3.setSize(popSize[0], popSize[1] / 2);
        m3.setFont(font);
        m1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGuide();
            }
        });
        m2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        m3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isOpenNetRecord = !isOpenNetRecord;
                if (isOpenNetRecord) m3.setText("关闭云图片记录");
                else m3.setText("开启云图片记录");
            }
        });
        pop.add(m1);
        pop.add(m3);
        pop.add(m2);
        pop.setLocation(globalE[0] - 4, globalE[1] - popSize[1] - 30);
        pop.setInvoker(pop);
        pop.setVisible(true);
        pop.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
                int eX = e.getX();
                int eY = e.getY();
                if (!(eX > 0 && eX < popSize[0] && eY > 0 && eY < popSize[1])) {
                    pop.setVisible(false);
                }
            }
        });
    }

    private void showGuide() {
        // 弹出教程说明
        guide = Guide.getInstance();
        guide.setVisible(true);
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
                ScreenFrame sf = new ScreenFrame(isOpenNetRecord);
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
