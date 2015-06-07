/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Shagy
 */
public class Produccion {
    private String _izq;
    private ListaString _der;
    private Produccion _next;
    private Produccion _prev;
    private int _punto;
    
    public Produccion(String pizq,ListaString pder){
        _izq = pizq;
        _der = pder;
        _next = null;
        _prev = null;
        _punto = 0;
    }

    public void aumentarPunto(){
        char charAt2 = _der.getHead().getStr().charAt(_punto);
        boolean flag = true;
        while (_punto < _der.getHead().getStr().length()-1 && (_der.getHead().getStr().substring(_punto,_punto+1).compareTo(_der.getHead().getStr().substring(_punto,_punto+1).toLowerCase()) == 0 || !Character.isLetter(charAt2))) {
            setPunto(_punto + 1);
            flag = false;
           
            if (_punto < _der.getHead().getStr().length()) {
                charAt2 = _der.getHead().getStr().charAt(_punto);
            }
        }
        if (_punto < _der.getHead().getStr().length() && (_der.getHead().getStr().substring(_punto).compareTo(_der.getHead().getStr().substring(_punto).toLowerCase()) == 0 || !Character.isLetter(charAt2))) {
            setPunto(_punto + 1);
//            }
            
        }
        else if(flag){
            setPunto(_punto + 1);
        }
    }
    
    
    /**
     * @return the _izq
     */
    public String getIzq() {
        return _izq;
    }

    /**
     * @param pizq the _izq to set
     */
    public void setIzq(String pizq) {
        _izq = pizq;
    }

    /**
     * @return the _der
     */
    public ListaString getDer() {
        return _der;
    }

    /**
     * @param pder the _der to set
     */
    public void setDer(ListaString pder) {
        _der = pder;
    }

    /**
     * @return the _prev
     */
    public Produccion getPrev() {
        return _prev;
    }

    /**
     * @param pprev the _prev to set
     */
    public void setPrev(Produccion pprev) {
        _prev = pprev;
    }

    /**
     * @return the _next
     */
    public Produccion getNext() {
        return _next;
    }

    /**
     * @param pnext the _next to set
     */
    public void setNext(Produccion pnext) {
        _next = pnext;
    }

    /**
     * @return the _punto
     */
    public int getPunto() {
        return _punto;
    }

    /**
     * @param ppunto the _punto to set
     */
    public void setPunto(int ppunto) {
        _punto = ppunto;
    }
}
