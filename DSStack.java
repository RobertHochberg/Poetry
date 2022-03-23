/**
 * When you extend an abstract class to make a 
 * non-abstract class, it is necessary to implement
 * all unimplemented abstract methods.
 */

class DSStack<E> extends DSLinkedList<E> {

    public E remove(){
        if(head == null)
            throw new NullPointerException("Nothing in list");

        LLC preivioustail = tail;
        tail = tail.prev;
        tail.next = null;

        return preivioustail.thing;
    }

    /*
     * Can't have a compareTo() method below, because we haven't promised the compiler
     * that the type E will implement the compareTo() method. 
     * 
     * We did that in DSNumericalStack
    public void sort(){
        if(tail == null || tail == head) return; // Size 0 or 1

        if(tail.thing.compareTo(head.thing) < 0) ...;
        System.out.println(3);
        // java tutorials generics
    }



    /**
     * Return the item at the top of the stack, but do not remove it from the stack.
     */
    public E peek(){
        return this.tail.thing;
    }
}