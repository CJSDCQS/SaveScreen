import org.apache.axis.encoding.Base64;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

class ScreenFrame extends JFrame {

    /*
     * 创建一个全屏的窗口，将全屏的图像放在JFrame的窗口上，以供来截屏。
     */
    Dimension di = Toolkit.getDefaultToolkit().getScreenSize();

    private boolean isOpenNetRecord;

    ScreenFrame(boolean isOpenNetRecord) {
        this.isOpenNetRecord = isOpenNetRecord;
        //设置大小，即全屏
        setSize(di);
        //返回此窗体的 contentPane对象
        getContentPane().add(new DrawRect());
    }

    class DrawRect extends JPanel implements MouseMotionListener, MouseListener {
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

            String base64;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(getImage, "bmp", stream);
            base64 = Base64.encode(stream.toByteArray());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("data:image/jpg;base64," + base64), null);
            stream.flush();
            stream.close();

            if (isOpenNetRecord) {
                // 提交图片到云端
                System.out.println("提交云端");
            }
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
