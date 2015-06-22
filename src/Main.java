

public class Main {

    public static void main(String args[]) throws Exception {
        
        
        /*Server go = new Server();
        go.startServer();*/
        Facade facade = new Facade();
        facade.leerGramatica();
        //ListaString a =facade.getSiguiente("ExpresionMult");
        facade.insertarHeaders();
        facade.calcularPNR();
        ListaString a = facade.getResumenTablaPNR();
        System.out.println("looooooooooooooool");
        for(int i=0;i<a.getTam();i++){
            System.out.println(a.buscarPos(i));
        }
//        System.out.println("looooooooooooooool");
        
        facade.llenarTablaExcel("tablaExcel");
      //  facade.getTabla().getHead().
        //facade.AnalisisSintactico("declarar int contador newline contador = 5 newline mover 2 contador newline contador = contador * 10 newline mover 3 3 newline si (  true       ) entonces { mover 0 3 newline } newline ");
        facade.AnalisisSintactico("declarar int contador newline declarar const char abeja  newline mover 2 contador newline contador = contador * 10 newline mover 3 3 newline haga casa = casa / 2 newline mientras ( 4 == casa ) newline ");
        
        //facade.AnalisisSemantico("declarar int contador newline contador = 5 newline mover 2 contador newline");
        
    }
}
