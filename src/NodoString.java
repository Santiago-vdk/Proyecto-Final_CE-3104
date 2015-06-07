/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Shagy
 */
public class NodoString {
    private String _str;
    private NodoString _next;
    private NodoString _prev;
    
    public NodoString(String pstr){
        _str = pstr;
        _next = null;
        _prev = null;
    }
   

    /**
     * @return the _str
     */
    public String getStr() {
        return _str;
    }

    /**
     * @param pstr the _str to set
     */
    public void setStr(String pstr) {
        _str = pstr;
    }

    /**
     * @return the _next
     */
    public NodoString getNext() {
        return _next;
    }

    /**
     * @param pnext the _next to set
     */
    public void setNext(NodoString pnext) {
        _next = pnext;
    }

    /**
     * @return the _prev
     */
    public NodoString getPrev() {
        return _prev;
    }

    /**
     * @param _prev the _prev to set
     */
    public void setPrev(NodoString pprev) {
        _prev = pprev;
    }

    
}
