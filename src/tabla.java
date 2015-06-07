public class tabla {

    private ListaProduccion _head;
    private ListaProduccion _tail;
    private int _tam;
    private ListaString _filas;
    private ListaString _columnas;

    public tabla() {
        _head = null;
        _tail = null;
        _filas = null;
        _columnas = null;
        _tam = 0;
    }

    public void llenarTabla(int i, int j) {
        for (int pi = 0; pi < i; pi++) {
            ListaProduccion listaProd = new ListaProduccion();
            if (pi == 0) {
                _head = listaProd;
                _tail = listaProd;
            } else {
                _tail.setNext(listaProd);
                listaProd.setPrev(_tail);
                _tail = listaProd;
            }
            for (int pj = 0; pj < j; pj++) {
                Produccion tmp = new Produccion(null,null);
                listaProd.insertar(tmp);
            }
        }
    }
    public void insertarProduccion2_0(String pizq,ListaString pder, int i, int j){
        ListaProduccion tmp1 = _head;
        for (int pi = 0; pi < i; pi++) {
            tmp1 = tmp1.getNext();
        }
        Produccion tmp2 = tmp1.getHead();
        for (int pj = 0; pj < j; pj++) {
            tmp2 = tmp2.getNext();
        }
        tmp2.setIzq(pizq);
        tmp2.setDer(pder);
}
    public void insertarProduccion(Produccion pProduccion, int i, int j) {
         System.out.println("inicio");
        ListaProduccion tmp1 = _head;
        for (int pi = 0; pi < i; pi++) {
            tmp1 = tmp1.getNext();
        }
        Produccion tmp2 = tmp1.getHead();
        for (int pj = 0; pj < j; pj++) {
            tmp2 = tmp2.getNext();
        }
        if (tmp2 == tmp1.getHead()) {
             System.out.println("Entre if");
              System.out.println(tmp2.getNext());
              
            pProduccion.setNext(tmp2.getNext());
            tmp2.getNext().setPrev(pProduccion);
            tmp1.setHead(pProduccion);
             System.out.println(tmp1.getHead().getIzq());
        }
        else if (tmp2 == tmp1.getTail()) {
             System.out.println("Entre else if");
              System.out.println(tmp2.getPrev());
            pProduccion.setPrev(tmp2.getPrev());
            tmp2.getPrev().setNext(pProduccion);
            tmp1.setTail(pProduccion);
             System.out.println(tmp1.getTail());
        } else {
             System.out.println("Entre else");
              System.out.println(tmp2.getNext());
               System.out.println(tmp2.getPrev());
               
            pProduccion.setNext(tmp2.getNext());
            pProduccion.setPrev(tmp2.getPrev());
            tmp2.getNext().setPrev(pProduccion);
            tmp2.getPrev().setNext(pProduccion);
             System.out.println(tmp2.getNext().getPrev().getIzq());
              System.out.println(tmp2.getPrev().getNext().getIzq());
        }

    }
    
    public String buscarEnPos(int i,int j){
        ListaProduccion tmp = _head;

        for(int k = 0; k < i; k++){
            tmp = tmp.getNext();
                        
       }
        Produccion tmp2 = tmp.getHead();
        for(int z = 0; z < j; z ++){
            tmp2 = tmp2.getNext();
            
        }

        if(tmp2.getIzq() == null){
            String resultado = "♥";
            return resultado;
        } else {
            String resultado = tmp2.getIzq() + "->" + tmp2.getDer().getHead().getStr();
            return resultado;
        }
       
    }
    

    /**
     * @return the _head
     */
    public ListaProduccion getHead() {
        return _head;
    }

    /**
     * @param phead the _head to set
     */
    public void setHead(ListaProduccion phead) {
        _head = phead;
    }

    /**
     * @return the _tail
     */
    public ListaProduccion getTail() {
        return _tail;
    }

    /**
     * @param ptail the _tail to set
     */
    public void setTail(ListaProduccion ptail) {
        _tail = ptail;
    }

    /**
     * @return the _tam
     */
    public int getTam() {
        return _tam;
    }

    /**
     * @param ptam the _tam to set
     */
    public void setTam(int ptam) {
        _tam = ptam;
    }

    /**
     * @return the _filas
     */
    public ListaString getFilas() {
        return _filas;
    }

    /**
     * @param pfilas the _filas to set
     */
    public void setFilas(ListaString pfilas) {
        _filas = pfilas;
    }

    /**
     * @return the _columnas
     */
    public ListaString getColumnas() {
        return _columnas;
    }

    /**
     * @param pcolumnas the _columnas to set
     */
    public void setColumnas(ListaString pcolumnas) {
        _columnas = pcolumnas;
    }

}
