package fr.uge.legoandbeer;

import java.util.Objects;

public record Lego(int quantity, String name) implements Article {
	public Lego {
		Objects.requireNonNull(name);
		if (quantity <= 0) {
			throw new IllegalArgumentException("quantity <= 0");
		}
	}
	
//	@Override
//    public long price() {
//        return (long) quantity * name.length() * 20;
//    }
}
