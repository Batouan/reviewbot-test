package fr.uge.legoandbeer;

import java.util.Locale;

public sealed interface Article permits Lego, Beer{
//	long price();
	static Article parseArticle(String line) {
		String[] articles = line.split(",", -1);
		if (articles.length != 3) {
			throw new IllegalArgumentException("lines invalid : " + line);
		}
		return switch(articles[0]) {
		case "lego" -> new Lego(Integer.parseInt(articles[1]), articles[2]);
		case "beer" -> new Beer(Integer.parseInt(articles[1]), BeerKind.valueOf(articles[2].toUpperCase(Locale.ROOT)));
		default -> throw new IllegalArgumentException("Unexpected value");
		};
	}
}
