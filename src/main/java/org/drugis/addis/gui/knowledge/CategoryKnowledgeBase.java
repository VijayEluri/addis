package org.drugis.addis.gui.knowledge;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.gui.CategoryKnowledge;

public abstract class CategoryKnowledgeBase implements CategoryKnowledge {
	private final String d_singular;
	private final String d_plural;
	private final String d_iconName;
	
	public CategoryKnowledgeBase(String singular, String iconName) {
		this(singular, singular + "s", iconName);
	}
	
	public CategoryKnowledgeBase(String singular, String plural, String iconName) {
		d_singular = singular;
		d_plural = plural;
		d_iconName = iconName;
	}

	public String getPlural() {
		return d_plural;
	}

	public String getSingular() {
		return d_singular;
	}
	
	public String getIconName(Class<? extends Entity> cls) {
		return d_iconName;
	}
}