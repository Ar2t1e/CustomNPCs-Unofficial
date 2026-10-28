package moe.plushie.armourers_workshop.api.common.skin.data;

import moe.plushie.armourers_workshop.api.common.library.ILibraryFile;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;

@SuppressWarnings("unused")
public interface ISkinIdentifier {

	int getSkinGlobalId();

	ILibraryFile getSkinLibraryFile();

	int getSkinLocalId();

	ISkinType getSkinType();

	boolean hasGlobalId();

	boolean hasLibraryFile();

	boolean hasLocalId();

	boolean isValid();

}
