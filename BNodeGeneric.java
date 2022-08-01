import java.util.*;

public class BNodeGeneric<T extends Comparable<T>> {
    Vector<T> keys; //claves
    Vector<BNodeGeneric<T>> children; //hijos
    int MinDeg; // Grado minimo de un nodo
    int num; //numero de claves del nodo
    boolean isLeaf; // Verdadero si es hoja

     // Constructor
     public BNodeGeneric(int deg,boolean isLeaf) {

        this.MinDeg = deg;
        this.isLeaf = isLeaf;
        this.keys = new Vector<T>(0, 2*this.MinDeg-1);
        for(int i = 0; i < 2*this.MinDeg-1; i++) { //Creamos vector para guardar claves los inicializamos 
        	//con null para que no de excepcion
        	this.keys.addElement(null);
        }
        this.children = new Vector<BNodeGeneric<T>>(0, 2*this.MinDeg);
        for(int i = 0; i < 2*this.MinDeg; i++) { //Creamos vector para guardar claves
        	//los inicializamos con null
        	this.children.addElement(null);
        }
        this.num = 0;
    }

    // Encuentra el primer índice de ubicación igual o mayor que la clave
    public int findKey(T key){

        int idx = 0;
        while (idx < num && keys.elementAt(idx).compareTo(key) < 0)
            ++idx;
        return idx;
    }

    public void remove(T key){

        int idx = findKey(key);
        if (idx < num && keys.elementAt(idx).compareTo(key) == 0){ // Buscar clave
            if (isLeaf) //clave en hoja
                removeFromLeaf(idx);
            else //clave no es hoja
                removeFromNonLeaf(idx);
        }
        else{
            if (isLeaf){ // Si es hoja la clave no existe
                System.out.printf("The key %d is does not exist in the tree\n",key);
                return;
            }

            // Esta bandera indica si la clave existe en el subárbol cuya raíz es el último hijo del nodo

            // Cuando idx es igual a num, se compara todo el nodo y el indicador es verdadero
            boolean flag = idx == num; 
            
            if (children.elementAt(idx).num < MinDeg) // Cuando el nodo secundario del nodo no está lleno llénelo primero
                fill(idx);
           
            if (flag && idx > num)
                children.elementAt(idx - 1).remove(key);
            else
                children.elementAt(idx).remove(key);
        }
    }

    public void removeFromLeaf(int idx){ //elimina clave en hoja
        for (int i = idx +1;i < num;++i)
            keys.set(i-1, keys.elementAt(i));
        num --;
    }

    public void removeFromNonLeaf(int idx){ //eliminar vuando no es hoja
        T key = keys.elementAt(idx);

        if (children.elementAt(idx).num >= MinDeg){
            T pred = getPred(idx);
            keys.set(idx, pred);
            children.elementAt(idx).remove(pred);
        }
        else if (children.elementAt(idx+1).num >= MinDeg){
            T succ = getSucc(idx);
            keys.set(idx, succ);
            children.elementAt(idx+1).remove(succ);
        }
        else{
            merge(idx);
            children.elementAt(idx).remove(key);
        }
    }

    public T getPred(int idx){  //encuentra el predecesor
        BNodeGeneric<T> cur = children.elementAt(idx);
        while (!cur.isLeaf)
            cur = cur.children.elementAt(cur.num);
        return cur.keys.elementAt(cur.num-1);
    }

    public T getSucc(int idx){  //encuentra el sucesor

    	BNodeGeneric<T> cur = children.elementAt(idx+1);
        while (!cur.isLeaf)
            cur = cur.children.elementAt(0);
        return cur.keys.elementAt(0);
    }

    public void fill(int idx){ 
        if (idx != 0 && children.elementAt(idx-1).num >= MinDeg)
            borrowFromPrev(idx);
        else if (idx != num && children.elementAt(idx+1).num >= MinDeg)
            borrowFromNext(idx);
        else{
            if (idx != num)
                merge(idx);
            else
                merge(idx-1);
        }
    }

    public void borrowFromPrev(int idx){

        BNodeGeneric<T> child = children.elementAt(idx);
        BNodeGeneric<T> sibling = children.elementAt(idx-1);

       
        for (int i = child.num-1; i >= 0; --i) 
            child.keys.set(i+1, child.keys.elementAt(i));

        if (!child.isLeaf){ 
            for (int i = child.num; i >= 0; --i)
            child.children.set(i+1, child.children.elementAt(i));
        }

        
        child.keys.set(0, keys.elementAt(idx-1));
        if (!child.isLeaf)
            child.children.set(0, sibling.children.elementAt(sibling.num));

        keys.set(idx-1, sibling.keys.elementAt(sibling.num-1));
        child.num += 1;
        sibling.num -= 1;
    }

