package fr.uge.legoandbeer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class LegoAndBeer {
	private LegoAndBeer() {
		
	}
	
	public static List<Article> readArticlesFromFile(Path path) throws IOException {
		Objects.requireNonNull(path);
		try(Stream<String> stream = Files.lines(path)) {
			return stream.filter(line -> !line.isBlank()).map(Article::parseArticle).toList();
		}
	}
	
//	static void printReceipt(List<Article> articles) {
//	    Objects.requireNonNull(articles);
//	    long sum = 0;
//	    for (var article : articles) {
//	        sum += article.price();
//	    }
//	    IO.println("sum: " + sum);
//	}
	
	static void printReceipt(List<Article> articles) {
		Objects.requireNonNull(articles);
		long result = 0;
		for (var article : articles) {
			switch(article) {
			case Lego(int quantity, String name) -> result += (long) (quantity * name.length()) * 20;
			case Beer(int quantity, BeerKind kind) when kind.equals(BeerKind.BLONDE) -> result += (long) quantity;
			case Beer(int quantity, BeerKind _) -> result += (long) quantity * 3;
			}
		}
		IO.println("sum: " + result);
		articles.stream().flatMap(article -> switch(article) {
		case Beer _ -> null;
		case Lego lego -> Stream.of(lego);
		}).max(Comparator.comparingLong(lego -> lego.name().length() *20)).ifPresent(lego -> IO.println("max lego: " + lego.name()));
	}
	
	public static int test() {
		int i = 5;
		return 2;
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Usage: java fr.uge.legoandbeer.LegoAndBeer <file>");
			System.exit(1);
			return;
		}
		try {
			var articles = readArticlesFromFile(Path.of(args[0]));
			printReceipt(articles);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
			return;
		}
	}
}
