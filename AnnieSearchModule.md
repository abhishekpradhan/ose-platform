# Introduction #

This module is to take a feature query as input and execute the query processor on feature index to return a list of web pages along with the features they contain.

There's some obsolete learning code in the module too (using weka learning toolkit). We've been using LBJ to do learning now.

  * annieSearch :
> > java package for indexing, querying and ranking in Object Search

  * annieWeb
> > a set of java servlets for object search demo

# Details #

## Start writing code ##

  * Reverse-engineering


> The best way to understand the code is by "reverse-engineering". Start with
> http://localhost:8080/annieWeb/TestFeatureQuery.html and trace the code all the way to annieSearch "retrieval" module.

> Eclipse provides excellent tools to reverse engineer Java code. Press "F3" to navigate any declaration.

  * Query by features

> The following code demonstrates how to query by features

```
     String featureQuery  = "%TFFeature(HTMLTitle(canon))" ;
     OSSearcher searcher = new OSSearcher([INDEX PATH]);
     OSHits result = searcher.featureSearch(featureQuery);
     for (Document doc : result) {				
	List<FeatureValue> features = result.docFeatures();
	System.out.println("Doc ID : " + result.getDocID() + " doc features : " + features);
     }   
```





## Feature Language ##

### Query field ###
are denoted as ALL-CAPITAL word, e.g : BRAND for brand, PRICE for price...
when they're evaluated, these values are replaced by the corresponding values from a query

See the set of fields in DigitalCameraDomain and ProfessorDomain

### Basic Features ###
This maps to inverted indices

| **Name** | **Parameters** | **Description** | **Examples** |
|:---------|:---------------|:----------------|:-------------|
| Token | word | tokenized text in the body | Token(canon) |
| HTMLTitle | work  | tokenized text in the title | HTMLTitle(canon) |
| Number\_body | range | constrained number in the body | Number\_body(_range(100,200) )_|
| Number\_title | range | constrained number in the title | Number\_title(_range(100,200) )_|

### Combined Features ###

| **Name** | **Parameters** | **Description** | **Examples** |
|:---------|:---------------|:----------------|:-------------|
| Phrase | sequence of other features | consecutive features (as in a phrase) | Phrase(Token(optical),Token(zoom)) |
| And | set of other features | this feature is ALL sub features are on in a document | And(Token(buy),Token(now) |
| Or | set of other features | this feature is ONE of sub features is on in a document | Or(Phrase('my research'),Phrase('research interests')) |
| Proximity | Feature A,B, Number lower,upper | is ON if A and B is found within a proximity : lower <= A.position - B.position <= upper | Proximity(Phrase('research group'),Token(AREA),-10,10) |

### Per-Document Features ###

| **Name** | **Parameters** | **Description** | **Examples** |
|:---------|:---------------|:----------------|:-------------|
| %BooleanFeature | feature A | return 1/0 if feature A is On/Off | %BooleanFeature(Token(checkout)) |
| %CountNumber | feature A | same as %TFFeature but return the count by actually counting each occurrence in a doc | %CountNumber(Proximity(Number(MEGAPIXEL),Token(megapixel,megapixels,mp),-3,1)) |
| %FeatureSet | set of features | ???? | ??? |
| %Log | numeric feature A | return log(A) | %Log(%TFFeature(Token(DEPARTMENT))) |

### Some Example Features ###

%BooleanFeature(HTMLTitle(BRAND))

%Log(%TFFeature(Token(BRAND)))

%BooleanFeature(HTMLTitle(MODEL))

%Log(%TFFeature(Token(MODEL)) )

%BooleanFeature(Proximity(Number\_title(MPIX),HTMLTitle(megapixel,megapixels,mp),-3,0))

%DivideBy(%CountNumber(Proximity(Number\_body(ZOOM),Phrase(Token(optical),Token(zoom)),-4,3)),%CountNumber(Proximity(Number\_body(),Phrase(Token(optical),Token(zoom)),-4,3)))

%BooleanFeature(Phrase(Number\_title(ZOOM),HTMLTitle(x)))

%DivideBy(%CountNumber(Proximity(Number\_body(PRICE),Token('$',usd,dollar,dollars),-2,1)),%CountNumber(Proximity(Number\_body(),Token('$',usd,dollar,dollars),-2,1)))

%BooleanFeature(Proximity(Token(price),Phrase(Token('$'),Number\_body(PRICE)),-3,3))

%BooleanFeature(Token(checkout))

%BooleanFeature(Token(shop shopping))

%BooleanFeature(Token(shipping shipped ships))

%BooleanFeature(Phrase("add to cart"))

%BooleanFeature(Token(availability))

%BooleanFeature(HTMLTitle(NAME))

%TFFeature(Token(DEPT))

%BooleanFeature(HTMLTitle(homepage))

%BooleanFeature(Token(professor))

%BooleanFeature(Token(publication,publications,paper,papers))

%Log(%TFFeature(Token(bio,biography)))

%BooleanFeature(Token(teaching,class))

%BooleanFeature(Token(student,students))