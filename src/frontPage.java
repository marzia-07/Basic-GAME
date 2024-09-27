import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.geom.AffineTransform;
import java.util.Random;

public class frontPage extends JPanel implements ActionListener {
    private static final int PANEL_WIDTH =600;
    private static final int PANEL_HEIGHT = 600;
    private static final int BIRD_WIDTH = 25;
    private static final int BIRD_HEIGHT = 25;
    private static final int NUM_BIRD=7;
    private int backGroundImageX=0;
    private int backGroundImage2X=PANEL_WIDTH;

    private Image backGroundImage;
    private Image backGroundImage2;
    private Image[] bird;
    private Image car1;
    private Image carDown;
    private Image carUp;
    private int car1X=30;
    private int car1Y=450;
    private int[] birdX = {420,650,280,200,120,100,10};
    private int[] birdY = {20,5,20,40,30,10,30};
    private boolean[] movingRight={true,true,true,true,true,true,true};
   // private boolean carMoving = false;
    private Timer timer;
    public JButton startButton;
    private int score=0;
    //obstacles
    private int trapX=PANEL_WIDTH;
    private int trapY=500;
    private boolean carJumping=false;
    private int jumpHeight=0;
    private Image foodImage;
    private int foodX=200;
    private int foodY=350;
    private boolean foodEaten = false;
    private int foodRespawnCounter=0;
    private final int FOOD_RESPAWN_DELAY = 60;
    public frontPage(){
        this.setPreferredSize(new Dimension(PANEL_WIDTH,PANEL_HEIGHT));
        this.setLayout(null);
        loadImages();
        this.setBackground(Color.BLACK);
        startButton = new JButton("Start");
        startButton.setBounds(PANEL_WIDTH/2,PANEL_HEIGHT/2,100,50);
        startButton.addActionListener(new ButtonClickListener());
        this.add(startButton);
        timer=new Timer(50,this);
        timer.start();
        this.setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_UP && jumpHeight==0)
                    carJumping=true;

            }
        });
    }
    private void loadImages(){
        try {
            backGroundImage = new ImageIcon(getClass().getResource("/FrontPage.png")).getImage();
            backGroundImage2=new ImageIcon(getClass().getResource("/FrontPage.png")).getImage();
            car1=new ImageIcon(getClass().getResource("/car1.png")).getImage();
            carUp=new ImageIcon(getClass().getResource("/carUp.png")).getImage();
            foodImage =new ImageIcon(getClass().getResource("/1Food.png")).getImage();
            bird = new Image[NUM_BIRD];
            for (int i = 0; i < NUM_BIRD; i++) {

                bird[i] = new ImageIcon(getClass().getResource("/BirdImage1.png")).getImage();
            }


        }catch(Exception e){
            System.out.println("Error loading image:"+e.getMessage());
        }

    }
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2D=(Graphics2D) g;
        g2D.drawImage(backGroundImage,backGroundImageX,0,this.getWidth(),this.getHeight(),this);
        g2D.drawImage(backGroundImage2,backGroundImage2X,0,this.getWidth(),this.getHeight(),this);

        g2D.setFont(new Font("Arial",Font.BOLD,20));
        g2D.drawString("Score:"+score,10,20);

        if(car1!=null && carJumping==false){
            g2D.drawImage(car1,car1X,car1Y-jumpHeight,200,50,this);
        }
        else if(carJumping==true){
            g2D.drawImage(carUp,car1X,car1Y-jumpHeight,200,50,this);
        }

        int[] xPoints={trapX,trapX+50,trapX+25};
        int[] yPoints={trapY,trapY,trapY-80};
        Polygon triangle =new Polygon(xPoints,yPoints,3);
        g2D.setColor(Color.RED);
        g2D.fillPolygon(triangle);

        for(int i=0;i<NUM_BIRD;i++){
            if(bird[i]!=null){
                g2D.drawImage(bird[i],birdX[i],birdY[i],BIRD_WIDTH,BIRD_HEIGHT,this);
            }
        }
        if(!foodEaten) {
            g2D.drawImage(foodImage, foodX, foodY, 50, 50, this);
        }

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        backGroundImageX-=5;
        backGroundImage2X-=5;
        if(backGroundImageX<=-PANEL_WIDTH)
            backGroundImageX=PANEL_WIDTH;
        if(backGroundImage2X<=-PANEL_WIDTH)
            backGroundImage2X=PANEL_WIDTH;


        for(int i=0;i<NUM_BIRD;i++){
            birdX[i]+=5;
            if(birdX[i]>PANEL_WIDTH){
                birdX[i]=0;
            }
        }
        trapX-=5;
        if(trapX<-100)
            trapX=PANEL_WIDTH+new Random().nextInt(200);

        if(!foodEaten){
            foodX-=5;
            if(foodX<-50){
                respawnFood();
            }
        }
        if(carJumping){
            jumpHeight+=10;
            if(jumpHeight>=100){
                carJumping=false;
            }
        }
        else if(jumpHeight>0){
            jumpHeight-=10;
        }
        //collision detection
       Rectangle carRect=new Rectangle(car1X,car1Y-jumpHeight,200,50);
        /*Polygon triangle=new Polygon(new int[]{trapX,trapX+50,trapX+25},new int []{trapY,trapY,trapY-80},3);
       if(carRect.intersects(triangle.getBounds())&&!carJumping){
            if(e.getID()==KeyEvent.VK_UP)
            {carJumping=true;}
        }
       else if(carRect.intersects(triangle.getBounds())&& ! carJumping){
            timer.stop();
            JOptionPane.showMessageDialog(this,"Game Over!Your Score:"+score);
        }*/

        Rectangle foodRect = new Rectangle(foodX,foodY,50,50);
        if(carRect.intersects(foodRect)&&!foodEaten){
            foodEaten=true;
            score+=10;
            foodRespawnCounter=0;
        }
        if(foodEaten){
            foodRespawnCounter++;
            if(foodRespawnCounter>=FOOD_RESPAWN_DELAY){
                respawnFood();
            }
        }
        repaint();
    }
    private void respawnFood(){
        Random random=new Random();
        foodX=PANEL_WIDTH+random.nextInt(200);
        int minY=car1Y-100;
        int maxY=car1Y;
        foodY=random.nextInt(maxY-minY+1)+minY;
        foodEaten=false;
        foodRespawnCounter=0;
    }
    private int getLeftBoundary(int y){
        if(y<=260){
            return 20;
        }
        else if(y<=300){
            return 100;
        }
        else{
            return 50;
        }
    }
    private int getRightBoundary(int y){
        return PANEL_WIDTH;
    }
    public JButton getStartButton()
    {
        return startButton;
    }
    private class ButtonClickListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            if(e.getSource()==startButton){
                startButton.setVisible(false);
            }
        }
    }
}
