package keplerproject;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.lang.Thread.sleep;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hassan Fawaz PC
 */
public class Keplerproject extends Applet implements Runnable, ActionListener {

    int sleepTime;
    Panel panel1, panel2;
    Label ll1, ll2, ll3;
    float e;
    float vitesse;
    float GMm;
    float R;
    float T;
    float sunMass, earthMass;
    float G = (float) 1;
    Label l1, l2, l3, l4, l5, l6, l7, l8,l9;
    float previousX, previousY;
    Thread th;
    Font f;
    Choice ch;
    Button b, b1, reset_button;
    TextField t1, t2, t3;

    Label Semimaj, Semimin;
    Button majUp, majDown, minUp, minDown;

    float distance;
    Label l;
    private Image image1, image2, bgImage, image3, image4, image5;

    FlowLayout fl1;
    FlowLayout fl2;
DecimalFormat df;
    int c = 0;

    float pp;
    

    int x = 350, y = 200, coord_sunX, coord_sunY;
    float coord_earthX = (200 + x), coord_earthY = (200 + y / 2);
    ;
    float theta = 0;

    // Image earthpic;  
    public void init() {
         df = new DecimalFormat("###.###");
//           System.out.println(df.format(PI));

        reset_button = new Button("Reset");
        reset_button.addActionListener((ActionListener) this);

        reset_button.setVisible(false);

        Semimaj = new Label("     " + x);
        Semimin = new Label("     " + y);
        majUp = new Button(" + MAJ ");
        majDown = new Button(" - MAJ ");
        minUp = new Button(" + MIN ");
        minDown = new Button(" - MIN ");

        majUp.addActionListener((ActionListener) this);
        majDown.addActionListener((ActionListener) this);
        minUp.addActionListener((ActionListener) this);
        minDown.addActionListener((ActionListener) this);

        Panel panel_change = new Panel();
        panel_change.setLayout(new GridLayout(2, 3));
        panel_change.add(majDown);

        panel_change.add(Semimaj);
        panel_change.add(majUp);
        panel_change.add(minDown);
        panel_change.add(Semimin);
        panel_change.add(minUp);

        panel1 = new Panel();
        panel2 = new Panel();

        l1 = new Label("Earth's X:   " + coord_earthX);
        l2 = new Label("Earth's Y:    " + coord_earthY);
        l3 = new Label("Earth-sun");

        l5 = new Label();
        
        l6 = new Label();
        l7 = new Label("Period :");
        l8 = new Label("Vitess d'earth : ");
        l9 = new Label("");
        
        
        panel1.add(l1);
        panel1.add(l2);
        panel1.add(l3);
        panel1.add(l5);
        panel1.add(l6);
        panel1.add(l7);
        panel1.add(l9);
        panel1.add(l8);

        panel1.setLayout(new GridLayout(3, 3));

        bgImage = getImage(getDocumentBase(), "https://i.imgur.com/XUMkikp.jpg");

        image1 = getImage(getDocumentBase(), "https://i.imgur.com/mQht2Su.jpg");
        image2 = getImage(getDocumentBase(), "https://i.imgur.com/oKXanq1.png");
        image3 = getImage(getDocumentBase(), "https://i.imgur.com/aGZHgpt.jpg");
        image4 = getImage(getDocumentBase(), "https://text2image.com/user_images/202102/text2image_T2901371_20210225_210006.png");
        image5 = getImage(getDocumentBase(), "https://text2image.com/user_images/202102/text2image_Z2884020_20210225_210701.png");

        sunMass = 1000;
        earthMass = 10;

        GMm = G * (sunMass + earthMass);

        float a2 = (x / 2) * (x / 2);
        System.out.println("a2 : " + a2);
        float b2 = (y / 2) * (y / 2);
        System.out.println("b2 : " + b2);

        e = (float) Math.sqrt(1 - (b2 / a2));
        System.out.println("e :" + e);

        R = (x / 2) * (e + 1);
        System.out.println("R : " + R);
        l3.setText("Earth-sun : " + (int) R);
        System.out.println("GMm  :" + GMm);
        T = (float) ((2 * Math.PI) * Math.sqrt((R * R * R) / GMm));
        l7.setText("Period :" + (int) T);

        System.out.println("T : " + T);

        vitesse = (float) Math.sqrt(GMm / R);
        l8.setText("Vitess d'earth :" + df.format (vitesse));
        System.out.println("Virtess: " + vitesse);

        sleepTime = (int) T;

        fl1 = new FlowLayout(FlowLayout.LEFT);

        f = new Font("Serif", Font.BOLD, 36);

        setLayout(fl1);

        b = new Button("Start");
        // add(b);
        b1 = new Button("Pause");

        Panel p2 = new Panel();
        p2.setLayout(new GridLayout(1, 2));
        p2.add(b);
        p2.add(b1);
        p2.add(reset_button);

        add(p2);

        add(panel_change);
        add(panel1);

        b1.setVisible(false);

        b.addActionListener((ActionListener) this);
        b1.addActionListener((ActionListener) this);
        reset_button.addActionListener((ActionListener) this);

    }

