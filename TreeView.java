import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TreeView extends JFrame
{
    private JPanel panel;

    TreeView(Expression expression)
    {
        setType(Type.UTILITY);
        setFont(new Font("Times New Roman", Font.BOLD, 14));
        setTitle("ARBORE DE PARSARE");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 200,400 );
        panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.setBackground(new Color(159, 191, 223));
        setContentPane(panel);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));


        JLabel label = new JLabel("<html>Expresia Regex: " + expression.getsExp()+"<html>");
        label.setFont(new Font("Tahoma", Font.BOLD, 12));
        label.setPreferredSize(new Dimension(200,30));
        panel.add(label);



        JTree tree = new ParserTree().build(expression);
        panel.add(tree);
    }
}
