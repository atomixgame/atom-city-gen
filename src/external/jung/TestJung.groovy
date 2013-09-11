/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jung
import javax.swing.*
import groovy.swing.SwingBuilder
import jung.TestJung2;

def swing = new SwingBuilder()
swing.frame(title: 'CityGen Test1', defaultCloseOperation: JFrame.EXIT_ON_CLOSE,
    size: [800, 600], show: true, locationRelativeTo: null) {
    lookAndFeel("system")
    
    panel(TestJung2.createView())
    
}