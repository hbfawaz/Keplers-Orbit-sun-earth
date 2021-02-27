package keplerproject;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
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
import static java.lang.Double.NaN;

import static java.lang.Thread.sleep;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Hassan Fawaz
 */
public class Keplerproject extends Applet implements Runnable, ActionListener {

    /*Images used*/
    Image title_img, bg_img, earth_img, sun_img, temperature_img, credits_img;

    /*Buttons*/
    Button start_quit_btn, pause_resume_btn, reset_btn;

    Button maj_up_btn, maj_down_btn, min_up_btn, min_down_btn;
    Button sleepUp_btn, sleepDown_btn;

    /*Labels */
    Label sleep_label;
    Label maj_label, min_label;
    Label earth_x_label, earth_y_label, sun_x_label, sun_y_label;
    Label earth_sun_label, period_label, earth_speed_label;
    Label empty_label1, empty_label2;
    /*Panels*/
    Panel p_left, p_center, p_right;

    /*Font used*/
    Font F;

    /*Grid Layouts*/
    GridLayout gr_left, gr_right, gr_center;

    /*Flow Layout*/
    FlowLayout flow_of_grids;

    /*Format of float*/
    DecimalFormat df;

    /*Thread used*/
    Thread th; // to manage the loop 

    /*Variables used (int)*/
    int sleeptime; // in the sleep methode
    int sleeptime_factor;
    int handle_resume_pause_counter;
    int major, minor; //(x,y)
    int centerX, centerY;
    int Ax, Ay;
    int Bx, By;
    int sunX, sunY;
    int earthX, earthY;

    /*variable to use (Float)*/
    float e, speed, GMm, R, T, G;
    float sunMass, earthMass;
    float previousEarthX, previousEarthY;
    float distance;
    float infinitesimal_earth_earth_distance;
    float theta;
    float semi_major_pow2, semi_minor_pow2;

    public void init() {

        this.setSize(800, 600);

        handle_resume_pause_counter = 0;
        theta = 0;
        sleeptime_factor = 50;
        major = 350;
        minor = 200;
        earthX = 200 + major;
        earthY = (200 + minor / 2);
        sunX = 200 + ((minor * minor) / (2 * major));
        sunY = (200 + minor / 2);
        sunMass = 1000;
        earthMass = 10;
        G = 1;
        GMm = G * (earthMass + sunMass);
        semi_major_pow2 = (major / 2) * (major / 2);
        semi_minor_pow2 = (minor / 2) * (minor / 2);
        e = (float) Math.sqrt(1 - (semi_minor_pow2 / semi_major_pow2));
        R = (major / 2) * (e + 1); // Distance initial 
        T = (float) ((2 * Math.PI) * Math.sqrt((R * R * R) / GMm));
        speed = (float) Math.sqrt(GMm / R);

        df = new DecimalFormat("###.##");

        bg_img = getImage(getDocumentBase(), "https://i.imgur.com/zi7bfOp.jpg");
        earth_img = getImage(getDocumentBase(), "https://i.imgur.com/mQht2Su.jpg");
        sun_img = getImage(getDocumentBase(), "https://i.imgur.com/oKXanq1.png");

        p_left = new Panel();
        p_center = new Panel();
        p_right = new Panel();

        flow_of_grids = new FlowLayout();
        gr_left = new GridLayout(3, 1);
        gr_center = new GridLayout(3, 3);
        gr_right = new GridLayout(3, 3);

        p_left.setLayout(gr_left);
        p_center.setLayout(gr_center);
        p_right.setLayout(gr_right);

        start_quit_btn = new Button("Start");
        start_quit_btn.addActionListener((ActionListener) this);
        pause_resume_btn = new Button("Pause");
        pause_resume_btn.addActionListener((ActionListener) this);
        pause_resume_btn.setVisible(false);
        reset_btn = new Button("Reset");
        reset_btn.addActionListener((ActionListener) this);
        reset_btn.setVisible(false);
        maj_up_btn = new Button(" +major");
        maj_up_btn.addActionListener((ActionListener) this);
        maj_down_btn = new Button(" -major");
        maj_down_btn.addActionListener((ActionListener) this);
        min_up_btn = new Button(" +minor");
        min_up_btn.addActionListener((ActionListener) this);
        min_down_btn = new Button(" -minor");
        min_down_btn.addActionListener((ActionListener) this);
        sleepUp_btn = new Button(" +sleep");
        sleepUp_btn.addActionListener((ActionListener) this);
        sleepDown_btn = new Button(" -sleep");
        sleepDown_btn.addActionListener((ActionListener) this);

        sleep_label = new Label("     " + sleeptime_factor);

        empty_label1 = new Label("       ");
        empty_label2 = new Label("       ");

        maj_label = new Label("     " + major);
        min_label = new Label("     " + minor);

        earth_x_label = new Label("Earth's X:   " + earthX);
        earth_y_label = new Label("Earth's Y:    " + earthY);
        sun_x_label = new Label("Sun's X:   " + sunX);
        sun_y_label = new Label("Sun's Y:   " + sunY);

        earth_sun_label = new Label("Earth-sun: " + (int) R);
        period_label = new Label("Period: " + T);
        earth_speed_label = new Label("speed:" + df.format(speed));

        p_left.add(start_quit_btn);
        p_left.add(pause_resume_btn);
        p_left.add(reset_btn);

        p_center.add(maj_down_btn);
        p_center.add(maj_label);
        p_center.add(maj_up_btn);
        p_center.add(min_down_btn);
        p_center.add(min_label);
        p_center.add(min_up_btn);
        p_center.add(sleepDown_btn);
        p_center.add(sleep_label);
        p_center.add(sleepUp_btn);

        p_right.add(earth_x_label);
        p_right.add(sun_x_label);
        p_right.add(earth_sun_label);
        p_right.add(earth_y_label);
        p_right.add(sun_y_label);
        p_right.add(period_label);
        p_right.add(empty_label1);
        p_right.add(empty_label2);
        p_right.add(earth_speed_label);

        setLayout(flow_of_grids);

        add(p_left);
        add(p_center);
        add(p_right);

        th = new Thread(this);

    }

