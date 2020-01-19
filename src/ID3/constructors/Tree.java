package ID3.constructors;

import java.util.Arrays;

public class Tree {
    public Tree[] children;
    public int value;

    public Tree() {
    }

    public Tree(Tree[] children, int value) {
        this.children = children;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Tree{" +
                "children=" + Arrays.toString(children) +
                ", value=" + value +
                '}';
    }
}
