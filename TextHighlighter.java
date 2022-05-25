import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;

public class TextHighlighter
{
    public void highlight(JTextArea tarea, int startPos,int endPos) {
        Highlighter highlighter = tarea.getHighlighter();
        tarea.requestFocusInWindow();

        Highlighter.HighlightPainter redPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
        try {
            highlighter.addHighlight(startPos, endPos, redPainter);
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
