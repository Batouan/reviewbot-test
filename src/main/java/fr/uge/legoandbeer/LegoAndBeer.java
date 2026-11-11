
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
        try (Stream<String> stream = Files.lines(path)) {
            return stream
                    .filter(line -> line != null)
                    .map(String::trim)
                    .map(Article::parseArticle)
                    .toList();
        }
    }

    static void printReceipt(List<Article> articles) {
        long result = 0;

        for (var article : articles) {
            switch (article) {
                case Lego(int quantity, String name) ->
                    result += quantity * name.length() * 20;

                case Beer(int quantity, BeerKind kind)
                    when kind == BeerKind.BLONDE ->
                    result += quantity * 3;

                case Beer(int quantity, BeerKind _) ->
                    result += quantity;
            }
        }

        IO.println("sum: " + result);

        articles.stream()
                .filter(article -> article instanceof Lego)
                .map(article -> (Lego) article)
                .max(Comparator.comparingLong(
                        lego -> lego.quantity() * lego.name().length() * 20
                ))
                .ifPresent(lego ->
                        IO.println("max lego: " + lego.name())
                );
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Usage: java fr.uge.legoandbeer.LegoAndBeer <file>");
            System.exit(1);
        }

        try {
            var articles = readArticlesFromFile(Path.of(args[0]));
            printReceipt(articles);
        } catch (Exception e) {
            System.err.println("An error occurred");
            System.exit(1);
        }
    }
}
