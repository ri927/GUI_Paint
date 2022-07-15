package GUI_Paint;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.MouseInputAdapter;

public class Paint extends JFrame{
    Point startPoint = new Point(-1, -1); // 直線の始点
    Point endPoint = new Point(-1, -1); // 直線の終点
    Point p = new Point(-1 , -1);
    TestPanel testPanel; // 描画用パネルを入れる
    Dimension dim = null; // ウィンドウサイズを入れる
    Image image = null; // オフラインイメージを入れる
    Graphics bufferContext = null; // オフラインイメージのグラフィックスを入れる

    Color color;
    JPanel panel;
    JLabel label;
    JTextField rField = new JTextField(3);
    JTextField gField = new JTextField(3);
    JTextField bField = new JTextField(3);
    JTextField removeField = new JTextField(2);
    JTextField weightField = new JTextField(2);
    JToolBar toolbar1 = new JToolBar();
    JButton colorItem;

    boolean clear = false; //背景をリセット
    boolean setColor = false;
    private static int width = 1280;
    private static int height = 960;
    private String filePath;
    private File file;
    private int weight = 5; //ペンの太さ

    int r ;
    int g ;
    int b ;
    int num = 8;
    int colorLimit = 20;

    public static void main(String[] args) {
        JFrame w = new Paint( "Java Paint Tool" );
        w.setResizable(false); // フレームのサイズ変更を不可に設定
        w.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        w.setSize( width , height );
        w.setVisible( true );

    }
    public Paint (String title) {
        super(title);

        color = Color.black;
        panel = new JPanel();

        rField.setText("");
        gField.setText("");
        bField.setText("");

        weightField.setText("" + weight);

        setBackground(Color.white);


        testPanel = new TestPanel(); // 描画用パネルを生成
        testPanel.addMouseListener( new MouseCheck() ); // リスナを設定
        testPanel.addMouseMotionListener( new MouseCheck() ); // リスナを設定
        getContentPane().add(testPanel);


        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu file = new JMenu("FIle");
        menuBar.add(file);

        JMenu tool  = new JMenu("Tool");
        menuBar.add(tool);

        JMenuItem item;
        item = new JMenuItem( new SaveAction() ); // メニュー項目の生成
        file.add( item ); // メニューにメニュー項目を追加
        file.addSeparator(); // メニューにセパレータを追加
        item = new JMenuItem(new ExitAction()); // メニュー項目の生成
        file.add( item ); // メニューにメニュー項目を追加

        item = new JMenuItem(new SetColorAction());
        tool.add(item);

        item = new JMenuItem(new ClearAction());
        tool.add(item);

        toolbar1.setFloatable(true);
        toolbar1.setLayout(new GridLayout( 1, num));
        getContentPane().add(toolbar1, BorderLayout.NORTH);


        label = new JLabel("現在");
        label.setIcon(new CurrentColorIcon());		// イメージを表示させる
        toolbar1.add(label);

        colorItem = new JButton(new RedAction());
        colorItem.setIcon( new RedIcon()  ); // デフォルトアイコン
        toolbar1.add(colorItem);

        colorItem = new JButton(new GreenAction());
        colorItem.setIcon( new GreenIcon()  ); // デフォルトアイコン
        toolbar1.add(colorItem);

        colorItem = new JButton(new BlueAction());
        colorItem.setIcon( new BlueIcon()  ); // デフォルトアイコン
        toolbar1.add(colorItem);

        colorItem = new JButton(new CyanAction());
        colorItem.setIcon( new CyanIcon()  ); // デフォルトアイコン
        toolbar1.add(colorItem);

        colorItem = new JButton(new MagentaAction());
        colorItem.setIcon( new MagentaIcon()  ); // デフォルトアイコン
        toolbar1.add(colorItem);

        colorItem = new JButton(new YellowAction());
        colorItem.setIcon( new YellowIcon()  ); // デフォルトアイコン
        toolbar1.add(colorItem);

        colorItem = new JButton(new OrangeAction());
        colorItem.setIcon( new OrangeIcon()  ); // デフォルトアイコン
        toolbar1.add(colorItem);

        colorItem = new JButton(new BlackAction());
        colorItem.setIcon( new BlackIcon()  ); // デフォルトアイコン
        toolbar1.add(colorItem);

        JToolBar toolbar2 = new JToolBar();
        toolbar2.setFloatable(true);
        toolbar2.setLayout(new GridLayout( 1, 5));
        getContentPane().add(toolbar2 ,BorderLayout.SOUTH);

        JButton button ;
        button = new JButton(new SubAction());
        toolbar2.add(button);

        toolbar2.add(weightField);

        button = new JButton(new AddAction());
        toolbar2.add(button);

        Icon icon = new ImageIcon( "eraser.jpeg" );
        button = new JButton(new EraserAction() ); // テキストが右
        button.setIcon( icon );
        button.setHorizontalTextPosition( SwingConstants.RIGHT );
        toolbar2.add( button );
    }

