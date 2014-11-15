// Import header files
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class server
{

    // Declaring global variables
    Socket sock;
    JButton exit,clear;                                                         
    JFrame frame;
    JTextArea info;
    BufferedReader br;
    PrintWriter pw;
    TrayIcon ic;

    // setting port where all communication takes place
    static final int PORT=1025;

    // Stores all the connected clients
    ArrayList connected;

    // Main meathod
    public static void main(String[] args)
    {
	server ser=new server();
	ser.go();
	ser.networking();
    }

    public void go()
    {
	//Setting frame and its Properties
	frame=new JFrame("Server");
	frame.setResizable(false);
	frame.setLocation(750,370);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	// Adding buttons to frame
	clear=new JButton("Clear");
	clear.setToolTipText("Clear the information window.");
	
	// Adding Images and icons
	ImageIcon ico=new ImageIcon("labels/info.gif");
	ImageIcon icon=new ImageIcon("labels/server.png");
	frame.setIconImage(icon.getImage());

	// Setting Lables
	JLabel l1=new JLabel("Information Messages",ico,JLabel.CENTER);
	l1.setToolTipText("It displays users information those who are connecting or leaving server.");

	// Setting fonts for text area
	Font f=new Font("Comic Sans MS",Font.BOLD,15);
	Font f1=new Font("Ariel",Font.BOLD,15);
	l1.setFont(f1);

	ImageIcon ex=new ImageIcon("labels/exit.png");
	
	// Adding buttons and text fields to Frame
	exit=new JButton("Exit",ex);
	info=new JTextArea(13,30);
	info.setFont(f);
	info.setToolTipText("Information Window.");
	exit.setToolTipText("Stop and Exit Server");
	info.setEditable(false);
	info.setLineWrap(true);

	// Adding vertical and Horizontal scroll bars and setting their properties 
	JScrollPane jp=new JScrollPane(info);
	jp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	jp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	
	// If supported then minimize to System tray on pressing minimize Button 
       	if(SystemTray.isSupported())
	    {
		ic=new TrayIcon(icon.getImage());
		ic.setToolTip("Chat Server");
		ic.addMouseListener(new MouseAdapter()
		    {
			// Setting Mouse Handler
			@Override
			public void mouseClicked(MouseEvent ae)
			{
			    frame.setVisible(true);
			    frame.setExtendedState(frame.NORMAL);
			    SystemTray.getSystemTray().remove(ic);
			}
		    });
	    }

	// Set Theme for icons
	try
	    {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		SwingUtilities.updateComponentTreeUI(frame);
	    }
	catch(Exception e)
	    {
		info.append(e+"\n");
	    }
	
	// Adding pannels to Frame
	JPanel pane=new JPanel();
	JPanel pane1=new JPanel();
	JPanel pane2=new JPanel();
	
	// Setting adjustment listener to control vertical scrollbar
	jp.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener()
	    {
		public void adjustmentValueChanged(AdjustmentEvent ae)
		{
		    ae.getAdjustable().setValue(ae.getAdjustable().getMaximum());
		}
	    });
	
	// set window handler
	frame.addWindowListener(new WindowAdapter()
	    {
		// Action on closing window
		@Override
		public void windowClosing(WindowEvent e)
		{
		    JOptionPane.showMessageDialog(frame,"Server Turned off!!");
		}
		
		// Action on maximizing Window
		public void windowIconified(WindowEvent e)
		{
		    frame.setVisible(false);
		    try
			{
			    SystemTray.getSystemTray().add(ic);
			}
		    catch(Exception ex){}
		}
	    });
	
	// Adding handlers to text field
	clear.addActionListener(new ActionListener()
	    {
		public void actionPerformed(ActionEvent ae)
		{
		    if(ae.getSource()==clear)
			{
			    info.setText("");
			}
		}
	    });
	
	// Register button Handlers
	exit.addActionListener(new ActionListener()
	    {
		public void actionPerformed(ActionEvent ae)
		{
		    // When Exit button is pressed
		    if(ae.getSource()==exit)
			{
			    int c=JOptionPane.showConfirmDialog(frame,"Are you Sure?","Conformation",JOptionPane.YES_NO_OPTION);
			    if(c==JOptionPane.YES_OPTION)
				{
				    System.exit(0);
				}
			}
		}
	    });

	// Set allignment of components in frame
	pane2.add(BorderLayout.NORTH,l1);
	pane.add(BorderLayout.CENTER,jp);
	frame.add(BorderLayout.NORTH,pane2);
	pane1.add(clear);
	pane1.add(exit);
	frame.getContentPane().add(BorderLayout.CENTER,pane);
	frame.getContentPane().add(BorderLayout.SOUTH,pane1);
	frame.pack();
	frame.setVisible(true);
    }
    
    // Add new threads when new user enters 
    class reader implements Runnable
    {
	BufferedReader br;
	public reader(Socket clientsocket)
	{
	    try
		{
		    sock=clientsocket;

		    // Getting input stream
		    InputStreamReader ir=new InputStreamReader(sock.getInputStream());
		    br=new BufferedReader(ir);
		}
	    catch(IOException ex)
		{
		    info.append(ex+"\n");
		}
	}
	
	// thread handler
	public void run()
	{
	    String msg,n;
	    try
		{
		    // If message is not empty then broadcast message
		    while((msg=br.readLine())!=null)
			{
			    telleveryone(msg);
			}
		}
	    catch(SocketException ex)
		{
		    info.append("Client Disconnected.\n");
		}
	    catch(IOException ex)
		{
		    info.append(ex+"\n");
		}
	}
    }
    
    //Meathod to handle networking Activities
    public void networking()
    {
	connected=new ArrayList();
	String con;
	try
	    {
		// Create new socket 
		ServerSocket ss=new ServerSocket(PORT);
		info.append("Server Started at port : "+PORT+"\n");
		while(true)
		    {
			try
			    {
				// Get socket connection
				Socket clientsocket=ss.accept();
				InetAddress a=clientsocket.getInetAddress();
				info.append(a.getHostName()+" at IP Address "+a.getHostAddress()+" Connected to server.\n");

				// Get output steam
				PrintWriter pw=new PrintWriter(clientsocket.getOutputStream());
				connected.add(pw);

				//Create and start new thread
				Thread t1=new Thread(new reader(clientsocket));
				t1.start();
			    }
			catch(NullPointerException ex)
			    {
				info.append("Null pointer Exception ");
			    }
			catch(SocketException e)
			    {
				info.append("Connection Reset\n");
			    }
		    }
	    }
	catch(Exception ex)
	    {
		info.append("Inside Exception\n");
		info.append("Exception Raised : "+ex+"\n");
	    }
    }
    
    // Meathod to broadcast messages
    public void telleveryone(String str)
    {
	try
	    {
		// Iterate through list and send message to each user 
		Iterator ir=connected.iterator();
		while(ir.hasNext())
		    {
			PrintWriter pw=(PrintWriter)ir.next();
			pw.println(str);
			pw.flush();
		    }
	    }
	catch(Exception ex)
	    {
		info.append(ex+"\n");
	    }
    }
}
