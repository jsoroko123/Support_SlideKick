package com.support.objects;


public class NavigationItem {

	public int stringTitle;
	public int drawableIcon;
	private int index;


	public NavigationItem(int stringTitle, int drawableIcon, int mIndex) {
		this.stringTitle = stringTitle;
		this.drawableIcon = drawableIcon;
		this.index = mIndex;
	}

	public int getStringTitle() {
		return stringTitle;
	}

	public void setStringTitle(int stringTitle) {
		this.stringTitle = stringTitle;
	}

	public int getDrawableIcon() {
		return drawableIcon;
	}

	public void setDrawableIcon(int drawableIcon) {
		this.drawableIcon = drawableIcon;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}