public class ListaProduccion {
    
    private Produccion _head;
    private Produccion _tail;
    private ListaProduccion _prev;
    private ListaProduccion _next;
    private int _tam;
    
    public ListaProduccion(){
        _head = null;
        _tail = null;
        _prev = null;
        _next = null;
        _tam = 0;
        
    }
    
    
    
    public void sumarListas(ListaProduccion lProd){
        _tail.setNext(lProd.getHead());
        lProd.getHead().setPrev(_tail);
        _tail = lProd.getTail();
        _tam = _tam + lProd.getTam();
    }
    
    public void extenderGramatica(){
        ListaString lista = new ListaString();
        lista.insertar(_head.getIzq());
        Produccion tmp = new Produccion("Z", lista);
        tmp.setNext(_head);
        _head.setPrev(tmp);
        _head = tmp;
        _tam++;
    }
    
    public Produccion buscarIzq(String pizq){
        Produccion tmp = _head;
        
        while(tmp!=null && tmp.getIzq().compareTo(pizq)!=0){
            tmp = tmp.getNext();
        }
        return tmp; //retorna null en caso de que no lo encuentre
        
    }
    
   
    public Produccion buscarPos(int ppos){
            Produccion tmp = _head;
            for(int i=0; i<ppos; i++){
                tmp = tmp.getNext();
            }
            return tmp;
    }

    public void insertar(Produccion pprod){
        Produccion tmp = pprod;
        if(getTam() ==0){
            _head = tmp;
            _tail = _head;
            setTam(getTam() + 1);
        }
        else{
            _tail.setNext(tmp);
            tmp.setPrev(_tail);
            _tail = tmp;
            setTam(getTam() + 1);
            
        }
    }

    /**
     * @return the _head
     */
    public Produccion getHead() {
        return _head;
    }

    /**
     * @param phead the _head to set
     */
    public void setHead(Produccion phead) {
        _head = phead;
    }

    /**
     * @return the _tail
     */
    public Produccion getTail() {
        return _tail;
    }

    /**
     * @param ptail the _tail to set
     */
    public void setTail(Produccion ptail) {
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
     * @return the _prev
     */
    public ListaProduccion getPrev() {
        return _prev;
    }

    /**
     * @param pprev the _prev to set
     */
    public void setPrev(ListaProduccion pprev) {
        _prev = pprev;
    }

    /**
     * @return the _next
     */
    public ListaProduccion getNext() {
        return _next;
    }

    /**
     * @param pnext the _next to set
     */
    public void setNext(ListaProduccion pnext) {
        _next = pnext;
    }
    
}
