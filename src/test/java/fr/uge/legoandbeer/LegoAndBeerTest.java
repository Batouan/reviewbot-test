package fr.uge.legoandbeer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import module java.base;

import static org.junit.jupiter.api.Assertions.*;

public final class LegoAndBeerTest {

  @Nested
  public class Q1 {

    @Test
    @DisplayName("Lego exposes the quantity and name it was constructed with")
    public void legoStoresQuantityAndName() {
      var lego = new Lego(3, "brick");
      assertEquals(3, lego.quantity());
      assertEquals("brick", lego.name());
    }

    @Test
    @DisplayName("Lego constructor rejects a zero or negative quantity")
    public void legoRejectsNonPositiveQuantity() {
      assertThrows(IllegalArgumentException.class, () -> new Lego(0, "jay"));
      assertThrows(IllegalArgumentException.class, () -> new Lego(-1, "nya"));
    }

    @Test
    @DisplayName("Lego constructor rejects a null name")
    public void legoRejectsNullName() {
      assertThrows(NullPointerException.class, () -> new Lego(1, null));
    }

    @Test
    @DisplayName("Beer exposes the quantity and kind it was constructed with")
    public void beerStoresQuantityAndKind() {
      var beer = new Beer(5, BeerKind.IPA);
      assertEquals(5, beer.quantity());
      assertEquals(BeerKind.IPA, beer.kind());
    }

    @Test
    @DisplayName("Beer constructor rejects a zero or negative quantity")
    public void beerRejectsNonPositiveQuantity() {
      assertThrows(IllegalArgumentException.class, () -> new Beer(0, BeerKind.WHITE));
      assertThrows(IllegalArgumentException.class, () -> new Beer(-2, BeerKind.WHITE));
    }

    @Test
    @DisplayName("Beer constructor rejects a null kind")
    public void beerRejectsNullKind() {
      assertThrows(NullPointerException.class, () -> new Beer(1, null));
    }

    @Test
    @DisplayName("BeerKind exposes exactly three values and valueOf resolves them")
    public void beerKindHasThreeValues() {
      assertEquals(3, BeerKind.values().length);
      assertEquals(BeerKind.BLONDE, BeerKind.valueOf("BLONDE"));
    }

    @Test
    @DisplayName("Legos with the same field values are equal and share a hash code")
    public void legoHaveFieldBasedEquality() {
      var a = new Lego(2, "duck");
      var b = new Lego(2, "duck");
      var c = new Lego(3, "duck");
      assertEquals(a, b);
      assertEquals(a.hashCode(), b.hashCode());
      assertNotEquals(a, c);
    }

    @Test
    @DisplayName("Beers with the same field valuess are equal and share a hash code")
    public void beerHaveFieldBasedEquality() {
      var a = new Beer(2, BeerKind.IPA);
      var b = new Beer(2, BeerKind.IPA);
      var c = new Beer(2, BeerKind.BLONDE);
      assertEquals(a, b);
      assertEquals(a.hashCode(), b.hashCode());
      assertNotEquals(a, c);
    }

    @Test
    @DisplayName("Lego and Beer implement exactly one, shared common supertype")
    public void legoAndBeerHaveACommonSuperType() {
      assertEquals(1, Lego.class.getInterfaces().length);
      assertEquals(1, Beer.class.getInterfaces().length);

      assertEquals(Lego.class.getInterfaces()[0], Beer.class.getInterfaces()[0]);
    }

    @Test
    @DisplayName("The common supertype does not declare a quantity() method itself")
    public void superTypeShouldNotDeclareAMethodQuantity() {
      assertEquals(1, Lego.class.getInterfaces().length);
      var superType = Lego.class.getInterfaces()[0];
      try {
        superType.getMethod("quantity");
        fail();
      }  catch (NoSuchMethodException _) {
        // okay
      }
    }

    @Test
    @DisplayName("Lego access flags are public and final")
    public void legoClass() {
      assertTrue(Lego.class.accessFlags().contains(AccessFlag.PUBLIC));
      assertTrue(Lego.class.accessFlags().contains(AccessFlag.FINAL));
    }

    @Test
    @DisplayName("Beer access flags are public and final")
    public void beerClass() {
      assertTrue(Beer.class.accessFlags().contains(AccessFlag.PUBLIC));
      assertTrue(Beer.class.accessFlags().contains(AccessFlag.FINAL));
    }