    public void paint(Graphics g) {

        g.drawImage(bg_img, 0, 0, this);

        g.setColor(Color.WHITE);
        g.drawOval(200, 200, major, minor);
        Ax = 200;
        Ay = 200 + (minor / 2);
        Bx = 200 + major;
        By = 200 + (minor / 2);
        g.drawLine(200 + major / 2, 200, 200 + major / 2, 200 + minor);
        g.drawLine(Ax, Ay, Bx, By);

        centerX = (Ax + Bx) / 2;
        centerY = (Ay + By) / 2;
        // System.out.println("center (" + centerX + "," + centerY + ")");

        sunX = 200 + ((minor * minor) / (2 * major));
        sunY = (200 + minor / 2);

        g.drawImage(earth_img, (int) earthX - 15, (int) earthY - 15, this);
        g.drawImage(sun_img, sunX - 25, sunY - 25, this);

        if (distance > 23 && distance < 72.5) {
            g.setColor(Color.BLUE);
            g.drawLine((int) earthX, (int) earthY, (int) sunX, (int) sunY);
        }
        if (distance > 72.5 && distance < 122) {
            g.setColor(Color.cyan);
            g.drawLine((int) earthX, (int) earthY, (int) sunX, (int) sunY);
        }
        if (distance > 122 && distance < 171.5) {
            g.setColor(Color.white);
            g.drawLine((int) earthX, (int) earthY, (int) sunX, (int) sunY);
        }
        if (distance > 171.5 && distance < 221) {
            g.setColor(Color.yellow);
            g.drawLine((int) earthX, (int) earthY, (int) sunX, (int) sunY);
        }
        if (distance > 221 && distance < 270.5) {
            g.setColor(Color.orange);
            g.drawLine((int) earthX, (int) earthY, (int) sunX, (int) sunY);
        }
        if (distance > 270.5 && distance < 320) {
            g.setColor(Color.red);
            g.drawLine((int) earthX, (int) earthY, (int) sunX, (int) sunY);
        }

        if (distance > 318) {
            //System.out.println("distance b3idee: " + distance);
            g.setColor(Color.RED);
            g.drawLine((int) earthX, (int) earthY, (int) sunX, (int) sunY);
        }

    }

