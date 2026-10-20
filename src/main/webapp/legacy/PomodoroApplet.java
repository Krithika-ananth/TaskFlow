// LEGACY ACADEMIC DEMONSTRATION ONLY.
// Modern browsers no longer support Java Applets. The main Task Flow application uses
// a JavaScript Pomodoro timer instead. This source demonstrates the historical Applet API.
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
public class PomodoroApplet extends Applet implements ActionListener {
    private int seconds=25*60; private final Label timer=new Label("25:00",Label.CENTER); private final Button start=new Button("START");
    public void init(){setLayout(new BorderLayout());timer.setFont(new Font("SansSerif",Font.BOLD,40));add(timer,BorderLayout.CENTER);Panel p=new Panel();p.add(start);Button reset=new Button("RESET");p.add(reset);add(p,BorderLayout.SOUTH);start.addActionListener(this);reset.addActionListener(e->{seconds=25*60;render();});}
    public void actionPerformed(ActionEvent e){new Thread(()->{while(seconds>0){try{Thread.sleep(1000);}catch(Exception ignored){}seconds--;render();}}).start();}
    private void render(){timer.setText(String.format("%02d:%02d",seconds/60,seconds%60));}
}
