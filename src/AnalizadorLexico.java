/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;

/**
 *
 * @author victor
 */
public class AnalizadorLexico {
    public ArrayList<String[]> tablaSimb=new ArrayList(); 
    public ArrayList<String> resultado = new ArrayList();
    private boolean simb=false;//determina si se esta definiendo un nuevo simbolo
    private String[] tokIns;
    private String[] simbTemp= new String[6];  
    
    
    
    public void analizar(String in)//analiza la linea de codigo token por token para formar una lista de 
    {
        tokIns = in.split("\\s+");
        clearTemp();
        for (int i=0; i<tokIns.length;i++)
        {
            if (    tokIns[i].equals("declarar")|
                    tokIns[i].equals("\\")|
                    tokIns[i].equals("<")|
                    tokIns[i].equals(">")|
                    tokIns[i].equals("<=")|
                    tokIns[i].equals(">=")|
                    tokIns[i].equals("==")|
                    tokIns[i].equals("!=")|
                    tokIns[i].equals("+")|
                    tokIns[i].equals("-")|
                    tokIns[i].equals("*")|
                    tokIns[i].equals("/")|
                    tokIns[i].equals("(")|
                    tokIns[i].equals(")")|
                    tokIns[i].equals("")|
                    tokIns[i].equals("true")|
                    tokIns[i].equals("false")|
                    tokIns[i].equals("si")|
                    tokIns[i].equals("entonces")|
                    tokIns[i].equals("sino")|
                    tokIns[i].equals("mientras")|
                    tokIns[i].equals("haga")|
                    tokIns[i].equals("newline")|
                    tokIns[i].equals("{")|
                    tokIns[i].equals("}")
                )
            {
                resultado.add(tokIns[i]);
            }
            
            else if (tokIns[i].matches("const"))
            {
                simb=true;
                simbTemp[4]="const";
                if (    tokIns[i+1].matches("char")|
                        tokIns[i+1].matches("int")|
                        tokIns[i+1].matches("float")|
                        tokIns[i+1].matches("string"))
                {
                    resultado.add("tipo");
                    simbTemp[2]=tokIns[i+1];
                    i++;
                    
                }
                else
                {
                    simb=false;
                    clearTemp();
                    /*codigo para meter un error en el archivo de errores*/
                }
                
            }
            else if (   tokIns[i].matches("char")|
                        tokIns[i].matches("int")|
                        tokIns[i].matches("float")|
                        tokIns[i].matches("string"))
            {
                simb=true;
                simbTemp[2]=tokIns[i];
                resultado.add("tipo");
            }
            else if (tokIns[i].matches("mover"))
            {
                resultado.add("mover");
                if (tokIns[i+1].matches("[1-4]"))
                {  
                    resultado.add("puerta");
                    i++;
                }
                else
                {
                    /*codigo para meter un error por id de puerta invalida*/
                }
                    
            }
            else if (tokIns[i].matches("[a-zA-z]([a-zA-Z | 0-9]{1,31})"))
            {
                resultado.add("id");
                int temp=simbPos(tokIns[i]);//ubicacion del simbolo en tabSimb, si no esxiste es -1
                if (simb)
                {
                    if(temp==-1)
                    {
                        simbTemp[0]="id";
                        simbTemp[1]=tokIns[i];
                        
                        tablaSimb.add(simbTemp.clone());
                        simb=false;
                        clearTemp();
                    }
                    else
                    {
                        /*añadir error de variabla ya declarada*/
                        clearTemp();
                        simb=false;
                    }
                }
                else
                {
                    if(temp==-1)
                    {
                        /*error de variable desconocida*/
                    }
                    else
                    {
                        int temp2 =Integer.parseInt(tablaSimb.get(temp)[5])+1;
                        //tablaSimb.get(temp)[3]= tokIns[i+1];
                        tablaSimb.get(temp)[5]= String.valueOf(temp2);
                        clearTemp();
                    }
                }
            }
            else if (tokIns[i].equals("="))
            {
                resultado.add("=");
                if (i==0 | i==tokIns.length-1)
                {
                    System.out.println("meter error de asignacion mal hubicada");
                    /*meter error de asignacion mal hubicada*/
                }
                else
                {
                    int temp = simbPos(tokIns[i - 1]);
                    if (temp == -1) 
                    {
                        System.out.println("meter error de variable no declarada*");
                        /*meter error de variable no declarada*/
                    }
                    else 
                    {
                        tablaSimb.get(temp)[3]= tokIns[i+1];
                    }
                }
                
            }
            else if (tokIns[i].matches("[0-9]*.?[0-9]*"))
            {
                resultado.add("num");
            }
            
            
            else
            {
                resultado.add("id");
                System.out.println("error token no identificado");
                /*codigo para meter un error en el archivo de errores*/
            }
        }
    }
    
    public void printTok()
    {
        for(String s: resultado)
        {
            System.out.println(s);
        }
    }
    public void printTab()
    {
        for (String[] sl: tablaSimb)
        {
            System.out.println("****");
            for(String s: sl)
            {
                System.out.println(s);
            }
        }
    }
    
    public int simbPos(String Nombre)//da la posicion de un simbolo en la tabla de simbolos, buscando por nombre
    {
        for (int i=0; i<tablaSimb.size();i++)
        {
            if (tablaSimb.get(i)[1].equals(Nombre))
            {
                return i;
            }
        }
        return -1;
    }
    
    private void clearTemp()
    {
        int i=0;
        while (i<6)
        {
            simbTemp[i]="0";
            i++;
        }
    }
}
