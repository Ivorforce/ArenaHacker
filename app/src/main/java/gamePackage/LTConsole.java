package gamePackage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.event.EventListenerList;

public class LTConsole extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	EventListenerList listeners;

	JTextField textField;
	
	private String lastAction;
	
	public LTConsole()
	{
		super("Console");
		
		listeners=new EventListenerList();
		
		textField=new JTextField();
		textField.addActionListener(this);
		
		setBounds(100, 30, 200, 60);
		
		add(textField);
	}
	
	public void addConsoleListener( LTConsoleListener listener ) 
	{ 
		listeners.add( LTConsoleListener.class, listener ); 
	} 
	 
	public void removeConsoleListener( LTConsoleListener listener ) 
	{ 
		listeners.remove( LTConsoleListener.class, listener ); 
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {

		if(textField.getText().matches("r"))
		{
			if(lastAction!=null)
			{
				for(LTConsoleListener listener : listeners.getListeners(LTConsoleListener.class))
				{
					listener.runConsoleScript(new LTConsoleEvent(this, lastAction));
				}
			}
		}
		else
		{
			lastAction=textField.getText();
			
			for(LTConsoleListener listener : listeners.getListeners(LTConsoleListener.class))
			{
				listener.runConsoleScript(new LTConsoleEvent(this, textField.getText()));
			}
		}
		
		textField.setText("");
	}

}
