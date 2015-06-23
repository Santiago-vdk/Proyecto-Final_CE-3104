
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Facade {
    private com _comunicador;
    private ListaProduccion _producciones;
    private tabla _tabla;
    private ListaString _resumenTablaPNR;
    private ListaString _ListaErrores;
    private ArrayList<String[]> tablaSimbolos = new ArrayList();
    private boolean error;
    private ArrayList<String> ejecutable=new ArrayList();
    private ListaString _prodCalculadas;
    public Facade() throws FileNotFoundException {
        _producciones = new ListaProduccion();
        _tabla = new tabla();
        _resumenTablaPNR = new ListaString();
        _ListaErrores = new ListaString();
        error = false;
        _comunicador = new com();
        _prodCalculadas = new ListaString();

    }
    
    //**********************************************************************************************************************************************

    public void AnalisisSemantico(String pEntrada) throws InterruptedException {
        int contador = 1;
        String[] tokens = pEntrada.split("\\s+");

        for (int i = 0; i < tokens.length; i++) {//ciclo para armar la tabla de simbolos
            if (tokens[i].equals("newline")) {
                contador++;
            } 
            
            else if (tokens[i].equals("declarar")) {
                String[] temp = new String[6];
                temp[0] = "id";//lexema
                temp[3] = "-";//valor temporal
                temp[5] = "0";//contador de usos
                if (tokens[i + 1].equals("const")) {//si es const
                    temp[4] = "1";//bool const
                    temp[2] = tokens[i + 2];//tipo
                    if (buscarSimbolo(tokens[i + 3]) == -1) {
                        temp[1] = tokens[i + 3];
                        tablaSimbolos.add(temp);
                    } else {
                        _ListaErrores.insertar("Analisis semantico. Error: Variable ya declarada. En linea: " + String.valueOf(contador));
                    }
                    i += 4;
                } else {
                    temp[4] = "0";//bool const
                    temp[2] = tokens[i + 1];//tipo
                    if (buscarSimbolo(tokens[i + 2]) == -1) {
                        temp[1] = tokens[i + 2];
                        tablaSimbolos.add(temp);
                    } else {
                        _ListaErrores.insertar("Analisis semantico. Error: Variable ya declarada. En linea: " + String.valueOf(contador));
                    }
                    i += 3;
                }

            } else if (tokens[i].equals("=")) {
                if (buscarSimbolo(tokens[i - 1]) == -1) {
                    _ListaErrores.insertar("Analisis semantico. Error: Variable no declarada. En linea: " + String.valueOf(contador));
                } else if (tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[4].equals("1") && !tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[5].equals("0")) {
                    _ListaErrores.insertar("Analisis semantico. Error: Reasignaccion a una constante. En linea: " + String.valueOf(contador));
                } else {
                    int indiceNL = buscarSigNL(i, tokens);
                    String tipo = tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[2];
                    if (indiceNL == i + 2) {

                        if (buscarSimbolo(tokens[i + 1]) != -1) {
                            if (tablaSimbolos.get(buscarSimbolo(tokens[i + 1]))[2].equals(tipo)) {
                                tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[3] = tablaSimbolos.get(buscarSimbolo(tokens[i + 1]))[3];
                            } else {
                                _ListaErrores.insertar("Analisis semantico. Error: asignacion de tipos incompatibles. En linea: " + String.valueOf(contador));
                            }

                        } else if (tipo.equals("char")) {
                            if (tokens[i + 1].length() == 1) {
                                tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[3] = tokens[i + 1];
                            } else {
                                _ListaErrores.insertar("Analisis semantico. Error: asignacion de tipos incompatibles. En linea: " + String.valueOf(contador));
                            }
                        } else if (tipo.equals("string")) {
                            tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[3] = tokens[i + 1];
                        } else if (tipo.equals("int")) {
                            if (tokens[i + 1].matches("[0-9]+")) {/*[0-9]*.?[0-9]**/

                                tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[3] = tokens[i + 1];
                            } else {
                                _ListaErrores.insertar("Analisis semantico. Error: asignacion de tipos incompatibles. En linea: " + String.valueOf(contador));
                            }
                        } else {
                            if (tokens[i + 1].matches("[0-9]*.?[0-9]")) {
                                tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[3] = tokens[i + 1];
                            } else {
                                _ListaErrores.insertar("Analisis semantico. Error: asignacion de tipos incompatibles. En linea: " + String.valueOf(contador));
                            }

                        }
                    } else {
                        float temp = expresionSuma(i + 1, indiceNL - 1, tokens, contador);
                        if (tipo.equals("float")) {
                            tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[3] = String.valueOf(temp);
                        } else if (tipo.equals("int")) {
                            int temp2 = Float.floatToIntBits(temp);
                            tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[3] = String.valueOf(temp2);
                        }
                    }
                    i += indiceNL - i - 1;
                }

            } else if (tokens[i].equals("mover")) {
                int indiceNL = buscarSigNL(i, tokens);
                if (expresionSuma(i + 2, indiceNL, tokens, contador) > 120) {
                    error = true;
                    _ListaErrores.insertar("Analisis semantico. Error: tiempo mayor al permitido. En linea: " + String.valueOf(contador));
                } else {
                    ejecutable.add(tokens[i + 1]);
                    ejecutable.add(String.valueOf(expresionSuma(i + 2, indiceNL, tokens, contador)));
                }
                i += indiceNL - i - 1;
            } else if (tokens[i].equals("si")) {
                int cont1 = 1;
                int cont2 = i + 2;
                while (cont1 != 0) {
                    if (tokens[cont2].equals("(")) {
                        cont1++;
                    } else if (tokens[cont2].equals(")")) {
                        cont1--;
                    }
                    cont2++;
                }
                if (expresionCondicion(i + 1, i + cont2, tokens, contador)) {//si se cumple la condicion

                    if (tokens[cont2 + 2].equals("{")) {
                        int cont3 = 1;
                        int cont4 = cont2 + 3;
                        while (cont3 != 0) {
                            if (tokens[cont4].equals("{")) {
                                cont3++;
                            } else if (tokens[cont4].equals("}")) {
                                cont3--;
                            }
                            cont4++;
                        }
                        expresionSi(cont2 + 3, cont4 - 1, tokens, contador);
                        i = cont4;

                    } else {
                        int indiceNL = buscarSigNL(i, tokens);
                        i = indiceNL - 1;
                        expresionSi(cont2 + 3, indiceNL - 1, tokens, contador);
                    }

                    /*saltarse el sino*/
                } else {// no se cumple la condicion

                    if (tokens[cont2 + 2].equals("{")) {
                        int cont3 = 1;
                        int cont4 = cont2 + 3;
                        while (cont3 != 0) {
                            if (tokens[cont4].equals("{")) {
                                cont3++;
                            } else if (tokens[cont4].equals("}")) {
                                cont3--;
                            }
                            cont4++;
                        }
                        if (tokens[cont4].equals("sino")) {
                            /*llama a declaracionesAnidadas()*/
                            if (tokens[cont4 + 1].equals("{")) {
                                int cont5 = 1;
                                int cont6 = cont4 + 1;
                                while (cont5 != 0) {
                                    if (tokens[cont6].equals("{")) {
                                        cont5++;
                                    } else if (tokens[cont6].equals("}")) {
                                        cont5--;
                                    }
                                    cont6++;
                                }
                                expresionSi(cont4 + 2, cont2 - 1, tokens, contador);
                                i = cont4;

                            } else {
                                int indiceNL = buscarSigNL(i, tokens);
                                i = indiceNL - 1;
                                expresionSi(cont2 + 3, indiceNL - 1, tokens, contador);
                            }


                       } 
                        else{
                        i=cont4;
                        }
                    }
                    else {
                        int indiceNL = buscarSigNL(i,tokens);
                        if(tokens[indiceNL].equals("sino")){
                            /*llamar funcion del sino*/
                           if (tokens[indiceNL + 1].equals("{")) {
                                int cont5 = 1;
                                int cont6 = indiceNL + 1;
                                while (cont5 != 0) {
                                    if (tokens[cont6].equals("{")) {
                                        cont5++;
                                    } else if (tokens[cont6].equals("}")) {
                                        cont5--;
                                    }
                                    cont6++;
                                }
                                expresionSi(indiceNL + 2, cont2 - 1, tokens, contador);
                                i = indiceNL;

                            } else {
                                int indiceNL2 = buscarSigNL(i, tokens);
                                i = indiceNL2 - 1;
                                expresionSi(cont2 + 3, indiceNL2 - 1, tokens, contador);
                            }
                        }
                        else{
                        i=indiceNL-1;
                        }
                        
                    }
                    

                }
            }
            else if (tokens[i].equals("mientras")) {
                int cont1 = 1;
                int cont2 = i + 2;
                while (cont1 != 0) {
                    if (tokens[cont2].equals("(")) {
                        cont1++;
                    } else if (tokens[cont2].equals(")")) {
                        cont1--;
                    }
                    cont2++;
                }
                if (tokens[cont2+2].equals("{")) {
                    int cont3 = 1;
                    int cont4 = cont2 + 3;
                    while (cont3 != 0) {
                        if (tokens[cont4].equals("{")) {
                            cont3++;
                        } else if (tokens[cont4].equals("}")) {
                            cont3--;
                        }
                        cont4++;
                    }
                    expresionWhile(cont2+3,cont4-1,i+3,cont2-1,tokens,contador);//llamar a funcion while
                    i=cont4;

                }
                else{
                    int indiceNL = buscarSigNL(i,tokens);
                    i=indiceNL -1;
                    expresionWhile(cont2+3,indiceNL-1,i+3,cont2-1,tokens,contador);//llamar a funcion while
                    //mover indice
                }


            }
            else if(tokens[i].equals("haga")){
                if (tokens[i+1].equals("{")) {
                    int cont1 = 1;
                    int cont2 = i + 2;
                    while (cont1 != 0) {
                        if (tokens[cont2].equals("{")) {
                            cont1++;
                        } else if (tokens[cont2].equals("}")) {
                            cont1--;
                        }
                        cont2++;
                    }
                    
                    int cont3 = 1;
                    int cont4 = cont2 + 2;
                    while (cont3 != 0) {
                        if (tokens[cont4].equals("(")) {
                            cont3++;
                        } else if (tokens[cont4].equals(")")) {
                            cont3--;
                        }
                        cont4++;
                    }
                    
                    expresionSi(i+1,cont2,tokens,contador);//llamar a funcion if
                    expresionWhile(i+1,cont2-1,cont2+3,cont4-1,tokens,contador);//llamar a funcion while
                    i=cont4;

                }
                else{
                    int indiceNL = buscarSigNL(i,tokens);
                    int cont3 = 1;
                    int cont4 = indiceNL + 2;
                    while (cont3 != 0) {
                        if (tokens[cont4].equals("(")) {
                            cont3++;
                        } else if (tokens[cont4].equals(")")) {
                            cont3--;
                        }
                        cont4++;
                    }
                    
                    expresionSi(i+1,indiceNL,tokens,contador);
                    expresionWhile(i+1,indiceNL-1,indiceNL+3,cont4-1,tokens,contador);//llamar a funcion while
                    i=cont4;
                }
                
            }

        }
        
        if(!error){
            System.out.println("SUCCESS, Linea Valida semanticamente");
            _comunicador.interpretar(ejecutable);
        }

    }
    
    //**********************************************************************************************************************************************
    
    private void expresionSi(int a, int j, String[] tokens,int contador){

        for (int i = a; i < j; i++) {//ciclo para armar la tabla de simbolos
            if (tokens[i].equals("newline")) {
                contador++;
            } 
            
            else if (tokens[i].equals("=")) {
                if (buscarSimbolo(tokens[i - 1]) == -1) {
                    _ListaErrores.insertar("Analisis semantico. Error: Variable no declarada. En linea: " + String.valueOf(contador));
                } else if (tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[4].equals("1") && !tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[5].equals("0")) {
                    _ListaErrores.insertar("Analisis semantico. Error: Reasignaccion a una constante. En linea: " + String.valueOf(contador));
                } else {
                    int indiceNL = buscarSigNL(i, tokens);
                    String tipo = tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[2];
                    if (indiceNL == i + 2) {

                        if (buscarSimbolo(tokens[i + 1]) != -1) {
                            if (tablaSimbolos.get(buscarSimbolo(tokens[i + 1]))[2].equals(tipo)) {
                                tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[3] = tablaSimbolos.get(buscarSimbolo(tokens[i + 1]))[3];
                            } else {
                                _ListaErrores.insertar("Analisis semantico. Error: asignacion de tipos incompatibles. En linea: " + String.valueOf(contador));
                            }

                        } else if (tipo.equals("char")) {
                            if (tokens[i + 1].length() == 1) {
                                tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[3] = tokens[i + 1];
                            } else {
                                _ListaErrores.insertar("Analisis semantico. Error: asignacion de tipos incompatibles. En linea: " + String.valueOf(contador));
                            }
                        } else if (tipo.equals("string")) {
                            tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[3] = tokens[i + 1];
                        } else if (tipo.equals("int")) {
                            if (tokens[i + 1].matches("[0-9]+")) {/*[0-9]*.?[0-9]**/

                                tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[3] = tokens[i + 1];
                            } else {
                                _ListaErrores.insertar("Analisis semantico. Error: asignacion de tipos incompatibles. En linea: " + String.valueOf(contador));
                            }
                        } else {
                            if (tokens[i + 1].matches("[0-9]*.?[0-9]")) {
                                tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[3] = tokens[i + 1];
                            } else {
                                _ListaErrores.insertar("Analisis semantico. Error: asignacion de tipos incompatibles. En linea: " + String.valueOf(contador));
                            }

                        }
                    } else {
                        float temp = expresionSuma(i + 1, indiceNL - 1, tokens, contador);
                        if (tipo.equals("float")) {
                            tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[3] = String.valueOf(temp);
                        } else if (tipo.equals("int")) {
                            int temp2 = Float.floatToIntBits(temp);
                            tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[3] = String.valueOf(temp2);
                        }
                    }
                    i += indiceNL - i - 1;
                }

            } 
            else if (tokens[i].equals("mover")) {
                int indiceNL = buscarSigNL(i, tokens);
                if (expresionSuma(i + 2, indiceNL, tokens, contador) > 120) {
                    error = true;
                    _ListaErrores.insertar("Analisis semantico. Error: tiempo mayor al permitido. En linea: " + String.valueOf(contador));
                } else {
                    ejecutable.add(tokens[i + 1]);
                    ejecutable.add(String.valueOf(expresionSuma(i + 2, indiceNL, tokens, contador)));
                }
                i += indiceNL - i - 1;
            } 
            else if (tokens[i].equals("si")) {
                int cont1 = 1;
                int cont2 = i + 2;
                while (cont1 != 0) {
                    if (tokens[cont2].equals("(")) {
                        cont1++;
                    } else if (tokens[cont2].equals(")")) {
                        cont1--;
                    }
                    cont2++;
                }
                if (expresionCondicion(i + 1, i + cont2, tokens, contador)) {//si se cumple la condicion

                    if (tokens[cont2 + 2].equals("{")) {
                        int cont3 = 1;
                        int cont4 = cont2 + 3;
                        while (cont3 != 0) {
                            if (tokens[cont4].equals("{")) {
                                cont3++;
                            } else if (tokens[cont4].equals("}")) {
                                cont3--;
                            }
                            cont4++;
                        }
                        expresionSi(cont2 + 3, cont4 - 1, tokens, contador);
                        i = cont4;

                    } else {
                        int indiceNL = buscarSigNL(i, tokens);
                        i = indiceNL - 1;
                        expresionSi(cont2 + 3, indiceNL - 1, tokens, contador);
                    }

                    /*saltarse el sino*/
                } else {// no se cumple la condicion

                    if (tokens[cont2 + 2].equals("{")) {
                        int cont3 = 1;
                        int cont4 = cont2 + 3;
                        while (cont3 != 0) {
                            if (tokens[cont4].equals("{")) {
                                cont3++;
                            } else if (tokens[cont4].equals("}")) {
                                cont3--;
                            }
                            cont4++;
                        }
                        if (tokens[cont4].equals("sino")) {
                            /*llama a declaracionesAnidadas()*/
                            if (tokens[cont4 + 1].equals("{")) {
                                int cont5 = 1;
                                int cont6 = cont4 + 1;
                                while (cont5 != 0) {
                                    if (tokens[cont6].equals("{")) {
                                        cont5++;
                                    } else if (tokens[cont6].equals("}")) {
                                        cont5--;
                                    }
                                    cont6++;
                                }
                                expresionSi(cont4 + 2, cont2 - 1, tokens, contador);
                                i = cont4;

                            } else {
                                int indiceNL = buscarSigNL(i, tokens);
                                i = indiceNL - 1;
                                expresionSi(cont2 + 3, indiceNL - 1, tokens, contador);
                            }


                       } 
                        else{
                        i=cont4;
                        }
                    }
                    else {
                        int indiceNL = buscarSigNL(i,tokens);
                        if(tokens[indiceNL].equals("sino")){
                            /*llamar funcion del sino*/
                           if (tokens[indiceNL + 1].equals("{")) {
                                int cont5 = 1;
                                int cont6 = indiceNL + 1;
                                while (cont5 != 0) {
                                    if (tokens[cont6].equals("{")) {
                                        cont5++;
                                    } else if (tokens[cont6].equals("}")) {
                                        cont5--;
                                    }
                                    cont6++;
                                }
                                expresionSi(indiceNL + 2, cont2 - 1, tokens, contador);
                                i = indiceNL;

                            } else {
                                int indiceNL2 = buscarSigNL(i, tokens);
                                i = indiceNL2 - 1;
                                expresionSi(cont2 + 3, indiceNL2 - 1, tokens, contador);
                            }
                        }
                        else{
                        i=indiceNL-1;
                        }
                        
                    }
                    

                }
            }
            else if (tokens[i].equals("mientras")) {
                int cont1 = 1;
                int cont2 = i + 2;
                while (cont1 != 0) {
                    if (tokens[cont2].equals("(")) {
                        cont1++;
                    } else if (tokens[cont2].equals(")")) {
                        cont1--;
                    }
                    cont2++;
                }
                if (tokens[cont2+2].equals("{")) {
                    int cont3 = 1;
                    int cont4 = cont2 + 3;
                    while (cont3 != 0) {
                        if (tokens[cont4].equals("{")) {
                            cont3++;
                        } else if (tokens[cont4].equals("}")) {
                            cont3--;
                        }
                        cont4++;
                    }
                    expresionWhile(cont2+3,cont4-1,i+3,cont2-1,tokens,contador);//llamar a funcion while
                    i=cont4;

                }
                else{
                    int indiceNL = buscarSigNL(i,tokens);
                    i=indiceNL -1;
                    expresionWhile(cont2+3,indiceNL-1,i+3,cont2-1,tokens,contador);//llamar a funcion while
                    //mover indice
                }


            }
            else if(tokens[i].equals("haga")){
                if (tokens[i+1].equals("{")) {
                    int cont1 = 1;
                    int cont2 = i + 2;
                    while (cont1 != 0) {
                        if (tokens[cont2].equals("{")) {
                            cont1++;
                        } else if (tokens[cont2].equals("}")) {
                            cont1--;
                        }
                        cont2++;
                    }
                    
                    int cont3 = 1;
                    int cont4 = cont2 + 2;
                    while (cont3 != 0) {
                        if (tokens[cont4].equals("(")) {
                            cont3++;
                        } else if (tokens[cont4].equals(")")) {
                            cont3--;
                        }
                        cont4++;
                    }
                    
                    expresionSi(i+1,cont2,tokens,contador);//llamar a funcion if
                    expresionWhile(i+1,cont2-1,cont2+3,cont4-1,tokens,contador);//llamar a funcion while
                    i=cont4;

                }
                else{
                    int indiceNL = buscarSigNL(i,tokens);
                    int cont3 = 1;
                    int cont4 = indiceNL + 2;
                    while (cont3 != 0) {
                        if (tokens[cont4].equals("(")) {
                            cont3++;
                        } else if (tokens[cont4].equals(")")) {
                            cont3--;
                        }
                        cont4++;
                    }
                    
                    expresionSi(i+1,indiceNL,tokens,contador);
                    expresionWhile(i+1,indiceNL-1,indiceNL+3,cont4-1,tokens,contador);//llamar a funcion while
                    i=cont4;
                }
                
            }

        }
    }
    
//**********************************************************************************************************************************************
    
    private void expresionWhile(int a, int j,int Icondicion,int Fcondicion, String[] tokens, int contador){
        while(expresionCondicion(Icondicion,Fcondicion,tokens,contador)){
        for (int i = a; i < j; i++) {//ciclo para armar la tabla de simbolos
            if (tokens[i].equals("newline")) {
                contador++;
            } 
            
            else if (tokens[i].equals("=")) {
                if (buscarSimbolo(tokens[i - 1]) == -1) {
                    _ListaErrores.insertar("Analisis semantico. Error: Variable no declarada. En linea: " + String.valueOf(contador));
                } else if (tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[4].equals("1") && !tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[5].equals("0")) {
                    _ListaErrores.insertar("Analisis semantico. Error: Reasignaccion a una constante. En linea: " + String.valueOf(contador));
                } else {
                    int indiceNL = buscarSigNL(i, tokens);
                    String tipo = tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[2];
                    if (indiceNL == i + 2) {

                        if (buscarSimbolo(tokens[i + 1]) != -1) {
                            if (tablaSimbolos.get(buscarSimbolo(tokens[i + 1]))[2].equals(tipo)) {
                                tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[3] = tablaSimbolos.get(buscarSimbolo(tokens[i + 1]))[3];
                            } else {
                                _ListaErrores.insertar("Analisis semantico. Error: asignacion de tipos incompatibles. En linea: " + String.valueOf(contador));
                            }

                        } else if (tipo.equals("char")) {
                            if (tokens[i + 1].length() == 1) {
                                tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[3] = tokens[i + 1];
                            } else {
                                _ListaErrores.insertar("Analisis semantico. Error: asignacion de tipos incompatibles. En linea: " + String.valueOf(contador));
                            }
                        } else if (tipo.equals("string")) {
                            tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[3] = tokens[i + 1];
                        } else if (tipo.equals("int")) {
                            if (tokens[i + 1].matches("[0-9]+")) {/*[0-9]*.?[0-9]**/

                                tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[3] = tokens[i + 1];
                            } else {
                                _ListaErrores.insertar("Analisis semantico. Error: asignacion de tipos incompatibles. En linea: " + String.valueOf(contador));
                            }
                        } else {
                            if (tokens[i + 1].matches("[0-9]*.?[0-9]")) {
                                tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[3] = tokens[i + 1];
                            } else {
                                _ListaErrores.insertar("Analisis semantico. Error: asignacion de tipos incompatibles. En linea: " + String.valueOf(contador));
                            }

                        }
                    } else {
                        float temp = expresionSuma(i + 1, indiceNL - 1, tokens, contador);
                        if (tipo.equals("float")) {
                            tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[3] = String.valueOf(temp);
                        } else if (tipo.equals("int")) {
                            int temp2 = Float.floatToIntBits(temp);
                            tablaSimbolos.get(buscarSimbolo(tokens[i - 1]))[3] = String.valueOf(temp2);
                        }
                    }
                    i += indiceNL - i - 1;
                }

            } 
            else if (tokens[i].equals("mover")) {
                int indiceNL = buscarSigNL(i, tokens);
                if (expresionSuma(i + 2, indiceNL, tokens, contador) > 120) {
                    error = true;
                    _ListaErrores.insertar("Analisis semantico. Error: tiempo mayor al permitido. En linea: " + String.valueOf(contador));
                } else {
                    ejecutable.add(tokens[i + 1]);
                    ejecutable.add(String.valueOf(expresionSuma(i + 2, indiceNL, tokens, contador)));
                }
                i += indiceNL - i - 1;
            } 
            else if (tokens[i].equals("si")) {
                int cont1 = 1;
                int cont2 = i + 2;
                while (cont1 != 0) {
                    if (tokens[cont2].equals("(")) {
                        cont1++;
                    } else if (tokens[cont2].equals(")")) {
                        cont1--;
                    }
                    cont2++;
                }
                if (expresionCondicion(i + 1, i + cont2, tokens, contador)) {//si se cumple la condicion

                    if (tokens[cont2 + 2].equals("{")) {
                        int cont3 = 1;
                        int cont4 = cont2 + 3;
                        while (cont3 != 0) {
                            if (tokens[cont4].equals("{")) {
                                cont3++;
                            } else if (tokens[cont4].equals("}")) {
                                cont3--;
                            }
                            cont4++;
                        }
                        expresionSi(cont2 + 3, cont4 - 1, tokens, contador);
                        i = cont4;

                    } else {
                        int indiceNL = buscarSigNL(i, tokens);
                        i = indiceNL - 1;
                        expresionSi(cont2 + 3, indiceNL - 1, tokens, contador);
                    }

                    /*saltarse el sino*/
                } else {// no se cumple la condicion

                    if (tokens[cont2 + 2].equals("{")) {
                        int cont3 = 1;
                        int cont4 = cont2 + 3;
                        while (cont3 != 0) {
                            if (tokens[cont4].equals("{")) {
                                cont3++;
                            } else if (tokens[cont4].equals("}")) {
                                cont3--;
                            }
                            cont4++;
                        }
                        if (tokens[cont4].equals("sino")) {
                            /*llama a declaracionesAnidadas()*/
                            if (tokens[cont4 + 1].equals("{")) {
                                int cont5 = 1;
                                int cont6 = cont4 + 1;
                                while (cont5 != 0) {
                                    if (tokens[cont6].equals("{")) {
                                        cont5++;
                                    } else if (tokens[cont6].equals("}")) {
                                        cont5--;
                                    }
                                    cont6++;
                                }
                                expresionSi(cont4 + 2, cont2 - 1, tokens, contador);
                                i = cont4;

                            } else {
                                int indiceNL = buscarSigNL(i, tokens);
                                i = indiceNL - 1;
                                expresionSi(cont2 + 3, indiceNL - 1, tokens, contador);
                            }


                       } 
                        else{
                        i=cont4;
                        }
                    }
                    else {
                        int indiceNL = buscarSigNL(i,tokens);
                        if(tokens[indiceNL].equals("sino")){
                            /*llamar funcion del sino*/
                           if (tokens[indiceNL + 1].equals("{")) {
                                int cont5 = 1;
                                int cont6 = indiceNL + 1;
                                while (cont5 != 0) {
                                    if (tokens[cont6].equals("{")) {
                                        cont5++;
                                    } else if (tokens[cont6].equals("}")) {
                                        cont5--;
                                    }
                                    cont6++;
                                }
                                expresionSi(indiceNL + 2, cont2 - 1, tokens, contador);
                                i = indiceNL;

                            } else {
                                int indiceNL2 = buscarSigNL(i, tokens);
                                i = indiceNL2 - 1;
                                expresionSi(cont2 + 3, indiceNL2 - 1, tokens, contador);
                            }
                        }
                        else{
                        i=indiceNL-1;
                        }
                        
                    }
                    

                }
            }
            else if (tokens[i].equals("mientras")) {
                int cont1 = 1;
                int cont2 = i + 2;
                while (cont1 != 0) {
                    if (tokens[cont2].equals("(")) {
                        cont1++;
                    } else if (tokens[cont2].equals(")")) {
                        cont1--;
                    }
                    cont2++;
                }
                if (tokens[cont2+2].equals("{")) {
                    int cont3 = 1;
                    int cont4 = cont2 + 3;
                    while (cont3 != 0) {
                        if (tokens[cont4].equals("{")) {
                            cont3++;
                        } else if (tokens[cont4].equals("}")) {
                            cont3--;
                        }
                        cont4++;
                    }
                    expresionWhile(cont2+3,cont4-1,i+3,cont2-1,tokens,contador);//llamar a funcion while
                    i=cont4;

                }
                else{
                    int indiceNL = buscarSigNL(i,tokens);
                    i=indiceNL -1;
                    expresionWhile(cont2+3,indiceNL-1,i+3,cont2-1,tokens,contador);//llamar a funcion while
                    //mover indice
                }


            }
            else if(tokens[i].equals("haga")){
                if (tokens[i+1].equals("{")) {
                    int cont1 = 1;
                    int cont2 = i + 2;
                    while (cont1 != 0) {
                        if (tokens[cont2].equals("{")) {
                            cont1++;
                        } else if (tokens[cont2].equals("}")) {
                            cont1--;
                        }
                        cont2++;
                    }
                    
                    int cont3 = 1;
                    int cont4 = cont2 + 2;
                    while (cont3 != 0) {
                        if (tokens[cont4].equals("(")) {
                            cont3++;
                        } else if (tokens[cont4].equals(")")) {
                            cont3--;
                        }
                        cont4++;
                    }
                    
                    expresionSi(i+1,cont2,tokens,contador);//llamar a funcion if
                    expresionWhile(i+1,cont2-1,cont2+3,cont4-1,tokens,contador);//llamar a funcion while
                    i=cont4;

                }
                else{
                    int indiceNL = buscarSigNL(i,tokens);
                    int cont3 = 1;
                    int cont4 = indiceNL + 2;
                    while (cont3 != 0) {
                        if (tokens[cont4].equals("(")) {
                            cont3++;
                        } else if (tokens[cont4].equals(")")) {
                            cont3--;
                        }
                        cont4++;
                    }
                    
                    expresionSi(i+1,indiceNL,tokens,contador);
                    expresionWhile(i+1,indiceNL-1,indiceNL+3,cont4-1,tokens,contador);//llamar a funcion while
                    i=cont4;
                }
                
            }

        }
        }
        
    }
    
    
//**********************************************************************************************************************************************

    private boolean expresionCondicion(int i, int j, String[] t, int contador) {
        if (buscarOpRel(i, t, "<") != -1) {
            return expresionSuma(i, buscarOpRel(i, t, "<") - 1, t, contador) < expresionSuma(buscarOpRel(i, t, "<") + 1, j, t, contador);
        } else if (buscarOpRel(i, t, "<=") != -1) {
            return expresionSuma(i, buscarOpRel(i, t, "<=") - 1, t, contador) <= expresionSuma(buscarOpRel(i, t, "<=") + 1, j, t, contador);
        } else if (buscarOpRel(i, t, ">") != -1) {
            return expresionSuma(i, buscarOpRel(i, t, ">") - 1, t, contador) > expresionSuma(buscarOpRel(i, t, ">") + 1, j, t, contador);
        } else if (buscarOpRel(i, t, ">=") != -1) {
            return expresionSuma(i, buscarOpRel(i, t, ">=") - 1, t, contador) >= expresionSuma(buscarOpRel(i, t, ">=") + 1, j, t, contador);
        } else if (buscarOpRel(i, t, "==") != -1) {
            return expresionSuma(i, buscarOpRel(i, t, "==") - 1, t, contador) == expresionSuma(buscarOpRel(i, t, "==") + 1, j, t, contador);
        } else/* (buscarOpRel(i,t,"!=")!=-1)*/ {
            return expresionSuma(i, buscarOpRel(i, t, "!=") - 1, t, contador) != expresionSuma(buscarOpRel(i, t, "!=") + 1, j, t, contador);
        }
    }

    private int buscarOpRel(int n, String[] t, String op) {
        int pos = n;
        while (!t[pos].equals("newline")) {
            if (t[pos].equals(op)) {
                return pos;
            }
            pos++;
        }
        return -1;
    }

    private float expresionSuma(int i, int j, String[] t, int contador) {
        float op1;

        if (i == j) {
            if (t[i].matches("[0-9]*.?[0-9]*")) {
                return Float.valueOf(t[i]);
            } else {
                if (buscarSimbolo(t[i]) != -1) {
                    if ((tablaSimbolos.get(buscarSimbolo(t[i]))[2].equals("int")
                            || (tablaSimbolos.get(buscarSimbolo(t[i]))[2].equals("float")))
                            && !tablaSimbolos.get(buscarSimbolo(t[i]))[5].equals("0")) {
                        return Float.valueOf(tablaSimbolos.get(buscarSimbolo(t[i]))[3]);
                    } else {
                        _ListaErrores.insertar("Analisis semantico. Error: operacion invalida. en linea: " + String.valueOf(contador));
                        error = true;
                        return 0;
                    }
                } else {
                    _ListaErrores.insertar("Analisis semantico. Error: variable no inicializada. en linea: " + String.valueOf(contador));
                    error = true;
                    return 0;
                }
            }
        } else if (t[i].equals("(")) {
            int cont1 = 1;
            int cont2 = i;
            while (cont1 != 0) {

                if (t[cont2].equals("(")) {
                    cont1++;
                } else if (t[cont2].equals(")")) {
                    cont1--;
                }
                cont2++;
            }

            if (t[cont2 + 1].equals("+")) {
                return (expresionSuma(i, cont2, t, contador) + expresionSuma(cont2 + 2, j, t, contador));
            } else if (t[cont2 + 1].equals("-")) {
                return (expresionSuma(i, cont2, t, contador) - expresionSuma(cont2 + 2, j, t, contador));
            } else if (t[cont2 + 1].equals("/")) {
                return (expresionSuma(i, cont2, t, contador) / expresionSuma(cont2 + 2, j, t, contador));
            } else {// multiplicacion
                return (expresionSuma(i, cont2, t, contador) * expresionSuma(cont2 + 2, j, t, contador));
            }

            //return expresionSuma(i,cont2,t,contador);   
        } else {
            if (t[i].matches("[0-9]*.?[0-9]*")) {
                op1 = Float.valueOf(t[i]);
                if (t[i + 1].equals("+")) {
                    return (op1 + expresionSuma(i + 2, j, t, contador));
                } else if (t[i + 1].equals("-")) {
                    return (op1 - expresionSuma(i + 2, j, t, contador));
                } else if (t[i + 1].equals("/")) {
                    return (op1 / expresionSuma(i + 2, j, t, contador));
                } else {// multiplicacion
                    return (op1 * expresionSuma(i + 2, j, t, contador));
                }

            } else {
                if (buscarSimbolo(t[i]) != -1) {
                    if ((tablaSimbolos.get(buscarSimbolo(t[i]))[2].equals("int")
                            || (tablaSimbolos.get(buscarSimbolo(t[i]))[2].equals("float")))
                            && !tablaSimbolos.get(buscarSimbolo(t[i]))[5].equals("0")) {
                        op1 = Float.valueOf(tablaSimbolos.get(buscarSimbolo(t[i]))[3]);

                        if (t[i + 1].equals("+")) {
                            return (op1 + expresionSuma(i + 2, j, t, contador));
                        } else if (t[i + 1].equals("-")) {
                            return (op1 - expresionSuma(i + 2, j, t, contador));
                        } else if (t[i + 1].equals("/")) {
                            return (op1 / expresionSuma(i + 2, j, t, contador));
                        } else {// multiplicacion
                            return (op1 * expresionSuma(i + 2, j, t, contador));
                        }

                    } else {
                        _ListaErrores.insertar("Analisis semantico. Error: operacion invalida. en linea: " + String.valueOf(contador));
                        error = true;
                        return 0;
                    }
                } else {
                    _ListaErrores.insertar("Analisis semantico. Error: variable no inicializada. en linea: " + String.valueOf(contador));
                    error = true;
                    return 0;
                }
            }

        }

    }

    private int buscarSimbolo(String nombre) {
        for (int i = 0; i < tablaSimbolos.size(); i++) {
            if (tablaSimbolos.get(i)[1].equals(nombre)) {
                return i;
            }
        }
        return -1;
    }

    private int buscarSigNL(int n, String[] t) {
        int pos = n;
        while (pos < t.length) {
            if (t[pos].equals("newline")) {
                return pos;
            }
        }
        return pos;
    }

    /**
     * **********************************************************************************************************************
     */
    /**
     * **********************************************************************************************************************
     */
    /**
     * **********************************************************************************************************************
     */
    /**
     * **********************************************************************************************************************
     */
    /**
     * **********************************************************************************************************************
     */
    /**
     * **********************************************************************************************************************
     */
    /**
     * **********************************************************************************************************************
     */
    /**
     * **********************************************************************************************************************
     */
    /**
     * **********************************************************************************************************************
     */
    public void AnalisisSintactico(String pEntrada) throws InterruptedException, IOException {//analisador sintactico
        int contador=1;
        AnalizadorLexico lexico = new AnalizadorLexico();
        lexico.analizar(pEntrada);
        String entrada = lexico.getResultado();
        _ListaErrores.sumarListas(lexico.getListaErrores());
        NodoString temp = _ListaErrores.getHead();
        while(temp!=null){
            if(temp.getStr()!=null){
                logFileLexico(temp.getStr());
            }
        }
        //int contador = 0;
        String pila = _producciones.getHead().getIzq() + " $";
        
        while (pila.compareTo("$") != 0) {
            String terminal = extraerTerminal(entrada);
            logFileSintactico("***************************************************************************************");
            logFileSintactico(pila);
            logFileSintactico(entrada);
            
            if(terminal.equals("newline")){
                contador++;
            }
            char charAt2 = pila.substring(0, 1).charAt(0);
            if (terminal.compareTo("-1") == 0) {
                _ListaErrores.insertar("Analisis Sintactico. Error: terminal desconocido. En linea: " + String.valueOf(contador));
                logFileSintactico("Analisis Sintactico. Error: terminal desconocido. En linea: " + String.valueOf(contador));
                break;
            } else if ((pila.substring(0, 1).compareTo(pila.substring(0, 1).toUpperCase()) == 0) && Character.isLetter(charAt2)) {
                int indiceEspacio = pila.indexOf(" ", 0);
                String palabra = pila.substring(0, indiceEspacio);
                String tmp = _tabla.buscarEnPos(_tabla.getFilas().buscarElem(palabra), _tabla.getColumnas().buscarElem(terminal));
                if (tmp.compareTo("♥") == 0) {
                    _ListaErrores.insertar("Analisis Sintactico. Error: casilla nula en la tabla. En linea: " + String.valueOf(contador));
                    logFileSintactico("Analisis Sintactico. Error: terminal desconocido. En linea: " + String.valueOf(contador));
                    break;
                } else {
                    int indice = tmp.indexOf("->");
                    tmp = tmp.substring(indice + 2);
                    if (tmp.compareTo("ñ ") == 0) {
                        pila = pila.substring(indiceEspacio + 1);
                    } else {
                        String palabra2 = palabra + " ";
                        pila = pila.replaceFirst(palabra2, tmp);
                    }
                }
            } else {
                String terminalEnPila = extraerTerminal(pila);
                if (terminalEnPila.compareTo(terminal) == 0) {
                    entrada = entrada.substring(terminal.length() + 1);
                    pila = pila.substring(terminal.length() + 1);
                } else {
                    _ListaErrores.insertar("Analisis Sintactico. Error: comparacion pila-entrada fallida. En linea: " + String.valueOf(contador));
                    logFileSintactico("Analisis Sintactico. Error: comparacion pila-entrada fallida. En linea: " + String.valueOf(contador));
                    break;
                }
            }
        }
        if (pila.compareTo("$") == 0 && entrada.equals("newline ")) {
            System.out.println("SUCCESS, Linea Valida sintacticamente");
             logFileSintactico("SUCCESS, Linea Valida sintacticamente");
            
            AnalisisSemantico(pEntrada);
        } else {
            
            _ListaErrores.insertar("Analisis Sintactico. Error: valor despues de '$' en la entrada. En linea: " + String.valueOf(contador));
            logFileSintactico("SUCCESS, Linea Valida sintacticamente");
            
        }

    }

    public String extraerTerminal(String pString) {
        NodoString temporal = _tabla.getColumnas().getHead();
        int indice = pString.indexOf(" ",0);
        String comparando = pString.substring(0, indice);
        while (temporal != null) {
            if (comparando.equals(temporal.getStr())) {
                return temporal.getStr();
            }
            temporal = temporal.getNext();
        }
        return "-1";
    }

//    public void leerPrueba() throws IOException {
//        BufferedReader br = new BufferedReader(new FileReader("Evaluaciones.txt"));
//        try {
//            StringBuilder sb = new StringBuilder();
//            String line = br.readLine();
//
//            while (line != null) {
//                sb.append(line);
//                sb.append(System.lineSeparator());
//                logFile(ejecutarPrueba(line));
//                line = br.readLine();
//
//            }
//        } finally {
//            br.close();
//        }
//    }
    public void logFileLexico(String pLog) throws FileNotFoundException, IOException {
        File log = new File("OutputAnálisisLéxico.txt");
        try {
            if (log.exists() == false) {
                System.out.println("Se ha creado un nuevo archivo para registros.");
                log.createNewFile();
            }
            try (PrintWriter out = new PrintWriter(new FileWriter(log, true))) {
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ssa");
                String formattedDate = sdf.format(date);
                out.append(formattedDate + "-" + pLog + "\r\n");
            }
        } catch (IOException e) {
            System.out.println("ERROR AL AÑADIR REGISTRO!!");
        }
    }
    
    public void logFileSintactico(String pLog) throws FileNotFoundException, IOException {
        File log = new File("OutputAnálisisSintáctico.txt");
        try {
            if (log.exists() == false) {
                System.out.println("Se ha creado un nuevo archivo para registros.");
                log.createNewFile();
            }
            try (PrintWriter out = new PrintWriter(new FileWriter(log, true))) {
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ssa");
                String formattedDate = sdf.format(date);
                out.append(formattedDate + "-" + pLog + "\r\n");
            }
        } catch (IOException e) {
            System.out.println("ERROR AL AÑADIR REGISTRO!!");
        }
    }
    
    public void logFileSemantico(String pLog) throws FileNotFoundException, IOException {
        File log = new File("OutputAnálisisSemántico.txt");
        try {
            if (log.exists() == false) {
                System.out.println("Se ha creado un nuevo archivo para registros.");
                log.createNewFile();
            }
            try (PrintWriter out = new PrintWriter(new FileWriter(log, true))) {
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ssa");
                String formattedDate = sdf.format(date);
                out.append(formattedDate + "-" + pLog + "\r\n");
            }
        } catch (IOException e) {
            System.out.println("ERROR AL AÑADIR REGISTRO!!");
        }
    }

    public void insertarHeaders() {
        ListaString temporal = new ListaString();
        for (int i = 0; i < _producciones.getTam(); i++) {
            temporal.insertar(_producciones.buscarPos(i).getIzq());
        }
        getTabla().setFilas(temporal); //getTabla().setColumnas(temporal);
        ListaString columnas = new ListaString();
        Produccion temporal2 = _producciones.getHead();
        while (temporal2 != null) {
            NodoString temporal3 = temporal2.getDer().getHead();
            while (temporal3 != null) {
                String ladoDerecho = temporal3.getStr();
                while (ladoDerecho.length() > 0) {

                    String[] partesLadoDerecho = ladoDerecho.split("|");
                    char charAt2 = partesLadoDerecho[0].charAt(0);
                    if ((partesLadoDerecho[0].compareTo(partesLadoDerecho[0].toUpperCase()) != 0)
                            && (Character.isLetter(charAt2) == true) && (partesLadoDerecho[0].compareTo("ñ") != 0)
                            || (Character.isLetter(charAt2) == false && partesLadoDerecho[0].compareTo("ñ") != 0)) {

                        int indiceEspacio = ladoDerecho.indexOf(" ", 0);
                        String terminal = ladoDerecho.substring(0, indiceEspacio);
                        if (columnas.buscarElem(terminal) == -1) {
                            //System.out.println(terminal);
                            columnas.insertar(terminal);
                        }
                        ladoDerecho = ladoDerecho.substring(terminal.length() + 1);
                    } else {
                        int indiceEspacio = ladoDerecho.indexOf(" ", 0);
                        String terminal = ladoDerecho.substring(0, indiceEspacio);
                        ladoDerecho = ladoDerecho.substring(terminal.length() + 1);
                    }

                }
                temporal3 = temporal3.getNext();
            }

            temporal2 = temporal2.getNext();
        }
        getTabla().setColumnas(columnas);

    }

//
//    public void insertarHeaders2() {
//        ListaString temporal = new ListaString();
//        for (int i = 0; i < _producciones.getTam(); i++) {
//            temporal.insertar(_producciones.buscarPos(i).getIzq());
//        }
//        getTabla().setFilas(temporal); //getTabla().setColumnas(temporal);
//        ListaString filas = new ListaString();
//        Produccion temporal2 = _producciones.getHead();
//        while (temporal2 != null) {
//            NodoString temporal3 = temporal2.getDer().getHead();
//            while (temporal3 != null) {
//                int k = 1;
//                String ladoDerecho = temporal3.getStr();
//                String[] partesLadoDerecho = ladoDerecho.split("|");
//                while (k < partesLadoDerecho.length) {
//                    char charAt2 = partesLadoDerecho[k].charAt(0);
//                    if ((partesLadoDerecho[k].compareTo(partesLadoDerecho[k].toUpperCase()) != 0 && Character.isLetter(charAt2) == true && partesLadoDerecho[k].compareTo("ñ") != 0) 
//                            || (Character.isLetter(charAt2) == false && partesLadoDerecho[k].compareTo("ñ") != 0)) {
//                        if ((k + 1 < partesLadoDerecho.length)
//                                && ((partesLadoDerecho[k + 1].compareTo(partesLadoDerecho[k + 1].toUpperCase()) == 0
//                                && Character.isLetter(charAt2) == true && partesLadoDerecho[k + 1].compareTo("ñ") != 0)
//                                || (Character.isLetter(charAt2) == false && partesLadoDerecho[k + 1].compareTo("ñ") != 0))) {
//                            if (filas.buscarElem(partesLadoDerecho[k]) == -1) {
//                                filas.insertar(partesLadoDerecho[k]);
//                            }
//                            k++;
//                        } else {
//                            int g = k;
//                            String terminalDeVariosCaracteres = "";
//                            while (g < partesLadoDerecho.length
//                                    && ((partesLadoDerecho[g].compareTo(partesLadoDerecho[g].toUpperCase()) != 0
//                                    && Character.isLetter(charAt2) == true && partesLadoDerecho[g].compareTo("ñ") != 0)
//                                    || (Character.isLetter(charAt2) == false && partesLadoDerecho[g].compareTo("ñ") != 0))) {
//
//                                terminalDeVariosCaracteres = terminalDeVariosCaracteres + partesLadoDerecho[g];
//                                g++;
//                            }
//                            k = g + 1;
//                            if (filas.buscarElem(terminalDeVariosCaracteres) == -1) {
//                                filas.insertar(terminalDeVariosCaracteres);
//                            }
//                        }
//                    } else {
//                        k++;
//                    }
//
//                }
//                temporal3 = temporal3.getNext();
//            }
//            temporal2 = temporal2.getNext();
//        }
//        filas.insertar("$");
//        getTabla().setColumnas(filas); //getTabla().setFilas(filas);
//    }
    public void calcularPNR() {
        _tabla.llenarTabla(_tabla.getFilas().getTam(), _tabla.getColumnas().getTam()); //_tabla.llenarTabla(_tabla.getColumnas().getTam(), _tabla.getFilas().getTam()); 
        Produccion temporal = _producciones.getHead();

        while (temporal != null) {
            NodoString temporal2 = temporal.getDer().getHead();
            while (temporal2 != null) {
                if (temporal2.getStr().compareTo("ñ ") == 0) {
                    _prodCalculadas = new ListaString();//se resetea el contador para la recursividad en siguiente
                    ListaString resultadoTemporal = getSiguiente(temporal.getIzq());
                       //ListaString resultadoTemporal = new ListaString(); 
                    String stringTabla = "";
                    for (int t = 0; t < resultadoTemporal.getTam(); t++) {
                        stringTabla = stringTabla + "," + resultadoTemporal.buscarPos(t);
                    }
                    stringTabla = stringTabla.substring(1);
                    getResumenTablaPNR().insertar("Se calcula el SIGUIENTE de " + temporal.getIzq() + ":" + stringTabla);

                    ListaString parteDer = new ListaString();
                    parteDer.insertar(temporal2.getStr());
                    //Produccion prod = new Produccion(temporal.getIzq(), parteDer);
                    while (resultadoTemporal.getTam() > 0) {
                        _tabla.insertarProduccion2_0(temporal.getIzq(), parteDer, _tabla.getFilas().buscarElem(temporal.getIzq()), _tabla.getColumnas().buscarElem(resultadoTemporal.getHead().getStr()));
                        resultadoTemporal.borrarElem(resultadoTemporal.getHead().getStr());
                    }
                    //Insertamos en la tabla
                } 
                else {
                    ListaString resultadoTemporal = getPrimeroDer(temporal2.getStr());

                    String stringTabla = "";
                    for (int t = 0; t < resultadoTemporal.getTam(); t++) {
                        stringTabla = stringTabla + "," + resultadoTemporal.buscarPos(t);
                    }
                    stringTabla = stringTabla.substring(1);
                    getResumenTablaPNR().insertar("Se calcula el PRIMERO de " + temporal2.getStr() + ":" + stringTabla);

                    ListaString parteDer = new ListaString();
                    parteDer.insertar(temporal2.getStr());
                    while (resultadoTemporal.getTam() > 0) {
                        _tabla.insertarProduccion2_0(temporal.getIzq(), parteDer, _tabla.getFilas().buscarElem(temporal.getIzq()), _tabla.getColumnas().buscarElem(resultadoTemporal.getHead().getStr()));

                        resultadoTemporal.borrarElem(resultadoTemporal.getHead().getStr());
                    }
                    //Insertamos en la tabla
                }
                temporal2 = temporal2.getNext();
            }
            temporal = temporal.getNext();
        }

    }

//    public String identificador(String pEntrada) {
//        char charAt1 = pEntrada.charAt(0);
//        boolean flagLetra = false;
//        if(!Character.isAlphabetic(charAt1) && !Character.isDigit(charAt1) && pEntrada.length() == 1){
//            return pEntrada;
//        }
//        for (int i = 0; i < pEntrada.length(); i++) {
//            char charAt2 = pEntrada.charAt(i);
//            if (Character.isAlphabetic(charAt2)) {
//                flagLetra = true;
//            }
//            else if (!Character.isDigit(charAt2)){
//                return "-1";
//            }
//        }
//        if(flagLetra){
//            return "id";
//        }
//        else{
//            return "num";
//        }
//    }
//    public void analisisLexico() throws UnsupportedEncodingException, FileNotFoundException, IOException {
//        String path = "Evaluaciones.txt";
//        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path), "ISO-8859-1"));
//        String line;
//        String parseado = "";
//        while ((line = in.readLine()) != null) {
//            String[] tmp = line.split("|"); //Agrga espacio
//            String palabra = "";
//            for (int i = 0; i < tmp.length; i++) { //Elimina espacios
//                //System.out.println(i + "," + line.length());
//
//                if (tmp[i].compareTo(" ") != 0) {
//                    palabra = palabra + tmp[i];
//                    if (i == tmp.length - 1) {
//                        String terminal = identificador(palabra);
//                        if (terminal.compareTo("-1") == 0) {
//                            logFile("Error 5, Componente lexico no encontrado");
//                            break;
//                        } else {
//                            parseado = parseado + terminal;
//                        }
//                    }
//                } else if (tmp[i].compareTo(" ") == 0 && palabra.compareTo("") != 0) {
//                    String terminal = identificador(palabra);
//
//                    if (terminal.compareTo("-1") == 0) {
//                        logFile("Error 5, Componente lexico no encontrado");
//
//                        break;
//                        //return "Error 1"; Escribir en archivo error
//                    } else {
//                        parseado = parseado + terminal;
//                    }
//                    palabra = "";
//                }
//            }
//            parseado = parseado + "$";
//            System.out.println(parseado);
//            logFile(ejecutarPrueba(parseado));
//
//        }
//
//        in.close();
//    }
    public void leerGramatica() throws UnsupportedEncodingException, FileNotFoundException, IOException {
        String path = "Gramatica.txt";
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path), "ISO-8859-1"));
        String line;
        int i = 1;
        while ((line = in.readLine()) != null) {
            if (line.indexOf("->", 0) != -1) {
                //System.out.println("Leyendo linea " + i + ": " + line);
                ListaString listaDerecha = new ListaString();
                int before = line.indexOf("->", 0); //Indice adonde se encontro el simbolo "->"
                String izq = line.substring(0, before);
                String der = line.substring(before + 2, line.length());
                if (der.indexOf("|") != -1) {
                    String[] partes = der.split("\\|");
                    for (int k = 0; k < partes.length; k++) {
                        listaDerecha.insertar(partes[k]);
                    }
                } else {
                    //System.out.println(der);
                    listaDerecha.insertar(der);
                }
                Produccion prod = new Produccion(izq, listaDerecha);
                _producciones.insertar(prod);
                i++;
            } else {
                System.out.println("Error, linea sin '->'");
                break;
            }
        }
        in.close();
    }

    public ListaString sumarListaString(ListaString lista1, ListaString lista2) {
        ListaString tmp = lista1;
        for (int i = 0; i < lista2.getTam(); i++) {
            if (lista1.buscarElem(lista2.buscarPos(i)) == -1) {
                tmp.insertar(lista2.buscarPos(i));
            }
        }
        return tmp;
    }

    public ListaString getSiguiente(String pNombre) {
        _prodCalculadas.insertar(pNombre);
        ListaString result = new ListaString();
        Produccion temporal = _producciones.getHead();
        if (pNombre.compareTo(temporal.getIzq()) == 0) {//regla 1
            result.insertar("$");
        }
        while (temporal != null) {
            NodoString temporal2 = temporal.getDer().getHead();
            while (temporal2 != null) {
                if (temporal2.getStr().indexOf(pNombre) != -1) {

                    String cortado = temporal2.getStr().substring(temporal2.getStr().indexOf(pNombre), temporal2.getStr().length());
                    
                    int indiceEspacio = cortado.indexOf(" ", 0);
                    String palabra = cortado.substring(0, indiceEspacio);
                    if (temporal2.getStr().indexOf(pNombre) + palabra.length() != temporal2.getStr().length() - 1) {
                        
                        ListaString resultadoParcial = getPrimeroDer(temporal2.getStr().substring(temporal2.getStr().indexOf(pNombre) + palabra.length() + 1));
                        
                        
                        
                        if (resultadoParcial.buscarElem("ñ ") == -1 && resultadoParcial.buscarElem("ñ") == -1) {//no cumple regla 3
                            result = sumarListaString(result, resultadoParcial);
                            
                       
                        } else {//cumple regla 3
                            resultadoParcial.borrarElem("ñ ");
                           resultadoParcial.borrarElem("ñ"); 
                            
                            if (_prodCalculadas.buscarElem(temporal.getIzq()) == -1) {
                                result = sumarListaString(result, getSiguiente(temporal.getIzq()));
                            }
                        }
                    } else {
                        if (_prodCalculadas.buscarElem(temporal.getIzq()) == -1) {
                            result = sumarListaString(result, getSiguiente(temporal.getIzq()));
                        }
                    }
                }
                temporal2 = temporal2.getNext();
            }
            temporal = temporal.getNext();
        }
        return result;
    }

    public String getTerminal(String pString) {
//        int i = 1;
//        while (i < pString.length() && pString.substring(i, i + 1).compareTo((pString.substring(i, i + 1)).toLowerCase()) == 0) {
//            i++;
//        }
        int indiceEspacio = pString.indexOf(" ", 0);
        String palabra = pString.substring(0, indiceEspacio);
        //return pString.substring(0, i);
        return palabra;

    }

    public ListaString getPrimeroDer(String pNombre) {
        ListaString result = new ListaString();
        String caracter = pNombre.substring(0, 1);

        char charAt2 = caracter.charAt(0);
        if (caracter.compareTo(caracter.toUpperCase()) == 0 && Character.isLetter(charAt2)) {
            int indiceEspacio = pNombre.indexOf(" ", 0);
            if (indiceEspacio != -1) {
                String palabra = pNombre.substring(0, indiceEspacio);
                ListaString resultadoTemporal = getPrimero(palabra);
                result = sumarListaString(result, resultadoTemporal);
            } else {
                ListaString resultadoTemporal = getPrimero(pNombre);
                result = sumarListaString(result, resultadoTemporal);
            }
        } else {
            if (result.buscarElem(getTerminal(pNombre)) == -1) {
                result.insertar(getTerminal(pNombre));
            }
        }
        return result;

    }

    public ListaString getPrimero(String pNombre) {
        Produccion tmp = _producciones.buscarIzq(pNombre);
        ListaString derecha = tmp.getDer();
        ListaString result = new ListaString();
        int contador = 0;
        while (contador < derecha.getTam()) {
            String caracter = (derecha.buscarPos(contador)).substring(0, 1);
            char charAt2 = caracter.charAt(0);
            if (caracter.compareTo(caracter.toUpperCase()) == 0 && Character.isLetter(charAt2)) {
                int indiceEspacio = derecha.buscarPos(contador).indexOf(" ", 0);
                String palabra = derecha.buscarPos(contador).substring(0, indiceEspacio);
                if (palabra.compareTo(pNombre) != 0) {
                    ListaString resultadoTemporal = getPrimero(palabra);
                    result = sumarListaString(result, resultadoTemporal);
                }
            } else {
                if (result.buscarElem(getTerminal(derecha.buscarPos(contador))) == -1) {
                    result.insertar(getTerminal(derecha.buscarPos(contador)));
                }
            }
            contador++;
        }

        return result;
    }

    public void llenarTablaExcel(String pNombre) throws FileNotFoundException, IOException {
        Workbook wb = new HSSFWorkbook();
        //Workbook wb = new XSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();
        Sheet sheet = wb.createSheet(pNombre);
        for (int i = 0; i <= _tabla.getFilas().getTam(); i++) {
            Row row = sheet.createRow((short) i);
            for (int j = 0; j <= _tabla.getColumnas().getTam(); j++) {

                if (i == 0 && j != _tabla.getColumnas().getTam()) {
                    Cell cell = row.createCell(j + 1);
                    cell.setCellValue(_tabla.getColumnas().buscarPos(j));
                } else {
                    if (j == 0) {
                        Cell cell = row.createCell(j);

                        cell.setCellValue(_tabla.getFilas().buscarPos(i - 1));
                    } else {
                        if (i != 0) {
                            Cell cell = row.createCell(j);
                            cell.setCellValue(_tabla.buscarEnPos(i - 1, j - 1));
                        }
                    }
                }
            }
        }

        Sheet sheetB = wb.createSheet("B");
        for (int i = 0; i < getResumenTablaPNR().getTam(); i++) {
            Row row = sheetB.createRow((short) i);
            Cell cell = row.createCell(0);
            cell.setCellValue(getResumenTablaPNR().buscarPos(i));
        }
        FileOutputStream fileOut = new FileOutputStream("workbook.xls");
        wb.write(fileOut);
        fileOut.close();
    }

    /**
     * @return the _producciones
     */
    public ListaProduccion getProducciones() {
        return _producciones;
    }

    /**
     * @param pproducciones the _producciones to set
     */
    public void setProducciones(ListaProduccion pproducciones) {
        _producciones = pproducciones;
    }

    /**
     * @return the _tabla
     */
    public tabla getTabla() {
        return _tabla;
    }

    /**
     * @param ptabla the _tabla to set
     */
    public void setTabla(tabla ptabla) {
        _tabla = ptabla;
    }