    public void start() {
        th = new Thread(this);

        this.setSize(800, 600);

    }

    public void paint(Graphics g) {
        g.drawImage(bgImage, 0, 0, this);

//        g.drawImage(image1, (int) coord_earthX , (int) coord_earthY , this);
//        g.drawImage(image2, coord_sunX - 25, coord_sunY - 25, this);
        g.setColor(Color.WHITE);
        g.drawOval(200, 200, x, y);
        g.drawLine(200 + x / 2, 200, 200 + x / 2, 200 + y);
        g.drawLine(200, 200 + y / 2, 200 + x, 200 + y / 2);
        coord_sunX = 200 + ((y * y) / (2 * x));
        coord_sunY = (200 + y / 2);
        l5.setText("Sun's X:  " + coord_sunX);
        l6.setText("Sun's Y:  " + coord_sunY);

        g.drawImage(image1, (int) coord_earthX - 15, (int) coord_earthY - 15, this);
        g.drawImage(image2, coord_sunX - 25, coord_sunY - 25, this);

        g.drawImage(image3, 0, 500, this);
        g.drawImage(image4, 660, 550, this);
        g.drawImage(image5, 300, 70, this);

        //  System.out.print("RRRRR"+ R);
        if (distance > 23 && distance < 72.5) {
            g.setColor(Color.BLUE);
            g.drawLine((int) coord_earthX, (int) coord_earthY, (int) coord_sunX, (int) coord_sunY);
        }
        if (distance > 72.5 && distance < 122) {
            g.setColor(Color.cyan);
            g.drawLine((int) coord_earthX, (int) coord_earthY, (int) coord_sunX, (int) coord_sunY);
        }
        if (distance > 122 && distance < 171.5) {
            g.setColor(Color.white);
            g.drawLine((int) coord_earthX, (int) coord_earthY, (int) coord_sunX, (int) coord_sunY);
        }
        if (distance > 171.5 && distance < 221) {
            g.setColor(Color.yellow);
            g.drawLine((int) coord_earthX, (int) coord_earthY, (int) coord_sunX, (int) coord_sunY);
        }
        if (distance > 221 && distance < 270.5) {
            g.setColor(Color.orange);
            g.drawLine((int) coord_earthX, (int) coord_earthY, (int) coord_sunX, (int) coord_sunY);
        }
        if (distance > 270.5 && distance < 320) {
            g.setColor(Color.red);
            g.drawLine((int) coord_earthX, (int) coord_earthY, (int) coord_sunX, (int) coord_sunY);
        }

        if (distance > 318) {
            System.out.println("distance b3idee: " + distance);
            g.setColor(Color.RED);
            g.drawLine((int) coord_earthX, (int) coord_earthY, (int) coord_sunX, (int) coord_sunY);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b) {

            if (b.getLabel().equals("Quit")) {
                System.exit(0);
            } else {
                th.start();
                b1.setVisible(true);
                b.setLabel("Quit");
                reset_button.setVisible(true);
            }

        }

        if (e.getSource() == b1) {

            b.setLabel("Quit");
            if (c % 2 == 0) {
                b1.setLabel("resume");
                th.suspend();
                c++;

            } else {
                b1.setLabel("pause");
                th.resume();
                c++;

            }
        }
        if (e.getSource() == reset_button) {
            x = 350;
            y = 200;
            Semimaj.setText("     " + x);
            Semimin.setText("     " + y);

        }

        if (e.getSource() == majUp) {
            if (x == 150) {
                majDown.setVisible(true);
            }
            x = x + 5;
            Semimaj.setText("     " + x);

            float a2 = (x / 2) * (x / 2);
            float b2 = (y / 2) * (y / 2);
            float new_e = (float) Math.sqrt(1 - (b2 / a2));
            R = (x / 2) * (new_e + 1);
            T = (float) ((2 * Math.PI) * Math.sqrt((R * R * R) / GMm));
            l7.setText("Period :" + (int) T);

            l5.setText("Sun's X:  " + coord_sunX);
            l6.setText("Sun's Y:  " + coord_sunY);

        }

        if (e.getSource() == majDown) {
            if (x == 150) {
                majDown.setVisible(false);
            } else {
                x = x - 5;
                Semimaj.setText("     " + x);
                float a2 = (x / 2) * (x / 2);
                float b2 = (y / 2) * (y / 2);
                float new_e = (float) Math.sqrt(1 - (b2 / a2));
                R = (x / 2) * (new_e + 1);
                T = (float) ((2 * Math.PI) * Math.sqrt((R * R * R) / GMm));
                l7.setText("Period :" + (int) T);
                l5.setText("Sun's X:  " + coord_sunX);
                l6.setText("Sun's Y:  " + coord_sunY);

            }
        }
        if (e.getSource() == minUp) {
            if (y == 0) {
                minDown.setVisible(true);
            }
            y = y + 5;
            Semimin.setText("     " + y);
            float a2 = (x / 2) * (x / 2);
            float b2 = (y / 2) * (y / 2);
            float new_e = (float) Math.sqrt(1 - (b2 / a2));
            R = (x / 2) * (new_e + 1);
            T = (float) ((2 * Math.PI) * Math.sqrt((R * R * R) / GMm));
            l7.setText("Period :" + (int) T);
            l5.setText("Sun's X:  " + coord_sunX);
            l6.setText("Sun's Y:  " + coord_sunY);

        }
        if (e.getSource() == minDown) {
            if (y == 0) {
                minDown.setVisible(false);
            } else {
                y = y - 5;
                Semimin.setText("     " + y);
                float a2 = (x / 2) * (x / 2);
                float b2 = (y / 2) * (y / 2);
                float new_e = (float) Math.sqrt(1 - (b2 / a2));
                R = (x / 2) * (new_e + 1);
                T = (float) ((2 * Math.PI) * Math.sqrt((R * R * R) / GMm));
                l7.setText("Period :" + (int) T);
                l5.setText("Sun's X:  " + coord_sunX);
                l6.setText("Sun's Y:  " + coord_sunY);

            }
        }

    }

