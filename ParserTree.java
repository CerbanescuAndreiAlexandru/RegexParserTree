import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Vector;

public class ParserTree
{
    public JTree build(Expression expr){
        String expr1 = expr.getsExp();
        DefaultMutableTreeNode root=new DefaultMutableTreeNode("Root");

        Vector<DefaultMutableTreeNode> nodes = new Vector<DefaultMutableTreeNode>();
        nodes.add(root);
        Vector<Parser.StateElement> tree = expr.getTree();
        int k = 0;
        String txt = "";

        int d = -1;

        for (int i = 0; i < tree.size(); i++) {
            if(tree.get(i).state == Parser.State.SET_OPEN) { // [
                if(k > 0) {
                    k = tree.get(i).nPos;
                    if(txt != "") {
                        DefaultMutableTreeNode x=new DefaultMutableTreeNode(txt);
                        nodes.elementAt(nodes.size() - 1).add(x);
                        txt = "";
                    }
                }
                d = tree.get(i).nPos;
                DefaultMutableTreeNode x=new DefaultMutableTreeNode("[  ]");
                nodes.elementAt(nodes.size() - 1).add(x);
                nodes.add(x);
            }
            else if(tree.get(i).state == Parser.State.SET_FINISH || tree.get(i).state == Parser.State.SET_AFTER_SET || (tree.get(i).state == Parser.State.TERM && tree.get(i - 1).state == Parser.State.PAREN_FLAG) || tree.get(i).state == Parser.State.INTERVAL_TYPE) { // ], ), }
                d = tree.get(i).nPos;
                if(txt != "") {
                    DefaultMutableTreeNode txt1=new DefaultMutableTreeNode(txt);
                    nodes.elementAt(nodes.size() - 1).add(txt1);
                    txt = "";
                }
                if(nodes.size() > 1)
                    nodes.removeElementAt(nodes.size() - 1);
            }
            else if(tree.get(i).state == Parser.State.SET_AFTER_RANGE) {
                if(tree.get(i).nPos != k) {
                    k = tree.get(i).nPos;
                    if(k != d) {
                        txt = txt + expr1.charAt(k);
                    }
                }
                d = tree.get(i).nPos;
                if(txt != "") {
                    DefaultMutableTreeNode txt1=new DefaultMutableTreeNode(txt);
                    nodes.elementAt(nodes.size() - 1).add(txt1);
                    txt = "";
                }
            }
            else if(tree.get(i).state == Parser.State.OPEN_PAREN || tree.get(i).state == Parser.State.OPEN_PAREN_QUANT) { // [
                if(k > 0) {
                    k = tree.get(i).nPos;
                    if(txt != "") {
                        DefaultMutableTreeNode x=new DefaultMutableTreeNode(txt);
                        nodes.elementAt(nodes.size() - 1).add(x);
                        txt = "";
                    }
                }
                d = tree.get(i).nPos;
                DefaultMutableTreeNode x=new DefaultMutableTreeNode("(  )");
                nodes.elementAt(nodes.size() - 1).add(x);
                nodes.add(x);
            }
            else if(tree.get(i).state == Parser.State.INTERVAL_OPEN) { // [
                if(k > 0) {
                    k = tree.get(i).nPos;
                    if(txt != "") {
                        DefaultMutableTreeNode x=new DefaultMutableTreeNode(txt);
                        nodes.elementAt(nodes.size() - 1).add(x);
                        txt = "";
                    }
                }
                d = tree.get(i).nPos;
                DefaultMutableTreeNode x=new DefaultMutableTreeNode("{  }");
                nodes.elementAt(nodes.size() - 1).add(x);
                nodes.add(x);
            }
            else if(tree.get(i).nPos != k) {
                k = tree.get(i).nPos;
                if(k != d && expr1.charAt(k) != '[') {
                    txt = txt + expr1.charAt(k);
                }

            }
        }
        if(tree.get(tree.size() - 1).nPos != k) {
            k = tree.get(tree.size() - 1).nPos;
            if(tree.get(tree.size() - 1).nPos != d) {
                txt = txt + expr1.charAt(k);
            }
        }
        if(txt != "") {
            DefaultMutableTreeNode txt1=new DefaultMutableTreeNode(txt);
            nodes.elementAt(nodes.size() - 1).add(txt1);
        }
        JTree jt=new JTree(root);

        return jt;
    }
}

