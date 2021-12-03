# What is this?
Tom Marvolo Riddle -> I am Lord Voldemort

Sometimes you want a nice, cursed anagram of your name. This repository helps you do just that.

Finding anagrams of a given word using a dictionary is a fairly trivial exercise in programming.
However, the resulting anagram words comprise an enormous solution set (hundreds of thousands) 
and are not funny unless you do some additional work.

This repository contains four different word lists:
1. words.txt are ~70,000 normal dictionary words
2. urban-words.txt are 50,000 words from a breadth-first search on Urban Dictionary
3. wikipedia-words.txt are 50,000 words from repeated accesses of random Wikipedia pages
4. log-words.txt is the 50,000 urban dictionary words scaled so that words common in
Urban Dictionary and rare in Wikipedia are scored first. This will prioritize slang, curses and
other generally foul words.

AnagramMe will use log-words.txt to generate the dankest anagrams of a query string.

# How do I use it?
You may choose to remove the Wikipedia and UrbanDictionary scrapers if you don't want to 
generate your own dictionaries. If you do, you will need to download and set up the Jsoup
jar file.

You can change the anagrammed string in the main function of AnagramMe. My name is left there for
reference.

The program will output the best 2,000 anagrams. These will generally be better toward the top.
Please note that this program can produce offensive output; it's a direct, unfiltered crawl from
Urban Dictionary. It's up to you to choose not to use words that you find distasteful.

# Other Notes:
You can change the penaltySize of the AnagramMe function by changing a static variable at the
top of the page. A penaltySize of 1 corresponds to dividing the sum of word scores in an anagram
by the number of words in the anagram (i.e., taking the average). Size of 2 divides the anagram
again by the number of words. This will tend to prioritize shorter sentences of longer words.