    @Override
    public void run() {

        while (true) {
            try {

                previousX = coord_earthX;
                previousY = coord_earthY;

                theta = (float) (theta + 0.0751);
                coord_earthX = (float) (((x / 2) * Math.cos(theta)) + (200 + x / 2));
                coord_earthY = (float) (((y / 2) * Math.sin(theta)) + (200 + y / 2));
                distance = (float) Math.sqrt((coord_earthX - (coord_sunX - 25)) * (coord_earthX - (coord_sunX - 25)) + (coord_earthY - (coord_sunY - 25)) * (coord_earthY - (coord_sunY - 25)));
                System.out.println("distance : " + distance);

                l1.setText("Earth's X: " + (int) coord_earthX + "  ");

                l2.setText("Earth's Y: " + (int) coord_earthY + "  ");
                l3.setText("Earth-sun: " + (int) distance);

                pp = (float) Math.sqrt(((coord_earthX - previousX) * (coord_earthX - previousX)) + ((coord_earthY - previousY) * (coord_earthY - previousY)));
                vitesse = (float) Math.sqrt(GMm / distance);
                l8.setText("Earth speed:" + df.format (vitesse));

                sleepTime = (int) (pp / vitesse) * 80; // from s->ms
                // System.out.println("SleepTime : " + sleepTime);
                sleep(sleepTime);

                repaint();

            } catch (InterruptedException ex) {
                Logger.getLogger(Keplerproject.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    private void setBackground(Image image3) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
