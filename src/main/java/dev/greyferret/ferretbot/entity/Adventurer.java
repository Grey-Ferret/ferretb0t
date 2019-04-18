package dev.greyferret.ferretbot.entity;

import java.util.Objects;

public class Adventurer {
	public Adventurer(Viewer viewer) {
		this.viewer = viewer;
		if (viewer.isSub() || viewer.isVip()) {
			this.lives = 2;
		} else {
			this.lives = 1;
		}
		this.selectedKey = "";
	}

	private Viewer viewer;
	private int lives;
	private String selectedKey;

	public Viewer getViewer() {
		return viewer;
	}

	public void setViewer(Viewer viewer) {
		this.viewer = viewer;
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

	public String getSelectedKey() {
		return selectedKey;
	}

	public void setSelectedKey(String selectedKey) {
		this.selectedKey = selectedKey;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Adventurer)) return false;

		Adventurer that = (Adventurer) o;

		return Objects.equals(viewer, that.viewer);

	}

	@Override
	public int hashCode() {
		return viewer != null ? viewer.hashCode() : 0;
	}


}
