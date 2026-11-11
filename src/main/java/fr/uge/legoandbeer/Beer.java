package fr.uge.legoandbeer;

import java.util.Objects;

public record Beer(int quantity, BeerKind kind) implements Article {
	public Beer {
		Objects.requireNonNull(kind);
		if (quantity <= 0) {
			throw new IllegalArgumentException("quantity <= 0");
		}
	}
	
//	@Override
//    public long price() {
//        return switch (kind) {
//        case BLONDE -> quantity;
//        case default -> quantity * 3L;
//        };
//    }
}
