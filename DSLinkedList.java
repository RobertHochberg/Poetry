/**
* Generic class that can "hold" objects of any type
* This class will be a building-block class. We'll build
* *actual* data structures by subclassing DSLinkedList.
*
* Use this to build DSStack and DSQueue
*
* Any class with an abstract method, must be declared abstract.
 */

abstract class DSLinkedList <E> {
    LLC head;
    LLC tail;

    /**
     * Adds an item to the end
     */
    public void add(E item){
        LLC added = new LLC(item, null, tail);
        if(head == null){
            head = added;
            tail = added;
        } else {
            tail.next = added;
            tail = added;
        }
    }


    /**
     * Removes and returns an item
     * This behavior depends on what the subclass wants to do.
     * We can not implement this here!!!
     *
     * However, we want every subclass to implement it. That's our intention!
     */
     public abstract E remove();


     /**
      * Simple method to give a string representation of our Linked Lists.
      * Note that this works, since all Java objects implement toString().
      */
     public String toString(){
        String rv = "[";
        LLC llc = this.head;
        while(llc != null){
            rv += llc.thing.toString();
            if(llc.next != null){
                rv += ", ";
            }
            llc = llc.next;
        }
        rv += "]";

        return rv;
     }

   /** 
    * Inner class
    * Container to hold each List element
    */
    class LLC{
        E   thing;
        LLC next;
        LLC prev;

        public LLC(E t, LLC n, LLC p){
            this.thing = t;
            this.next = n;
            this.prev = p;
        }
    }
}