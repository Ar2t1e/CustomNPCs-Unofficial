package nikedemos.markovnames;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.util.LogWriter;

public class MarkovDictionary {

	public static final Random rnd = new Random();
	protected final HashMap2D<String, String, Integer> occurrences = new HashMap2D<>();
	protected int sequenceLen;

	public MarkovDictionary(String dictionary) { this(dictionary, 3); }

	public MarkovDictionary(String dictionary, int seqlen) {
		sequenceLen = 3;
		try { applyDictionary(dictionary, seqlen); } catch (Exception e) { LogWriter.error(e); }
	}

	public String getCapitalized(String str) {
		if (str == null || str.isEmpty()) { return str; }
		char[] chars = str.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return new String(chars);
	}

	public void incrementSafe(String key_0, String key_1) {
		if (occurrences.containsKeys(key_0, key_1)) {
			int curr = occurrences.get(key_0, key_1);
			occurrences.put(key_0, key_1, curr + 1);
		}
		else { occurrences.put(key_0, key_1, 1); }
	}

	public String generateWord() {
		if (occurrences.mMap.isEmpty()) { return "Noppes"; }
		int allEntries = 0;
		for (Map.Entry<String, Map<String, Integer>> pair : occurrences.mMap.entrySet()) {
			String k = pair.getKey();
			if (k.startsWith("_[") && k.endsWith("_")) { allEntries += occurrences.get(k, "_TOTAL_"); }
		}
		int randomNumber = rnd.nextInt(allEntries);
		Iterator<Map.Entry<String, Map<String, Integer>>> it = occurrences.mMap.entrySet().iterator();
		StringBuilder sequence = new StringBuilder();
		while(it.hasNext()) {
			Map.Entry<String, Map<String, Integer>> pair = it.next();
			String k = pair.getKey();
			if (k.startsWith("_[") && k.endsWith("_")) {
				int topLevelEntries = occurrences.get(k, "_TOTAL_");
				if (randomNumber < topLevelEntries) {
					sequence.append(k, 1, sequenceLen + 1);
					break;
				}
				randomNumber -= topLevelEntries;
			}
		}
		StringBuilder word = new StringBuilder();
		word.append(sequence);
		while(sequence.charAt(sequence.length() - 1) != ']') {
			int subSize = 0;
			Map.Entry<String, Integer> entry;
			for(Iterator<Map.Entry<String, Integer>> j = occurrences.mMap.get(sequence.toString()).entrySet().iterator(); j.hasNext(); subSize += entry.getValue()) {
				entry = j.next();
			}
			randomNumber = rnd.nextInt(subSize);
			Iterator<Map.Entry<String, Integer>> k = occurrences.mMap.get(sequence.toString()).entrySet().iterator();
			String chosen;
			int occu;
			for(chosen = ""; k.hasNext(); randomNumber -= occu) {
				entry = k.next();
				occu = occurrences.get(sequence.toString(), entry.getKey());
				if (randomNumber < occu) {
					chosen = entry.getKey();
					break;
				}
			}
			word.append(chosen);
			sequence.delete(0, 1);
			sequence.append(chosen);
		}
		return getPost(word.substring(1, word.length() - 1));
	}

	public String getPost(String str) { return getCapitalized(str); }

	public void applyDictionary(String dictionaryFile, int seqLen) throws IOException {
		StringBuilder input = new StringBuilder();
		ResourceLocation resource = new ResourceLocation(CustomNpcs.MODID + ":markovnames/" + dictionaryFile);
		BufferedReader readIn = new BufferedReader(new InputStreamReader(getResource(resource), StandardCharsets.UTF_8));
		for (String line = readIn.readLine(); line != null; line = readIn.readLine()) { input.append(line).append(" "); }
		readIn.close();
		if (input.length() == 0) { throw new RuntimeException("Resource was empty: + " + resource); }
		if (sequenceLen != seqLen) {
			sequenceLen = seqLen;
			occurrences.clear();
		}
		String input_str = '[' + input.toString().toLowerCase().replaceAll("[\\t\\n\\r\\s]+", "][") + ']';
		for (int maxCursorPos = input_str.length() - 1 - sequenceLen, i = 0; i <= maxCursorPos; ++i) {
			String seqCurr = input_str.substring(i, i + sequenceLen);
			String seqNext = input_str.substring(i + sequenceLen, i + sequenceLen + 1);
			incrementSafe(seqCurr, seqNext);
            incrementSafe("_" + seqCurr + "_", "_TOTAL_");
		}
	}

	private InputStream getResource(ResourceLocation resourceLocation) {
		ModContainer container = Loader.instance().activeModContainer();
		if (container == null) { throw new RuntimeException("Failed to find current mod while looking for resource " + resourceLocation); }
		String resourcePath = String.format("/%s/%s/%s", "assets", resourceLocation.getResourceDomain(), resourceLocation.getResourcePath());
		InputStream resourceAsStream = null;
		try { resourceAsStream = container.getMod().getClass().getResourceAsStream(resourcePath); }
		catch (Exception e) { LogWriter.error(e); }
		if (resourceAsStream != null) { return resourceAsStream; }
		throw new RuntimeException("Could not find resource " + resourceLocation);
	}

}
