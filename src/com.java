
import jssc.*;
//import java.time.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Deivid
 */
public class com {
    SerialPort serialPort = new SerialPort("COM3");
    
    public void open(){
        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_9600,SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
            try {
                Thread.sleep(1700);
            } 
                catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        } 
      
        catch (SerialPortException ex) {
            Logger.getLogger(com.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void close(){
        try {
            serialPort.closePort();
        } catch (SerialPortException ex) {
            Logger.getLogger(com.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void send(String x){
       
    try {
        
    
    
    serialPort.writeString(x);
    
    }
    
    catch (SerialPortException ex) {
    System.out.println("Error enviando datos");
    }
    
    }
    
}
    
    