    public void borrowFromNext(int idx){

        BNodeGeneric<T> child = children.elementAt(idx);
        BNodeGeneric<T> sibling = children.elementAt(idx-1);

        child.keys.set(child.num, keys.elementAt(idx));

        if (!child.isLeaf)
            child.children.set(child.num+1, sibling.children.elementAt(0));           
        keys.set(idx, sibling.keys.elementAt(0));

        for (int i = 1; i < sibling.num; ++i)
            sibling.keys.set(i-1, sibling.keys.elementAt(i));

        if (!sibling.isLeaf){
            for (int i= 1; i <= sibling.num;++i)
                sibling.children.set(i-1, sibling.children.elementAt(i));
        }
        child.num += 1;
        sibling.num -= 1;
    }

    public void merge(int idx){ //unir
        BNodeGeneric<T> child = children.elementAt(idx);
        BNodeGeneric<T> sibling = children.elementAt(idx-1);

        child.keys.set(MinDeg-1, keys.elementAt(idx));

        for (int i =0 ; i< sibling.num; ++i)
            child.keys.set(i+MinDeg, sibling.keys.elementAt(i));
        if (!child.isLeaf){
            for (int i = 0;i <= sibling.num; ++i)
                child.children.set(i+MinDeg, sibling.children.elementAt(i));
        }
        for (int i = idx+1; i<num; ++i)
            keys.set(i-1, keys.elementAt(i));
        
        for (int i = idx+2;i<=num;++i)
            children.set(i-1, children.elementAt(i));

        child.num += sibling.num + 1;
        num--;
    }

    public void insertNotFull(T key){ //insertar cuando el nodo no esta lleno

        int i = num -1;

        if (isLeaf){ 
            
            while (i >= 0 && keys.elementAt(i).compareTo(key) > 0){
                keys.set(i+1, keys.elementAt(i)); 
                i--;
            }
            keys.set(i+1, key);
            num = num +1;
        }
        else{
           
            while (i >= 0 && keys.elementAt(i).compareTo(key) > 0)
                i--;
            if (children.elementAt(i+1).num == 2*MinDeg - 1){ 
                splitChild(i+1,children.elementAt(i+1));
                
                if (keys.elementAt(i+1).compareTo(key) < 0)
                    i++;
            }
            children.elementAt(i+1).insertNotFull(key);
        }
    }

    public void splitChild(int i ,BNodeGeneric<T>  y){

        // Creamos nodo para guardar y 
        BNodeGeneric<T> z = new BNodeGeneric<T> (y.MinDeg,y.isLeaf);
        z.num = MinDeg - 1;

        // pasamos lo que tiene y a z
        for (int j = 0; j < MinDeg-1; j++)
            z.keys.set(j, y.keys.elementAt(j+MinDeg));
        if (!y.isLeaf){
            for (int j = 0; j < MinDeg; j++)
                z.children.set(j, y.children.elementAt(+MinDeg));
        }
        y.num = MinDeg-1;

     // Insertar un nuevo hijo en el hijo
        for (int j = num; j >= i+1; j--)
            children.set(j+1, children.elementAt(j));
        children.set(i+1, z);

        // Mueve una clave en y a este nodo
        for (int j = num-1;j >= i;j--)
        	keys.set(j+1, keys.elementAt(j));
        keys.set(i, y.keys.elementAt(MinDeg-1));

        num = num + 1;
    }

    public void traverse(){ //mostrar el arbol 
        int i;
        for (i = 0; i< num; i++){
            if (!isLeaf)
                children.elementAt(i).traverse();
            System.out.printf(" %d",keys.elementAt(i)); //si es hoja imprimimos de izquierda a derecha, 
            //sino usamos recursividad
        }

        if (!isLeaf){
            children.elementAt(i).traverse();
        }
    }


    public BNodeGeneric<T> search(T key){ //buscar un nodo
        int i = 0;
        while (i < num -1  && key.compareTo(keys.elementAt(i)) > 0) //avanzamos hasta que sea el ultimo nodo
            i++;  //o hasta que key no sea mayor
        
        if (keys.elementAt(i).compareTo(key) == 0)
            return this; //si es igual lo habremos encontrado
        else if(isLeaf) {
        	return null; //si es hoja no exixstira el valo
        }
        else
        	return children.elementAt(i).search(key); // de lo contrario bajamos al nodo hijo para buscar
    }

}