    class TestPanel extends JPanel { // 描画用パネルの定義
        public void paintComponent(Graphics g) { // ここに描画したい内容を書く

            if (image == null) { // 一度だけ初期化
                dim = getSize(); // パネルのサイズを得る
                image = createImage(dim.width, dim.height); // オフラインイメージを生成
                bufferContext = image.getGraphics(); // オフラインイメージのグラフィックスを得る
            }

            boolean Numeric =  weightField.getText().matches("[+-]?\\d*(\\.\\d+)?");

            if(Numeric) {
                int w =  Integer.parseInt(weightField.getText());

                if(1 <= w  && w <= 50) {
                    weight = w;
                }
                else {
                    Object[] msg2 = { "入力形式が違います" , "ペンの太さは 1 ~ 50 の整数値のみを入力してください" };
                    JOptionPane.showMessageDialog( panel, msg2, "Warning",
                            JOptionPane.WARNING_MESSAGE );
                    weightField.setText(""  + weight);
                }
            }
            else {
                Object[] msg2 = { "入力形式が違います" , "ペンの太さは 1 ~ 50 の整数値のみを入力してください" };
                JOptionPane.showMessageDialog( panel, msg2, "Warning",
                        JOptionPane.WARNING_MESSAGE );
                weightField.setText(""  + weight);
            }

            Graphics2D g2 = (Graphics2D)bufferContext;
            BasicStroke bs = new BasicStroke(weight, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            g2.setStroke(bs);

            bufferContext.setColor(color);
            bufferContext.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y); // オフラインイメージに直線を描く
            g.drawImage(image, 0, 0, this);

            label.setIcon(new CurrentColorIcon());//現在の色の更新

            startPoint = endPoint; // 終点を次の始点に

            if(clear) { //画面をクリア
                bufferContext.setColor(Color.white);
                bufferContext.fillRect(0,0,width,height);
                g.drawImage(image,0,0,this);
                clear = false;
            }
        }
        public void draw() {
            bufferContext.fillRect(0, 0, width, height);
        }
    }

    class MouseCheck extends MouseInputAdapter {
        public void mousePressed( MouseEvent e ){
            startPoint = e.getPoint(); // 始点の設定
        }
        public void mouseDragged( MouseEvent e ){
            endPoint = e.getPoint(); // 終点の設定
            testPanel.repaint(); // パネルを再描画
        }
    }

    class CurrentColorIcon implements Icon{
        static final int width  = 30;
        static final int height = 30;

        public void paintIcon( Component c, Graphics g, int x, int y ) {
            g.setColor(color);
            g.fillRect(x, y, width,height);
        }
        public int getIconWidth() {
            return this.width;
        }
        public int getIconHeight() {
            return this.height;
        }
    }


    class RedIcon   implements Icon{
        static final int width  = 30;
        static final int height = 30;

        public void paintIcon( Component c, Graphics g, int x, int y ) {
            g.setColor( Color.red );
            g.fillRect( x, y, width, height );
        }
        public int getIconWidth() {
            return this.width;
        }
        public int getIconHeight() {
            return this.height;
        }
    }

    class GreenIcon   implements Icon{
        static final int width  = 30;
        static final int height = 30;

        public void paintIcon( Component c, Graphics g, int x, int y ) {
            g.setColor( Color.green );
            g.fillRect( x, y, width, height );
        }
        public int getIconWidth() {
            return this.width;
        }
        public int getIconHeight() {
            return this.height;
        }
    }

    class BlueIcon   implements Icon{
        static final int width  = 30;
        static final int height = 30;

        public void paintIcon( Component c, Graphics g, int x, int y ) {
            g.setColor( Color.blue );
            g.fillRect( x, y, width, height );
        }
        public int getIconWidth() {
            return this.width;
        }
        public int getIconHeight() {
            return this.height;
        }
    }

    class CyanIcon   implements Icon{
        static final int width  = 30;
        static final int height = 30;