    @Test
    @DisplayName("BeerKind is declared public and final")
    public void beerKindClass() {
      assertTrue(BeerKind.class.accessFlags().contains(AccessFlag.PUBLIC));
      assertTrue(BeerKind.class.accessFlags().contains(AccessFlag.FINAL));
    }
  }


  @Nested
  public class Q2 {

    @Test
    @DisplayName("A well-formed lego line parses into the matching Lego")
    public void parsesLegoLine() {
      var article = Article.parseArticle("lego,4,castle");
      assertEquals(new Lego(4, "castle"), article);
    }

    @Test
    @DisplayName("A well-formed beer line parses into the matching Beer")
    public void parsesBeerLine() {
      var article = Article.parseArticle("beer,6,ipa");
      assertEquals(new Beer(6, BeerKind.IPA), article);
    }

    @Test
    @DisplayName("Beer kind parsing ignores letter case")
    public void parsesBeerKindCaseInsensitively() {
      var lower = Article.parseArticle("beer,1,blonde");
      var mixed = Article.parseArticle("beer,1,bLonde");
      var upper = Article.parseArticle("beer,1,BLONDE");
      assertEquals(new Beer(1, BeerKind.BLONDE), lower);
      assertEquals(new Beer(1, BeerKind.BLONDE), mixed);
      assertEquals(new Beer(1, BeerKind.BLONDE), upper);
    }

    @Test
    @DisplayName("Beer kind parsing is locale-independent")
    public void parsesBeerKindXXX() {
      var locale =  Locale.getDefault();
      Locale.setDefault(Locale.of("tr", "TR"));
      try {
        var ipa = Article.parseArticle("beer,1,ipa");
        assertEquals(new Beer(1, BeerKind.IPA), ipa);
      } finally {
        Locale.setDefault(locale);
      }
    }

    @Test
    @DisplayName("Lines with too few or too many tokens are rejected for both article types")
    public void rejectsWrongTokenCount() {
      assertThrows(IllegalArgumentException.class, () -> Article.parseArticle("lego,4"));
      assertThrows(IllegalArgumentException.class, () -> Article.parseArticle("lego,4,castle,extra"));
      assertThrows(IllegalArgumentException.class, () -> Article.parseArticle("beer,4"));
      assertThrows(IllegalArgumentException.class, () -> Article.parseArticle("beer,4,blonde,extra"));
    }

    @Test
    @DisplayName("A line with an unrecognized article type is rejected")
    public void rejectsUnknownArticleType() {
      assertThrows(IllegalArgumentException.class, () -> Article.parseArticle("wine,4,red"));
    }

    @Test
    @DisplayName("A beer line with an unrecognized beer kind is rejected")
    public void rejectsUnknownBeerKind() {
      assertThrows(IllegalArgumentException.class, () -> Article.parseArticle("beer,4,rose"));
    }

    @Test
    @DisplayName("A lego line with a trailing comma is rejected")
    public void rejectsLineWithTrailingComma() {
      assertThrows(IllegalArgumentException.class, () -> Article.parseArticle("lego,4,castle,"));
    }

    @Test
    @DisplayName("Lines with a zero or negative quantity are rejected for both article types")
    public void rejectsNonPositiveQuantity() {
      assertThrows(IllegalArgumentException.class, () -> Article.parseArticle("lego,-1,castle"));
      assertThrows(IllegalArgumentException.class, () -> Article.parseArticle("beer,0,blonde"));
    }

    @Test
    @DisplayName("Lines whose quantity token isn't a valid number are rejected for both article types")
    public void rejectsNonNumericQuantity() {
      assertThrows(IllegalArgumentException.class, () -> Article.parseArticle("lego,abc,castle"));
      assertThrows(IllegalArgumentException.class, () -> Article.parseArticle("beer,abc,blonde"));
    }

    @Test
    @DisplayName("Article is abstract")
    public void articleClass() {
      assertTrue(Article.class.accessFlags().contains(AccessFlag.ABSTRACT));
    }
  }


  @Nested
  public class Q3 {

