package ID3;

import ID3.constructors.ID3;

public class main {
    public static void main(String []args){
        ID3 id3=new ID3();
        id3.train();
        id3.printTree();
        id3.classify();

    }


}
