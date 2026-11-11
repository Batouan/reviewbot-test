# Exercice 2 - Polymorphism vs Pattern-Matching

Après moult rebondissements, nous venons enfin d'ouvrir notre magasin "Lego and Beer" destiné aux motards adeptes de légo et de bière, notre cœur de cible.
Comme le nom du magasin l'indique, un article vendu dans notre magasin est soit une boîte de Lego, soit une bière (en fait, plusieurs car on a une quantité associée).
Une boîte de Lego est définie par une quantité (`quantity`, un entier strictement positif) et un nom (`name`, non `null`).
Une bière est définie par une quantité (`quantity`, un entier strictement positif) et une sorte de bière (`kind`) parmi `BLONDE`, `WHITE` ou `IPA`.

Les tests JUnit 5 de cet exercice sont `LegoAndBeerTest.java`.

1. Dans le package `fr.uge.legoandbeer`, modéliser avec des classes, interfaces, enums, etc, les données du domaine (qu'est-ce qu'une bière, qu'est-ce qu'une boîte de Lego, etc). Chaque constructeur doit valider ses paramètres.
   Vérifier que les tests marqués "Q1" passent.
   Note : les tests des questions suivantes utilisent sûrement des classes ou méthodes qui, à ce stade, n'ont pas encore été écrites, donc le code risque de ne pas compiler. **Commentez les autres tests (Q2, Q3, etc.), et vous les dé-commenterez au fur et à mesure.**

```java
public record Lego(int quantity, String name) implements Article {
	public Lego {
		Objects.requireNonNull(name);
		if (quantity <= 0) {
			throw new IllegalArgumentException("quantity <= 0");
		}
	}
}

public record Beer(int quantity, BeerKind kind) implements Article {
	public Beer {
		Objects.requireNonNull(kind);
		if (quantity <= 0) {
			throw new IllegalArgumentException("quantity <= 0");
		}
	}
}

public enum BeerKind {
	BLONDE,
	WHITE,
	IPA
}

public sealed interface Article permits Lego, Beer{
	
}

public final class LegoAndBeer {
	private LegoAndBeer() {
		
	}
}

```


2. Chaque article est décrit par une ligne de texte de la forme `"lego,<quantity>,<name>"` ou `"beer,<quantity>,<kind>"` (par exemple `"lego,4,castle"` ou `"beer,6,ipa"`), la sorte de bière étant insensible à la casse.
   Écrire une méthode `parseArticle(line)` dans `Article` (si vous n'avez pas nommé l'interface pour les bières et les légos ainsi, faites un "refactor") qui renvoie l'`Article` correspondant, et qui lève une `IllegalArgumentException` si la ligne ne contient pas exactement 3 champs séparés par une virgule, si le type d'article n'est ni `"lego"` ni `"beer"`, ou si la sorte de bière est inconnue.
   Note : pour découper une chaîne de caractères, il existe en Java la méthode `String.split(separator, -1)`.
   Note2 : pour la sorte de bière, il faut être insensible à la casse indépendamment de la locale par défaut de la machine (pensez à la Turquie et à son `i` sans point...).
   Vérifier que les tests marqués "Q2" passent.

```java
public sealed interface Article permits Lego, Beer{
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
```


3. Dans une classe `LegoAndBeer`, écrire une méthode statique `readArticlesFromFile(path)` qui lit un fichier contenant une ligne par article (les lignes vides ou contenants des blancs (`String.isBlank()`) devant être ignorées) et renvoie la liste, non modifiable, des articles correspondants.
   Rappel : `java.nio.file.Files.lines(path)` renvoie un `Stream<String>` associé à un descripteur de fichier qu'il faut impérativement fermer.
   Vérifier que les tests marqués "Q3" passent.

```java
public static List<Article> readArticlesFromFile(Path path) throws IOException {
    try(Stream<String> stream = Files.lines(path)) {
        return stream.filter(line -> !line.isBlank()).map(Article::parseArticle).toList();
    }
}
```


4. On veut maintenant calculer le prix du reçu (`printReceipt`) toujours dans la classe `LegoAndBeer`. Le prix d'une boîte de Lego est la quantité multipliée par le nombre de caractères du nom, le tout multiplié par 20. Le prix d'une bière est la quantité si la bière est `BLONDE`, ou trois fois la quantité sinon.
   On commence par la solution "orienté objet" classique utilisant le `polymorphisme`.
   - On souhaite écrire une méthode d'aide (*helper method*) `printReceipt(articles)` qui parcourt la liste des articles avec une boucle `for`, calcule la somme, et affiche `"sum: "` et la somme.
   - On souhaite écrire une méthode `main(args)` : elle doit vérifier qu'il y a exactement un argument (le chemin du fichier), sinon afficher l'usage sur `stderr` et quitter avec le code 1 ; lire le fichier ; en cas d'erreur afficher le message d'erreur sur `stderr` et quitter avec le code 1 ; sinon appeler `printReceipt`.
     Note : N'oubliez pas de gérer **correctement** les exceptions!

   Est-ce que la méthode `printReceipt` doit-être publique ? Même question avec la méthode `main` ?
   Écrire les méthodes `printReceipt(articles)` et `main(args)`.
   Vérifier que les tests marqués "Q4" passent.

