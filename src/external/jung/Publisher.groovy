/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jung

class Publisher {  
   String name  
   List books = []  
  
   String toString() { name }  
}  
  
// griffon-app/domain/Book.groovy  
class Book {  
   String title  
   List authors = []  
  
   String toString() { title  }  
}  
  
// griffon-app/domain/Author.groovy  
class Author {  
   String name  
  
   String toString() { name }  
}  