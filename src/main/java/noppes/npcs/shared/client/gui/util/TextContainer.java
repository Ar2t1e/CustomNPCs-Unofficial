package noppes.npcs.shared.client.gui.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextContainer {

	public static boolean colored = true;

	public Pattern regexString = Pattern.compile("([\"'])(?:\\\\.|[^\"'])*?\\1", Pattern.MULTILINE);
	public Pattern regexFunction = Pattern.compile("\\b(if|else|switch|with|for|while|in|var|const|let|throw|then|function|continue|break|foreach|return|try|catch|finally|do|this|typeof|instanceof|new)(?=\\W)");
	public Pattern regexWord = Pattern.compile("(\\p{L}+\\.?|[./,:;!|])+|\n|$");
	public Pattern regexNumber = Pattern.compile("\\b-?(?:0[xX][\\dA-Fa-f]+|0[bB][01]+|0[oO][0-7]+|\\d*\\.?\\d+(?:[Ee][+-]?\\d+)?[fFbBdDlLsS]?|NaN|null|Infinity|unidentified|true|false)\\b");
	public Pattern regexComment = Pattern.compile("/\\*[\\s\\S]*?(?:\\*/|$)|//.*|#.*");
	public String text;
	public List<TextMarkUp> makeup = new ArrayList<>();
	public List<TextLineData> lines = new ArrayList<>();
	public int visibleLines = 1;
	public int lineHeight;
	public int totalHeight;
	public int linesCount;

	private final Map<String, Integer> hash = new HashMap<>();
	private final TrueTypeFont font;
	private final int width;
	private final int height;
	private boolean lighting;

	public TextContainer(String textIn, TrueTypeFont fontIn, int widthIn, int heightIn, boolean lightingIn) {
		text = textIn.replaceAll("\\r?\\n|\\r", "\n");
		font = fontIn;
		width = widthIn;
		height = heightIn;
		lighting = lightingIn;
	}

	public TextContainer copy() {
		TextContainer container = new TextContainer(text, font, width, height, lighting);
		container.text = text;
		for (TextMarkUp mark : new ArrayList<>(makeup)) { container.makeup.add(new TextMarkUp(mark.start, mark.end, mark.c, mark.level)); }
		for (TextLineData line : new ArrayList<>(lines)) { container.lines.add(new TextLineData(line.text, line.start, line.end)); }
		container.visibleLines = visibleLines;
		container.lineHeight = lineHeight;
		container.totalHeight = totalHeight;
		container.linesCount = linesCount;
		return container;
	}

	public void setLighting(boolean lightingIn) {
		lighting = lightingIn;
		if (lighting) { formatCodeText(); }
	}

	public boolean compareMarkUps(TextMarkUp mu1, TextMarkUp mu2) {
		return mu1 == null || mu1.start > mu2.start;
	}

	public void formatCodeText() {
		if (!colored) { return; }
		makeup.clear();
		TextMarkUp markup;
		for (int start = 0; (markup = getNextMatching(start)) != null; start = markup.end) { makeup.add(markup); }
	}

	private TextMarkUp getNextMatching(int start) {
		TextMarkUp markup = null;
		String s = text.substring(start);
		// Numbers
		Matcher matcher = regexNumber.matcher(s);
		if (matcher.find()) {
			markup = new TextMarkUp(matcher.start(), matcher.end(), '6', 0);
		}
		// Functions
		matcher = regexFunction.matcher(s);
		if (matcher.find()) {
			TextMarkUp markup2 = new TextMarkUp(matcher.start(), matcher.end(), '2', 0);
			if (compareMarkUps(markup, markup2)) { markup = markup2; }
		}
		// Strings
		matcher = regexString.matcher(s);
		if (matcher.find()) {
			TextMarkUp markup2 = new TextMarkUp(matcher.start(), matcher.end(), '4', 7);
			if (compareMarkUps(markup, markup2)) { markup = markup2; }
		}
		// Comments
		matcher = regexComment.matcher(s);
		if (matcher.find()) {
			TextMarkUp markup2 = new TextMarkUp(matcher.start(), matcher.end(), '8', 7);
			if (compareMarkUps(markup, markup2)) { markup = markup2; }
		}
		// Offset
		if (markup != null) {
            markup.start += start;
            markup.end += start;
		}
		return markup;
	}

	public void init() {
		lines.clear();
		if (lineHeight == 0) { lineHeight = 12; }
		int totalChars = 0;
		for (String l : text.split("\n")) {
			StringBuilder line = new StringBuilder();
			int lineWidth = 0;
			Matcher m = regexWord.matcher(l);
			for (int i = 0; m.find(); i = m.start()) {
				String word = l.substring(i, m.start());
				// end current line and start new
				if (lineWidth + getWordWidth(word) > width - 10) {
					lines.add(new TextLineData(line.toString(), totalChars, totalChars + line.length()));
					totalChars += line.length();
					line = new StringBuilder();
					lineWidth = 0;
				}
				// next, if word is too long
				if (getWordWidth(word)  > width - 10) {
					StringBuilder w = new StringBuilder();
					for (int c = 0; c < word.length(); c++) {
						if (font.width(w.toString() + word.charAt(c)) <= width - 10) { w.append(word.charAt(c)); }
						else {
							lines.add(new TextLineData(w.toString(), totalChars, totalChars + w.length()));
							totalChars += w.length();
							line = new StringBuilder();
							lineWidth = 0;
							w = new StringBuilder("" + word.charAt(c));
						}
					}
					line.append(w);
					lineWidth += getWordWidth(w.toString());
				}
				else {
					line.append(word);
					lineWidth += getWordWidth(word);
				}
			}
			lines.add(new TextLineData(line.toString(), totalChars, totalChars + line.length() + 1));
			totalChars += line.length() + 1;
		}
		linesCount = lines.size();
		totalHeight = linesCount * lineHeight;
		visibleLines = Math.max(height / lineHeight, 1);
		if (lighting) { formatCodeText(); }
	}

	private int getWordWidth(String word) {
		if (!hash.containsKey(word)) { hash.put(word, font.width(word)); }
		return hash.get(word);
	}

}
