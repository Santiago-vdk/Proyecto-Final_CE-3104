
import java.util.ArrayList;
import jssc.*;
//import java.math.
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
    public void esperar(Float x){
        try {
            Thread.sleep(Math.round(x));
        } catch (InterruptedException ex) {
            Logger.getLogger(com.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
   public void interpretar(ArrayList<String> x) throws InterruptedException{
        open();
        for(int i=0;i<x.size();i=i+2){
           if(x.get(i).equals("0")){
              send("b");
              esperar(Float.parseFloat(x.get(i+1)));
              send("c");
              esperar(Float.parseFloat(x.get(i+1)));
              send("a");
           }
           if(x.get(i).equals("1")){
              send("e");
              esperar(Float.parseFloat(x.get(i+1)));
              send("d");
           }
           if(x.get(i).equals("2")){
              send("g");
              esperar(Float.parseFloat(x.get(i+1)));
              send("f");
           }
           if(x.get(i).equals("3")){
              send("i");
              esperar(Float.parseFloat(x.get(i+1)));
              send("h");
           }
       }
   } 
}
    
    

