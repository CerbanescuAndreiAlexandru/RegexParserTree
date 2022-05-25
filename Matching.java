import javax.swing.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.regex.Matcher;

public class Matching {

	int nStart,nEnd;
	TextHighlighter textHighlighter = new TextHighlighter();

	public void testRegex(final String sRegex, JTextArea textArea) {
		try {
			final Pattern pattern = Pattern.compile(sRegex);
			final Matcher matcher = pattern.matcher(textArea.getText());

			boolean found = false;

			while(matcher.find()) {
				 nStart = matcher.start();
				 nEnd = matcher.end();
				String sMatched = textArea.getText().substring(nStart, nEnd);
				textHighlighter.highlight(textArea,nStart,nEnd);
				found = true;

				System.out.println(sMatched);
			}

			if(!found)
				System.out.println("No matches found.");
		}
		catch(PatternSyntaxException e) {
			System.out.println(e);
		}
	}
}