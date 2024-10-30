package projetoesdarvores;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import java.awt.Color;

/**
 * Simulador de árvores binárias de busca:
 *     Simula as operações de inserir e remover chaves;
 *     Simula os percursos (pré-ordem, em ordem, pós-ordem e em nível).
 * 
 * @author Prof. Dr. David Buzatto
 */
public class SimuladorABB extends EngineFrame {

    public SimuladorABB() {
        super( 800, 600, "Simulador de Árvores Binárias de Busca", 60, true );
    }

    @Override
    public void create() {
        
    }

    @Override
    public void update() {
                            
    }

    @Override
    public void draw() {
        
    }
    
    private void desenharSeta( double x, double y, int tamanho, double graus, Color cor ) {
        
        drawLine( 
                x, y, 
                x + Math.cos( Math.toRadians( graus - 135 ) ) * tamanho,
                y + Math.sin( Math.toRadians( graus - 135 ) ) * tamanho,
                cor
        );
        
        drawLine( 
                x, y, 
                x + Math.cos( Math.toRadians( graus + 135 ) ) * tamanho,
                y + Math.sin( Math.toRadians( graus + 135 ) ) * tamanho,
                cor
        );
        
    }
    
    public static void main( String[] args ) {
        new SimuladorABB();
    }
    
}
