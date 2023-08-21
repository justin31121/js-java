package tests;

import static js.Io.*;
import js.Req;
import java.io.*;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;


class GetTest {

    static int WIDTH = 400;
    static int HEIGHT = 400;
    static String path = "https://static.vecteezy.com/ti/gratis-vektor/t2/10994412-nike-logo-schwarz-mit-namen-kleidung-design-symbol-abstrakte-fussball-illustration-mit-weissem-hintergrund-kostenlos-vektor.jpg";
    
    public static void main(String[] args) throws IOException {
	
	JFrame frame = new JFrame();
	frame.setTitle("Title");
	frame.setSize(WIDTH, HEIGHT);
	frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	frame.setLayout(null);

	Req.Result result = Req.builder(path, "GET")
	    .lazyBuild();
	
	JLabel label = new JLabel(new ImageIcon(ImageIO.read(result.inputStream)),
				  JLabel.CENTER);
	label.setBounds(0, 0, WIDTH, HEIGHT);
	frame.add(label);

	frame.setVisible(true);
    }
}