    public void reset() {
        major = 350;
        minor = 200;
        maj_label.setText("     " + major);
        min_label.setText("     " + minor);

        semi_major_pow2 = (major / 2) * (major / 2);
        semi_minor_pow2 = (minor / 2) * (minor / 2);
        float new_eccentricity = (float) Math.sqrt(1 - (semi_minor_pow2 / semi_major_pow2));
        R = (major / 2) * (new_eccentricity + 1);
        T = (float) ((2 * Math.PI) * Math.sqrt((R * R * R) / GMm));
        period_label.setText("Period :" + df.format(T));
        sunX = 200 + ((minor * minor) / (2 * major));
        sunY = (200 + minor / 2);
        sun_x_label.setText("Sun's X:  " + sunX);
        sun_y_label.setText("Sun's Y:  " + sunY);

        sleeptime_factor = 50;

        sleep_label.setText("     " + sleeptime_factor);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == start_quit_btn) {

            if (start_quit_btn.getLabel().equals("Quit")) {
                System.exit(0); //to quit the app
            } else {
                th.start();
                pause_resume_btn.setVisible(true);
                start_quit_btn.setLabel("Quit");
                reset_btn.setVisible(true);
            }

        }

        if (e.getSource() == pause_resume_btn) {

            start_quit_btn.setLabel("Quit");
            if (handle_resume_pause_counter % 2 == 0) {
                pause_resume_btn.setLabel("resume");
                th.suspend();
                handle_resume_pause_counter++;

            } else {
                pause_resume_btn.setLabel("pause");
                th.resume();
                handle_resume_pause_counter++;

            }
        }
        if (e.getSource() == reset_btn) {

            reset();

        }

        /**
         * *************Buttons actions with their cases****************/
        if (e.getSource() == maj_up_btn) {

            if (sunX < Ax || sunX > centerX) {

                JOptionPane.showMessageDialog(null,
                        "eccentricity can't be 0 or 1",
                        "Alert!!",
                        JOptionPane.ERROR_MESSAGE);
                reset();

            } else {

                major = major + 5;

                maj_label.setText("     " + major);

                semi_major_pow2 = (major / 2) * (major / 2);
                semi_minor_pow2 = (minor / 2) * (minor / 2);
                float new_eccentricity = (float) Math.sqrt(1 - (semi_minor_pow2 / semi_major_pow2));
                //System.out.println("new e : " + new_eccentricity);

                R = (major / 2) * (new_eccentricity + 1);
                T = (float) ((2 * Math.PI) * Math.sqrt((R * R * R) / GMm));
                period_label.setText("Period :" + df.format(T));

                sun_x_label.setText("Sun's X:  " + sunX);
                sun_y_label.setText("Sun's Y:  " + sunY);
            }

        }

        if (e.getSource() == maj_down_btn) {

            if (sunX < Ax || sunX > centerX) {
                JOptionPane.showMessageDialog(null,
                        "eccentricity can't be 0 or 1",
                        "Alert!!",
                        JOptionPane.ERROR_MESSAGE);
                reset();

            } else {
                major = major - 5;
                maj_label.setText("     " + major);
                semi_major_pow2 = (major / 2) * (major / 2);
                semi_minor_pow2 = (minor / 2) * (minor / 2);
                float new_eccentricity = (float) Math.sqrt(1 - (semi_minor_pow2 / semi_major_pow2));
                // System.out.println("new e ee: " + new_eccentricity);

                R = (major / 2) * (new_eccentricity + 1);
                T = (float) ((2 * Math.PI) * Math.sqrt((R * R * R) / GMm));
                period_label.setText("Period :" + df.format(T));
                sun_x_label.setText("Sun's X:  " + sunX);
                sun_y_label.setText("Sun's Y:  " + sunY);
            }

        }
        if (e.getSource() == min_up_btn) {

            if (sunX < Ax || sunX > centerX) {

                JOptionPane.showMessageDialog(null,
                        "eccentricity can't be 0 or 1",
                        "Alert!!",
                        JOptionPane.ERROR_MESSAGE);
                reset();

            } else {

                minor = minor + 5;
                min_label.setText("     " + minor);
                semi_major_pow2 = (major / 2) * (major / 2);
                semi_minor_pow2 = (minor / 2) * (minor / 2);
                float new_eccentricity = (float) Math.sqrt(1 - (semi_minor_pow2 / semi_major_pow2));
                // System.out.println("new e : " + new_eccentricity);
                R = (major / 2) * (new_eccentricity + 1);
                T = (float) ((2 * Math.PI) * Math.sqrt((R * R * R) / GMm));
                period_label.setText("Period :" + df.format(T));
                sun_x_label.setText("Sun's X:  " + sunX);
                sun_y_label.setText("Sun's Y:  " + sunY);
            }

        }