    @Test
    @DisplayName("Reading a file with several lines, including blank ones, returns only the parsed articles in order")
    public void readsMultipleArticlesAndSkipsBlankLines(@TempDir Path tempDir) throws Exception {
      var content = """
          lego,3,brick

          beer,5,blonde
          """;
      var file = tempDir.resolve("articles.txt");
      Files.writeString(file, content);

      var articles = LegoAndBeer.readArticlesFromFile(file);

      assertEquals(List.of(new Lego(3, "brick"), new Beer(5, BeerKind.BLONDE)), articles);
    }

    @Test
    @DisplayName("Reading an empty file returns an empty list")
    public void emptyFileProducesEmptyList(@TempDir Path tempDir) throws Exception {
      var file = tempDir.resolve("empty.txt");
      Files.writeString(file, "");

      var articles = LegoAndBeer.readArticlesFromFile(file);

      assertTrue(articles.isEmpty());
    }

    @Test
    @DisplayName("A file containing one unparsable line causes readArticlesFromFile to throw")
    public void invalidLineInFileThrows(@TempDir Path tempDir) throws Exception {
      var file = tempDir.resolve("broken.txt");
      Files.writeString(file, "lego,3,brick\nnotanarticle\n");

      assertThrows(IllegalArgumentException.class, () -> LegoAndBeer.readArticlesFromFile(file));
    }

    @Test
    @DisplayName("Reading a file that doesn't exist throws IOException")
    public void missingFileThrowsIOException(@TempDir Path tempDir) {
      var file = tempDir.resolve("does-not-exist.txt");

      assertThrows(IOException.class, () -> LegoAndBeer.readArticlesFromFile(file));
    }

    @Test
    @DisplayName("The list returned by readArticlesFromFile is unmodifiable")
    public void readsReturnAnUnmodifiableList(@TempDir Path tempDir) throws Exception {
      var content = """
          lego,1,yoda
          lego,1,stormtrooper
          beer,2,ipa
          """;
      var file = tempDir.resolve("articles.txt");
      Files.writeString(file, content);

      var articles = LegoAndBeer.readArticlesFromFile(file);

      assertThrows(UnsupportedOperationException.class,
          () -> articles.add(new Lego(1, "yoda")));
    }

    @Test
    @DisplayName("readArticlesFromFile always calls close() on the file stream")
    public void readArticlesFromFileClosesStream() throws IOException {
      ClassModel classModel;
      try (var in = LegoAndBeer.class.getResourceAsStream(LegoAndBeer.class.getSimpleName() + ".class")) {
        if (in == null) {
          throw new IOException("could not locate class file for LegoAndBeer");
        }
        classModel = ClassFile.of().parse(in.readAllBytes());
      }
      var method = classModel.methods().stream()
          .filter(m -> m.methodName().stringValue().equals("readArticlesFromFile"))
          .findFirst()
          .orElseThrow(() -> new AssertionError("no method readArticlesFromFile"));
      var code = method.code()
          .orElseThrow(() -> new AssertionError("readArticlesFromFile should not be abstract"));

      assertFalse(code.exceptionHandlers().isEmpty(), "no try block around the Stream");

      // The method close() should be called
      var closeInvocations = code.elementList().stream()
          .filter(InvokeInstruction.class::isInstance)
          .map(InvokeInstruction.class::cast)
          .filter(invoke -> invoke.name().stringValue().equals("close"))
          .toList();

      assertFalse(closeInvocations.isEmpty(),
          "expected readArticlesFromFile to invoke close() on the Stream<String>, but none was found");
    }

    @Test
    @DisplayName("LegoAndBeer class is declared public and final")
    public void legoAndBeerClass() {
      assertTrue(LegoAndBeer.class.accessFlags().contains(AccessFlag.PUBLIC));
      assertTrue(LegoAndBeer.class.accessFlags().contains(AccessFlag.FINAL));
    }

    @Test
    @DisplayName("LegoAndBeer utility class exposes no public constructor")
    public void legoAndBeerClassHasNoPublicConstructor() {
      assertFalse(Arrays.stream(LegoAndBeer.class.getConstructors())
          .anyMatch(constructor -> constructor.accessFlags().contains(AccessFlag.PUBLIC)));
    }
  }


  // Used by Q4, Q5, etc.
  private static String captureReceiptOutput(List<Article> articles) {
    var originalOut = System.out;
    var buffer = new ByteArrayOutputStream();
    System.setOut(new PrintStream(buffer, false, StandardCharsets.UTF_8));
    try {
      LegoAndBeer.printReceipt(articles);
    } finally {
      System.setOut(originalOut);
    }
    return buffer.toString();
  }


