import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {

    private JPanel contentPane;
    private JTextField expressionField;

    public MainMenu(DB dataBase) {
        setForeground(Color.RED);
        setBackground(Color.BLACK);
        setType(Type.UTILITY);
        setFont(new Font("Times New Roman", Font.BOLD, 14));
        setTitle("ANALIZA UNEI EXPRESII REGEX");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 765, 550);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(159, 191, 223));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel label = new JLabel("Introduceti expresia regex :");
        label.setFont(new Font("Tahoma", Font.BOLD, 20));
        label.setBounds(237, 173, 329, 55);
        contentPane.add(label);

        expressionField = new JTextField();
        expressionField.setBounds(10, 253, 729, 43);
        contentPane.add(expressionField);
        expressionField.setColumns(20);

        JButton MatchingButton = new JButton("Matching");
        MatchingButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        MatchingButton.setBackground(new Color(67, 115, 163));
        MatchingButton.setBounds(160, 336, 120, 55);
        contentPane.add(MatchingButton);

        JButton parseButton = new JButton("Parsare");
        parseButton.setForeground(Color.BLACK);
        parseButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        parseButton.setBackground(new Color(67, 115, 163));
        parseButton.setBounds(491, 336, 120, 55);
        contentPane.add(parseButton);

        parseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Parser parser = new Parser();
                Expression expression = parser.Parse(expressionField.getText());
                dataBase.Write(expression);
                if(expression.getIsValid())
                {
                    int optionPane = JOptionPane.showConfirmDialog(null,"Expresia a fost parsata cu succes\nVizualizati arborele de parsare?","",JOptionPane.YES_NO_OPTION);
                    if(optionPane == 0)
                    {
                        TreeView tree = new TreeView(expression);
                        tree.setVisible(true);
                    }

                }
                else
                    JOptionPane.showMessageDialog(null,expression.getNposErrorDescription());
            }
        });

        JButton exitButton = new JButton("EXIT");
        exitButton.setBounds(650, 477, 89, 23);
        contentPane.add(exitButton);

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        MatchingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    MatcherMenu matcherMenu = new MatcherMenu(expressionField.getText());
                    matcherMenu.setVisible(true);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
    }
}
