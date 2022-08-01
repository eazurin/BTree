public class BTreeGeneric<E extends Comparable<E>> {
	BNodeGeneric<E> root; //raiz del arbol
    int MinDeg; //minimo grado
    
    public BTreeGeneric(int deg){ //constructor
        this.root = null;
        this.MinDeg = deg;
    }
    
    public boolean add(E value) { //Operacion insertar
    		if (root == null){ //si la raiz en nula
                root = new BNodeGeneric<E>(MinDeg,true); //Creamos un nuevo nodo en la raiz
                root.keys.set(0, value); //Le asignamos la clave 
                root.num = 1; //modificamos el numero de claves 
                return true;  //REtornamos true ya que se inderto
            }
    		else if(search(value)) { //Si el valor ya existe en el arbol
    			System.out.println("Valor repetido");
    			return false; //Retornamos false ya que no lo insertaremos
    		}
            else {
                if (root.num == 2*MinDeg-1){   //Si la raiz esta llena
                    BNodeGeneric<E> s = new BNodeGeneric<E>(MinDeg,false); //creamos nodo
                    s.children.set(0, root); //su hijo resa la raiz
                    s.splitChild(0,root); //separamos la raiz por la propiedad de insercion del arbol b
                    int i = 0;
                    if (s.keys.elementAt(0).compareTo(value) < 0)
                        i++;
                    s.children.elementAt(i).insertNotFull(value);
                    root = s;
                }
                else {
                    root.insertNotFull(value);
                }
                return true; 
            }	
    }

    public E remove(E value) { //operación eliminar
        if (root == null){
            System.out.println("The tree is empty");
            return null; //Si el arbol esta vacio retornaremos null y enviaremos mensaje 
        }

        root.remove(value); //invocareremos a remove del BNodeGeneric

        if (root.num == 0){  
            if (root.isLeaf)  //si es hoja eliminamos
                root = null;
            else
                root = root.children.elementAt(0); //si no bajamos al hijo
        }
        return value;
    }

    public void clear() {
        this.root = null; //eliminamos el arbol          
        System.out.println("Árbol eliminado");
    }

    public boolean search(E value) {
        if (root == null){
            return false; //si la raiz es nula no existira el valor
        }
        else{
            if(root.search(value) == null) //si root.search nos retorna un nulo es porque no lo encontro
                return false;
            else
                return true;
        }
    }

    public int size() {
        if(this.root == null) { //si la raiz es nulo retornamos 0
        	return 0;
        }
        else {
        	int cont = 0;
        	 BNodeGeneric<E> current = this.root;
        	 while(current != null) { //Si no bajamos de nivel y vamos aumentando el contador
        		 cont++;
        		 current = current.children.elementAt(0);
        	 }
        	 return cont;
        }
    }

    public void traverse(){
        if (root != null){  //si no es nulo usamos traverse
            root.traverse();
        }
    }

    public static void main(String[] args) {

        BTreeGeneric<Integer> t = new BTreeGeneric<Integer>(2); 
        t.add(1);
        t.add(1);
        t.add(7);
        t.add(10);
        t.add(11);
        t.add(13);
        t.add(14);
        t.add(15);
        t.traverse();
        System.out.println();
        t.remove(11);
        t.traverse();
        System.out.println();
        System.out.println(t.size());
        System.out.println(t.search(1));
    }
}
