# Aristotle, *Politics*

A citable digital edition.

**Status**: <span style="color: red;">In progress</span>.

## Bibliography

Text: `urn:cts:greekLit:tlg0086.tlg035.ellis:`

*A Treatise on Government*, By Aristotle. Translated From The Greek Of Aristotle By William Ellis, A.M. London & Toronto Published By J M Dent & Sons Ltd. & In New York By E. P. Dutton &. Co. First Issue Of This Edition 1912 Reprinted 1919, 1923, 1928

Aristotle was a Greek philosopher from the 4th Century BCE. In his time and place, “philosophy” had a broad meaning, including things like ethics and metaphysics (still included in the discipline of Philosophy), but also things that we would now consider sociology, political science, psychology, and the natural sciences.

The *Politics* (called in this 1912 English translation *A Treatise on Government*) is Aristotle’s longest—6 volumes—discussion of human beings and their societies in general.

Basic bibliography:

- E. Clayton, “Aristotle, *Politics*” (The Internet Encyclopedia of Philosophy) <https://www.iep.utm.edu/aris-pol/>.

A plain-text, citable edition following the protocols of the [CITE Architecture](http://cite-architecture.org). The file in `text/arist_politics.cex` is intended for machine-processing and has been validated as to character-set and spelling.

There is a human-readable HTML site, derived from the `.cex` file, in `html/`. The scripts that generated that site are in `/src/main/scala/`.

Included Scala scripts take advantage of the code libraries in the [CITE Architecture](http://cite-architecture.org).

This version of this text is [in the Public Domain](http://creativecommons.org/publicdomain/zero/1.0/): 

![license](http://i.creativecommons.org/p/zero/1.0/88x31.png)

Edited, 12/9/2019

## Running Scripts for Validation, Publication, and Analysis

This repository is an SBT project for running Scala code. Everything should be reproducible.

### Requirements

- A Java JDK 1.8 or higher.
- [SBT](https://www.scala-sbt.org) Installed and on the PATH.

### Running

- Clone this repository.
- Navigate to this repository's root level.
- `$ sbt console`
- `scala> :load scripts/character-validation.sc`
- etc.

## Code Contents


## Character Validation

The text has been machine validated as to character-set using [a Scala script](https://github.com/Eumaeus/CSC-270-Work/blob/master/scripts/corpus-char-validate.sc). The following is a complete inventory of the characters present in this text:

<div style="font-size: 75%">

| Character | Character | Character | Character | Character |
|-----------|-----------|-----------|-----------|-----------|
| `space` (20) | `!` (21) | `"` (22) | `'` (27) | `(` (28) |
| `)` (29) | `,` (2c) | `-` (2d) | `.` (2e) | `0` (30) |
| `1` (31) | `2` (32) | `3` (33) | `4` (34) | `5` (35) |
| `6` (36) | `7` (37) | `8` (38) | `9` (39) | `:` (3a) |
| `;` (3b) | `?` (3f) | `A` (41) | `B` (42) | `C` (43) |
| `D` (44) | `E` (45) | `F` (46) | `G` (47) | `H` (48) |
| `I` (49) | `J` (4a) | `K` (4b) | `L` (4c) | `M` (4d) |
| `N` (4e) | `O` (4f) | `P` (50) | `R` (52) | `S` (53) |
| `T` (54) | `U` (55) | `V` (56) | `W` (57) | `X` (58) |
| `Y` (59) | `Z` (5a) | `[` (5b) | `]` (5d) | `_` (5f) |
| `a` (61) | `b` (62) | `c` (63) | `d` (64) | `e` (65) |
| `f` (66) | `g` (67) | `h` (68) | `i` (69) | `j` (6a) |
| `k` (6b) | `l` (6c) | `m` (6d) | `n` (6e) | `o` (6f) |
| `p` (70) | `q` (71) | `r` (72) | `s` (73) | `t` (74) |
| `u` (75) | `v` (76) | `w` (77) | `x` (78) | `y` (79) |
| `z` (7a) | `…` (2026) |

</div>

Confirm character-validation with:

~~~
$ sbt console
scala> :load scripts/character-validation.sc
~~~

This will generate a file `validation-data/charTable.md` containing each distinct character present in the text, with its Unicode value.

## Spelling Validation

This English translation of Aristotle's *Politics* has been spell-checked against two files. One is [a standard English word-list](https://github.com/Eumaeus/CSC-270-Work/tree/master/validation-data/SCOWL-wl) generated from the [SCOWL](http://wordlist.aspell.net) online tool. The second is [a user-dictionary](https://github.com/Eumaeus/CSC-270-Work/blob/master/validation-data/userDictionary.txt). 

The spell-check script at [/scripts/character-validation.sc](https://github.com/Eumaeus/CSC-270-Work/blob/master/scripts/character-validation.sc).

Confirm spelling validation with:

~~~
$ sbt console
scala> :load scripts/corpus-spelling.sc
~~~

## Ngram Analysis

An NGram is a recurring pattern of N-number of words. This repository includes a basic Scala script showing how NGram analysis can work with, and be enhanced by, the [CITE Architecture](http://cite-architecture.org).

Running: 

~~~
$ sbt console
scala> :load scripts/ngrams.sc
~~~

This script analyzes the text for 3-grams. 

1. It generates a *citable tokenized exemplar* of the text, divided into word-tokens, with punctuation removed, and all words lower-cased: `noPuncCorpus`.
1. It creates a Vector of all possible patterns of 3 words, as a `Vector[ (CtsUrn, String) ]`, with citations keyed to `noPuncCorpus`: `ngt`.
1. It creates a Histogram of those 3-Grams, consisting of a `Vector[ (String, Int) ]`, sorted by frequency (most common NGrams at the bottom): `ngh`.

See the histogram with: `scala> showMe(ngh)`

Find all citations to occurances of one Ngram with, e.g.:

~~~scala
scala> val oneNG: Set[CtsUrn] = urnsForNGram( "the administration of", ngt)
scala> showMe(oneNG)
~~~

If we ask for NGrams where `n=1`, we simply get a list of the vocabulary for this text, sorted by frequency: 

~~~scala
scala> val vocab = makeNGramTuples(1, noPuncCorpus)
scala> val vocabHisto = makeNGramHisto(vocab)
~~~

The script includes a function `takePercent( histo: Vector[(String, Int)], targetPercent: Int)`. This is a tail-recurive method that will take, starting with the most frequent, items from a histogram that add up to a desired percentage of all occurrances. In other words, "What English words do I need to know to recognize 50% of the words in this text?":

~~~scala
scala> val halfOfAllWords = takePercent(vocabHisto, 50)
scala> showMe(halfOfAllWords)
~~~

