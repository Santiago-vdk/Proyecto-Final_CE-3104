
public class ListaString {
    private NodoString _head;
    private NodoString _tail;
    private int _tam;
    private ListaString _next;
    private ListaString _prev;
    
    public ListaString(){
        _head = null;
        _tail = null;
        _next = null;
        _prev = null;
        _tam = 0;
        
    }
    
    public void sumarListas(ListaString plista){
        if(plista == null){
            //suma lista vacia
        }
        else if(_tail != null){
        _tail.setNext(plista.getHead());
        plista.getHead().setPrev(_tail);
        _tail = plista.getTail();
        _tam = _tam + plista.getTam();
        }
        else{
           _head.setNext(plista.getHead());
           plista.getHead().setPrev(_head);
           _tail = plista.getTail();
        _tam = _tam + plista.getTam();
        }
        
    }
    
    public int buscarElem(String pelem){
        NodoString tmp = _head;
        int i = 0;
        while(tmp  != null && tmp.getStr().compareTo(pelem)!=0){
            tmp = tmp.getNext();
            i++;
        }
        if(tmp == null){
            return -1;
        }
        else{
            return i;
        }
    }
    
    public void borrarElem(String pelem){
        
        NodoString tmp = _head;
        while(tmp  != null){
            if(tmp.getStr().compareTo(pelem)==0){
                if(tmp.getStr().compareTo(_head.getStr())==0){
                    _head = tmp.getNext();
                }
                else if(tmp.getStr().compareTo(_tail.getStr())==0){
                    _tail = tmp.getPrev();
                }
                else{
                    tmp.getNext().setPrev(tmp.getPrev());
                    tmp.getPrev().setNext(tmp.getNext());
                }
                _tam--;
            }
            tmp = tmp.getNext();
        }
    }
    
    public String buscarPos(int ppos){
            NodoString tmp = _head;
            for(int i=0; i<ppos; i++){
                tmp = tmp.getNext();
            }
            return tmp.getStr();
        
    }

    public void insertar(String pprod){
        NodoString tmp = new NodoString(pprod);
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
    public NodoString getHead() {
        return _head;
    }

    /**
     * @param phead the _head to set
     */
    public void setHead(NodoString phead) {
        _head = phead;
    }

    /**
     * @return the _tail
     */
    public NodoString getTail() {
        return _tail;
    }

    /**
     * @param ptail the _tail to set
     */
    public void setTail(NodoString ptail) {
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
     * @return the _next
     */
    public ListaString getNext() {
        return _next;
    }

    /**
     * @param pnext the _next to set
     */
    public void setNext(ListaString pnext) {
        _next = pnext;
    }

    /**
     * @return the _prev
     */
    public ListaString getPrev() {
        return _prev;
    }

    /**
     * @param pprev the _prev to set
     */
    public void setPrev(ListaString pprev) {
        _prev = pprev;
    }
    
}