        public void paintIcon( Component c, Graphics g, int x, int y ) {
            g.setColor( Color.cyan );
            g.fillRect( x, y, width, height );
        }
        public int getIconWidth() {
            return this.width;
        }
        public int getIconHeight() {
            return this.height;
        }
    }

    class MagentaIcon   implements Icon{
        static final int width  = 30;
        static final int height = 30;

        public void paintIcon( Component c, Graphics g, int x, int y ) {
            g.setColor( Color.magenta );
            g.fillRect( x, y, width, height );
        }
        public int getIconWidth() {
            return this.width;
        }
        public int getIconHeight() {
            return this.height;
        }
    }

    class YellowIcon   implements Icon{
        static final int width  = 30;
        static final int height = 30;

        public void paintIcon( Component c, Graphics g, int x, int y ) {
            g.setColor( Color.yellow );
            g.fillRect( x, y, width, height );
        }
        public int getIconWidth() {
            return this.width;
        }
        public int getIconHeight() {
            return this.height;
        }
    }

    class OrangeIcon   implements Icon{
        static final int width  = 30;
        static final int height = 30;

        public void paintIcon( Component c, Graphics g, int x, int y ) {
            g.setColor( Color.orange );
            g.fillRect( x, y, width, height );
        }
        public int getIconWidth() {
            return this.width;
        }
        public int getIconHeight() {
            return this.height;
        }
    }

    class BlackIcon   implements Icon{
        static final int width  = 15;
        static final int height = 15;

        public void paintIcon( Component c, Graphics g, int x, int y ) {
            g.setColor( Color.black );
            g.fillRect( x, y, width, height );
        }
        public int getIconWidth() {
            return this.width;
        }
        public int getIconHeight() {
            return this.height;
        }
    }

    class OriginalColorIcon   implements Icon{
        static final int width  = 30;
        static final int height = 30;

        int r;
        int g;
        int b;

        public OriginalColorIcon(int r,int g, int b){
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public void paintIcon( Component c, Graphics gra, int x, int y ) {
            gra.setColor( new Color(r , g ,  b) );
            gra.fillRect( x, y, width, height );
        }
        public int getIconWidth() {
            return this.width;
        }
        public int getIconHeight() {
            return this.height;
        }
    }


    class BlackAction extends AbstractAction {
        public void actionPerformed( ActionEvent e) {
            color = Color.black;
        }
    }

    class RedAction extends AbstractAction {
        public void actionPerformed( ActionEvent e) {
            color = Color.red;
        }
    }

    class GreenAction extends AbstractAction {
        public void actionPerformed( ActionEvent e) {
            color = Color.green;
        }
    }

    class BlueAction extends AbstractAction {
        public void actionPerformed( ActionEvent e) {
            color = Color.blue;
        }
    }

    class CyanAction extends AbstractAction {
        public void actionPerformed( ActionEvent e) {
            color = Color.cyan;
        }
    }

    class MagentaAction extends AbstractAction {
        public void actionPerformed( ActionEvent e) {
            color = Color.magenta;
        }
    }

    class YellowAction extends AbstractAction {
        public void actionPerformed( ActionEvent e) {
            color = Color.yellow;
        }
    }

    class OrangeAction extends AbstractAction {
        public void actionPerformed( ActionEvent e) {
            color = Color.orange;
        }
    }

    class OriginalColorAction extends AbstractAction {
        int r;
        int b;
        int g;

        public OriginalColorAction(int r, int g , int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }
        public void actionPerformed( ActionEvent e) {
            color = new Color(r,g,b);
        }
    }


