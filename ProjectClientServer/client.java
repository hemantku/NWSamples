// Import header files required
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import java.awt.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;

public class client
{
    // Declare global variables.
    JFrame frame;
    JButton login,logout,exit,send,b5,clear;
    JTextArea incoming;
    JList online;
    JTextField outgoing;
    JLabel l1,l2;
    Font f,f1;
    JPanel pan,pan1,pan2,pan3,pan4,pan5;
    String user;
    Socket sock;
    BufferedReader br;
    PrintWriter pw;
    ImageIcon img,sen,ex,log,onl,title_icon;
    Image im;
    JScrollPane j1,j2;
    
    // port to connect to server
    static final int PORT=1025;
    
    // Stores the connected clients
    ArrayList<String> names=new ArrayList<String>();

    //Initialize variables
    public void initialize()
    {
	// pannels for adding components
	pan=new JPanel();
	pan1=new JPanel();
	pan2=new JPanel();
	pan3=new JPanel();
	pan4=new JPanel();
	pan5=new JPanel();
	
	//setting fonts for Text Fields
	f=new Font("Comic Sans MS",Font.BOLD,15);
	f1=new Font("Comic Sans MS",Font.BOLD,35);
	
	// Adding Lables 
	sen=new ImageIcon("labels/send.png");
	ex=new ImageIcon("labels/exit.png");
	log=new ImageIcon("labels/login.gif");
	onl=new ImageIcon("labels/users.gif");
	l1=new JLabel("CHAT SERVER");
	l1.setFont(f1);
	l2=new JLabel(onl);
	login=new JButton("Login",log);
	online=new JList();

	online.setVisibleRowCount(10);
	online.setFixedCellWidth(250);
	online.setFixedCellHeight(20);
	online.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	online.setFont(f);
	
	// Adding Buttons
	logout=new JButton("Logout");
	exit=new JButton("Exit",ex);
	send=new JButton("Send",sen);
	send.setToolTipText("Send the message");
	b5=new JButton("Clear Chat");
	b5.setToolTipText("Clear Chat Window.");
	clear=new JButton("Clear");
	clear.setToolTipText("Clear the input area.");
	incoming=new JTextArea(17,35);
	incoming.setToolTipText("Chat Window");
	outgoing=new JTextField(20);
	outgoing.setToolTipText("Type your Message Here.");
	
	// Adding Text Fields
	incoming.setBackground(Color.black);
	incoming.setForeground(Color.white);
	online.setBackground(Color.black);
	online.setForeground(Color.white);
	online.setToolTipText("Show Online Clients");

	// setting properties of components
	outgoing.setFocusable(false);
	logout.setEnabled(false);
	login.setEnabled(false);
	login.setToolTipText("Click to Login and Chat.");
	
	// Adding icons
	title_icon=new ImageIcon("labels/chat.gif");
	im=title_icon.getImage();
	frame.setIconImage(im);
	
	// Setting vertical and Horizontal scroll bars
	j1=new JScrollPane(incoming);
	j2=new JScrollPane(online);
	j1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	j1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);		
	j2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	j2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
	
	incoming.setLineWrap(true);
	incoming.setEditable(false);
	
