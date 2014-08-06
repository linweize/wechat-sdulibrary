package org.room325.yzm;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class ColorNum implements Comparable<ColorNum> {

	public int value = 0;

	public int count = 0;

	public ColorNum(int value) {
		this.value = value;
		count = 1;
	}

	public void add() {
		count++;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Integer) {
			return this.value == (Integer) obj;
		} else if (obj instanceof ColorNum) {
			return ((ColorNum) obj).value == this.value;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "ColorNum [value=" + value + ", count=" + count + "]";
	}

	@Override
	public int compareTo(ColorNum o) {
		return o.count - this.count;
	}
}
