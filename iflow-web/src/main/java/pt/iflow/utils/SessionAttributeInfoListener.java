package pt.iflow.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import pt.iflow.api.utils.Logger;

public class SessionAttributeInfoListener implements HttpSessionAttributeListener{

	@Override
	public void attributeAdded(HttpSessionBindingEvent arg0) {
		Logger.warning("", this, "attributeAdded", "SessionAttributeInfoListener: attribute name: " + arg0.getName() + ", class: " + arg0.getValue().getClass());	
		
		try{
			// Serialize data object to a byte array
			ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
			ObjectOutputStream out = new ObjectOutputStream(bos) ;
			out.writeObject(arg0.getValue());
			out.close();

			// Get the bytes of the serialized object
			byte[] buf = bos.toByteArray();
			} catch (IOException e) {
				Logger.error("", this, "attributeAdded", "Session Attribute serialization: attribute name: " + arg0.getName() + ", class: " + arg0.getValue().getClass(), e);
			}
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent arg0) {
		try{
			// Serialize data object to a byte array
			ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
			ObjectOutputStream out = new ObjectOutputStream(bos) ;
			out.writeObject(arg0.getValue());
			out.close();

			// Get the bytes of the serialized object
			byte[] buf = bos.toByteArray();
			} catch (IOException e) {
				Logger.error("", this, "attributeReplaced", "Session Attribute serialization: attribute name: " + arg0.getName() + ", class: " + arg0.getValue().getClass(), e);
			}
		
	}

}
