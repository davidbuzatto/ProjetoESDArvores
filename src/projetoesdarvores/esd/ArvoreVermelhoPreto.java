package projetoesdarvores.esd;

import aesd.ds.implementations.linear.LinkedQueue;
import aesd.ds.implementations.linear.ResizingArrayList;
import aesd.ds.interfaces.List;
import aesd.ds.interfaces.Queue;
import java.util.Iterator;

/**
 * Implementação de uma árvore vermelho-preto (Red-Black Tree).
 * 
 * Implementação baseada na obra: SEDGEWICK, R.; WAYNE, K. Algorithms. 
 * 4. ed. Boston: Pearson Education, 2011. 955 p.
 * 
 * @param <Key> Tipo das chaves que serão armazenadas na árvore.
 * @param <Value> Tipo dos valores associados às chaves armazenadas na árvore.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class ArvoreVermelhoPreto<Key extends Comparable<Key>, Value> implements Iterable<Key> {

    /*
     * Classe interna estática que define os nós da árvore vermelho-preto.
     */
    public static class Node<Key extends Comparable<Key>, Value> {
        
        Key key;
        Value value;
        Node<Key, Value> left;
        Node<Key, Value> right;

        NodeColor color;
        int size;
        
        @Override
        public String toString() {
            return key + " -> " + value + " (" + ( color == NodeColor.RED ? "R" : "B" ) + ")";
        }
        
    }
    
    /*
     * Enumeração para especificação da cor do nó.
     */
    private enum NodeColor {
        RED,
        BLACK
    }

    // raiz da árvore
    private Node<Key, Value> root;
    
    /**
     * Constrói uma Árvore vermelho-preto vazia.
     */
    public ArvoreVermelhoPreto() {
        root = null;
    }
    
    /**
     * Verifica se um nó é vermelho.
     * 
     * @param node O nó a ser verificado
     * @return true caso o nó seja vermelho, false caso seja preto ou null.
     */
    private boolean isRed( Node<Key, Value> node ) {
        if ( node == null ) {
            return false;
        }
        return ( ( Node<Key, Value>) node ).color == NodeColor.RED;
    }

    /**
     * Retorna o tamanho do nó.
     * 
     * @param node O nó a ser verificado.
     * @return O tamanho do nó na subárvore enraizada em node ou 0
     * caso seja null.
     */
    private int nodeSize( Node<Key, Value> node ) {
        if ( node == null ) {
            return 0;
        }
        return ( (Node<Key, Value>) node ).size;
    }
    
    public int getSize() {
        return nodeSize( root );
    }
    
    public void put( Key key, Value value ) throws IllegalArgumentException {
        
        if ( key == null ) {
            throw new IllegalArgumentException( "first argument to put() is null" );
        }
        
        if ( value == null ) {
            delete( key );
            return;
        }
        
        root = (Node<Key, Value>) put( root, key, value );
        root.color = NodeColor.BLACK;
        
    }
    
    private Node<Key, Value> put( Node<Key, Value> node, Key key, Value value ) {
        
        if ( node == null ) {
            
            Node<Key, Value> rbNode = new Node<>();
            rbNode.key = key;
            rbNode.value = value;
            rbNode.left = null;
            rbNode.right = null;
            rbNode.color = NodeColor.RED;
            rbNode.size = 1;
            
            node = rbNode;
            
            return node;
            
        }

        int comp = key.compareTo( node.key );
        
        if ( comp < 0 ) {
            node.left = put( node.left, key, value );
        } else if ( comp > 0 ) {
            node.right = put( node.right, key, value );
        } else {
            node.value = value;
        }
            
        // consertando os links inclinados à direita
        if ( isRed( node.right ) && !isRed( node.left ) ) {
            node = rotateLeft( node );
        }
        if ( isRed( node.left ) && isRed( node.left.left ) ) {
            node = rotateRight( node );
        }
        if ( isRed( node.left ) && isRed( node.right ) ) {
            flipColors( node );
        }

        ( (Node<Key, Value>) node ).size = nodeSize( node.left ) + nodeSize( node.right ) + 1;

        return node;
        
    }
    
    public Value get( Key key ) throws IllegalArgumentException {
        if ( key == null ) {
            throw new IllegalArgumentException( "argument to get() is null" );
        }
        return get( root, key );
    }
    
    private Value get( Node<Key, Value> node, Key key ) {
        
        while ( node != null ) {
            
            int comp = key.compareTo( node.key );
            
            if ( comp < 0 ) {
                node = node.left;
            } else if ( comp > 0 ) {
                node = node.right;
            } else {
                return node.value;
            }
            
        }

        return null;

    }
    
    public void delete( Key key ) throws IllegalArgumentException {
        
        if ( key == null ) {
            throw new IllegalArgumentException( "argument to delete() is null" );
        }
        
        if ( !contains( key ) ) {
            return;
        }
        
        // se ambos os filhos da raiz forem pretos, configura a raiz como vermelho
        if ( !isRed( root.left ) && !isRed( root.right ) ) {
            root.color = NodeColor.RED;
        }

        root = (Node<Key, Value>) delete( root, key );
        if ( !isEmpty() ) {
            root.color = NodeColor.BLACK;
        }
        
    }
    
    private Node<Key, Value> delete( Node<Key, Value> node, Key key ) {

        if ( key.compareTo( node.key ) < 0 ) {
            
            if ( !isRed( node.left ) && !isRed( node.left.left ) ) {
                node = moveRedLeft( node );
            }
            
            node.left = delete( node.left, key );
            
        } else {
            
            if ( isRed( node.left ) ) {
                node = rotateRight( node );
            }
            
            if ( key.compareTo( node.key ) == 0 && ( node.right == null ) ) {
                return null;
            }
            
            if ( !isRed( node.right ) && !isRed( node.right.left ) ) {
                node = moveRedRight( node );
            }
            
            if ( key.compareTo( node.key ) == 0 ) {
                Node<Key, Value> x = min( node.right );
                node.key = x.key;
                node.value = x.value;
                node.right = deleteMin( node.right );
            } else {
                node.right = delete( node.right, key );
            }
            
        }
        
        return balance( node );
        
    }
    
    public boolean contains( Key key ) throws IllegalArgumentException {
        return get( key ) != null;
    }
    
    private Node<Key, Value> deleteMin( Node<Key, Value> node ) {
        
        if ( node.left == null ) {
            return null;
        }

        if ( !isRed( node.left ) && !isRed( node.left.left ) ) {
            node = moveRedLeft( node );
        }

        node.left = deleteMin( node.left );
        
        return balance( node );
        
    }
    
    /**
     * Faz com que o link inclinado à esquerda se incline para a direita.
     * 
     * @param node nó de origem
     * @return a nova raiz da subárvore.
     */
    private Node<Key, Value> rotateRight( Node<Key, Value> node ) {
        
        Node<Key, Value> newRoot = (Node<Key, Value>) node.left;
        node.left = newRoot.right;
        
        newRoot.right = node;
        newRoot.color = ( ( Node<Key, Value> ) newRoot.right ).color;
        ( ( Node<Key, Value> ) newRoot.right ).color = NodeColor.RED;
        newRoot.size = ( ( Node<Key, Value> ) node ).size;
        
        ( ( Node<Key, Value> ) node ).size = nodeSize( node.left ) + nodeSize( node.right ) + 1;
        
        return newRoot;
        
    }

    /**
     * Faz com que o link inclinado à direita se incline para a esquerda.
     * 
     * @param node nó de origem
     * @return a nova raiz da subárvore.
     */
    private Node<Key, Value> rotateLeft( Node<Key, Value> node ) {
        
        Node<Key, Value> newRoot = (Node<Key, Value>) node.right;
        node.right = newRoot.left;
        
        newRoot.left = node;
        newRoot.color = ( ( Node<Key, Value> ) newRoot.left ).color;
        ( ( Node<Key, Value> ) newRoot.left ).color = NodeColor.RED;
        newRoot.size = ( ( Node<Key, Value> ) node ).size;
        
        ( ( Node<Key, Value> ) node ).size = nodeSize( node.left ) + nodeSize( node.right ) + 1;
        
        return newRoot;
        
    }

    /**
     * Inverte a cor de um nó e de seus filhos.
     * 
     * @param rbNode nó a ser alterado.
     */
    private void flipColors( Node<Key, Value> node ) {
        
        Node<Key, Value> rbNode = (Node<Key, Value>) node;
        Node<Key, Value> rbNodeLeft = (Node<Key, Value>) node.left;
        Node<Key, Value> rbNodeRight = (Node<Key, Value>) node.right;
        
        rbNode.color           = rbNode.color == NodeColor.RED ? NodeColor.BLACK : NodeColor.RED;
        rbNodeLeft.color   = rbNodeLeft.color == NodeColor.RED ? NodeColor.BLACK : NodeColor.RED;
        rbNodeRight.color = rbNodeRight.color == NodeColor.RED ? NodeColor.BLACK : NodeColor.RED;
        
    }

    /**
     * Assumindo que o nó é vermelho e ambos os seus filhos são pretos, faz
     * com que a esquerda do nó ou um de seus filhos seja vermelho.
     * 
     * @param node nó a ser movido.
     * @return o nó modificado.
     */
    private Node<Key, Value> moveRedLeft( Node<Key, Value> node ) {

        flipColors( node );
        
        if ( isRed( node.right.left ) ) {
            node.right = rotateRight( node.right );
            node = rotateLeft( node );
            flipColors( node );
        }
        
        return node;
        
    }

    /**
     * Assumindo que o nó é vermelho e ambos os seus filhos são pretos, faz
     * com que nó da direita ou um de seus filhos seja vermelho.
     * 
     * @param no nó a ser movido.
     * @return o nó modificado.
     */
    private Node<Key, Value> moveRedRight( Node<Key, Value> no ) {
        
        flipColors( no );
        
        if ( isRed( no.left.left ) ) {
            no = rotateRight( no );
            flipColors( no );
        }
        
        return no;
        
    }

    /**
     * Recupera a condição de existência (invariante) para a árvore vermelho-preto.
     * 
     * @param node nó de origem.
     * @return o nó modificado.
     */
    private Node<Key, Value> balance( Node<Key, Value> node ) {

        if ( isRed( node.right ) ) {
            node = rotateLeft( node );
        }
        
        if ( isRed( node.left ) && isRed( node.left.left ) ) {
            node = rotateRight( node );
        }
        
        if ( isRed( node.left ) && isRed( node.right ) ) {
            flipColors( node );
        }

        ( (Node<Key, Value>) node ).size = nodeSize( node.left ) + nodeSize( node.right ) + 1;
        
        return node;
        
    }
    
    private Node<Key, Value> min( Node<Key, Value> node ) {
        
        if ( node.left == null ) {
            return node;
        } else {
            return min( node.left );
        }
        
    }
    
    public void clear() {
        root = (Node<Key, Value>) clear( root );
    }

    private Node<Key, Value> clear( Node<Key, Value> node ) {

        if ( node != null ) {
            node.left = clear( node.left );
            node.right = clear( node.right );
        }

        return null;

    }
    
    public boolean isEmpty() {
        return root == null;
    }
    
    @Override
    public Iterator<Key> iterator() {
        return traverseInOrder().iterator();
    }
    
    public Iterable<Key> getKeys() {
        Queue<Key> keys = new LinkedQueue<>();
        for ( Key k : traverseInOrder() ) {
            keys.enqueue( k );
        }
        return keys;
    }
    
    private List<Key> traverseInOrder() {
        List<Key> keys = new ResizingArrayList<>();
        inOrder( root, keys );
        return keys;
    }
    
    private void inOrder( Node<Key, Value> node, List<Key> keys ) {
        if ( node != null ) {
            inOrder( node.left, keys );
            keys.add( node.key );
            inOrder( node.right, keys );
        }
    }
    
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        
        if ( !isEmpty()) {
            preOrderForPrint( root, "", null, sb );
        } else {
            sb.append( "empty red-black tree!\n" );
        }
        
        return sb.toString();
        
    }
    
    private void preOrderForPrint( Node<Key, Value> node, String ident, String leftRight, StringBuilder sb ) {
        
        if ( node != null ) {
            
            String rootIdent = "";
            String leafIdent = "";
            
            if ( node != root ) {
                rootIdent = ident + "|--";
                leafIdent = ident + "|  ";
            }
            
            sb.append( rootIdent );
            if ( leftRight != null ) {
                sb.append( "(" ).append( leftRight ).append( ") " );
            }
            sb.append( node );
            if ( node == root ) {
                sb.append(  " <- root" );
            }
            sb.append( "\n" );
            
            preOrderForPrint( node.left, leafIdent, "L", sb );
            preOrderForPrint( node.right, leafIdent, "R", sb );
            
        }
        
    }
    
}