**La Méthode `printReceipt` n'a pas besoin d'être publique car c'est un helper utilisée uniquement dans le main.**

**Le main doit être public.**

```java
public record Lego(int quantity, String name) implements Article {

    @Override
    public long price() {
        return (long) quantity * name.length() * 20;
    }
}

public record Beer(int quantity, BeerKind kind) implements Article {

    @Override
    public long price() {
        return switch (kind) {
            case BLONDE -> quantity;
            case default -> quantity * 3L;
        };
    }
}

static void printReceipt(List<Article> articles) {
    Objects.requireNonNull(articles);
    long sum = 0;
    for (var article : articles) {
        sum += article.price();
    }
    IO.println("sum: " + sum);
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
```

5. Le problème du `polymorphisme`, c'est qu'il faut modifier `Article` (et donc **toutes** ses implantations) à chaque fois qu'on veut ajouter un nouveau calcul. Dans notre cas, le prix n'est pas vraiment une propriété d'un article, c'est une règle de gestion propre au magasin.
   On va préférer réécrire le code en utilisant le `pattern matching`. Pour cela, on va garder les types `Lego` et `Beer` mais commenter le code nécessaire au `polymorphisme`.
   A part l'explication ci-dessus, dans quel cas utilise-t-on le `polymorphisme` et dans cas, utilise-t-on le `pattern matching` ?
   Réécrire `printReceipt` en utilisant le `pattern matching` et les *record patterns*.
   Vérifier que les tests marqués "Q5" passent et que les tests précédents passent toujours.
   Note: penser aux `record patterns` et au `when`!

**On utilise le pattern matching quand l'interface est sealed et qu'on ajoute souvent de nouvelles opérations. On utilise le polymorphisme quand l'interface n'est pas sealed et que les implémentations sont peu nombreuses.**

```java

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
}
```


6. On veut maintenant afficher, sur une seconde ligne, le nom de la boîte de Lego la plus chère du reçu. En cas d'égalité de prix, on garde la première boîte rencontrée dans la liste.
   L'affichage doit être "max lego: " suivit du nom de la boîte de Lego. Il ne doit pas y avoir d'affichage, s'il n'y a pas de boîte de Lego.
   Pour cette question, utiliser `Collectors.partitioningBy` pour séparer les bières des Lego, puis `Stream.max` avec un comparateur créé avec `Comparator.comparing*` pour obtenir la boîte de Lego avec le prix unitaire le plus élevé.
   Vérifier que les tests marqués "Q6" passent.

```java
static void printReceipt(List<Article> articles) {
		//...
		var partition = articles.stream().collect(Collectors.partitioningBy(article -> switch (article) {
	        case Lego _ -> true;
	        case Beer _ -> false;
		}));
		partition.get(true).stream()
	    .map(article -> switch (article) {
	        case Lego lego -> lego;
	        case Beer _ -> null;
	    })
	    .max(Comparator.comparingLong(lego -> lego.name().length() * 20)).ifPresent(lego -> IO.println("max lego: " + lego.name()));
		
	}
```


7. Le `partitioningBy` + cast de la question précédente est exactement le genre de code que le pattern matching est censé nous éviter d'écrire.
   Remplacer ce code par une solution utilisant `flatMap` avec un `switch` à base de pattern matching sur `Article`.
   Note: vous devez absolument comprendre comment marche `flatMap`, c'est une méthode essentielle qui sert de base à la notion de `Gatherer` que nous verrons plus tard en cours.
   Astuce : `flatMap` accepte qu'une fonction renvoie `null` à la place d'un stream pour indiquer qu'il n'y a pas d'élément (cela évite d'allouer un stream vide).
   Il n'y a pas de nouveaux tests pour cette question : vérifier que les tests marqués "Q6" passent toujours.


```java
static void printReceipt(List<Article> articles) {
    //...
    articles.stream().flatMap(article -> switch(article) {
    case Beer _ -> null;
    case Lego lego -> Stream.of(lego);
    }).max(Comparator.comparingLong(lego -> lego.name().length() *20)).ifPresent(lego -> IO.println("max lego: " + lego.name()));
}
```

8. On parcourt actuellement la liste des articles deux fois (une fois pour la somme, une fois pour le Lego avec le prix le plus élevé). Regrouper les deux calculs en un seul parcours de `Stream`, en utilisant `Collectors.teeing()` pour combiner un `Collectors.summingLong()` et un `Collectors.maxBy()` (avec en intermédiaire un `Collectors.flatMapping()` pour faire l'équivalent du `flatMap`).
   Une fois que vous avez réussi votre implantation, à votre avis, quelle implantation est la meilleure pour la production entre votre implantation précédente et votre nouvelle implantation ?
   Il n'y a pas de nouveaux tests pour cette question.