  private record ProcessResult(String stdout, String stderr, int exitCode) {}

  private static ProcessResult runMain(String... arguments) throws Exception {
    var javaHome = System.getProperty("java.home");
    var javaBin = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
    var executable = Path.of(javaHome, "bin", javaBin);
    var classpath = System.getProperty("java.class.path");

    var command = new ArrayList<String>();
    command.add(executable.toString());
    command.add("-classpath");
    command.add(classpath);
    command.add("fr.uge.legoandbeer.LegoAndBeer");
    command.addAll(List.of(arguments));

    var process = new ProcessBuilder(command).redirectErrorStream(false).start();
    try {
      var stdout = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
      var stderr = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);

      process.waitFor();

      return new ProcessResult(stdout, stderr, process.exitValue());
    } finally {
      process.destroyForcibly();
    }
  }

  @Nested
  public class Q4 {

    @Test
    @DisplayName("Total price for Legos is correctly computed")
    public void sumsLegosOnly() {
      var articles = List.<Article>of(new Lego(2, "duck"), new Lego(1, "star"));
      // 2*4*20 + 1*4*20 = 160 + 80 = 240
      assertTrue(captureReceiptOutput(articles).contains("sum: 240"));
    }

    @Test
    @DisplayName("Total price for Beers is correctly computed")
    public void sumsBeersOnly() {
      var articles = List.<Article>of(
          new Beer(3, BeerKind.BLONDE),
          new Beer(2, BeerKind.IPA),
          new Beer(5, BeerKind.WHITE));
      // 3*1 + 2*3 + 5*3 = 3 + 6 + 15 = 24
      assertTrue(captureReceiptOutput(articles).contains("sum: 24"));
    }

    @Test
    @DisplayName("Total price for a mix of Legos and Beers is correctly computed")
    public void sumsMixedArticles() {
      var articles = List.<Article>of(
          new Lego(2, "duck"),
          new Lego(1, "star"),
          new Beer(3, BeerKind.BLONDE),
          new Beer(2, BeerKind.IPA),
          new Beer(5, BeerKind.WHITE));
      // 240 + 24 = 264
      assertTrue(captureReceiptOutput(articles).contains("sum: 264"));
    }

    @Test
    @DisplayName("Summing a large number of Legos computes the correct total")
    public void sumsLargeNumberOfLegos() {
      var articles = IntStream.range(1, 10_000)
          .<Article>mapToObj(i -> new Lego(i, "lego"))
          .toList();
      // (1 + 2 + 3 + ... + 9999) * 80 = 3999600000
      assertTrue(captureReceiptOutput(articles).contains("sum: 3999600000"));
    }

    @Test
    @DisplayName("An empty article list computes a total sum of zero")
    public void emptyListPrintsZero() {
      assertTrue(captureReceiptOutput(List.of()).contains("sum: 0"));
    }


    @Test
    @DisplayName("A sum that exceeds Integer.MAX_VALUE is computed correctly")
    public void sumDoesNotOverflowIntRange() {
      var articles = List.<Article>of(
          new Beer(800_000_000, BeerKind.BLONDE),
          new Beer(800_000_000, BeerKind.BLONDE),
          new Beer(800_000_000, BeerKind.BLONDE));

      var output = captureReceiptOutput(articles);

      assertTrue(output.lines().toList().contains("sum: 2400000000"));
    }

    @Test
    @DisplayName("The printed total equals the sum of each article's own per-unit price")
    public void sumMatchesTotalOfPerUnitPrices() {
      var articles = List.<Article>of(new Lego(2, "duck"), new Beer(3, BeerKind.BLONDE));

      var lines = captureReceiptOutput(articles).lines().toList();

      assertTrue(lines.contains("sum: 163"));
    }

    @Test
    @DisplayName("A single Lego whose price alone exceeds Integer.MAX_VALUE is summed correctly")
    public void singleArticleSumExceedsIntegerMaxValue() {
      assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
        var longName = "a".repeat(5000);
        var articles = List.<Article>of(new Lego(21_475, longName));

        var lines = captureReceiptOutput(articles).lines().toList();

        assertTrue(lines.contains("sum: 2147500000"));
      });
    }

    @Test
    @DisplayName("A running total across multiple articles that crosses Integer.MAX_VALUE is summed correctly")
    public void sumAcrossMultipleArticlesExceedsIntegerMaxValue() {
      assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
        var name = "b".repeat(1000);
        var articles = List.<Article>of(new Lego(60_000, name), new Lego(60_000, name));

        var lines = captureReceiptOutput(articles).lines().toList();

        assertTrue(lines.contains("sum: 2400000000"));
      });
    }

    @Test
    @DisplayName("A Beer with an extremely large quantity is priced without running out of memory")
    public void largeQuantityDoesNotThrowOutOfMemory() {
      assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
        var articles = List.<Article>of(new Beer(716_000_000, BeerKind.IPA));

        var lines = captureReceiptOutput(articles).lines().toList();

        assertTrue(lines.contains("sum: 2148000000"));
      });
    }

    @Test
    @DisplayName("The sum line is printed as a plain integer, with no decimal point")
    public void sumIsPrintedAsPlainIntegerWithoutDecimalPoint() {
      var lines = captureReceiptOutput(List.of(new Lego(1, "duck"))).lines().toList();

      var sumLine = lines.stream().filter(l -> l.startsWith("sum: ")).findFirst().orElseThrow();
      assertFalse(sumLine.contains("."));
    }


    @Test
    @DisplayName("Running main() with no arguments prints usage to stderr and exits with status 1")
    public void noArgsPrintsUsageAndExitsWithError() throws Exception {
      var result = runMain();

      assertEquals(1, result.exitCode());
      assertTrue(result.stderr().contains("Usage:"));
      assertTrue(result.stdout().isEmpty());
    }

    @Test
    @DisplayName("Running main() with more than one argument prints usage to stderr and exits with status 1")
    public void tooManyArgsPrintsUsageAndExitsWithError() throws Exception {
      var result = runMain("file1.txt", "file2.txt");

      assertEquals(1, result.exitCode());
      assertTrue(result.stderr().contains("Usage:"));
      assertTrue(result.stdout().isEmpty());
    }

    @Test
    @DisplayName("Running main() with a valid file prints the receipt and exits with status 0")
    public void validFilePrintsReceiptAndExitsSuccessfully(@TempDir Path tempDir) throws Exception {
      var content = """
          lego,2,duck
          beer,3,blonde
          """;
      var file = tempDir.resolve("articles.txt");
      Files.writeString(file, content);

      var result = runMain(file.toString());

      // 2*4*20 + 3*1 = 160 + 3 = 163
      assertEquals(0, result.exitCode());
      assertTrue(result.stdout().contains("sum: 163"));
      assertTrue(result.stderr().isEmpty());
    }

    @Test
    @DisplayName("Running main() with an empty file prints a sum of zero and exits with status 0")
    public void emptyFilePrintsZeroSum(@TempDir Path tempDir) throws Exception {
      var file = tempDir.resolve("empty.txt");
      Files.writeString(file, "");

      var result = runMain(file.toString());

      assertEquals(0, result.exitCode());
      assertTrue(result.stdout().contains("sum: 0"));
    }

    @Test
    @DisplayName("Running main() with a nonexistent file prints an error to stderr and exits with status 1")
    public void missingFilePrintsErrorAndExitsWithError(@TempDir Path tempDir) throws Exception {
      var missing = tempDir.resolve("does-not-exist.txt");

      var result = runMain(missing.toString());

      assertEquals(1, result.exitCode());
      assertTrue(result.stdout().isEmpty());
      assertFalse(result.stderr().isBlank());
    }

    @Test
    @DisplayName("Running main() against a file with an unparsable line reports the offending line on stderr and exits with status 1")
    public void invalidArticleLinePrintsErrorAndExitsWithError(@TempDir Path tempDir) throws Exception {
      var file = tempDir.resolve("broken.txt");
      Files.writeString(file, "lego,3,brick\nnotanarticle\n");

      var result = runMain(file.toString());

      assertEquals(1, result.exitCode());
      assertTrue(result.stdout().isEmpty());
      assertTrue(result.stderr().contains("notanarticle"));
    }

    @Test
    @DisplayName("Running main() against a file with an unrecognized beer kind prints an error to stderr and exits with status 1")
    public void unknownBeerKindPrintsErrorAndExitsWithError(@TempDir Path tempDir) throws Exception {
      var file = tempDir.resolve("badbeer.txt");
      Files.writeString(file, "beer,2,rose\n");

      var result = runMain(file.toString());

      assertEquals(1, result.exitCode());
      assertTrue(result.stdout().isEmpty());
      assertFalse(result.stderr().isBlank());
    }

    @Test
    @DisplayName("Running main() with a directory path instead of a file prints an error to stderr and exits with status 1")
    public void mainOnDirectoryPathPrintsErrorAndExitsWithError(@TempDir Path tempDir) throws Exception {
      var result = runMain(tempDir.toString());

      assertEquals(1, result.exitCode());
      assertTrue(result.stdout().isEmpty());
      assertFalse(result.stderr().isBlank());
    }
  }


  @Nested
  public class Q5 {

    @Test
    @DisplayName("The common supertype is closed and declares Lego and Beer as permitted subclasses")
    public void hierarchyIsClosed() {
      assertEquals(1, Lego.class.getInterfaces().length);
      assertNotNull(Lego.class.getInterfaces()[0].getPermittedSubclasses());
      assertEquals(1, Beer.class.getInterfaces().length);
      assertNotNull(Beer.class.getInterfaces()[0].getPermittedSubclasses());
    }

    @Test
    @DisplayName("The common supertype declares no instance methods of its own")
    public void commonSuperTypeIsEmpty() {
      assertEquals(1, Lego.class.getInterfaces().length);
      assertEquals(0, Arrays.stream(Lego.class.getInterfaces()[0].getMethods())
          .filter(m -> !m.accessFlags().contains(AccessFlag.STATIC))
          .count());
      assertEquals(1, Beer.class.getInterfaces().length);
      assertEquals(0, Arrays.stream(Beer.class.getInterfaces()[0].getMethods())
          .filter(m -> !m.accessFlags().contains(AccessFlag.STATIC))
          .count());
    }

    @Test
    @DisplayName("Lego and Beer expose no shared public methods beyond Object defaults")
    public void noCommonMethodsBetweenLegoAndBeer() {
      var filtered = Set.of("equals", "hashCode", "toString", "quantity");
      var legoMethods = Arrays.stream(Lego.class.getMethods())
          .filter(m -> m.getDeclaringClass() != Object.class)
          .map(Method::getName)
          .filter(name -> !filtered.contains(name))
          .collect(Collectors.toSet());
      var beerMethods = Arrays.stream(Beer.class.getMethods())
          .filter(m -> m.getDeclaringClass() != Object.class)
          .map(Method::getName)
          .filter(name -> !filtered.contains(name))
          .collect(Collectors.toSet());
      assertTrue(Collections.disjoint(legoMethods, beerMethods));
    }
  }


  @Nested
  public class Q6 {

    @Test
    @DisplayName("With a single Lego, printReceipt prints both the sum line and a matching max lego line")
    public void printsMaxLegoWhenOneLegoPresent() {
      var articles = List.<Article>of(new Lego(2, "duck"));

      var output = captureReceiptOutput(articles);

      var lines = output.lines().toList();
      assertEquals("sum: 160", lines.get(0));
      assertEquals("max lego: duck", lines.get(1));
    }

    @Test
    @DisplayName("Among several Legos, the one with the longest name is reported as the max lego")
    public void pickPriciestLego() {
      var articles = List.<Article>of(
          new Lego(1, "duck"),
          new Lego(1, "castle"),
          new Lego(1, "car"));

      var output = captureReceiptOutput(articles);

      assertTrue(output.lines().toList().contains("max lego: castle"));
    }

    @Test
    @DisplayName("When two Legos tie on price, the first one encountered in the list wins")
    public void tieOnPriceKeepsFirstEncountered() {
      // "duck" and "lion" both have length 4; "duck" appears first.
      var articles = List.<Article>of(
          new Lego(1, "duck"),
          new Lego(1, "lion"));

      var output = captureReceiptOutput(articles);

      assertTrue(output.lines().toList().contains("max lego: duck"));
    }

    @Test
    @DisplayName("When the list contains only Beers, no max lego line is printed")
    public void noMaxLegoLineWhenOnlyBeersPresent() {
      var articles = List.<Article>of(
          new Beer(3, BeerKind.BLONDE),
          new Beer(2, BeerKind.IPA));

      var output = captureReceiptOutput(articles);

      var lines = output.lines().toList();
      assertTrue(lines.getFirst().startsWith("sum: "));
      assertFalse(output.contains("max lego"));
    }

    @Test
    @DisplayName("Beer articles are excluded from max-lego selection")
    public void ignoresBeersWhenComputingMaxLego() {
      var articles = List.<Article>of(
          new Beer(10, BeerKind.IPA),
          new Lego(1, "car"),
          new Beer(20, BeerKind.WHITE));

      var output = captureReceiptOutput(articles);

      assertTrue(output.lines().toList().contains("max lego: car"));
    }

    @Test
    @DisplayName("Max-lego selection still finds the correct Lego when Legos and Beers are interleaved")
    public void partitioningPreservesOriginalOrderAcrossInterleavedArticles() {
      var articles = List.<Article>of(
          new Lego(1, "cat"),
          new Beer(5, BeerKind.WHITE),
          new Lego(1, "elephant"),
          new Beer(1, BeerKind.BLONDE),
          new Lego(1, "dog"));

      var output = captureReceiptOutput(articles);

      assertTrue(output.lines().toList().contains("max lego: elephant"));
    }

    @Test
    @DisplayName("Max-lego comparison is based purely on price, ignoring the Lego's quantity")
    public void maxLegoIgnoresQuantityAndOnlyComparesPrice() {
      var articles = List.<Article>of(new Lego(100, "a"), new Lego(1, "bb"));

      var output = captureReceiptOutput(articles);

      assertTrue(output.lines().toList().contains("max lego: bb"));
    }

    @Test
    @DisplayName("Max-lego selection remains correct even when the overall sum overflows the int range")
    public void maxLegoStillCorrectWhenSumOverflowsIntRange() {
      assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
        var longName = "a".repeat(5000);
        var articles = List.<Article>of(new Lego(21_475, longName), new Lego(1, "x"));

        var output = captureReceiptOutput(articles);

        assertTrue(output.lines().toList().contains("max lego: " + longName));
      });
    }

    @Test
    @DisplayName("Running main() with a lego-only file prints both the correct sum and the correct max lego line")
    public void mainPrintsMaxLegoLineForLegoOnlyFile(@TempDir Path tempDir) throws Exception {
      var content = """
      lego,1,duck
      lego,1,castle
      """;
      var file = tempDir.resolve("legos.txt");
      Files.writeString(file, content);

      var result = runMain(file.toString());

      assertEquals(0, result.exitCode());
      var lines = result.stdout().lines().toList();
      assertTrue(lines.contains("sum: 200"));       // 1*4*20 + 1*6*20
      assertTrue(lines.contains("max lego: castle"));
    }

    @Test
    @DisplayName("Running main() with a beer-only file prints the correct sum but no max lego line")
    public void mainOmitsMaxLegoLineForBeerOnlyFile(@TempDir Path tempDir) throws Exception {
      var content = """
      beer,3,blonde
      beer,2,ipa
      """;
      var file = tempDir.resolve("beers.txt");
      Files.writeString(file, content);

      var result = runMain(file.toString());

      assertEquals(0, result.exitCode());
      assertTrue(result.stdout().contains("sum: 9"));
      assertFalse(result.stdout().contains("max lego"));
    }

    @Test
    @DisplayName("Running main() against a file containing blank lines still parses correctly")
    public void mainWithBlankLinesInFileStillWorks(@TempDir Path tempDir) throws Exception {
      var content = """
      lego,1,duck

      beer,2,ipa

      """;
      var file = tempDir.resolve("blanks.txt");
      Files.writeString(file, content);

      var result = runMain(file.toString());

      assertEquals(0, result.exitCode());
      assertTrue(result.stdout().contains("sum: 86")); // 1*4*20 + 2*3
    }

    @Test
    @DisplayName("Running main() end-to-end with interleaved Legos and Beers still reports the correct max lego line")
    public void mainPreservesOrderingAcrossInterleavedArticles(@TempDir Path tempDir) throws Exception {
      var content = """
      lego,1,cat
      beer,5,white
      lego,1,elephant
      beer,1,blonde
      lego,1,dog
      """;
      var file = tempDir.resolve("mixed.txt");
      Files.writeString(file, content);

      var result = runMain(file.toString());

      assertEquals(0, result.exitCode());
      assertTrue(result.stdout().lines().toList().contains("max lego: elephant"));
    }
  }
}