	// Button Handlers
	incoming.addKeyListener(new buttons());
	outgoing.addKeyListener(new buttons());
	login.addActionListener(new buttons());
	logout.addActionListener(new buttons());
    }
    
    public void go()   
    {
	// Adding and setting frame
	frame=new JFrame("Client Window");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setLocation(700,350);
	
	// Setting theme
	try
	    {
		UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		SwingUtilities.updateComponentTreeUI(frame.getContentPane());
	    }
	catch(Exception ex){}


	initialize();
	
	logout.setToolTipText("Logout Chat");
	exit.addActionListener(new buttons());
	exit.setToolTipText("Logout and Exit");
	send.addActionListener(new buttons());
	b5.addActionListener(new buttons());
	clear.addActionListener(new buttons());

	// Adding components to pannels
	pan5.add(l1);
	pan5.setBackground(Color.white);
	pan1.add(j1);
	pan2.add(login);
	pan2.add(clear);
	pan2.add(outgoing);
	pan2.add(send);
	pan2.add(b5);
	pan2.add(logout);
	pan2.add(exit);
	pan4.add(BorderLayout.NORTH,l2);
	pan4.add(j2);
	pan.add(pan2);
	pan.add(pan3);
	pan.add(pan4);

	// Setting Backgroung color of pannels
	pan1.setBackground(Color.white);
	pan2.setBackground(Color.white);
	pan4.setBackground(Color.white);
	pan.setBackground(Color.white);
	frame.getContentPane().add(pan4);

	// Setting positions of Pannels
	frame.getContentPane().add(BorderLayout.NORTH,pan5);
	frame.getContentPane().add(BorderLayout.WEST,pan1);
	frame.getContentPane().add(BorderLayout.SOUTH,pan);

	// This code will handle the position of vertical scroll bar.
	// It always move scroll bar to end of line.
	j1.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener()
	    {  
		public void adjustmentValueChanged(AdjustmentEvent e) 
		{  
		    e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
		}
	    });


	// This class will control the sending and Recieving of Messages
	class Message implements Runnable
	{
	    String msg;
	    public void run()
	    {
		String n;
		try
		    {
			while((msg=br.readLine())!=null)
			    {
				// When a new user logs in.
				if(msg.startsWith("$"))
				    {
					// If name is uniqe
					if((names.contains(n=msg.substring(1,msg.length())))==false)
					    {
						// add user to online users list and display message.
						incoming.append("****"+n+" Logged in****\n");
						names.add(n);
						online.setListData(names.toArray());

					    }
					// Else do nothing and Continue
					else
					    continue;

				    }

				// If a new user logs in then " ~##~ " message is broadcast by that user
				// and on recieving this message all the users that are loggen in will broadcast their usernames
				// so that new user can update its online user list
				else if(msg.equals("~##~"))
				    {
					// if user is logged
					if(logout.isEnabled())
					    {
						pw.println("$"+user);
						pw.flush();
					    }

				    }
				
				// If some user logs out
				else if(msg.startsWith("#"))
					{
					    n=msg.substring(1,msg.length());
					    incoming.append("****"+n+" Logged out****\n");
					    
					    // update online users list
					    if(names.contains(n));
						{
						    names.remove(n);
						    online.setListData(names.toArray());
						}
					}
				
				// Else display the message.
				else
				    {
					incoming.append(msg+"\n");
				    }
			    }
		    }

		// When new user connects 
		catch(SocketException ex)
		    {
			
			incoming.append(ex+"\n");

			// Starts Thread for new user
			if(logout.isEnabled())
			    {
				int reply=JOptionPane.showConfirmDialog(frame,ex+" Do you want to start server again and continue?","Error",JOptionPane.YES_NO_OPTION);
				if(reply==JOptionPane.YES_OPTION)
				    {
					networking();
					login.setEnabled(false);
					Thread t1=new Thread(new Message());
					t1.start();
				    }
				else
				    System.exit(0);
			    }

			else
			    System.exit(0);
		    }

		catch(Exception x)
		    {
			incoming.append(x+" Exception in run\n");
		    }
	    }
	}
	
	// Set frame properties and Start frame
	frame.setSize(750,500);
	frame.setResizable(false);
	frame.setVisible(true);

	// window Handler 
	frame.addWindowListener(new WindowAdapter()
	    {
		public void windowClosing(WindowEvent we)
		{
		    exit.doClick();
		}
	    });

	networking();
	Thread t1=new Thread(new Message());
	t1.start();

    }

    // Control all the networking
    private void networking()
    {
	try
	    {
		Object[] option={"Manual","Automatic"};
	
		// getting server's IP address
		int reply=JOptionPane.showOptionDialog(frame,"How you want to provide server Address?","Server Address",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,option,option[1]);
		if(reply==JOptionPane.YES_OPTION)
		    {
			String server_address=JOptionPane.showInputDialog(frame,"Enter Server Address or PC-Name.","Input Address",JOptionPane.OK_CANCEL_OPTION);
			if(server_address!=null)
			    {
				sock=new Socket(InetAddress.getByName(server_address),PORT);
			    }
			else
			    {
				SocketException sc=new SocketException();
				throw sc;
			    }
		    }

		// This code will ping on all the IP addresses in the network and scans for the server
		else
		    {
			InetAddress a=InetAddress.getLocalHost();
			String ipaddress=a.getHostAddress();
			String subnet=ipaddress.substring(0,(ipaddress.lastIndexOf('.'))+1);
			incoming.append("Scaning for Server please wait......\n");
			for(int i=0;i<256;i++)
			    {
				String s1=subnet+""+i;
				incoming.append("pinging "+s1+"\n");
				InetAddress ad=InetAddress.getByName(s1);
				if(ad.isReachable(PORT))
				    {
					sock=new Socket(ad,PORT);
					incoming.append("Server Found\n");
					break;
				    }
			    }
		    }

		// Recieving input and output streams
		InputStreamReader ir=new InputStreamReader(sock.getInputStream());
		br=new BufferedReader(ir);
		pw=new PrintWriter(sock.getOutputStream());
		login.setEnabled(true);
		incoming.append("Connected to Server.please login.\n");
		pw.println("~##~");
		pw.flush();
		login.requestFocus();
	    }

	// Handling server failure exception
	catch(Exception ex)
	    {
		JOptionPane.showMessageDialog(frame,"Server Not Running or Failed!!","Error",JOptionPane.INFORMATION_MESSAGE);
		incoming.append("Server not running or Connection Problem : "+ex+"\n");
		System.exit(0);
	    }
    }

    // class for setting Handlers
    class buttons implements ActionListener,KeyListener
    {
	String str;
	public void actionPerformed(ActionEvent ae)
	{

	    // When send button is pressed
	    if(ae.getSource()==send)
		{
		    // If user is logged in.
		    if(logout.isEnabled())
			{
			    // Broadcast message
			    String str1=outgoing.getText();
			    str=str1.trim();
			    if(str.length()!=0)
				{
				    pw.println(user+" : "+str);
				    pw.flush();
				    outgoing.setText("");
				    outgoing.requestFocus();
				}
			}
		    
		    // If user is not logged in then dispaly a message.
		    else
			{
			    JOptionPane.showMessageDialog(frame,"You must Login First","Login",JOptionPane.INFORMATION_MESSAGE);
			}
		}

	    // If login Button is Pressed
	    if(ae.getSource()==login)
		{

		    // Give your user name.
		    String usr=JOptionPane.showInputDialog(frame,"Enter Your Name.");
		    String user1=usr.trim();
		    user=user1.toUpperCase();

		    // If name is not Uniqe
		    if(names.contains(user)==true)
			{
			    JOptionPane.showMessageDialog(frame,"User name Already exists.\nEnter a unique username.","Error",JOptionPane.INFORMATION_MESSAGE); 
			}

		    // If Username is not Empty then Login and Broadcast Username
		    else if(user.length()!=0)
			{
			    JOptionPane.showMessageDialog(frame,"Login Successful!!");
			    incoming.setText("");
			    pw.println("$"+user);
			    pw.flush();
			    pw.println("@**"+new Date()+"**\n\n\n");
			    pw.flush();
			    outgoing.setFocusable(true);
			    login.setEnabled(false);
			    logout.setEnabled(true);
			    frame.setTitle(user+"'s Chat Window");
			}
		    
		    // Else Display Login Failed Message
		    else
			{
			    JOptionPane.showMessageDialog(frame,"Login Failed!!");
			}
		}

	    // If Logout Button is Pressed then Broadcast username and Logout
	    if(ae.getSource()==logout)
		{
		    JOptionPane.showMessageDialog(frame,"Logout Successful.");
		    frame.setTitle("Client Window");
		    pw.println("#"+user);
		    pw.flush();
		    incoming.setText("");
		    outgoing.setText("");
		    login.setEnabled(true);
		    login.requestFocus();
		    logout.setEnabled(false);
		    outgoing.setFocusable(false);
		}

	    // If Exit Button is Pressed then Logout and Exit
	    if(ae.getSource()==exit)
		{

		    // If user is logged in.
		    if(logout.isEnabled())
			{
			    
			    // Broadcast username and logout
			    int reply=JOptionPane.showConfirmDialog(frame,"Are You sure?","Exit",JOptionPane.YES_NO_OPTION);
			    if(reply==JOptionPane.YES_OPTION)
				{
				    JOptionPane.showMessageDialog(frame,"Logout Successful","Exit",JOptionPane.INFORMATION_MESSAGE);
				    pw.println("#"+user);
				    pw.flush();
				    System.exit(0);
				}
			}

		    // Else simply Exit
		    else
			System.exit(0);
		}

	    // If clear button is Pressed the clear the outgoing text Field
 	    if(ae.getSource()==clear)
		{
		    outgoing.setText("");
		    outgoing.requestFocus();
		}

	    // If clear Button is Pressed then clear the chat Displey area
	    if(ae.getSource()==b5)
		{
		    incoming.setText("");
		    outgoing.requestFocus();
		}
	}

	// KeyBoard handlers
	public void keyPressed(KeyEvent ke)
	{
	    String s1=outgoing.getText();
	    String s=s1.trim();
	    int key=ke.getKeyCode();

	    // If Enter key is pressed then broadcast message in the Outgoing text Field
	    if((key==KeyEvent.VK_ENTER)&&(s.length()!=0))
		{
		    send.doClick();
		}
	}

	public void keyReleased(KeyEvent ke)
	{}

	public void keyTyped(KeyEvent ke)
	{}

    }

    // Main Function
    public static void main(String args[])
	{
	    new client().go();
	}
}
