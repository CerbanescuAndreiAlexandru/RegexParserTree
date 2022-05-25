import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MatcherMenu extends JFrame {

    private JPanel contentPane;


    public MatcherMenu(String expression) {
        setTitle("ANALIZA UNEI EXPRESII REGEX");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 795, 549);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setBackground(new Color(159, 191, 223));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel label = new JLabel("Introduceti textul:");
        label.setFont(new Font("Times New Roman", Font.BOLD, 21));
        label.setBounds(310, 160, 259, 63);
        contentPane.add(label);

        JButton matchButton = new JButton("Matching");
        matchButton.setFont(new Font("Tahoma", Font.BOLD, 20));
        matchButton.setBackground(new Color(67, 115, 163));
        matchButton.setBounds(24, 360, 154, 85);
        contentPane.add(matchButton);

        JTextArea text = new JTextArea();
        text.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        text.setBounds(10, 234, 759, 100);
        contentPane.add(text);

        JButton closeButton = new JButton("Close");
        closeButton.setBounds(680, 476, 89, 23);
        contentPane.add(closeButton);

        JLabel expLabel = new JLabel(expression);
        expLabel.setFont(new Font("Times New Roman", Font.BOLD, 21));
        expLabel.setBounds(10, 60, 759, 89);
        contentPane.add(expLabel);

        JLabel label2 = new JLabel("Expresia regex:");
        label2.setFont(new Font("Times New Roman", Font.BOLD, 21));
        label2.setBounds(310, 11, 170, 38);
        contentPane.add(label2);

        matchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Parser parser = new Parser();
                Expression exp = parser.Parse(expression);
                if(exp.getIsValid())
                {
                    Matching matching = new Matching();
                    matching.testRegex(expression,text);
                }
                else
                    JOptionPane.showMessageDialog(null,exp.getNposErrorDescription());

            }
        });

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

    }
}