        if (e.getSource() == min_down_btn) {

            if (minor == 0) {
                JOptionPane.showMessageDialog(null,
                        "Out of bounds",
                        "Alert!!",
                        JOptionPane.ERROR_MESSAGE);
                reset();
            } else if (sunX < Ax || sunX > centerX) {

                JOptionPane.showMessageDialog(null,
                        "eccentricity can't be 0 or 1",
                        "Alert!!",
                        JOptionPane.ERROR_MESSAGE);
                reset();

            } else {

                minor = minor - 5;
                min_label.setText("     " + minor);
                semi_major_pow2 = (major / 2) * (major / 2);
                semi_minor_pow2 = (minor / 2) * (minor / 2);
                float new_eccentricity = (float) Math.sqrt(1 - (semi_minor_pow2 / semi_major_pow2));
//                System.out.println("new e : " + new_eccentricity);

                R = (major / 2) * (new_eccentricity + 1);
                T = (float) ((2 * Math.PI) * Math.sqrt((R * R * R) / GMm));
                period_label.setText("Period :" + df.format(T));
                sun_x_label.setText("Sun's X:  " + sunX);
                sun_y_label.setText("Sun's Y:  " + sunY);
            }

        }
        /**
         * ********************************************************************/

        if (e.getSource() == sleepDown_btn) {
            if (sleeptime_factor == 5) {
                JOptionPane.showMessageDialog(null,
                        "You reached the minimal factor",
                        "Alert!!",
                        JOptionPane.ERROR_MESSAGE);
                reset();
            }
            sleeptime_factor -= 5;
            sleep_label.setText("     " + sleeptime_factor);

        }

        if (e.getSource() == sleepUp_btn) {
            sleeptime_factor += 5;

            sleep_label.setText("     " + sleeptime_factor);

        }

    }

    @Override
    public void run() {
        while (true) {
            try {

                previousEarthX = earthX;
                previousEarthY = earthY;

                theta = (float) (theta + 0.0751);

                earthX = (int) (float) (((major / 2) * Math.cos(theta)) + (200 + major / 2));
                earthY = (int) (float) (((minor / 2) * Math.sin(theta)) + (200 + minor / 2));
                distance = (float) Math.sqrt((earthX - (sunX - 25)) * (earthX - (sunX - 25)) + (earthY - (sunY - 25)) * (earthY - (sunY - 25)));

                earth_x_label.setText("Earth's X: " + (int) earthX + "  ");
                earth_y_label.setText("Earth's Y: " + (int) earthY + "  ");
                earth_sun_label.setText("Earth-sun: " + (int) distance);

                infinitesimal_earth_earth_distance = (float) Math.sqrt(((earthX - previousEarthX) * (earthX - previousEarthX)) + ((earthY - previousEarthY) * (earthY - previousEarthY)));
                speed = (float) Math.sqrt(GMm / distance);
                earth_speed_label.setText("Earth speed:" + df.format(speed));

                /*Le zebde*/
                sleeptime = (int) (infinitesimal_earth_earth_distance / speed) * sleeptime_factor; // for better results
                sleep(sleeptime);

                repaint();

            } catch (InterruptedException ex) {
                Logger.getLogger(Keplerproject.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
