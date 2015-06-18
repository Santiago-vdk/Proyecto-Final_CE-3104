
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.CreationHelper;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class Facade {

    private ListaProduccion _producciones;
    private tabla _tabla;
    private ListaString _resumenTablaPNR;

    public Facade() throws FileNotFoundException {
        _producciones = new ListaProduccion();
        _tabla = new tabla();
        _resumenTablaPNR = new ListaString();
        

    }

    public String ejecutarPrueba(String pPrueba) {
        String entrada = pPrueba;
        String pila = _producciones.getHead().getIzq() + "$";
        while (pila.compareTo("$") != 0) {
            String terminal = extraerTerminal(entrada);
            int i = 1;
            char charAt2 = pila.substring(0, i).charAt(0);
            if (terminal.compareTo("-1") == 0) {
                return "Error 1, terminal desconocido.";
            } else if ((pila.substring(0, i).compareTo(pila.substring(0, i).toUpperCase()) == 0) && Character.isLetter(charAt2)) {
                String tmp = _tabla.buscarEnPos(_tabla.getFilas().buscarElem(pila.substring(0, i)), _tabla.getColumnas().buscarElem(terminal));
                if (tmp.compareTo("♥") == 0) {
                    return "Error 2, casilla nula en la tabla.";
                } else {
                    tmp = tmp.substring(3);
                    if (tmp.compareTo("ñ") == 0) {
                        pila = pila.substring(1);
                    } else {
                        pila = pila.replaceFirst(pila.substring(0, i), tmp);
                    }
                }
            } else {
                String terminalEnPila = extraerTerminal(pila);
                if (terminalEnPila.compareTo(terminal) == 0) {
                    entrada = entrada.substring(terminal.length());
                    pila = pila.substring(terminal.length());
                } else {
                    return "Error 3, comparacion pila-entrada FALLIDA.";
                }
            }
        }
        if(pila.compareTo(entrada) == 0){
            return "SUCCESS, Linea Valida";
        }
        else{
            return "Error 4, valor despues de '$' en la entrada.";
        }
        
    }

    public String extraerTerminal(String pString) {
        NodoString temporal = _tabla.getColumnas().getHead();
        while (temporal != null) {
            if (pString.indexOf(temporal.getStr()) == 0) {
                return temporal.getStr();
            }
            temporal = temporal.getNext();
        }
        return "-1";
    }

    public void leerPrueba() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("Evaluaciones.txt"));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                logFile(ejecutarPrueba(line));
                line = br.readLine();

            }
        } finally {
            br.close();
        }
    }

    public void logFile(String pLog) throws FileNotFoundException, IOException {
        File log = new File("ResultadoPredictivoNoRecursivoPila.txt");
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
                            System.out.println(terminal);
                            columnas.insertar(terminal);
                        }
                        ladoDerecho = ladoDerecho.substring(terminal.length()+1);
                    }
                    else{
                        int indiceEspacio = ladoDerecho.indexOf(" ", 0);
                        String terminal = ladoDerecho.substring(0, indiceEspacio);
                        ladoDerecho = ladoDerecho.substring(terminal.length()+1);
                    }
                    
                    
                }
                temporal3 = temporal3.getNext();
            }

            temporal2=temporal2.getNext();
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
                if (temporal2.getStr().compareTo("ñ") == 0) {
                    ListaString resultadoTemporal = getSiguiente(temporal.getIzq());

                    String stringTabla = "";
                    for (int t = 0; t < resultadoTemporal.getTam(); t++) {
                        stringTabla = stringTabla + ", " + resultadoTemporal.buscarPos(t);
                    }
                    stringTabla = stringTabla.substring(1);
                    _resumenTablaPNR.insertar("Se calcula el SIGUIENTE de " + temporal.getIzq() + ": " + stringTabla);

                    ListaString parteDer = new ListaString();
                    parteDer.insertar(temporal2.getStr());
                    //Produccion prod = new Produccion(temporal.getIzq(), parteDer);
                    while (resultadoTemporal.getTam() > 0) {
                        _tabla.insertarProduccion2_0(temporal.getIzq(), parteDer, _tabla.getFilas().buscarElem(temporal.getIzq()), _tabla.getColumnas().buscarElem(resultadoTemporal.getHead().getStr()));
                        resultadoTemporal.borrarElem(resultadoTemporal.getHead().getStr());
                    }
                    //Insertamos en la tabla
                } else {
                    ListaString resultadoTemporal = getPrimeroDer(temporal2.getStr());

                    String stringTabla = "";
                    for (int t = 0; t < resultadoTemporal.getTam(); t++) {
                        stringTabla = stringTabla + ", " + resultadoTemporal.buscarPos(t);
                    }
                    stringTabla = stringTabla.substring(1);
                    _resumenTablaPNR.insertar("Se calcula el PRIMERO de " + temporal2.getStr() + ": " + stringTabla);

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

    public String identificador(String pEntrada) {
        char charAt1 = pEntrada.charAt(0);
        boolean flagLetra = false;
        if(!Character.isAlphabetic(charAt1) && !Character.isDigit(charAt1) && pEntrada.length() == 1){
            return pEntrada;
        }
        for (int i = 0; i < pEntrada.length(); i++) {
            char charAt2 = pEntrada.charAt(i);
            if (Character.isAlphabetic(charAt2)) {
                flagLetra = true;
            }
            else if (!Character.isDigit(charAt2)){
                return "-1";
            }
        }
        if(flagLetra){
            return "id";
        }
        else{
            return "num";
        }
    }
        
    public void analisisLexico() throws UnsupportedEncodingException, FileNotFoundException, IOException {
        String path = "Evaluaciones.txt";
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path), "ISO-8859-1"));
        String line;
        String parseado = "";
        while ((line = in.readLine()) != null) {
            String[] tmp = line.split("|"); //Agrga espacio
            String palabra = "";
            for (int i = 0; i < tmp.length; i++) { //Elimina espacios
                //System.out.println(i + "," + line.length());

                if (tmp[i].compareTo(" ") != 0) {
                    palabra = palabra + tmp[i];
                    if (i == tmp.length - 1) {
                        String terminal = identificador(palabra);
                        if (terminal.compareTo("-1") == 0) {
                            logFile("Error 5, Componente lexico no encontrado");
                            break;
                        } else {
                            parseado = parseado + terminal;
                        }
                    }
                } else if (tmp[i].compareTo(" ") == 0 && palabra.compareTo("") != 0) {
                    String terminal = identificador(palabra);

                    if (terminal.compareTo("-1") == 0) {
                        logFile("Error 5, Componente lexico no encontrado");

                        break;
                        //return "Error 1"; Escribir en archivo error
                    } else {
                        parseado = parseado + terminal;
                    }
                    palabra = "";
                }
            }
            parseado = parseado + "$";
            System.out.println(parseado);
            logFile(ejecutarPrueba(parseado));

        }

        in.close();
    }

    public void leerGramatica() throws UnsupportedEncodingException, FileNotFoundException, IOException {
        String path = "Gramatica.txt";
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path), "ISO-8859-1"));
        String line;
        int i = 1;
        while ((line = in.readLine()) != null) {
            if (line.indexOf("->", 0) != -1) {
                System.out.println("Leyendo linea " + i + ": " + line);
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
        ListaString result = new ListaString();
        Produccion temporal = _producciones.getHead();
        if (pNombre.compareTo(temporal.getIzq()) == 0) {//regla 1
            result.insertar("$");
        }
        while (temporal != null) {
            NodoString temporal2 = temporal.getDer().getHead();
            while (temporal2 != null) {
                if (temporal2.getStr().indexOf(pNombre) != -1) {
                    
                    
                    String cortado = temporal2.getStr().substring(temporal2.getStr().indexOf(pNombre),temporal2.getStr().length());
                    int indiceEspacio = cortado.indexOf(" ", 0);
                    String palabra = cortado.substring(0,indiceEspacio);
                    
                    if (temporal2.getStr().indexOf(pNombre)+palabra.length() != temporal2.getStr().length() - 1) {
                        ListaString resultadoParcial = getPrimeroDer(temporal2.getStr().substring(temporal2.getStr().indexOf(pNombre)+palabra.length()+1));
                        if (resultadoParcial.buscarElem("ñ") == -1) {//no cumple regla 3
                            result = sumarListaString(result, resultadoParcial);
                        } else {//cumple regla 3
                            resultadoParcial.borrarElem("ñ");
                            result = sumarListaString(result, resultadoParcial);
                            if (pNombre.compareTo(temporal.getIzq()) != 0) {
                                result = sumarListaString(result, getSiguiente(temporal.getIzq()));
                            }
                        }
                    } else {
                        if (pNombre.compareTo(temporal.getIzq()) != 0) {
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
            if(indiceEspacio != -1){
                String palabra = pNombre.substring(0, indiceEspacio);
                ListaString resultadoTemporal = getPrimero(palabra);
                result = sumarListaString(result, resultadoTemporal);
            }
            else{
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
                if(palabra.compareTo(pNombre)!=0){
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
    

    
    
    
    
    
    
//
//    public void llenarTablaExcel(String pNombre) throws FileNotFoundException, IOException {
//        Workbook wb = new HSSFWorkbook();
//        //Workbook wb = new XSSFWorkbook();
//        CreationHelper createHelper = wb.getCreationHelper();
//        Sheet sheet = wb.createSheet(pNombre);
//        for (int i = 0; i <= _tabla.getFilas().getTam(); i++) {
//            Row row = sheet.createRow((short) i);
//            for (int j = 0; j <= _tabla.getColumnas().getTam(); j++) {
//
//                if (i == 0 && j != _tabla.getColumnas().getTam()) {
//                    Cell cell = row.createCell(j + 1);
//                    cell.setCellValue(_tabla.getColumnas().buscarPos(j));
//                } else {
//                    if (j == 0) {
//                        Cell cell = row.createCell(j);
//
//                        cell.setCellValue(_tabla.getFilas().buscarPos(i - 1));
//                    } else {
//                        if (i != 0) {
//                            Cell cell = row.createCell(j);
//                            cell.setCellValue(_tabla.buscarEnPos(i - 1, j - 1));
//                        }
//                    }
//                }
//            }
//        }
//
//        Sheet sheetB = wb.createSheet("B");
//        for (int i = 0; i < _resumenTablaPNR.getTam(); i++) {
//            Row row = sheetB.createRow((short) i);
//            Cell cell = row.createCell(0);
//            cell.setCellValue(_resumenTablaPNR.buscarPos(i));
//        }
//        FileOutputStream fileOut = new FileOutputStream("workbook.xls");
//        wb.write(fileOut);
//        fileOut.close();
//    }

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
    
    
    public ListaProduccion encontrarProduccion(String pProd){
        ListaProduccion resultado = new ListaProduccion();
        Produccion tmp = _producciones.getHead();
        for (int i = 0; i < _producciones.getTam(); i++){
            if(tmp.getIzq().compareTo(pProd) == 0){
                resultado.insertar(clonar(tmp));
            }
            tmp = tmp.getNext();
        }
        //resultado.getTail().setNext(null);
        return resultado;
    }
    
    
    public Produccion clonar(Produccion pprod){
        ListaString der = pprod.getDer();
        Produccion result = new Produccion(pprod.getIzq(),der);
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
    
    
}