    class SetColorAction extends AbstractAction {
        SetColorAction() {
            putValue( Action.NAME, "色の追加" );
            putValue( Action.SHORT_DESCRIPTION, "色の追加" );
        }
        public void actionPerformed( ActionEvent e) {
            Object[] msg = { "Rの値を入力" , rField, "Gの値を入力",gField ,  "Bの値を入力", bField };
            Object[] option = { "色を追加する", "閉じる" }; // ボタン表示用オブジェクトの配列
            int ans = JOptionPane.showOptionDialog( panel, msg, "色の追加",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                    option, option[0] );

            boolean rNumeric =  rField.getText().matches("[+-]?\\d*(\\.\\d+)?");
            boolean gNumeric =  gField.getText().matches("[+-]?\\d*(\\.\\d+)?");
            boolean bNumeric =  bField.getText().matches("[+-]?\\d*(\\.\\d+)?");

            int rf = Integer.parseInt(rField.getText());
            int gf = Integer.parseInt(gField.getText());
            int bf = Integer.parseInt(bField.getText());

            if(ans == 0) {
                if(rNumeric && gNumeric && bNumeric && !rField.getText().equals("") && !gField.getText().equals("") && !bField.getText().equals("")
                        && 0 <= rf && rf <= 255 && 0 <= gf && gf <= 255 && 0 <= bf && bf <= 255 ) {
                    r = Integer.parseInt(rField.getText());
                    g = Integer.parseInt(gField.getText());
                    b = Integer.parseInt(bField.getText());
                    setColor = true;
                }
                else {
                    Object[] msg2 = { "入力形式が違います" , "0 ~ 255の数値のみを入力してください" };
                    JOptionPane.showMessageDialog( panel, msg2, "Warning",
                            JOptionPane.WARNING_MESSAGE );
                }

                if(num < colorLimit) {
                    if(setColor) {
                        num += 1;
                        colorItem = new JButton(new OriginalColorAction(r,g,b));
                        colorItem.setIcon( new OriginalColorIcon(r,g,b)  ); // デフォルトアイコン
                        toolbar1.add(colorItem);
                        setColor = false;
                    }
                }
                else {
                    Object[] msg2 = { "これ以上色を追加できません" };
                    JOptionPane.showMessageDialog( panel, msg2, "Warning",
                            JOptionPane.WARNING_MESSAGE );
                }
            }
        }
    }

    class ClearAction extends AbstractAction{
        ClearAction() {
            putValue( Action.NAME, "画面クリア" );
            putValue( Action.SHORT_DESCRIPTION, "画面クリア" );
        }
        public void actionPerformed(ActionEvent e) {
            clear = true;
        }
    }


    class SubAction extends AbstractAction{
        SubAction() {
            putValue( Action.NAME, "ー" );
            putValue( Action.SHORT_DESCRIPTION, "ー" );
        }
        public void actionPerformed(ActionEvent e) {
            int subWeight = Integer.valueOf(weightField.getText());
            if(1 < subWeight) {
                subWeight--;
            }
            String subStr = "" + subWeight;
            weightField.setText(subStr);
        }
    }


    class AddAction extends AbstractAction{
        AddAction() {
            putValue( Action.NAME, "＋" );
            putValue( Action.SHORT_DESCRIPTION, "＋" );
        }
        public void actionPerformed(ActionEvent e) {
            int addWeight = Integer.valueOf(weightField.getText());
            if(addWeight < 50) {
                addWeight++;
            }
            String addStr = "" + addWeight;
            weightField.setText(addStr);
        }
    }

    class EraserAction extends AbstractAction{
        EraserAction() {
            putValue( Action.NAME, "消しゴム" );
            putValue( Action.SHORT_DESCRIPTION, "消しゴム" );
        }
        public void actionPerformed(ActionEvent e) {
            color = Color.white;
        }
    }


    class SaveAction extends AbstractAction {
        SaveAction() {
            putValue( Action.NAME, "保存" );
            putValue( Action.SHORT_DESCRIPTION, "保存" );
        }
        public void actionPerformed( ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser( "." ); // カレントディレクトリを指定してファイルチューザを生成
            fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY ); // モードを設定
            fileChooser.setDialogTitle( "ファイル選択" ); // タイトルを指定

            int ret = fileChooser.showSaveDialog( label ); // ダイアログを開く

            if( ret != JFileChooser.APPROVE_OPTION ) return; // 選ばれていなければ

            filePath = fileChooser.getSelectedFile().getAbsolutePath(); // 選ばれていればそのファイルのパスを得る

            if (!filePath.toLowerCase().endsWith(".png")) { // 拡張子の確認
                file = new File(filePath + ".png"); // 付いてなければ付ける
            }

            try {
                ImageIO.write((BufferedImage) image, "png", file); // imageを指定フォーマットで指定パスに保存
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    class ExitAction extends AbstractAction {
        ExitAction() {
            putValue( Action.NAME, "終了" );
            putValue( Action.SHORT_DESCRIPTION, "終了" );
        }
        public void actionPerformed( ActionEvent e) {
            Object[] msg = { "プログラムを終了します" };
            int ans = JOptionPane.showConfirmDialog( panel, msg, "プログラムの終了",
                    JOptionPane.YES_NO_CANCEL_OPTION );
            if ( ans == 0 ) {
                System.exit(0);
            }
        }
    }
}
