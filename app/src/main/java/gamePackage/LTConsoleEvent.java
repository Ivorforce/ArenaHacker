package gamePackage;
import java.util.EventObject;

public class LTConsoleEvent extends EventObject{

	private static final long serialVersionUID = 1L;
	
	private String script; 
 
	public LTConsoleEvent( Object source, String script) 
	{ 
		super( source ); 
		this.script = script; 
	} 
 
	public String getScript() 
	{ 
		return script; 
	} 
}