//-----------------------------------------------------------------------------------------------------------------------------------------------------//

    /*
     public void llenarHeadersLR(){
     _tablaLR = new TablaLR();
         
         
     ListaString tmp = new ListaString();
     for(int i=0;i<_cerraduras.getTam();i++){
     String str = Integer.toString(i);
     tmp.insertar(str);
     }
     ListaString tmp6 = new ListaString();
     Produccion prod2 = _producciones.getHead();
     for(int i=0;i<_producciones.getTam();i++){
     String str = prod2.getIzq();
     tmp6.insertar(str);
     prod2 = prod2.getNext();
     }
         
     _tablaLR.setIrA(tmp6);
         
     Produccion prod = _producciones.getHead();
     ListaString tmp3 = new ListaString();
         
     //         while(prod != null){
     //             NodoString tmp2 = prod.getDer().getHead();
     //             String tmp4 = tmp2.getStr();
     //             while(tmp4!= null){
     //             
     //                 int i = 1;
     //        while (i < tmp4.length() && tmp4.substring(i, i + 1).compareTo((tmp4.substring(i, i + 1)).toLowerCase()) == 0) {
     //            i++;
     //        }
     //        tmp3.insertar(tmp4.substring(0, i));
     //        if(i<tmp4.length()){
     //            tmp4 = tmp4.substring(i);
     //        }
     //        else{
     //            tmp2.getStr();
     //            
     //        }
     //         }
     //             }
     _tablaLR.setColumnas(tmp);
     _tablaLR.setaccion(_resumenTablaPNR);

     }*/
    public ListaProduccion encontrarProduccion(String pProd) {
        ListaProduccion resultado = new ListaProduccion();
        Produccion tmp = _producciones.getHead();
        for (int i = 0; i < _producciones.getTam(); i++) {
            if (tmp.getIzq().compareTo(pProd) == 0) {
                resultado.insertar(clonar(tmp));
            }
            tmp = tmp.getNext();
        }
        //resultado.getTail().setNext(null);
        return resultado;
    }

    public Produccion clonar(Produccion pprod) {
        ListaString der = pprod.getDer();
        Produccion result = new Produccion(pprod.getIzq(), der);
        result.setPunto(pprod.getPunto());
        return result;
    }

    /*
    
     public void llenarTablaExcel2(String pNombre) throws FileNotFoundException, IOException {
     Workbook wb = new HSSFWorkbook();
     //Workbook wb = new XSSFWorkbook();
     CreationHelper createHelper = wb.getCreationHelper();
     Sheet sheet = wb.createSheet(pNombre);
     _tablaLR.getIrA()
                
                
                
     for (int i = 0; i <= _tablaLR.getFilas().getTam(); i++) {
     Row row = sheet.createRow((short) i);
     for (int j = 0; j <= _tablaLR.getColumnas().getTam(); j++) {

     if (i == 0 && j != _tablaLR.getColumnas().getTam()) {
     Cell cell = row.createCell(j + 1);
     cell.setCellValue(_tablaLR.getColumnas().buscarPos(j));
     } else {
     if (j == 0) {
     Cell cell = row.createCell(j);

     cell.setCellValue(_tablaLR.getFilas().buscarPos(i - 1));
     } else {
     if (i != 0) {
     Cell cell = row.createCell(j);
     cell.setCellValue(_tablaLR.buscarEnPos(i - 1, j - 1));
     }
     }
     }
     }
     }
    
    
     */
    /**
     * @return the _ListaErrores
     */
    public ListaString getListaErrores() {
        return _ListaErrores;
    }

    /**
     * @param _ListaErrores the _ListaErrores to set
     */
    public void setListaErrores(ListaString _ListaErrores) {
        this._ListaErrores = _ListaErrores;
    }

    /**
     * @return the _resumenTablaPNR
     */
    public ListaString getResumenTablaPNR() {
        return _resumenTablaPNR;
    }

}
