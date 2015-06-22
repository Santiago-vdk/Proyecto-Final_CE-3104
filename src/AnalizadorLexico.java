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
    private ArrayList<String> resultado = new ArrayList();
    private String[] tokIns;
    private ListaString listaErrores = new ListaString();
    int contadorlinea=1;
    
    
    
    
    
    public void analizar(String in)//analiza la linea de codigo token por token para formar una lista de 
    {
        tokIns = in.split("\\s+");
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
                    tokIns[i].equals("{")|
                    tokIns[i].equals("}")|
                    tokIns[i].equals("=")
                )
            {
                resultado.add(tokIns[i]);
            }
            else if(tokIns[i].equals("newline")){
                contadorlinea++;
                resultado.add(tokIns[i]);
            }
            
            else if (tokIns[i].matches("const"))
            {
                if (    tokIns[i+1].matches("char")|
                        tokIns[i+1].matches("int")|
                        tokIns[i+1].matches("float")|
                        tokIns[i+1].matches("string"))
                {
                    resultado.add("tipo");
                    i++;
                    
                }
                else
                {
                    listaErrores.insertar("Analizador Lexico. Error: Tipo de dato desconocido. en linea: "+String.valueOf(contadorlinea));/*codigo para meter un error en el archivo de errores*/
                }
                
            }
            else if (   tokIns[i].matches("char")|
                        tokIns[i].matches("int")|
                        tokIns[i].matches("float")|
                        tokIns[i].matches("string"))
            {
                resultado.add("tipo");
            }
            else if (tokIns[i].matches("mover"))
            {
                resultado.add("mover");
                if (tokIns[i+1].matches("[0-3]"))
                {  
                    resultado.add("puerta");
                    i++;
                }
                else
                {
                    listaErrores.insertar("Analizador Lexico. Error: ID de puerta invalido. en linea: "+String.valueOf(contadorlinea)); /*codigo para meter un error por id de puerta invalida*/
                }
                    
            }
            else if (tokIns[i].matches("[a-zA-z]([a-zA-Z | 0-9]{1,31})"))
            {
                resultado.add("id");
            }
            else if (tokIns[i].matches("[0-9]*.?[0-9]*"))
            {
                resultado.add("num");
            }
            
            
            else
            {
                resultado.add("id");
                System.out.println("error token no identificado");
                listaErrores.insertar("Analizador Lexico. Error: ID invalido. en linea: "+String.valueOf(contadorlinea)); 
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
 
    
    public String getResultado()
    {
        String temp="";
        for(String s: resultado)
        {
            temp=temp+s+" ";
        }
        return temp;
    }

    /**
     * @return the listaErrores
     */
    public ListaString getListaErrores() {
        return listaErrores;
    }
}
