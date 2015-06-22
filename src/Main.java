

public class Main {

    public static void main(String args[]) throws Exception {
        
        
        /*Server go = new Server();
        go.startServer();*/
        Facade facade = new Facade();
        facade.AnalisisSemantico("declarar int num1 newline declarar const int num2 newline declarar const int num3 newline declarar string cadena newline declarar char caracter newline num1 = 3 newline num2 = 5 newline num3 = num1 + num2 newline si ( num3 < 9 ) entonces mover 0 5 newline ");

// facade.leerGramatica();
        //facade.getPrimeroDer(null)
//        facade.insertarHeaders();
//        facade.calcularPNR();
//        ListaString a = facade.getResumenTablaPNR();
//        System.out.println("looooooooooooooool");
//        for(int i=0;i<a.getTam();i++){
//            System.out.println(a.buscarPos(i));
//        }
//        System.out.println("looooooooooooooool");
        
//        facade.llenarTablaExcel("tablaExcel");
        //facade.getTabla().getHead().
        //facade.AnalisisSintactico("declarar int contador newline contador = 5 newline mover 2 contador newline");
        //facade.AnalisisSemantico("declarar int contador newline contador = 5 newline mover 2 contador newline");
        
    }
}
