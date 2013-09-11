/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package script

dirName = "D:/JGE/MY GAMES/CityGen/src/images/gimp_icon/DPixel-black/images"
new File(dirName).eachFile() { file -> 
    if (file.getName() =~ /.png/){
        def newName = file.getName().replace("stock-tool-","")
        File f = new File(dirName + "/" + newName)
        file.renameTo(f)
        println file.getName() + " -> " + f.getName() 
        //println file.getName() + " -> " + newName
    }
} 
