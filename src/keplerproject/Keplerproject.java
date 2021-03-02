package keplerproject;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    /*Grid Layouts*/
    GridLayout gr_left, gr_right, gr_center;

    /*Flow Layout*/
    FlowLayout flow_of_grids;

    /*Format of float*/
    DecimalFormat df;

    /*Thread used*/
    Thread th; // to manage the loop 

    /*Variables used (int)*/
    int sleeptime; // to modelise the speed
    int sleeptime_factor; // to enhance performance accrodingly
    int handle_resume_pause_counter;
    int major, minor;
    int centerX, centerY;// the origin (center) of the ellipse
    int Ax, Ay; // left vertex of the ellipse
    int Bx, By; // right vertex of the ellipse
    int sunX, sunY; 
    int earthX, earthY; 

    /*variable used(Float)*/
    float e, speed, GMm, R, T, G;// eccentricity, earth's speed, G(m+M), Period, Gravitational constant.
    float sunMass, earthMass;
    float previousEarthX, previousEarthY; // used in run() to calculate speed
    float distance; // distance between the earth and sun
    float infinitesimal_earth_earth_distance; // used in run() to calculate the distance between the previous and new location of the earth
    float theta;
    float semi_major_pow2, semi_minor_pow2;

    public void init() {

        this.setSize(800, 600); // windows size
        th = new Thread(this); // define the thread
		
             /*** initial values of variables***/
    
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
        
            /***Equations that calculate initial values***/
            
        GMm = G * (earthMass + sunMass);
        semi_major_pow2 = (major / 2) * (major / 2);
        semi_minor_pow2 = (minor / 2) * (minor / 2);
        e = (float) Math.sqrt(1 - (semi_minor_pow2 / semi_major_pow2));
        R = (major / 2) * (e + 1); // Distance initial 
        T = (float) ((2 * Math.PI) * Math.sqrt((R * R * R) / GMm));
        speed = (float) Math.sqrt(GMm / R);

        df = new DecimalFormat("###.##"); // i.e. 2.333333 --> 2.33
        
            /*** adding images using their links***/
            
        bg_img = getImage(getDocumentBase(), "https://i.imgur.com/zi7bfOp.jpg");
        earth_img = getImage(getDocumentBase(), "https://i.imgur.com/mQht2Su.jpg");
        sun_img = getImage(getDocumentBase(), "https://i.imgur.com/oKXanq1.png");
           
        /***defining the panels with their layout***/
        
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
        
            /***defining all the buttons and assigning them to their Listeners ***/
            
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

                    /***defining each label with its initial value***/
                    
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

                    /***adding all the controllers to their panels***/
                    
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
        
        setLayout(flow_of_grids); // to arrange the Grids into one Flow Layout
        
        add(p_left);
        add(p_center);
        add(p_right);

        

    }

    public void paint(Graphics g) {

        g.drawImage(bg_img, 0, 0, this); // set the background image

        g.setColor(Color.WHITE);
        g.drawOval(200, 200, major, minor); // drawing the ellipse
        
                   /***initialize the major and minor axis as lines with their center***/
                   
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

                    /***adding the earth and sun at their initial positions***/
                    
        g.drawImage(earth_img, (int) earthX - 15, (int) earthY - 15, this);
        g.drawImage(sun_img, sunX - 25, sunY - 25, this);
        
            /***drawing a heat line between the earth and sun depending on their distance and the heat spectrum***/
            
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

    public void reset() { // to reset to the initial conditions
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

        if (e.getSource() == start_quit_btn) { // Toggle button between Start and Quit 

            if (start_quit_btn.getLabel().equals("Quit")) {
                System.exit(0); //to quit the app
            } else {
                th.start(); // launching the thread
                pause_resume_btn.setVisible(true);
                start_quit_btn.setLabel("Quit");
                reset_btn.setVisible(true);
            }

        }

        if (e.getSource() == pause_resume_btn) { // Toggle button between Pause and Resume 

            start_quit_btn.setLabel("Quit");
            if (handle_resume_pause_counter % 2 == 0) {
                pause_resume_btn.setLabel("resume");
                th.suspend(); // pause the thread
                handle_resume_pause_counter++;

            } else {
                pause_resume_btn.setLabel("pause");
                th.resume(); // resume the thread
                handle_resume_pause_counter++;

            }
        }
        if (e.getSource() == reset_btn) { // calling the reset button

            reset();

        }

        
                    /***Buttons actions with their cases***/
              /***4 buttons that increase and decrease the major and minor***/
         /***conditions are imposed to assure that the eccentricity is neither 0 or 1***/
        if (e.getSource() == maj_up_btn) {

            if (sunX < Ax || sunX > centerX) {

                JOptionPane.showMessageDialog(null, // Swing alert box to alert the user when e is 0 or 1
                        "eccentricity can't be 0 or 1",
                        "Alert!!",
                        JOptionPane.ERROR_MESSAGE);
                reset(); // reset after pressing OK

            } else {

                major = major + 5; 

                maj_label.setText("     " + major);

                semi_major_pow2 = (major / 2) * (major / 2);
                semi_minor_pow2 = (minor / 2) * (minor / 2);
                float new_eccentricity = (float) Math.sqrt(1 - (semi_minor_pow2 / semi_major_pow2));
                //System.out.println("new e : " + new_eccentricity);
                
         /***updating variables after increasing the major***/
         
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
                // System.out.println("new e: " + new_eccentricity);
                
         /***update variables after decreasing the major***/
         
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

            if (minor == 0) {            //condition when the ellipse turns into a line
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
        
                 /***increase or decrease sleeptime factor***/

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

                /*Le beurre*/
                sleeptime = (int) (infinitesimal_earth_earth_distance / speed) * sleeptime_factor; // for better results
                sleep(sleeptime);

                repaint();

            } catch (InterruptedException ex) {
                Logger.getLogger(Keplerproject.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}




